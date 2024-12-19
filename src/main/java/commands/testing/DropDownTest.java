package commands.testing;

import commands.ICommand;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.List;

public class DropDownTest implements ICommand {
    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public String getDescription() {
        return "drop down test";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals("drop")) {
            event.reply("Choose your favorite food")
                    .addActionRow(
                            StringSelectMenu.create("drop-test")
                                    .addOption("Pizza", "pizza", "Classic") // SelectOption with only the label, value, and description
                                    .addOptions(SelectOption.of("Hamburger", "hamburger") // another way to create a SelectOption
                                            .withDescription("Tasty") // this time with a description
                                            .withEmoji(Emoji.fromUnicode("\uD83C\uDF54")) // and an emoji
                                            .withDefault(true)) // while also being the default option
                                    .build())
                    .queue();
        }
    }
}
