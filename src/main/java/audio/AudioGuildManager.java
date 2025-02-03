package audio;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.player.*;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import utils.Embedder;

import java.util.List;

public class AudioGuildManager {
    private long guildId;
    private LavalinkClient client;
    private TrackScheduler trackScheduler;

    public AudioGuildManager(long guildId, LavalinkClient client) {
        this.guildId = guildId;
        this.client = client;
        this.trackScheduler = new TrackScheduler(guildId, client);

        client.on(TrackEndEvent.class).subscribe(trackEndEvent -> {
            if (trackEndEvent.getEndReason().getMayStartNext()) {
                Track nextTrack = trackScheduler.playNextTrack();
                if (nextTrack == null) {
                    System.out.println("Warteschlange leer – Musik gestoppt.");
                }
            }
        });
    }

    public Link getLink() {
        return client.getOrCreateLink(guildId);
    }

    public long getGuildId() {
        return guildId;
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public void play(String query, SlashCommandInteractionEvent event, VoiceChannel channel) {
        getLink().loadItem(query).subscribe(loadResult -> {
            if (loadResult instanceof TrackLoaded trackLoaded) {

                Track track = trackLoaded.getTrack();
                playOrQueueTrack(track, event);


            } else if (loadResult instanceof PlaylistLoaded playlistLoaded) {
                List<Track> tracks = playlistLoaded.getTracks();
                if (tracks != null && tracks.size() > 1) {
                    playOrQueuePlaylist(tracks, event);
                    return;
                } else if (tracks.size() == 1) {
                    playOrQueueTrack(tracks.getFirst(), event);
                    return;
                }

                EmbedBuilder embed = Embedder.createErrorMessage(event.getMember(), "Play", String.format("Error with the provided playlist", query));
                event.getHook().sendMessageEmbeds(embed.build()).queue();

            } else if (loadResult instanceof NoMatches) {

                EmbedBuilder embed = Embedder.createErrorMessage(event.getMember(), "Play", String.format("No matches for: %s", query));
                event.getHook().sendMessageEmbeds(embed.build()).queue();

            } else if (loadResult instanceof LoadFailed) {

                EmbedBuilder embed = Embedder.createErrorMessage(event.getMember(), "Play", "Loading your Track failed.");
                event.getHook().sendMessageEmbeds(embed.build()).queue();

            } else if (loadResult instanceof SearchResult searchResult) {

                Track track = searchResult.getTracks().getFirst();

                playOrQueueTrack(track, event);
            }
        });
    }

    public void playOrQueueTrack(Track track, SlashCommandInteractionEvent event) {
        getLink().getPlayer().subscribe(player -> {
            if (player.getTrack() == null) {
                player.setTrack(track).subscribe();
                event.replyEmbeds(Embedder.createPlayingEmbed(event, track).build()).queue();
            } else {
                trackScheduler.addTrackToQueue(track);
                event.replyEmbeds(Embedder.createAddedToQueueEmbed(event, track).build()).queue();
            }
        });
    }

    public void playOrQueuePlaylist(List<Track> tracks, SlashCommandInteractionEvent event) {
        getLink().getPlayer().subscribe(player -> {
            if (!tracks.isEmpty()) {
                Track firstTrack = tracks.getFirst();
                player.setTrack(firstTrack).subscribe();
                trackScheduler.addTracksToQueue(tracks.subList(1, tracks.size()));
                event.replyEmbeds(Embedder.createPlayingEmbed(event, firstTrack).build()).queue();
            } else {
                trackScheduler.addTracksToQueue(tracks);
            }
        });
    }
}
