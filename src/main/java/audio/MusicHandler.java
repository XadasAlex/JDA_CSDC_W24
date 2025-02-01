package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicHandler {
    private static MusicHandler instance;

    private final AudioPlayerManager playerManager;
    // Speichert für jede Guild den entsprechenden GuildMusicManager
    private final Map<Long, GuildMusicManager> musicManagers;

    private MusicHandler() {
        this.playerManager = new DefaultAudioPlayerManager();
        // Registriere Audioquellen (z.B. YouTube, SoundCloud, etc.)
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.musicManagers = new HashMap<>();
    }

    public static synchronized MusicHandler getInstance() {
        if (instance == null) {
            instance = new MusicHandler();
        }
        return instance;
    }

    // Gibt den GuildMusicManager für die jeweilige Guild zurück (erstellt einen, falls noch nicht vorhanden)
    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager manager = musicManagers.get(guildId);
        if (manager == null) {
            manager = new GuildMusicManager(playerManager.createPlayer());
            musicManagers.put(guildId, manager);
        }
        return manager;
    }

    // Methode zum Laden und Abspielen eines Tracks – hier wird direkt die Guild übergeben.
    public void loadAndPlay(Guild guild, String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        playerManager.loadItemOrdered(musicManager, trackUrl, new com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler() {
            @Override
            public void trackLoaded(com.sedmelluq.discord.lavaplayer.track.AudioTrack track) {
                musicManager.scheduler.queue(track);
                // Optional: Hier könntest du eine Nachricht an einen TextChannel senden
            }

            @Override
            public void playlistLoaded(com.sedmelluq.discord.lavaplayer.track.AudioPlaylist playlist) {
                com.sedmelluq.discord.lavaplayer.track.AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                musicManager.scheduler.queue(firstTrack);
                // Optional: Nachricht an einen TextChannel senden
            }

            @Override
            public void noMatches() {
                // Optional: Log oder Nachricht an einen TextChannel
            }

            @Override
            public void loadFailed(com.sedmelluq.discord.lavaplayer.tools.FriendlyException exception) {
                // Optional: Log oder Nachricht an einen TextChannel
            }
        });
    }
}
