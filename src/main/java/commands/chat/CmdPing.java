package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Set;

public class CmdPing implements ICommand {
    @Override
    public String getName() {
        return "ping";
    }

    public static String getDisplayName() {
        return "Ping";
    }

    @Override
    public String getDescription() {
        return "returns PONG!";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("PONG!").queue();
    }

}
