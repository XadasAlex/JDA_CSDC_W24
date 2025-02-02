package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class CmdLeave implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            event.getGuild().getAudioManager().closeAudioConnection();
            event.reply("🚪 Left the voice channel.").queue();
        } else {
            event.reply("❌ I'm not connected to a voice channel.").queue();
        }
    }

    @Override
    public String getName() { return "leave"; }
    @Override
    public String getDescription() { return "Bot leaves the voice channel"; }
    @Override
    public List<OptionData> getOptions() { return List.of(); }
}
