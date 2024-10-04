package listeners;

import bot.utils.ErrorHandler;
import bot.utils.MessageAnalyzer;
import handlers.CommandHandler;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class MessageListener extends ListenerAdapter {
    private final MessageAnalyzer MA;
    private final CommandHandler CH;
    private final ErrorHandler EH;

    public MessageListener(MessageAnalyzer ma, CommandHandler ch, ErrorHandler eh) {
        this.MA = ma;
        this.CH = ch;
        this.EH = eh;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE))
        {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        }
        else
        {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }

        String content = event.getMessage().getContentDisplay();

        if (MA.isCommand(content)) {
            List<String> commandList = MA.getContentList(content);
            CommandHandler.forwardCommand(event, commandList);
        }
    }
}
