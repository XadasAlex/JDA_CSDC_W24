package utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.CompletableFuture;

public class GuildCommands {


    public static void kick(MessageReceivedEvent event) {
        Member member = Helper.getTaggedMember(event);
        if (member != null) kick(event.getGuild(), member);
    }

    public static boolean kick(Guild guild, Member member) {
        boolean kicked = false;
        try {
            guild.moveVoiceMember(member, null).queue();
            kicked = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return kicked;
    }


    public static CompletableFuture<VoiceChannel> createVoiceChannel(Guild guild, String mergedChannel) {
        CompletableFuture<VoiceChannel> future = new CompletableFuture<>();

        guild.createVoiceChannel(mergedChannel).queue(future::complete, future::completeExceptionally);

        return future;
    }
}
