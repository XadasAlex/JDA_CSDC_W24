package listeners;

import utils.MessageSender;
import utils.GuildCommands;
import utils.MessageCommands;
import utils.VoiceCommands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageListener extends ListenerAdapter {
    private final static String PREFIX = "-";

    private static boolean isCommand(MessageReceivedEvent event) {
        return event.getMessage().getContentRaw().startsWith(PREFIX);
    }

    private static boolean isCommand(String content) {
        return content.startsWith(PREFIX);
    }

    public static void handleMessage(MessageReceivedEvent event) {

        if (!isCommand(event)) return;
        String message = event.getMessage().getContentRaw();
        message = message.replaceFirst(PREFIX, "");
        List<String> command = Arrays.asList(message.split("\s"));


        switch (command.getFirst()) {
            case "embed":
                MessageSender.sendEmbed(event);
            case "ping":
                MessageCommands.ping(event);
                break;
            case "kick":
                GuildCommands.kick(event);
                break;
            case "gpt":
                MessageCommands.gptResponse(event, command);
                break;
            case "join":
                VoiceCommands.joinMembersChannel(event);
                break;
            case "leave":
                VoiceCommands.leaveCurrentChannel(event);
                break;
            case "play":
                //VoiceCommands.play(event);
                break;
            case "help":
                MessageSender.sendHelpList(event);
                break;
            case "test":
                MessageSender.sendEmbedTest(event);
                break;
        }

        String combined2Command = command.stream().limit(2).collect(Collectors.joining(" "));
        System.out.println(combined2Command);

        switch (combined2Command) {
            case "create vc":
                break;
            case "create tc":
                break;
        }


    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getContentRaw());

        if (event.getAuthor().isBot()) return;

        if (event.isFromGuild()) {
            handleMessage(event);
        }
    }


}
