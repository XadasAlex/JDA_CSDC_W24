package audio;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;

import java.util.LinkedList;
import java.util.Queue;

public class TrackScheduler {
    private final LavalinkClient client;
    private final long guildId;
    private final Queue<Track> trackQueue; // Warteschlange als Queue für FIFO-Handling

    public TrackScheduler(long guildId, LavalinkClient client) {
        this.guildId = guildId;
        this.client = client;
        this.trackQueue = new LinkedList<>();
    }

    // Fügt einen Track zur Warteschlange hinzu
    public void addTrackToQueue(Track track) {
        trackQueue.offer(track);
    }

    // Fügt mehrere Tracks zur Warteschlange hinzu
    public void addTracksToQueue(Iterable<Track> tracks) {
        for (Track track : tracks) {
            trackQueue.offer(track);
        }
    }

    // Gibt den aktuell spielenden Track zurück
    public Track getCurrent() {
        return client.getOrCreateLink(guildId).getPlayer().block().getTrack();
    }

    // Gibt den nächsten Track zurück und entfernt ihn aus der Queue
    public Track getNext() {
        return trackQueue.poll();
    }

    // Startet den nächsten Track, falls verfügbar
    public Track playNextTrack() {
        Track nextTrack = getNext();
        if (nextTrack != null) {
            client.getOrCreateLink(guildId).getPlayer().subscribe(player -> {
                player.setTrack(nextTrack).subscribe();
            });
            return nextTrack;
        }
        return null;
    }

    // Überprüft, ob die Warteschlange leer ist
    public boolean isQueueEmpty() {
        return trackQueue.isEmpty();
    }
}
