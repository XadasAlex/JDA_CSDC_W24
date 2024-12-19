package commands.chat;

import commands.ICommand;
import utils.Helper;
import utils.polls.Poll;
import utils.polls.PollManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static utils.MessageSender.createEmbedBlueprint;

public class CmdPoll implements ICommand {

    public static EmbedBuilder createPollEmbed(SlashCommandInteractionEvent event, String title, String description, List<String> pollOptions, List<Integer> results) {
        EmbedBuilder pollEmbed = createEmbedBlueprint(event, title, getDisplayName() ,description);
        return addOptionFields(pollEmbed, pollOptions, results);
    }

    public static EmbedBuilder createPollEmbed(Member member, String title, String description, List<String> pollOptions, List<Integer> results) {
        System.out.println(member.getEffectiveName());
        EmbedBuilder pollEmbed = createEmbedBlueprint(member, title, getDisplayName() ,description);
        return addOptionFields(pollEmbed, pollOptions, results);
    }

    private static EmbedBuilder addOptionFields(EmbedBuilder pollEmbed, List<String> pollOptions, List<Integer> results) {
        List<String> numberEmojis = Helper.getPollEmojis();

        for (int i = 0; i < pollOptions.size(); i++) {
            double percent = 0;
            if (results != null) {
                int total = results.stream().mapToInt(Integer::intValue).sum();
                if (total > 0) {
                    percent = (double) results.get(i) / total * 100;
                }
            }

            pollEmbed.addField(numberEmojis.get(i) + ": " + pollOptions.get(i), Helper.createProgressBar(percent, 20) + "  " + String.format("%.2f", percent) + "%", false);
        }

        return pollEmbed;
    }

    public static EmbedBuilder createPollEmbed(Message message, String title, String description, List<String> pollOptions, List<Integer> results) {
        EmbedBuilder pollEmbed = createEmbedBlueprint(message, title, getDisplayName(), description);
        return addOptionFields(pollEmbed, pollOptions, results);
    }

    @Override
    public String getName() {
        return "dpoll";
    }

    public static String getDisplayName() {
        return "This command is deprecated use /poll instead";
    }

    @Override
    public String getDescription() {
        return "Creates A Poll";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "title", "Title of the poll", true),
                new OptionData(OptionType.STRING, "description", "Description of the poll", true),
                new OptionData(OptionType.STRING, "options", "Poll options, at least two and delimit them with \",\"", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping options = event.getOption("options");
        OptionMapping titleOption = event.getOption("title");
        OptionMapping descriptionOption = event.getOption("description");

        if (options != null && titleOption != null && descriptionOption != null) {
            String stringPolls = options.getAsString();
            String title = titleOption.getAsString();
            String description = descriptionOption.getAsString();

            List<String> pollOptions = Arrays.stream(stringPolls.split(",")).map(String::trim).toList();

            if (pollOptions.size() < 2 || pollOptions.size() > 8) return;

            EmbedBuilder pollEmbed = createPollEmbed(event, title, description, pollOptions, null);
            List<String> numberEmojis = Helper.getPollEmojis();

            CompletableFuture<InteractionHook> hook = new CompletableFuture<>();

            event.replyEmbeds(pollEmbed.build()).queue(hook::complete, hook::completeExceptionally);

            hook.thenAccept(pollHook -> {
                RestAction<Message> retrievedMessage = pollHook.retrieveOriginal();
                retrievedMessage.queue(msg -> {
                            for (int i = 0; i < pollOptions.size(); i++) {
                                Emoji reaction = Emoji.fromUnicode(numberEmojis.get(i));
                                msg.addReaction(reaction).queue();
                            }

                            Poll poll = new Poll(msg, event.getMember(), title, description, pollOptions);
                            PollManager.getInstance().addPoll(msg.getGuildId(), poll);
                        }
                );
            });
        }
    }
}
