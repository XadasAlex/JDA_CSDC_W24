package utils;

public class ChatMessage {
    private final String timestamp;
    private final String content;
    private final String channelName;

    public ChatMessage(String timestamp, String content, String channelName) {
        this.timestamp = timestamp;
        this.content = content;
        this.channelName = channelName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getChannelName() {
        return channelName;
    }
}
