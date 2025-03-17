package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;
import utils.Helper;

public class CmdJoin implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Helper.handleVoiceChannelJoin(event);

    }

    @Override
    public String getName() { return "join"; }
    @Override
    public String getDescription() { return "Bot joins your voice channel"; }
    @Override
    public List<OptionData> getOptions() { return List.of(); }
}
