package audio;
/*

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import launcher.Bot;

public class MusicHandler {
    private final AudioPlayerManager playerManager;
    private final Bot bot;

    public MusicHandler(AudioPlayerManager playerManager, Bot bot) {
        this.playerManager = playerManager;
        this.bot = bot;
    }

    public void loadItem(String guildId, String identifier) {
        TrackScheduler scheduler = bot.getTrackScheduler(guildId);

        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                scheduler.queue(track);
                scheduler.startNextTrack();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    scheduler.queue(track);
                }
                scheduler.startNextTrack();
            }

            @Override
            public void noMatches() {
                System.out.println("No matches found for " + identifier);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("Could not load track: " + throwable.getMessage());
            }
        });
    }

    public void playCurrent() {

    }
}


 */
