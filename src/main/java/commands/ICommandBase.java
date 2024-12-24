package commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface ICommandBase {
    String getName();

    String getDescription();

    List<OptionData> getOptions();
}
