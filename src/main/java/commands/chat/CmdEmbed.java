package commands.chat;

import launcher.Bot;
import commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Set;

public class CmdEmbed implements ICommand {
    @Override
    public String getName() {
        return "embed";
    }

    public static String getDisplayName() {
        return "Embed";
    }


    @Override
    public String getDescription() {
        return "prints a basic embed - for testing purposes";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        Bot botInstance = Bot.getInstance();
        SelfUser botUser = botInstance.getJda().getSelfUser();
        Member author = event.getMember();

        embed.setTitle("test", botInstance.getAvatarUrl());
        embed.setAuthor(botUser.getEffectiveName(), botUser.getEffectiveAvatarUrl(), botUser.getEffectiveAvatarUrl());
        embed.setColor(botInstance.getDefaultColor());
        embed.setDescription("description test");
        embed.setFooter(String.format("Requested by: %s", author.getEffectiveName()), author.getEffectiveAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
    }
}
