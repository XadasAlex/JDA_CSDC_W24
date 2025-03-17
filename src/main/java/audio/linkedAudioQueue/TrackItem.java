package audio.linkedAudioQueue;

import dev.arbjerg.lavalink.client.player.Track;

public class TrackItem {
    TrackItem prev;
    TrackItem next;
    Track track;
    public TrackItem(Track track, TrackItem prev, TrackItem next) {
        this.track = track;
        this.prev = prev;
        this.next = next;
    }


}
