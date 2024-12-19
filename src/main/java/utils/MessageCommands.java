package utils;

import api.ChatGPT;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MessageCommands {
    public static String pingDescription = "Responds with PONG to the ping command.";
    public static String gptDescription = "Processes your provided message with the Openai - ChatGPT API and returns the response";

    public static void ping(MessageReceivedEvent event) {
        event.getChannel().sendMessage("PONG!").queue();
    }

    public static void gptResponse(MessageReceivedEvent event, List<String> command) {
        String gptMessage = command.stream().skip(1).collect(Collectors.joining(" "));
        gptResponse(event, gptMessage);
    }

    public static void gptResponse(MessageReceivedEvent event, String gptMessage) {
        CompletableFuture<Message> future = new CompletableFuture<>();

        event.getChannel().sendMessage("One Moment fetching the ChatGPT Response...")
                .queue(future::complete, future::completeExceptionally);

        ChatGPT gpt = ChatGPT.getInstance();

        String response = gpt.getChatGPTResponse(gptMessage);
        future.thenAccept(message -> {
            message.editMessage(response).queue();
        });
    }

    public static void gptResponse(SlashCommandInteractionEvent event, String gptMessage) {
        ChatGPT gpt = ChatGPT.getInstance();

        String response = gpt.getChatGPTResponse(gptMessage);
        event.reply(String.format("ChatGPT: %s", response)).queue();
    }
}
