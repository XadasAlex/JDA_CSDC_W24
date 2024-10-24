package bot.utils;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.List;

public class ContentSender {
    public static void pingPong(MessageReceivedEvent e, List<String> content) throws
            UnsupportedOperationException,
            IllegalArgumentException,
            InsufficientPermissionException
    {
        MessageChannel channel = e.getChannel();
        channel.sendMessage("Pong!").queue();
    }

    public static void vote(MessageReceivedEvent e, List<String> content) {
        System.out.println(content);
    }
}
