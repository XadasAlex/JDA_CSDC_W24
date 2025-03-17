package audio;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.player.*;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.w3c.dom.Text;
import reactor.core.publisher.Mono;
import utils.Embedder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioGuildManager {
    private long guildId;
    private LavalinkClient client;
    private TrackScheduler trackScheduler;
    /* private TextChannel mrc; //music request channel */

    public AudioGuildManager(long guildId, LavalinkClient client /*TextChannel dedicatedMusicRequestChannel*/) {
        this.guildId = guildId;
        this.client = client;
        this.trackScheduler = new TrackScheduler(guildId, client);
        // todo : maybe?!
        /*this.mrc = dedicatedMusicRequestChannel;*/

        client.on(TrackStartEvent.class).subscribe(trackStartEvent -> {
            trackScheduler.setCurrentTrack(trackStartEvent.getTrack());
        });

        client.on(TrackEndEvent.class).subscribe(trackEndEvent -> {
            if (trackEndEvent.getEndReason().getMayStartNext()) {
                trackScheduler.playNextTrack();
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
                    playOrQueuePlaylist(playlistLoaded, tracks, event);
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
                event.replyEmbeds(Embedder.createPlayingEmbed(event, track, this, false).build()).queue();
            } else {
                trackScheduler.addTrackToQueue(track);
                event.replyEmbeds(Embedder.createPlayingEmbed(event, track, this, true).build()).queue();
            }
        });
    }

    public void playOrQueuePlaylist(PlaylistLoaded playList, List<Track> tracks, SlashCommandInteractionEvent event) {
        getLink().getPlayer().subscribe(player -> {
            // if currently playing
            if (player.getTrack() != null && !tracks.isEmpty()) {
                trackScheduler.addTracksToQueue(tracks);
                event.replyEmbeds(Embedder.createSongQueuing(event, playList, this, false).build()).queue();
            } else {
                // if there's nothing playing
                Track firstTrack = tracks.getFirst();
                player.setTrack(firstTrack).subscribe();
                event.replyEmbeds(Embedder.createPlayingEmbed(event, firstTrack, this, false).build()).queue();
                trackScheduler.addTracksToQueue(tracks.subList(1, tracks.size()));
                EmbedBuilder embed = Embedder.createSongQueuing(event, playList, this, true);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }
        });
    }
}
