package commands.testing;

import commands.ICommandAsync;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AsyncTest implements ICommandAsync {
    public CompletableFuture<Void> asyncExecute(SlashCommandInteractionEvent event) {
        return null;
    }

    @Override
    public CompletableFuture<Void> executeWithPermission(SlashCommandInteractionEvent event) {
        return ICommandAsync.super.executeWithPermission(event);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Set.of(Permission.MESSAGE_SEND);
    }

    @Override
    public CompletableFuture<Void> executeAsync(SlashCommandInteractionEvent event) {
        return null;
    }
}
