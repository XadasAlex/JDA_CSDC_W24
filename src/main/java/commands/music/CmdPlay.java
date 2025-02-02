package commands.music;

import commands.ICommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.*;
import launcher.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CmdPlay implements ICommand {
    private final Queue<Track> trackQueue = new LinkedList<>();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LavalinkClient client = Bot.getInstance().getLavalinkClient();
        String query = event.getOption("query").getAsString();
        if (query == null || query.isEmpty()) {
            event.reply("❌ You must provide a search query or URL!").setEphemeral(true).queue();
            return;
        }

        // ✅ Ensure YouTube search works if no URL is provided
        if (!query.startsWith("http")) {
            query = "ytsearch:" + query;
        }

        Member member = event.getMember();
        if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            event.reply("❌ You must be in a voice channel!").queue();
            return;
        }

        VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
        if (!event.getGuild().getAudioManager().isConnected()) {
            event.getGuild().getAudioManager().openAudioConnection(channel);
        }

        long guildId = event.getGuild().getIdLong();
        Link link = client.getOrCreateLink(guildId);

        event.deferReply().queue();

        // ✅ Load the item from Lavalink (can be a single track, playlist, or search result)
        String finalQuery = query;
        link.loadItem(query).subscribe(loadResult -> {
            if (loadResult instanceof TrackLoaded trackLoaded) {
                System.out.println("single track");
                Track track = trackLoaded.getTrack();
                playOrQueueTrack(link, track, event);
            } else if (loadResult instanceof PlaylistLoaded playlistLoaded) {
                System.out.println("playlist");
                List<Track> tracks = playlistLoaded.getTracks();
                if (!tracks.isEmpty()) {
                    for (Track track : tracks) {
                        trackQueue.add(track);
                    }
                    event.getHook().sendMessage("📥 Added " + tracks.size() + " tracks from playlist: " + playlistLoaded.getInfo().getName()).queue();
                    playNextTrack(link, event);
                }
            } else if (loadResult instanceof NoMatches) {
                System.out.println("nomatches");
                event.getHook().sendMessage("❌ No results found for: " + finalQuery).queue();
            } else if (loadResult instanceof LoadFailed) {
                System.out.println("load failed");
                event.getHook().sendMessage("❌ Failed to load track: " + finalQuery).queue();
            } else if (loadResult instanceof SearchResult searchResult) {
                Track track = searchResult.getTracks().get(0);
                playOrQueueTrack(link, track, event);
            }
        });
    }

    private void playOrQueueTrack(Link link, Track track, SlashCommandInteractionEvent event) {
        link.getPlayer().subscribe(player -> {
            if (player.getTrack() == null) {
                player.setTrack(track).subscribe();
                event.getHook().sendMessage("▶️ Now playing: " + track.getInfo().getTitle()).queue();
            } else {
                trackQueue.add(track);
                event.getHook().sendMessage("📥 Added to queue: " + track.getInfo().getTitle()).queue();
            }
        });
    }

    private void playNextTrack(Link link, SlashCommandInteractionEvent event) {
        link.getPlayer().subscribe(player -> {
            if (player.getTrack() == null && !trackQueue.isEmpty()) {
                Track nextTrack = trackQueue.poll();
                player.setTrack(nextTrack).subscribe();
                event.getHook().sendMessage("▶️ Now playing: " + nextTrack.getInfo().getTitle()).queue();
            }
        });
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays a song from a given URL or search query";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "query", "The URL or search term of the song", true));
    }
}
