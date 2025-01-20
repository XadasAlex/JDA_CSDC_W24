package listeners;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.Embedder;
import utils.GuildSettings;
import utils.Helper;

public class ChatListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        GuildSettings gs = Bot.getInstance().getGuildSettingsHashMap().get(event.getGuild().getId());

        handleChatRestriction(gs, event);
    }

    private void handleChatRestriction(GuildSettings gs, MessageReceivedEvent event) {
        Member member = event.getMember();

        if (!gs.isMemberChatRestricted(member.getId())) return;

        event.getMessage().delete().queue();
        // todo: chat restricted embed
        EmbedBuilder embed = Embedder.createErrorMessage(member, "Chat Restricted!", "You are not allowed to chat in this server. You have been chat restricted by a moderator.");
        event.getChannel().asTextChannel().sendMessageEmbeds(embed.build()).queue(msg -> {
            Helper.deleteAfter(msg, 5);
        });

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
