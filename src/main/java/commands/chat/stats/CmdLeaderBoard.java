package commands.chat.stats;

import commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import stats.MemberStats;
import utils.CommandIcons;
import utils.Embedder;

import java.util.Comparator;
import java.util.List;

public class CmdLeaderBoard implements ICommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder leaderBoardEmbed = createLeaderBoardEmbed(event);
        if (leaderBoardEmbed != null) {
            event.replyEmbeds(leaderBoardEmbed.build()).queue();
        }
    }

    private EmbedBuilder createLeaderBoardEmbed(SlashCommandInteractionEvent event) {
        List<MemberStats> memberStatsList = MemberStats.topMembers(event.getGuild().getId());

        if (memberStatsList != null) {
            memberStatsList.sort(Comparator.comparingInt(MemberStats::getExp));
            if (memberStatsList.size() > 10) {
                memberStatsList.subList(0, 10);
            }
            EmbedBuilder leaderBoardEmbed = Embedder.createBaseEmbed(event.getMember(), CommandIcons.COMMUNITY_ICON_URL, getName(),
                    String.format("Leaderboard for server: %s", event.getGuild().getName()),
                    "Listed below are the top 10 members of this guild ranked by exp.");
            for (MemberStats stats : memberStatsList) {
                Member member = event.getGuild().getMemberById(stats.getMemberId());
                if (member == null) continue;
                leaderBoardEmbed.addField(member.getEffectiveName(), Integer.toString(stats.getExp()), true);
            }

            return leaderBoardEmbed;


        } else {
            return null;
        }

    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "shows the leaderboard more with: ?leaderboard";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
