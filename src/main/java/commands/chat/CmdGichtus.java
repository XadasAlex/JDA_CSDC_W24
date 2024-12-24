package commands.chat;

import commands.ICommand;
import utils.Helper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class CmdGichtus implements ICommand {
    @Override
    public String getName() {
        return "gichtus";
    }

    public static String getDisplayName() {
        return "Gichtus Commandus";
    }

    @Override
    public String getDescription() {
        return "gichtus satzus";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String pythonScriptPath = "src/main/resources/python/Harrand.py";

        String generatedMessage = Helper.pythonRunner(pythonScriptPath);
        event.reply(generatedMessage).queue();
    }
}
