package utils;

import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class MessageSender {
    public static void sendMessage(MessageReceivedEvent event, String message) {
        event.getChannel().sendMessage(message).queue();
    }

    public static EmbedBuilder createBaseEmbed(MessageReceivedEvent event, String title, String description) {
        Member member = event.getMember();
        if (member == null) return null;
        return createBaseEmbed(member, title, description);
    }

    public static EmbedBuilder createErrorEmbed(String title, String description, Member author) {
        EmbedBuilder embed = new EmbedBuilder();
        Bot botInstance = Bot.getInstance();
        String avatarUrl = botInstance.getAvatarUrl();

        embed.setTitle(title, avatarUrl);
        embed.setAuthor(author.getEffectiveName(), null, author.getAvatarUrl());
        embed.setColor(botInstance.getErrorColor());
        embed.setDescription(description);
        embed.setFooter(String.format("Requested by: %s", title));
        return embed;
    }

    public static EmbedBuilder createBaseEmbed(Member author, String title, String description) {
        EmbedBuilder embed = new EmbedBuilder();
        Bot botInstance = Bot.getInstance();
        String avatarUrl = botInstance.getAvatarUrl();

        embed.setTitle(title, avatarUrl);
        embed.setAuthor(author.getEffectiveName(), null, author.getAvatarUrl());
        embed.setColor(botInstance.getDefaultColor());
        embed.setDescription(description);
        embed.setFooter(String.format("Requested by: %s", title));
        return embed;
    }

    public static EmbedBuilder createEmbedBlueprint(Message message, String title, String commandName, String description) {
        return createEmbedBlueprint(message.getMember(), title, commandName, description);
    }

    public static EmbedBuilder createEmbedBlueprint(Member member, String title, String commandName, String description) {
        EmbedBuilder embed = new EmbedBuilder();
        Bot botInstance = Bot.getInstance();
        SelfUser botUser = botInstance.getJda().getSelfUser();

        embed.setTitle(title, botInstance.getAvatarUrl());
        embed.setAuthor(botUser.getEffectiveName() + " " + commandName);
        embed.setColor(botInstance.getDefaultColor());
        embed.setDescription(description);
        embed.setFooter(String.format("Requested by: %s", member.getEffectiveName()), member.getEffectiveAvatarUrl());
        return embed;
    }

    public static EmbedBuilder createEmbedBlueprint(SlashCommandInteractionEvent event, String title, String commandName, String description) {
        return createEmbedBlueprint(event.getMember(), title, commandName, description);
    }

    public static void createAndSendEmbed(MessageReceivedEvent event, String command, String content) {
        EmbedBuilder embed = createBaseEmbed(event, command, content);
        if (embed == null) return;
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
    /*
    public static void sendPoll(MessageReceivedEvent event, String title, String description, List<String> pollOptions) {
        if (pollOptions.size() > 9) return;

        EmbedBuilder pollEmbed = createBaseEmbed(event, String.format("Poll: %s", title), description);

        // Emojis for the poll options
        String[] numberEmojis = {
                "\u0031\uFE0F\u20E3", "\u0032\uFE0F\u20E3", "\u0033\uFE0F\u20E3", "\u0034\uFE0F\u20E3",
                "\u0035\uFE0F\u20E3", "\u0036\uFE0F\u20E3", "\u0037\uFE0F\u20E3", "\u0038\uFE0F\u20E3",
                "\u0039\uFE0F\u20E3", "\uD83D\uDD1F"
        };

        // Determine the maximum length of the poll options
        int longestOptionLength = pollOptions.stream()
                .filter(option -> option instanceof String) // Ensure we are working with strings
                .mapToInt(String::length) // Get the length of each string
                .max() // Get the maximum length
                .orElse(0); // Default to 0 if no options exist

        longestOptionLength = Math.max(description.length(), longestOptionLength);
        // Calculate the amount of spacer based on the longest option
        int spacerAmount = (int) (20 + longestOptionLength / 70.0 * (70 - 20));
        String spacer = "=".repeat(spacerAmount);
        assert pollEmbed != null;
        // Add the poll options to the embed
        for (int i = 0; i < pollOptions.size(); i++) {
            String option = pollOptions.get(i);
            int optionLength = option.length();

            // Calculate how much to "center" the option in the field
            int remainingSpace = spacerAmount - optionLength + 2;
            String repeat = "  o    o  ".repeat((int) Math.ceil(remainingSpace / 8.0));

            // Create the field with the correct formatting

            pollEmbed.addField(
                    spacer,
                    String.format("%s%s%s%s",
                            repeat,
                            numberEmojis[i],
                            option,
                            optionLength % 2 == 0 ? repeat : repeat + "  o"),
                    false
            );
        }


        event.getChannel().sendMessageEmbeds(pollEmbed.build()).queue(message -> {
            for (int i = 0; i < pollOptions.size(); i++) {
                Emoji reaction = Emoji.fromUnicode(numberEmojis[i]);
                message.addReaction(reaction).queue();
            }
        });

    }

     */
}
