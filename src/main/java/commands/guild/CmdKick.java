package commands.guild;

import commands.ICommand;
import launcher.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.exceptions.PermissionException;
import utils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class CmdKick implements ICommand {
    // todo: genauer anschauen funktioniert noch nicht
    @Override
    public String getName() {
        return "kick";
    }
    
    public static String getDisplayName() {
        return "Disconnect User From Voice Channel";
    }

    @Override
    public String getDescription() {
        return "kicks a member from their current voice channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "member", "the member you want to be kicked out.", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping memberOption = event.getOption("member");

        if (memberOption == null) {
            EmbedBuilder embed = Embedder.createInvalidSyntaxEmbed(event.getMember(), "kick");
            event.replyEmbeds(embed.build()).queue(Helper::deleteAfter60);
            return;
        }

        Member memberToKick = memberOption.getAsMember();
        Guild guild = event.getGuild();

        if (memberToKick != null && guild != null) {
            GuildVoiceState voice = memberToKick.getVoiceState();

            if (voice != null && voice.inAudioChannel()) {
                try {
                    guild.moveVoiceMember(memberToKick, null).queue();
                } catch (PermissionException e) {
                    // Handle permission issue, e.g., bot doesn't have the required permissions
                    System.out.println("Bot lacks the necessary permissions to move the member: " + e.getMessage());
                    event.reply("I don't have the required permissions to move this member.").queue();
                } catch (IllegalArgumentException e) {
                    // Handle invalid arguments, e.g., member not in the guild or invalid parameters
                    System.out.println("Invalid arguments provided: " + e.getMessage());
                    event.reply("The provided member is not valid or in the guild.").queue();
                } catch (IllegalStateException e) {
                    // Handle illegal state (e.g., bot not in the same guild)
                    System.out.println("Illegal state: " + e.getMessage());
                    event.reply("I cannot move the member due to an internal issue.").queue();
                } catch (Exception e) {
                    // Generic exception handling for any unexpected errors
                    System.out.println("An error occurred while moving the member: " + e.getMessage());
                    event.reply("An error occurred while moving the member. Please try again.").queue();
                }
            }

        } else {
            EmbedBuilder embed = Embedder.createInvalidSyntaxEmbed(event.getMember(), getName());
            event.replyEmbeds(embed.build()).queue();
        }

    }
}
