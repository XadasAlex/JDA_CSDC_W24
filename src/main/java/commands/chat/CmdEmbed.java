package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;
import utils.Embedder;

import java.util.List;

public class CmdEmbed implements ICommand {
    @Override
    public String getName() {
        return "embed";
    }

    public static String getDisplayName() {
        return "Embed";
    }


    @Override
    public String getDescription() {
        return "prints a basic embed - for testing purposes";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.replyEmbeds(Embedder.createBaseErrorEmbed(event.getMember(), "stats").build()).queue(
                msg -> {
                    Helper.deleteAfter(msg, 3);
                }
        );
    }
}
