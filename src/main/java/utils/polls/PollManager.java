package utils.polls;

import net.dv8tion.jda.api.entities.Message;

import java.util.*;

public class PollManager {
    private static final PollManager INSTANCE = new PollManager();
    private static final Map<String, List<Poll>> guildPolls = new HashMap<>();
    private static final Map<String, Set<String>> multipleReactionsAllowedPolls = new HashMap<>();

    private PollManager() {}

    // Public method to access the single instance
    public static PollManager getInstance() {
        return INSTANCE;
    }

    public void addPoll(String guildId, Poll poll) {
        guildPolls.computeIfAbsent(guildId, k -> new ArrayList<>()).add(poll);
    }

    public Poll getPoll(Message message) {
        List<Poll> polls = guildPolls.getOrDefault(message.getGuildId(), List.of());
        return polls.stream().filter(p -> p.getMessageId().equals(message.getId())).findFirst().orElse(null);
    }

    public Poll getPoll(String guildId, String messageId) {
        List<Poll> polls = guildPolls.getOrDefault(guildId, List.of());
        return polls.stream().filter(p -> p.getMessageId().equals(messageId)).findFirst().orElse(null);
    }

    public boolean isPollInGuild(String guildId, String messageId) {
        return getPoll(guildId, messageId) != null;
    }

    public List<Poll> getPolls(String guildId) {
        return guildPolls.getOrDefault(guildId, List.of());
    }

    public boolean isMultipleReactionsAllowed(String guildId, String messageId) {
        return multipleReactionsAllowedPolls.getOrDefault(guildId, Set.of()).contains(messageId);
    }

    public void setMultipleReactionsAllowed(String guildId, String messageId, boolean allowed) {
        multipleReactionsAllowedPolls.computeIfAbsent(guildId, k -> new HashSet<>());
        if (allowed) {
            multipleReactionsAllowedPolls.get(guildId).add(messageId);
        } else {
            multipleReactionsAllowedPolls.get(guildId).remove(messageId);
        }
    }


}

