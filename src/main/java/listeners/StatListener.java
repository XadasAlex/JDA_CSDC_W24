package listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import stats.MemberStats;
import utils.Helper;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class StatListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String memberId = Objects.requireNonNull(event.getMember()).getId();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        if (!MemberStats.allowedStats(guildId, memberId)) return;

        MemberStats stats = MemberStats.getMemberStats(guildId, memberId);

        if (stats == null) return;

        stats.incrementBotInteractions();
        stats.updateSelf();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String memberId = Objects.requireNonNull(event.getMember()).getId();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        if (!MemberStats.allowedStats(guildId, memberId)) return;

        MemberStats stats = MemberStats.getMemberStats(event.getGuild().getId(), event.getMember().getId());

        if (stats == null) return;

        stats.incrementMessagesSent();

        String content = event.getMessage().getContentDisplay();
        long charactersSent = stats.getCharactersSent();
        stats.setCharactersSent(charactersSent + content.length());

        stats.updateSelf();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        String memberId = Objects.requireNonNull(event.getMember()).getId();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        if (!MemberStats.allowedStats(guildId, memberId)) return;

        MemberStats stats = MemberStats.getMemberStats(guildId, memberId);

        if (stats == null) return;
        // self reactions increase
        stats.incrementSelfReactionCount();
        stats.updateSelf();

        // other (who's message got reacted to) reactions increase
        String messageReactionId = event.getReaction().getMessageId();

        // async fetch message without blocking the main thread -> completable futures
        CompletableFuture<Message> message = event
                .getGuildChannel()
                .retrieveMessageById(messageReactionId)
                .submit();

        message.thenAccept(msg -> {
            Member otherMember = msg.getMember();
            // if member couldn't be fetched or
            // if the member reacted to their own message or
            // the other member disallowed stat-tracking
            if (otherMember == null || otherMember.getId().equals(memberId) || !MemberStats.allowedStats(guildId, otherMember.getId())) return;

            MemberStats otherStats = MemberStats.getMemberStats(guildId, otherMember.getId());

            if (otherStats == null) return;

            otherStats.incrementOtherReactionCount();
            otherStats.updateSelf();
        });
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        String memberId = Objects.requireNonNull(event.getMember()).getId();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        if (!MemberStats.allowedStats(guildId, memberId)) return;

        AudioChannel joined = event.getChannelJoined();
        AudioChannel left = event.getChannelLeft();

        if (joined == null) {
            leftGuildVc(event);
            return;
        }

        if (left == null) {
            joinedGuildVc(event);
            return;
        }

        if (joined.equals(left)) {
            changedVoiceState(event);

        } else {
            switchedGuildVc(event);
        }
    }

    public void joinedGuildVc(GuildVoiceUpdateEvent event) {
        MemberStats stats = MemberStats.getMemberStats(event.getGuild().getId(), event.getMember().getId());

        if (stats == null) return;

        if (stats.isInVoice()) return;

        stats.setLastTimeJoined(Helper.currentTimeSeconds());
        stats.joinedVoice();
        stats.updateSelf();
    }



    public void leftGuildVc(GuildVoiceUpdateEvent event) {
        MemberStats stats = MemberStats.getMemberStats(event.getGuild().getId(), event.getMember().getId());

        if (stats == null) return;

        if (!stats.isInVoice()) return;

        long lastTimeJoined = stats.getLastTimeJoined();

        long timeDelta = Helper.currentTimeSeconds() - lastTimeJoined;
        long totalTime = stats.getTotalTime();

        stats.setTotalTime(totalTime + timeDelta);
        stats.leftVoice();
        stats.updateSelf();
    }

    public void switchedGuildVc(GuildVoiceUpdateEvent event) {

    }

    public void changedVoiceState(GuildVoiceUpdateEvent event) {

    }
}
