package utils;

import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;

public class Embedder {
    public static EmbedBuilder createMissingPermissionEmbed(Member interactingMember, String commandName) {
        EmbedBuilder embed = createBaseErrorEmbed(interactingMember, commandName);
        embed.setAuthor("Nope! Missing Permissions", null, CommandIcons.LIGHTNING_ERROR_ICON_URL);
        embed.setTitle(String.format("You tried to use %s with insufficient permissions.", commandName));
        embed.setDescription("If you believe this is an error contact mods or write a ticket /ticket");

        return embed;
    }

    public static EmbedBuilder createInvalidSyntaxEmbed(Member interactingMember, String commandName) {
        EmbedBuilder embed = createBaseErrorEmbed(interactingMember, commandName);

        embed.setAuthor("Hmh? Syntax Error", null, CommandIcons.LIGHTNING_ERROR_ICON_URL);
        embed.setDescription("It seems there was an **issue** with your **input**. Please **double-check the syntax** and try again. If the problem persists, feel free to open a support ticket using **/ticket**.");

        return embed;
    }

    public static EmbedBuilder createBaseEmbed(Member interactingMember, String iconUrl, String commandName, String title, String content) {
        EmbedBuilder embed = new EmbedBuilder();

        String requester = "failed";

        if (interactingMember != null) {
            requester = interactingMember.getEffectiveName();
        }

        embed.setAuthor(String.format("Using - command: %s", commandName), null, iconUrl);
        embed.setTitle(title);
        embed.setDescription(content);
        embed.setFooter(String.format("Requested by: %s", requester));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getDefaultColor());

        return embed;
    }

    public static EmbedBuilder createErrorMessage(Member interactingMember, String commandName, String errorMessage) {
        EmbedBuilder embed = createBaseErrorEmbed(interactingMember, commandName);
        embed.setDescription(errorMessage);
        return embed;
    }

    public static EmbedBuilder createBaseErrorEmbed(Member interactingMember, String commandName) {
        EmbedBuilder embed = new EmbedBuilder();

        String requester = "failed";

        if (interactingMember != null) {
            requester = interactingMember.getEffectiveName();
        }

        embed.setAuthor("Oops! an error occurred!", null, CommandIcons.LIGHTNING_ERROR_ICON_URL);
        embed.setTitle(String.format("Error while handling: %s", commandName));
        embed.setDescription(String.format("Check the with the **/?%s command** for the **correct usage** of this command, if this **error persists** you can write a ticket with **/ticket** and we'll have a look at it.", commandName));
        embed.setFooter(String.format("Requested by: %s", requester));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getErrorColor());

        return embed;
    }

    public static EmbedBuilder createSwearWordEmbed(String iconUrl) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Swearing is not allowed on this server!", null, iconUrl);
        embed.setTitle("Watch Your Words, Please!");
        embed.setDescription("Your message contained inappropriate language. Please edit your communication to align with our community guidelines.");
        embed.setFooter(String.format("Requested by: %s", Bot.getInstance().getJda().getSelfUser().getName()));
        embed.setTimestamp(Instant.now());
        embed.setColor(Bot.getInstance().getDefaultColor());

        return embed;
    }
}
