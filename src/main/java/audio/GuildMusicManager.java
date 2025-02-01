package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class GuildMusicManager {
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayer player) {
        this.player = player;
        this.scheduler = new TrackScheduler(player);
        this.sendHandler = new AudioPlayerSendHandler(player);
        // Füge den Scheduler als Listener dem Player hinzu, damit er Track-End-Events empfängt
        player.addListener(scheduler);
    }

    public AudioSendHandler getSendHandler() {
        return sendHandler;
    }
}
