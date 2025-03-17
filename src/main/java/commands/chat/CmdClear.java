package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;

import java.util.List;

public class CmdClear implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

        OptionMapping amountOption = event.getOption("amount");
        int amount = amountOption != null ? amountOption.getAsInt() : 10;

        event.getChannel().getHistory().retrievePast(amount).queue(messages -> {
            if (!messages.isEmpty()) {
                event.getChannel().asTextChannel().purgeMessages(messages);
                event.reply(String.format("Deleted the last %d messages! ✅", amount)).queue(Helper::deleteAfter5);
            } else {
                event.reply("No messages found to delete. ❌").queue(Helper::deleteAfter5);
            }
        }, failure -> {
            event.reply("Failed to delete messages: " + failure.getMessage()).queue(Helper::deleteAfter5);
        });
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clears messages in the requested channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "amount of messages to be deleted, defaults to 10"));
    }
}
