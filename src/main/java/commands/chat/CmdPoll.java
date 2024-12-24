package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import utils.Helper;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CmdPoll implements ICommand {
    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public String getDescription() {
        return "creates a poll";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "title","Title of the poll", true),
                new OptionData(OptionType.STRING,"options", "Poll options, at least two and delimit them with \",\"", true),
                new OptionData(OptionType.BOOLEAN, "multi-answer", "allow members to vote for multiple answers", false),
                new OptionData(OptionType.STRING, "duration", "how long should the poll be active? format: 4d20h", false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping options = event.getOption("options");
        OptionMapping titleOption = event.getOption("title");
        OptionMapping multiAnswerOption = event.getOption("multi-answer");
        OptionMapping durationOption = event.getOption("duration");

        boolean hasDuration = durationOption != null;
        boolean multiAnswer = multiAnswerOption != null && multiAnswerOption.getAsBoolean();

        if (options != null && titleOption != null) {
            String stringPolls = options.getAsString();
            String title = titleOption.getAsString();

            List<String> pollOptions = Arrays.stream(stringPolls.split(",")).map(String::trim).toList();

            if (pollOptions.size() < 2 || pollOptions.size() > 8) return;

            List<String> numberEmojis = Helper.getPollEmojis();

            MessagePollBuilder poll = new MessagePollBuilder(title);
            poll.setMultiAnswer(multiAnswer);

            if (hasDuration) {
                String duration = durationOption.getAsString();
                long durationSeconds = Helper.getDurationInSecondsFromString(duration);
                Duration pollDuration = Duration.ofSeconds(durationSeconds);

                if (pollDuration.getSeconds() >= 3600 && pollDuration.getSeconds() < (7 * 24 * 3600)) {
                    System.out.println(pollDuration.getSeconds());
                    poll.setDuration(pollDuration);
                }
            }

            for (int i = 0; i < pollOptions.size(); i++) {
                Emoji number = Emoji.fromUnicode(numberEmojis.get(i));
                poll.addAnswer(pollOptions.get(i), number);
            }

            event.replyPoll(poll.build()).queue();
        }
    }
}
