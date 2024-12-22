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

    Set<Permission> getRequiredPermissions();

    default boolean hasPermission(SlashCommandInteractionEvent event) {
        Set<Permission> requiredPermissions = getRequiredPermissions();
        Set<Permission> userPermissions = new HashSet<>(Objects.requireNonNull(event.getMember()).getPermissions());

        return userPermissions.containsAll(requiredPermissions);
    }
}
