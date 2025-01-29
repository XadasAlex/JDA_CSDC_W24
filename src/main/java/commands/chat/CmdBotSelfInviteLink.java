package commands.chat;

import commands.ICommand;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.IconsGuild;
import utils.Embedder;
import utils.Helper;

import java.util.List;

public class CmdBotSelfInviteLink implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String inviteLink = Bot.getInstance().getJda().getInviteUrl(Permission.ADMINISTRATOR);

        EmbedBuilder embed = Embedder.createBaseEmbed(
                event.getMember(),
                IconsGuild.SETTINGS_ICON_URL,
                getName(),
                "Created an invite link for you to use me in another of your servers",
                inviteLink);

        event.replyEmbeds(embed.build()).queue(Helper::deleteAfter300);
    }

    @Override
    public String getName() {
        return "bot-invite-link";
    }

    @Override
    public String getDescription() {
        return "creates a invite link for this bot that you can use for your own server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
