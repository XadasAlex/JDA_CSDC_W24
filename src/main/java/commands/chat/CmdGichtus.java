package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import utils.Helper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Set;

public class CmdGichtus implements ICommand {
    @Override
    public String getName() {
        return "gichtus";
    }

    @Override
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return false;
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
    public Set<Permission> getRequiredPermissions() {
        return Set.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String pythonScriptPath = "src/main/resources/python/Harrand.py";  // Replace with your script path

        String generatedMessage = Helper.pythonRunner(pythonScriptPath);
        event.reply(generatedMessage).queue();
    }

    @Override
    public void executeWithPermission(SlashCommandInteractionEvent event) {
        ICommand.super.executeWithPermission(event);
    }
}
