package commands.chat;

import commands.ICommand;
import utils.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Random;

public class CmdRollDice implements ICommand {
    @Override
    public String getName() {
        return "dice";
    }

    @Override
    public String getDescription() {
        return "Rolls a dice";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "lower-bound", "defines the lowest number rollable", false),
                new OptionData(OptionType.INTEGER, "upper-bound", "defines the highest number rollable", false)
        );
    }

    private static int random(int lower, int upper) {
        Random random = new Random();
        if (upper < lower) {
            int temp = upper;
            upper = lower;
            lower = temp;
        }
        return random.nextInt((upper - lower) + 1) + lower;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping lower = event.getOption("lower-bound");
        OptionMapping upper = event.getOption("upper-bound");
        int lowerBound;
        int upperBound;
        int randInt;

        if (lower != null && upper != null) {
            lowerBound = lower.getAsInt();
            upperBound = upper.getAsInt();
            randInt = random(lowerBound, upperBound);

        } else if (lower == null && upper == null) {
            lowerBound = 1;
            upperBound = 6;
            randInt = random(lowerBound, upperBound);

        } else {
            lowerBound = lower == null ? 0 : lower.getAsInt();
            upperBound = upper == null ? 100 : upper.getAsInt();
            randInt = random(lowerBound, upperBound);

        }

        EmbedBuilder randomEmbed = MessageSender.createEmbedBlueprint(event,
                "Random number has been generated",
                "Roll Dice",
                String.format("Rolled a number in the bounds: [%d; %d]", lowerBound, upperBound));

        randomEmbed.addField("Here's the generated Number: ", "*" + randInt + "*", true);

        event.replyEmbeds(randomEmbed.build()).queue();

    }
}
