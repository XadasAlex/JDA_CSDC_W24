package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.CompletableFuture;

public interface ICommandAsync extends ICommandBase{

    CompletableFuture<Void> executeAsync(SlashCommandInteractionEvent event);
}
