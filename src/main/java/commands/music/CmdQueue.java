package commands.music;

import audio.AudioGuildManager;
import audio.TrackScheduler;
import commands.ICommand;
import dev.arbjerg.lavalink.client.player.Track;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.GuildSettings;
import utils.Helper;
import utils.IconsGuild;

import java.util.List;

public class CmdQueue implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!GuildSettings.isMusicAllowedInGuild(event.getGuild().getId())) {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), getName() , "Music isnt allowed on the server!").build()).queue();
            return;
        }

        long idLong = event.getGuild().getIdLong();
        AudioGuildManager audio = Bot.getInstance().getAudioGuildManagerById(idLong);
        TrackScheduler ts = audio.getTrackScheduler();

        EmbedBuilder queueEmbed = Embedder.createBaseEmbed(event.getMember(),
                IconsGuild.YOUTUBE_ICON,
                getName(),
                "Showing the current state of the Queue",
                "This will not be updated."
        );

        List<Track> trackQueue = ts.getTrackQueue().stream().toList();

        Track currentTrack = ts.getCurrent();
        Track previousTrack = ts.getPreviousTrack();

        createTrackQueueFieldPrevious(previousTrack, queueEmbed);
        createTrackQueueFieldCurrent(currentTrack, queueEmbed);

        long totalQueueTime = trackQueue.stream().mapToLong(track -> track.getInfo().getLength()).sum();

        queueEmbed.addField("Queue is listed below:", String.format("total amount: `[%s]`", Helper.formatMillisMMSS(totalQueueTime)), false);

        for (int i = 0; i < Math.min(trackQueue.size(), 10); i++) {
            Track track = trackQueue.get(i);
            String title = track.getInfo().getTitle();
            String link = track.getInfo().getUri();
            String duration = Helper.formatMillisMMSS(track.getInfo().getLength());
            String numberEmoji =  Helper.getPollEmojis().get(i);
            String linkEmoji = "\uD83D\uDD17";
            String name = String.format("[%s] %s:\n`[%s]`", numberEmoji, title, duration);
            String value = String.format("%s: %s", linkEmoji, link);
            queueEmbed.addField(name, value, false);
        }

        event.replyEmbeds(queueEmbed.build()).queue();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "shows the servers queue";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    private void createTrackQueueFieldPrevious(Track track, EmbedBuilder embed) {
        String name = null;
        String value = null;

        if (track != null) {
            String title = track.getInfo().getTitle();
            String artist = track.getInfo().getAuthor();
            String link = track.getInfo().getUri();
            String duration = Helper.formatMillisMMSS(track.getInfo().getLength());
            String linkEmoji = "\uD83D\uDD17";

            name = String.format("Previous track: %s:\n`[%s]`", title, duration);
            value = String.format("%s: %s", linkEmoji, link);
        } else {
            name = "No previous track!";
            value = "`[00:00]`";
        }

        embed.addField(name, value, false);
    }


    private void createTrackQueueFieldCurrent(Track track, EmbedBuilder embed) {
        String name = (String) null;
        String value = (String) null;

        if (track != null) {
            String title = track.getInfo().getTitle();
            String link = track.getInfo().getUri();
            String duration = Helper.formatMillisMMSS(track.getInfo().getLength());
            String linkEmoji = "\uD83D\uDD17";

            name = String.format("[\uD83D\uDD0A] %s:\n`[%s]`", title, duration);
            value = String.format("%s: %s", linkEmoji, link);
        } else {
            name = "No track is currently playing!";
            value = "`[00:00]`";
        }

        embed.addField(name, value, false);
    }
}
