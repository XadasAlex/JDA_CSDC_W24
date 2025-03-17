package utils;

import audio.AudioGuildManager;
import dev.arbjerg.lavalink.client.player.PlaylistLoaded;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class Embedder {
    public static EmbedBuilder createMissingPermissionEmbed(Member interactingMember, String commandName) {
        EmbedBuilder embed = createBaseErrorEmbed(interactingMember, commandName);
        embed.setAuthor("Nope! Missing Permissions", null, IconsGuild.LIGHTNING_ERROR_ICON_URL);
        embed.setTitle(String.format("You tried to use %s with insufficient permissions.", commandName));
        embed.setDescription("If you believe this is an error contact mods or write a ticket /ticket");

        return embed;
    }

    public static EmbedBuilder createInvalidSyntaxEmbed(Member interactingMember, String commandName) {
        EmbedBuilder embed = createBaseErrorEmbed(interactingMember, commandName);

        embed.setAuthor("Hmh? Syntax Error", null, IconsGuild.LIGHTNING_ERROR_ICON_URL);
        embed.setDescription("It seems there was an **issue** with your **input**. Please **double-check the syntax** and try again. If the problem persists, feel free to open a support ticket using **/ticket**.");

        return embed;
    }

    public static EmbedBuilder createBaseEmbed(Member interactingMember, String iconUrl, String commandName, String title, String content) {
        EmbedBuilder embed = new EmbedBuilder();

        String requester = "failed";

        if (interactingMember != null) {
            requester = interactingMember.getEffectiveName();
        }

        embed.setAuthor(String.format("Using - command: %s", commandName), null, iconUrl);
        embed.setTitle(title);
        embed.setDescription(content);
        embed.setFooter(String.format("Requested by: %s", requester));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getDefaultColor());

        return embed;
    }

    public static EmbedBuilder createErrorMessage(Member interactingMember, String commandName, String errorMessage) {
        EmbedBuilder embed = createBaseErrorEmbed(interactingMember, commandName);
        embed.setDescription(errorMessage);
        return embed;
    }

    public static EmbedBuilder createBaseErrorEmbed(Member interactingMember, String commandName) {
        EmbedBuilder embed = new EmbedBuilder();

        String requester = "failed";

        if (interactingMember != null) {
            requester = interactingMember.getEffectiveName();
        }

        embed.setAuthor("Oops! an error occurred!", null, IconsGuild.LIGHTNING_ERROR_ICON_URL);
        embed.setTitle(String.format("Error while handling: %s", commandName));
        embed.setDescription(String.format("Check the with the **/?%s command** for the **correct usage** of this command, if this **error persists** you can write a ticket with **/ticket** and we'll have a look at it.", commandName));
        embed.setFooter(String.format("Requested by: %s", requester));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getErrorColor());

        return embed;
    }

    public static EmbedBuilder createSwearWordEmbed(String iconUrl) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Swearing is not allowed on this server!", null, iconUrl);
        embed.setTitle("Watch Your Words, Please!");
        embed.setDescription("Your message contained inappropriate language. Please edit your communication to align with our community guidelines.");
        embed.setFooter(String.format("Requested by: %s", Bot.getInstance().getJda().getSelfUser().getName()));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getDefaultColor());

        return embed;
    }

    public static EmbedBuilder createSearchResultEmbed(SlashCommandInteractionEvent event, List<Track> tracks, String searchQuery) {
        EmbedBuilder embed = new EmbedBuilder();
        Member interactingMember = event.getMember();

        String requester = "failed";

        if (interactingMember != null) {
            requester = interactingMember.getEffectiveName();
        }

        embed.setAuthor("Using - command: Search", null, IconsGuild.LIGHTNING_ICON_URL);
        embed.setTitle(String.format("Search results for: %s", searchQuery));
        embed.setDescription("placeholder");
        for (int i = 0; i < Math.min(10, tracks.size()); i++) {
            Track track = tracks.get(i);
            embed.addField(
                    String.format("%02d) %s", i + 1, track.getInfo().getTitle()),
                    String.format("Duration: %s", Helper.formatSecondsHHMMSS(
                            track.getInfo().getLength() / 1000)
                    ),
                    false);
        }
        embed.setFooter(String.format("Requested by: %s", requester));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getDefaultColor());

        return embed;
    }

    public static EmbedBuilder createPlayingEmbed(SlashCommandInteractionEvent event, Track track, AudioGuildManager audio, boolean addedToQueue) {
        TrackInfo info = track.getInfo();
        String title = info.getTitle();
        String thumbnailUrl = info.getArtworkUrl();
        String link = info.getUri();
        String artist = info.getAuthor();
        long lengthMs = info.getLength();

        String etaPlaying = "00:00";
        String positionInQueue = "";
        int pos = 1;

        if (track.equals(audio.getTrackScheduler().getCurrent())) {
            etaPlaying = "now";
        } else {
            long etaPlayingLong = 0;
            pos = 1;
            for (Track trackInQueue : audio.getTrackScheduler().getTrackQueue()) {
                if (track.equals(trackInQueue)) break;
                pos++;
                etaPlayingLong += trackInQueue.getInfo().getLength();
            }
            etaPlaying = Helper.formatMillisMMSS(etaPlayingLong);
        }

        positionInQueue = Integer.toString(pos);

        String formattedDuration = Helper.formatMillisMMSS(lengthMs);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.CYAN);
        embed.setTitle(title + " by: " + artist, link);  // Titel als klickbaren Link setzen
        embed.setThumbnail(thumbnailUrl);
        embed.setAuthor(addedToQueue ? "Added to Queue" : "Now Playing", null, IconsGuild.YOUTUBE_ICON); // Icon für "Now Playing"
        embed.addField("Estimated time until played", etaPlaying, true);
        embed.addField("Track Length", formattedDuration, true);
        embed.addField("Position in queue", positionInQueue, true);
        embed.setFooter("Requested by " + event.getUser().getName(), event.getUser().getEffectiveAvatarUrl());

        return embed;
    }

    public static EmbedBuilder createSongQueuing(SlashCommandInteractionEvent event, PlaylistLoaded playList, AudioGuildManager audio, boolean startedPlaying) {
        Track firstTrack = playList.getTracks().get(startedPlaying ? 1 : 0);
        Track lastTrack = playList.getTracks().getLast();
        String playListThumbnail = firstTrack.getInfo().getUri();

        List<Track> tracks = playList.getTracks();

        long duration = 0;
        long etaPlayingLong = 0;
        int pos = 1;
        boolean playListStartFound = false;

        for (Track trackInQueue: audio.getTrackScheduler().getTrackQueue()) {
            if (trackInQueue.equals(firstTrack)) {
                playListStartFound = true;
            }

            if (trackInQueue.equals(lastTrack)) {
                break;
            }

            if (!playListStartFound) {
                pos += 1;
                etaPlayingLong += trackInQueue.getInfo().getLength();
            }

            if (playListStartFound) {
                duration += trackInQueue.getInfo().getLength();
            }
        }

        String etaPlaying = Helper.formatMillisMMSS(etaPlayingLong);
        String formattedDuration = Helper.formatMillisMMSS(duration);
        String positionInQueue = Integer.toString(pos);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.CYAN);
        embed.setTitle("A Playlist was added to the Queue");
        embed.setThumbnail(playListThumbnail);
        embed.setAuthor(String.format("Added %s more Song%s to the Queue",
                tracks.size(),
                Helper.sFormatting(tracks.size())),
                null,
                IconsGuild.YOUTUBE_ICON);
        embed.addField("Estimated time until played", etaPlaying, true);
        embed.addField("Playlist Length", formattedDuration, true);
        embed.addField("Position in queue", positionInQueue, true);
        embed.setFooter("Requested by " + event.getUser().getName(), event.getUser().getEffectiveAvatarUrl());

        return embed;
    }
}
