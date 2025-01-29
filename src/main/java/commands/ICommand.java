package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommand extends ICommandBase {
    void execute(SlashCommandInteractionEvent event);
}
