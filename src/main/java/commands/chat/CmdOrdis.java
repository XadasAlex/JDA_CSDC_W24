package commands.chat;

import audio.AudioGuildManager;
import commands.ICommand;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import launcher.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import utils.Embedder;
import utils.Helper;
import utils.IconsGuild;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CmdOrdis implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping amountOption = event.getOption("amount");

        int amount = amountOption != null ? Math.min(amountOption.getAsInt(), 10) : 1;

        Random rand = new Random();
        List<String[]> links = Helper.fetchOrdisQuoteLinks();

        System.out.println(links);

        if (links == null || links.isEmpty()) return;

        String[] ordisQuoteDescription = new String[amount];
        String[] quoteLink = new String[amount];

        for (int i = 0; i < amount; i++) {
            int quoteIndex = rand.nextInt(links.size());

            quoteLink[i] = links.get(quoteIndex)[0];
            ordisQuoteDescription[i] = links.get(quoteIndex)[1];
        }

        AudioGuildManager audio = Bot.getInstance().getAudioGuildManagerById(event.getGuild().getIdLong());

        Helper.handleVoiceChannelJoin(event);

        var embed = Embedder.createBaseEmbed(event.getMember(), IconsGuild.YOUTUBE_ICON, getName(), amount <= 1 ? "Playing an Ordis quote" : String.format("Playing %d Ordis quotes", amount), "A collection of voice lines from: https://warframe.fandom.com/wiki/Ordis/Quotes");

        for (int i = 0; i < amount; i++) {
            embed.addField(String.format("[%s]", Helper.getPollEmojis().get(i)), ordisQuoteDescription[i], false);
        }

        event.replyEmbeds(embed.build()).queue(msg -> Helper.deleteAfter(msg, 120));

        audio.getLink().getPlayer().subscribe(player -> {
            audio.getLink().loadItem(quoteLink[0]).subscribe(loadResult -> {
                if (loadResult instanceof TrackLoaded trackLoaded) {
                    Track track = trackLoaded.getTrack();
                    player.setTrack(track).subscribe();
                }
            });

            int expectedTrackCount = amount - 1;
            AtomicInteger loadedTracksCount = new AtomicInteger(0);
            Map<Integer, Track> loadedTracks = Collections.synchronizedMap(new TreeMap<>());

            for (int i = 1; i < amount; i++) {
                int idx = i;
                audio.getLink().loadItem(quoteLink[idx]).subscribe(loadResult -> {
                    if (loadResult instanceof TrackLoaded trackLoaded) {
                        loadedTracks.put(idx, trackLoaded.getTrack());

                        if (loadedTracksCount.incrementAndGet() == expectedTrackCount) {
                            List<Track> orderedTracks = new ArrayList<>(loadedTracks.values());
                            audio.getTrackScheduler().jumpQueue(orderedTracks);
                        }
                    }
                });
            }
        });
    }

    @Override
    public String getName() {
        return "ordis";
    }

    @Override
    public String getDescription() {
        return "plays an ordis quote (warframe).";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "amount", "how many ordis quotes should be played back to back. (max: 10)", false)
        );
    }
}
