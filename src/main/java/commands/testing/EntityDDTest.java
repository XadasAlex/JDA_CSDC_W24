package commands.testing;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;

import java.util.List;
import java.util.Set;

public class EntityDDTest implements ICommand {

    @Override
    public String getName() {
        return "edrop";
    }

    @Override
    public String getDescription() {
        return "entity drop down test";
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
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return ICommand.super.hasPermission(event);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals("edrop")) {
            event.reply("Choose the user to high-five")
                    .addActionRow(
                            EntitySelectMenu.create("edrop-test", EntitySelectMenu.SelectTarget.USER)
                                    .build())
                    .queue();
        }
    }

    @Override
    public void executeWithPermission(SlashCommandInteractionEvent event) {
        ICommand.super.executeWithPermission(event);
    }
}
