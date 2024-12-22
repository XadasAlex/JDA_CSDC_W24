package listeners;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class ReactionListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

    }

    /*
    private static void handlePollReactionRemove(MessageReactionRemoveEvent event) {
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        handlePoll(message);
    }

    private static void handlePoll(Message message) {
        List<MessageReaction> botReactions = message.getReactions().stream().filter(MessageReaction::isSelf).toList();
        Poll poll = PollManager.getInstance().getPoll(message);

        for (int i = 0; i < botReactions.size(); i++) {
            MessageReaction botReaction = botReactions.get(i);

            int count = botReaction.getCount() - 1;
            poll.updateResults(i, count);
        }
    }

    private static void handlePollReactionAdd(MessageReactionAddEvent event) {
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        handlePoll(message);
    }

     */
}
