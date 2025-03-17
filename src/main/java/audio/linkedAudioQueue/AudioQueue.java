package audio.linkedAudioQueue;

import dev.arbjerg.lavalink.client.player.Track;

public class AudioQueue {
    TrackItem head;
    TrackItem tail;

    public AudioQueue() {

    }

    public void remove(Track track) {

    }

    public void insert(Track track, int index) {
        TrackItem search = tail;

        for (int i = 0; i < index; i++) {
            search = search.next;
        }

        search.next = new TrackItem(track, search, search.next);
    }

    public void putFirst(Track track) {
        head.next = new TrackItem(track, head, null);
    }

    public void putLast(Track track) {
        tail.prev = new TrackItem(track, null, tail);
    }
}
