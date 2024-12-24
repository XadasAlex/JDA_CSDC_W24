package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommandPermissions extends ICommandBasePermissions {
    default void executeWithPermission(SlashCommandInteractionEvent event) {
        if (!hasPermission(event)) {
            // TODO: error replies class for that usecase
            event.reply("You don't have permission to use this command!").setEphemeral(true).queue();
            return;
        }

        execute(event);
    }

    public void execute(SlashCommandInteractionEvent event);

}
