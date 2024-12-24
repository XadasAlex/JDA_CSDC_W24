package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import stats.MemberStats;
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

        if (guild == null || member == null) return;

        boolean inVoice = false;
        boolean allow = allowOption.getAsBoolean();

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState != null) inVoice = voiceState.inAudioChannel();

        String guildId = guild.getId();
        String memberId = member.getId();

        String basePath = Helper.getBaseStatPath(guildId);
        String memberPath = Helper.getMemberStatPath(guildId, memberId);

        File statPath = new File(basePath);
        File memberStatFile = new File(memberPath);

        if (allow) {
            if (!statPath.exists() && !statPath.mkdirs()) {
                event.reply("Error creating stat folder for the guild").queue();
                return;
            }

            if (memberStatFile.exists()) {
                event.reply("You are already added to the tracked users for member-stats").queue();
                return;
            }

            try {
                if (memberStatFile.createNewFile()) {
                    new MemberStats(guildId, memberId, inVoice).updateSelf();
                    event.reply("Added you to the tracked users for member-stats").queue();
                } else {
                    event.reply("Error adding you to the tracked users for member-stats").queue();
                }
            } catch (IOException e) {
                event.reply("An error occurred while processing your request").queue();
                e.printStackTrace();
            }

        } else {
            if (!memberStatFile.exists()) {
                event.reply("You were never added to stat tracking").queue();
                return;
            }

            if (memberStatFile.delete()) {
                event.reply("Removed you from the tracked users for member-stats").queue();
            } else {
                event.reply("Failed to remove you from the tracked users for member-stats").queue();
            }
        }

    }


}
