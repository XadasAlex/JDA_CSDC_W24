package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class CmdJoin implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            event.reply("❌ You must be in a voice channel!").queue();
            return;
        }

        VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
        event.getGuild().getAudioManager().openAudioConnection(channel);
        event.reply("🎶 Joined voice channel: " + channel.getName()).queue();
    }

    @Override
    public String getName() { return "join"; }
    @Override
    public String getDescription() { return "Bot joins your voice channel"; }
    @Override
    public List<OptionData> getOptions() { return List.of(); }
}
