package commands.chat.stats;

import commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.MemberStats;
import utils.IconsGuild;
import utils.Embedder;
import utils.Helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CmdAllowStats implements ICommand {
    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.BOOLEAN, "allow", "providing true/false let's you access/deny member-stats", true)
        );
    }

    @Override
    public String getDescription() {
        return "You allow the bot to track the following: time-join, time-left, messages-sent.";
    }

    @Override
    public String getName() {
        return "allow-stats";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping allowOption = event.getOption("allow");

        if (allowOption == null) return;

        Guild guild = event.getGuild();
        Member member = event.getMember();

        assert guild != null;
        assert member != null;

        boolean inVoice = false;
        boolean allow = allowOption.getAsBoolean();

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState != null) inVoice = voiceState.inAudioChannel();

        String guildId = guild.getId();
        String memberId = member.getId();

        String basePath = Helper.getBaseStatPath(guildId);
        String memberPath = Helper.getMemberStatPath(guildId, memberId);

        File statPath = new File(basePath);
        File memberStatPath = new File(memberPath);

        String message;
        boolean error = false;

        try {
            if (allow) {
                message = handleAllow(statPath, memberStatPath, guild, member, inVoice);
            } else {
                message = handleDisallow(memberStatPath);
            }
        } catch (Exception e) {
            message = e.getMessage();
            error = true;
            e.printStackTrace();
        }


        EmbedBuilder embed;

        if (error) {
            embed = Embedder.createErrorMessage(event.getMember(), getName(), message);
        } else {
            embed = Embedder.createBaseEmbed(event.getMember(),
                    IconsGuild.STATS_ICON_URL,
                    getName(),
                    String.format("Member stat tracking for: %s", member.getEffectiveName()), message);
        }

        event.replyEmbeds(embed.build()).queue();

    }

    private String handleDisallow(File memberStatPath) throws IOException {
        if (!memberStatPath.exists()) {
            return "You were never added to stat tracking";
        }

        if (memberStatPath.delete()) {
            return "Removed you from the tracked users for member-stats";
        } else {
            throw new IOException("Failed to remove you from the tracked users for member-stats");
        }
    }

    private String handleAllow(File statPath, File memberStatPath, Guild guild, Member member, boolean inVoice) throws IOException {
        if (!statPath.exists() && !statPath.mkdirs()) {
            throw new IOException("Error creating stat folder for the guild");
        }

        if (memberStatPath.exists()) {
            return "You are already added to the tracked users for member-stats";
        }

        if (memberStatPath.createNewFile()) {
            new MemberStats(guild, member, inVoice).updateSelf();
            return "Added you to the tracked users for member-stats";
        } else {
            throw new IOException("Error adding you to the tracked users for member-stats");
        }

    }


}
