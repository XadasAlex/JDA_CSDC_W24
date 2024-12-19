package bot.utils;

import launcher.Bot;
import listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MessageAnalyzer extends MessageListener {
    private static final String PREFIX = "!";


    public static boolean isCommand(String content) {
        return content.startsWith(PREFIX);
    }

    public static List<String> getContentList(String content) {
        String[] contentList = content.split(" ");
        contentList[0] = contentList[0].replace(PREFIX, "");
        return Arrays.stream(contentList).toList();
    }

    public static Member getTaggedMember(Message message) {
        long memberId = extractTaggedId(message.getContentDisplay());

        JDA jda = Bot.getInstance().getJda();

        if (jda != null) {

            Guild guild = jda.getGuildById(Objects.requireNonNull(message.getGuildId()));

            if (guild != null) {

                return guild.getMemberById(memberId);

            }
        }

        return null;

    }

    private static long extractTaggedId(String content) {
        return 313027938062958592L;
    }
}
