package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;

public class CmdServerSettings implements ICommand {
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Settings for this server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(getName())) return;

        ActionRow settingsActionRow = createSettingsActionRow();

    }

    private ActionRow createSettingsActionRow() {
/*        return StringSelectMenu.create(getName())
                .addOption(
                        "general", "General", "general Settings"
                ).addOptions(
                        SelectOption.of()
                )
  */ return null;
    }
}
