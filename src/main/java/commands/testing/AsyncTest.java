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
    public CompletableFuture<Void> executeAsync(SlashCommandInteractionEvent event) {
        return null;
    }
}
