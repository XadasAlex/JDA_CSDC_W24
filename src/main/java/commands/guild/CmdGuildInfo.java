package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.CommandIcons;
import utils.Embedder;

import java.util.List;

public class CmdGuildInfo implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        if (guild == null) {
            String errorMessage = "Couldn't access the server information's, " +
                    "contact the an administrator and let them check my permissions " +
                    "regarding this issue. If this persists write a ticket using: /ticket";
            EmbedBuilder embed = Embedder.createErrorMessage(event.getMember(), getName(), errorMessage);
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        int memberCount = guild.getMemberCount();
        int memberOnlineCount = (int) guild.getMembers().stream().filter(m -> m.getOnlineStatus() == OnlineStatus.ONLINE).count();
        int voiceChannelCount = guild.getVoiceChannels().size();
        int textChannelCount = guild.getTextChannels().size();
        String guildName = guild.getName();
        String iconUrl = guild.getIconUrl();

        EmbedBuilder embed = Embedder.createBaseEmbed(event.getMember(), CommandIcons.COMMUNITY_ICON_URL, getName(), "Server Info", "");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Online/Total: %d/%d", memberOnlineCount, memberCount));
        embed.setDescription(sb.toString());
        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "serverinfo";
    }

    @Override
    public String getDescription() {
        return "displays informations regarding this server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
