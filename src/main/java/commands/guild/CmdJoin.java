package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Set;

public class CmdJoin implements ICommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "joins your voice channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }



    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }


}
