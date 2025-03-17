package audio;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.Track;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class TrackScheduler {
    private final LavalinkClient client;
    private final long guildId;
    private final Queue<Track> trackQueue;
    private Track previousTrack;
    private Track currentTrack;
    private boolean isLooped = false;
    private LinkedList<Track> test;

    public TrackScheduler(long guildId, LavalinkClient client) {
        this.guildId = guildId;
        this.client = client;
        this.trackQueue = new LinkedList<>();


    }

    public boolean isLooped() {
        return isLooped;
    }

    public void setLooped(boolean looped) {
        isLooped = looped;
    }

    public Queue<Track> getTrackQueue() {
        return trackQueue;
    }

    // Fügt einen Track zur Warteschlange hinzu
    public void addTrackToQueue(Track track) {
        trackQueue.offer(track);
    }

    // Fügt mehrere Tracks zur Warteschlange hinzu
    public void addTracksToQueue(Iterable<Track> tracks) {
        tracks.forEach(trackQueue::offer);
    }

    public void insertTrackInto(Track track, int index) {
        insertTracksInto(List.of(track), index);
    }

    public void insertTracksInto(List<Track> tracks, int index) {
        List<Track> copy = new ArrayList<>(trackQueue);

        if (index < 0 || index > copy.size()) {
            throw new IllegalArgumentException("Ungültiger Index: " + index);
        }

        List<Track> left = copy.subList(0, index);
        left.addAll(tracks);
        left.addAll(copy.subList(index, copy.size()));
        trackQueue.clear();
        left.forEach(trackQueue::offer);
    }

    public void jumpQueue(Track track) {
        jumpQueue(List.of(track));
    }

    public void jumpQueue(List<Track> tracks) {
        List<Track> right = new ArrayList<>(trackQueue);
        List<Track> left = new ArrayList<>(tracks);
        left.addAll(right);
        trackQueue.clear();
        left.forEach(trackQueue::offer);
    }

    public Track getCurrent() {
        return client.getOrCreateLink(guildId).getPlayer().block().getTrack();
    }

    public void setPreviousTrack(Track newPreviousTrack) {
        previousTrack = newPreviousTrack;
    }

    public Track getPreviousTrack() {
        return previousTrack;
    }

    // Gibt den nächsten Track zurück und entfernt ihn aus der Queue
    public Track getNext() {
        return trackQueue.poll();
    }

    public Track peekNext() {
        return trackQueue.peek();
    }

    // Startet den nächsten Track, falls verfügbar
    public Track playNextTrack() {
        Track nextTrack = isLooped ? currentTrack : getNext();
        if (nextTrack != null) {
            setPreviousTrack(getCurrent());
            client.getOrCreateLink(guildId).getPlayer().subscribe(player -> {
                player.setTrack(nextTrack).subscribe();
            });
            return nextTrack;
        }
        return null;
    }

    public void loopTrack() {
        Track track = getCurrent();
        if (track != null) {
            client.getOrCreateLink(guildId).getPlayer().subscribe(player -> {
                player.setTrack(track).subscribe();
            });
        }
    }

    // Überprüft, ob die Warteschlange leer ist
    public boolean isQueueEmpty() {
        return trackQueue.isEmpty();
    }

    public void clearQueue() {
        trackQueue.clear();
    }

    public void toggleLoop() {
        setLooped(!isLooped);
    }
    public void setCurrentTrack(Track track) {
        this.previousTrack = currentTrack;
        this.currentTrack = track;
    }
}
