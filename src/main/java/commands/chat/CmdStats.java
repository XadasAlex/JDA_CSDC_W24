package commands.chat;

import commands.ICommand;
import listeners.StatListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import stats.MemberStats;
import utils.Helper;
import utils.MessageSender;

import java.util.List;
import java.util.Objects;

public class CmdStats implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(getName())) return;

        OptionMapping taggedMember = event.getOption("member");
        Member member;
        if (taggedMember == null) {
            member = event.getMember();
        } else {
            member = taggedMember.getAsMember();
        }

        if (!MemberStats.allowedStats(member.getGuild().getId(), member.getId())) {
            event.reply("You first need to allow me to track your user-stats, use: /allow-stats true").queue();
            return;
        }

        EmbedBuilder statsEmbed = createStatsEmbed(event, member);

        if (statsEmbed != null) {
            event.replyEmbeds(statsEmbed.build()).queue();

        } else {
            EmbedBuilder errorEmbed = MessageSender.createErrorEmbed("An Error occurred while handling cmd: stats ", String.format("Error fetching stats for member: %s", member.getEffectiveName()), event.getMember());
            event.replyEmbeds(errorEmbed.build()).queue();
        }

    }

    private EmbedBuilder createStatsEmbed(SlashCommandInteractionEvent event, Member member) {
        if (member == null) return null;

        String memberId = member.getId();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        MemberStats stats = MemberStats.getMemberStats(guildId, memberId);

        if (stats == null) return null;

        EmbedBuilder statsEmbed = MessageSender
                .createBaseEmbed(event.getMember(),
                        String.format("Stats for user: %s", member.getEffectiveName()),
                        "description"
                );

        int exp = stats.getExp();

        statsEmbed.addField("Experience collected:", String.format("%dexp", exp), false);

        int selfReactions = stats.getSelfReactionCount();

        statsEmbed.addField("Reactions to other Member's Messages",
                String.format("Reacted %d time%s",
                        selfReactions,
                        Helper.sFormatting(selfReactions)
                ),
                false);

        int otherReactions = stats.getOtherReactionCount();

        statsEmbed.addField("Reactions from other Members",
                String.format("Members reacted %d time%s to your Messages.",
                        selfReactions,
                        Helper.sFormatting(selfReactions)
                ),
                false);


        long totalTime = stats.getTotalTime();
        String totalTimeFormatted = Helper.formatSeconds(totalTime);

        statsEmbed.addField("Time spent in Voice Channels",
                String.format("You spent %s in channels across this server.", totalTimeFormatted),
                false);

        String lastTimeJoinedString = Helper.getTimeAgo(stats.getLastTimeJoined());

        statsEmbed.addField("Last time joined a voice-channel", lastTimeJoinedString, false);


        int messagesSent = stats.getMessagesSent();
        long charactersSent = stats.getCharactersSent();
        int averageCharacters;
        if (messagesSent < 1) {
            averageCharacters = 0;

        } else {
            averageCharacters = (int) (charactersSent / messagesSent);
        }

        statsEmbed.addField(
                String.format("You've sent %s message%s on this server",
                        messagesSent,
                        Helper.sFormatting(messagesSent)
                ),
                String.format("with an average of %d characters",
                        averageCharacters),
                false
        );
        int botInteractions = stats.getBotInteractions();

        statsEmbed.addField(
                "Bot's enslaved counter",
                String.format("You've used bot commands for a total of %d time%s",
                        botInteractions,
                        Helper.sFormatting(botInteractions)
                ),
                false);


        return statsEmbed;
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "lets you display your server stats";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.MENTIONABLE, "member", "the mentioned members stats will be displayed")
        );
    }
}
