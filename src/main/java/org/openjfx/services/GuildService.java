package org.openjfx.services;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import utils.ChatMessage;
import utils.Helper;
import java.util.ArrayList;
import java.util.List;

public class GuildService {

    private final JDA jda;

    public GuildService(JDA jda) {
        this.jda = jda;
    }

    public void loadGuildsIntoListView(ListView<String> guildListView) {
        if (jda == null) {
            System.err.println("JDA is not initialized!");
            return;
        }

        new Thread(() -> {
            try {
                jda.awaitReady();
                List<String> guildNames = jda.getGuilds()
                        .stream()
                        .map(guild -> String.format("%s (%s)", guild.getName(), guild.getId()))
                        .toList();

                Platform.runLater(() -> guildListView.getItems().setAll(guildNames));
            } catch (InterruptedException e) {
                System.err.println("JDA initialization interrupted: " + e.getMessage());
            }
        }).start();
    }

    public void loadMembersChatHistoryIntoTableView(TableView<ChatMessage> memberChatTableView, Member member) {
        if (jda == null) {
            System.err.println("JDA is not initialized!");
            return;
        }

        new Thread(() -> {
            try {
                jda.awaitReady();

                // Lade die Nachrichten des Members
                List<Message> chatHistory = loadMembersChatHistory(member.getGuild(), member);

                // Konvertiere die Nachrichten in ChatMessage-Objekte
                List<ChatMessage> chatMessages = chatHistory.stream()
                        .map(msg -> new ChatMessage(
                                Helper.formatTimestamp(msg.getTimeCreated()),
                                msg.getContentDisplay(),
                                msg.getChannel().getName()
                        ))
                        .toList();

                // Aktualisiere die TableView im JavaFX-Thread
                Platform.runLater(() -> memberChatTableView.getItems().setAll(chatMessages));

            } catch (InterruptedException e) {
                System.err.println("Error loading chat history: " + e.getMessage());
            }
        }).start();
    }

    private List<Message> loadMembersChatHistory(Guild guild, Member member) {
        List<Message> messages = new ArrayList<>();

        guild.getTextChannels().parallelStream().forEach(channel -> {
            if (!member.hasPermission(channel, Permission.MESSAGE_SEND)) return;

            // Lade nur die letzten 50 Nachrichten
            channel.getIterableHistory()
                    .takeAsync(50) // Nur 50 Nachrichten laden
                    .thenAcceptAsync(retrievedMessages -> {
                        retrievedMessages.stream()
                                .filter(message -> message.getAuthor().getId().equals(member.getId()))
                                .forEach(messages::add);
                    }).join(); // Warte auf Abschluss
        });

        return messages;
    }

    public String extractGuildIdFromSelection(String selection) {
        int startIdx = selection.lastIndexOf('(') + 1;
        int endIdx = selection.lastIndexOf(')');
        return selection.substring(startIdx, endIdx);
    }
}
