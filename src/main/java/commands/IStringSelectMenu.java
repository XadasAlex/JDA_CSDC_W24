package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public interface IStringSelectMenu {
    // Methode zur Erstellung des StringSelectMenu
    StringSelectMenu createStringSelectMenu(SlashCommandInteractionEvent event);
    void onStringSelectionInteraction(StringSelectInteractionEvent event);
}