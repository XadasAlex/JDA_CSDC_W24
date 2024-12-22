package commands.testing;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import utils.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Set;

public class ButtonTest implements ICommand {
    @Override
    public String getName() {
        return "button";
    }

    @Override
    public String getDescription() {
        return "simple button test";
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
    public void execute(SlashCommandInteractionEvent event) {
        String description = "Diese Frage stellte mir Elon Musk, gegen Ende eines langen Abends in einem eldlen Fisch Restaurant im Silicon Valley.";
        EmbedBuilder buttonEmbed = MessageSender.createEmbedBlueprint(
                event,
                "Impfen ist gesund?",
                "Button",
                description);

        event.replyEmbeds(buttonEmbed.build())
                .addActionRow(
                        Button.primary("hello", "click me"),
                        Button.success("emoji", Emoji.fromFormatted("<:minn:245267426227388416>"))
                ).queue();


    }

    @Override
    public void executeWithPermission(SlashCommandInteractionEvent event) {
        ICommand.super.executeWithPermission(event);
    }

    @Override
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return false;
    }

}
