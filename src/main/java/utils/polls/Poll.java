package utils.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static commands.chat.CmdPoll.createPollEmbed;


public class Poll {
    private final String messageId;
    private final String guildId;
    private final String title;
    private final String description;
    private final Message message;
    private final Member member;
    private final List<String> options;
    private final List<Integer> results;



    public Poll(Message message, Member member, String title, String description, List<String> options) {
        this.member = member;
        this.message = message;
        this.messageId = message.getId();
        this.guildId = message.getGuildId();
        this.options = options;
        this.title = title;
        this.description = description;
        this.results = new ArrayList<>(Collections.nCopies(options.size(), 0)); // Initialize results with 0
    }

    public String getMessageId() {
        return messageId;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void updateResults(int optionIndex, int count) {
        if (results.get(optionIndex) == count) return;

        results.set(optionIndex, count);
        update();
    }

    public int getTotalVotes() {
        return results.stream().mapToInt(Integer::intValue).sum();
    }

    public void update() {
        MessageEmbed embed = message.getEmbeds().getFirst();

        String title = embed.getTitle();
        String description = embed.getDescription();

        List<String> options = new ArrayList<>();
        for (MessageEmbed.Field field : embed.getFields()) {
            String fieldValue = field.getName();
            options.add(fieldValue.substring(fieldValue.indexOf(':')+1).trim());
        }

        // Create a new embed with the updated poll results
        EmbedBuilder pollEmbed = createPollEmbed(member, title, description, options, results);

        // Edit the original message with the updated embed
        message.editMessageEmbeds(pollEmbed.build()).queue();
    }
}