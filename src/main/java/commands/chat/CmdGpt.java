package commands.chat;

import commands.ICommand;
import utils.MessageCommands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class CmdGpt implements ICommand {
    @Override
    public String getName() {
        return "gpt";
    }

    public static String getDisplayName() {
        return "ChatGPT Request";
    }

    @Override
    public String getDescription() {
        return "replies with a response from the chatgpt api";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "input", "your request to chatgpt", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping input = event.getOption("input");
        if (input != null) {
            String gptMessage = input.getAsString();
            // event.getChannel().sendMessage(String.format("%s asked GPT: %s", event.getMember().getEffectiveName(), gptMessage)).queue();
            MessageCommands.gptResponse(event, gptMessage);
        }
    }
}
