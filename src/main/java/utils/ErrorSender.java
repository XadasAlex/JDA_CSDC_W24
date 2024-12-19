package utils;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ErrorSender {
    public static void permissionError(MessageReceivedEvent event) {
        MessageSender.sendMessage(event, "Error: Insufficient Permissions to use this command.");
    }

    public static void notFoundError(MessageReceivedEvent event) {
        MessageSender.sendMessage(event, "Error: Not Found");
    }

    public static void notFoundError(MessageReceivedEvent event, String message) {
        MessageSender.sendMessage(event, "Error not found: " + message);
    }

    public static void notFoundError(MessageChannel channel, String message) {
        channel.sendMessage("Error not found: " + message).queue();
    }

    public static void wrongSyntax(MessageReceivedEvent event) {
        MessageSender.sendMessage(event, "Error: Wrong Syntax");
    }
}
