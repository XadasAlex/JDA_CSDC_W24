package bot.utils;

import java.util.Arrays;
import java.util.List;

public class MessageAnalyzer {
    private final String PREFIX;

    public MessageAnalyzer(String prefix) {
        this.PREFIX = prefix;
    }

    public boolean isCommand(String content) {
        return content.startsWith(PREFIX);
    }

    public List<String> getContentList(String content) {
        String[] contentList = content.split(" ");
        contentList[0] = contentList[0].replace(PREFIX, "");
        return Arrays.stream(contentList).toList();
    }
}
