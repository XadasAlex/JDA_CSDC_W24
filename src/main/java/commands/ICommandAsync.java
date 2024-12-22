package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.CompletableFuture;

public interface ICommandAsync extends ICommandBase{

    public CompletableFuture<Void> executeAsync(SlashCommandInteractionEvent event);

    default CompletableFuture<Void> executeWithPermission(SlashCommandInteractionEvent event) {
        if (!hasPermission(event)) {
            // TODO: error replies class for that usecase
            event.reply("You don't have permission to use this command!").setEphemeral(true).queue();
            return CompletableFuture.completedFuture(null);
        }
        return executeAsync(event);
    }
}
