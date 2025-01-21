package listeners;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.CommandIcons;
import utils.Embedder;
import utils.GuildSettings;
import utils.Helper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        GuildSettings gs = Bot.getInstance().getGuildSettingsHashMap().get(event.getGuild().getId());

        handleChatRestriction(gs, event);
        handleSwearWords(gs, event);
    }

    private void handleSwearWords(GuildSettings gs, MessageReceivedEvent event) {
        if (gs.isAllowSwear()) return;

        try {
            List<String> swearWords = Helper.loadSwearWordsFromFile();

            String message = event.getMessage().getContentRaw();
            message = message.replace("3", "e")
                    .replace("@", "a")
                    .replace("1", "i")
                    .replace("0", "o");

            boolean containsSwearWord = false;
            for (String swearWord : swearWords) {
                if (message.contains(swearWord)) {
                    containsSwearWord = true;
                    break;
                }
            }

            if (!containsSwearWord) return;

            event.getMessage().delete().queue();
            EmbedBuilder embed = Embedder.createSwearWordEmbed(CommandIcons.SETTINGS_ICON_URL);
            event.getChannel().sendMessageEmbeds(embed.build()).queue(msg -> Helper.deleteAfter(msg, 5));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
