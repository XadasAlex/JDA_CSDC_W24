package handlers;

import bot.utils.ContentSender;
import bot.utils.ChannelActions;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandHandler {
    public static void forwardCommand(MessageReceivedEvent event, List<String> commandList) {
        switch (commandList.getFirst()) {
            case "ping":
                ContentSender.pingPong(event, commandList);
                break;
            case "join":
                Channel c = ChannelActions.getMembersChannel(event.getMember());
                ChannelActions.joinChannel(event.getGuild().getAudioManager(), (AudioChannel) c);
                break;
            case "leave":
                ChannelActions.leaveChannel(event.getGuild().getAudioManager());
                break;
            case "vote":
                ContentSender.vote(event, commandList);

        }
    }
}
