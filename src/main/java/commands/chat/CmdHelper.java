package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;

public class CmdHelper implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(getName())) return;

        event.reply("Which command you want to know more about?")
                .addActionRow(
                        createStringSelectMenu()
                ).queue();
    }

    public StringSelectMenu createStringSelectMenu() {
        List<SelectOption> options = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            options.add(SelectOption.of(
                    String.format("label%d",i),
                    String.format("value%d", i)
            ));
        }
        return StringSelectMenu.create(getName())
                .addOptions(options)
                .build();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "helps you with the commands";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
