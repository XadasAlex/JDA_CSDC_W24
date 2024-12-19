package utils;

import launcher.Bot;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceCommands {
    public static void createVoiceChannel(MessageReceivedEvent event) {
        //if (!(Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_CHANNEL)))
    }

    public static void createVoiceChannelAndMove() {

    }

    public static Channel getMembersChannel(Member member) {
        Channel channel = null;

        try {

            channel = member.getVoiceState().getChannel();


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return channel;
    }

    public static void leaveCurrentChannel(MessageReceivedEvent event) {
        AudioManager am = event.getGuild().getAudioManager();
        leaveChannel(am);
    }

    public static void joinTaggedChannel() {
        //Helper.getIdFromString()
    }

    public static void joinMembersChannel(MessageReceivedEvent event) {
        /*
        Channel channel = getMembersChannel(event.getMember());
        AudioChannel audioChannel = (AudioChannel) channel;
        AudioManager audioManager = event.getGuild().getAudioManager();
        joinChannel(audioManager, audioChannel);
    */
        Channel channel = getMembersChannel(event.getMember());
        AudioChannel audioChannel = (AudioChannel) channel;
        event.getJDA().getDirectAudioController().connect(audioChannel);
    }


    public static void joinChannel(AudioManager am, AudioChannel ac) {
        am.openAudioConnection(ac);
    }

    public static void leaveChannel(AudioManager am) {
        am.closeAudioConnection();
    }

    private static AudioSendHandler createAudioSendHandlerForGuild(MessageReceivedEvent event) {
        event.getGuild().getId();

        return null;
    }
/*
    public static void play(MessageReceivedEvent event) {
        Channel channelToJoin = getMembersChannel(event.getMember());
        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.openAudioConnection((AudioChannel) channelToJoin);
        AudioSendHandler ash = createAudioSendHandlerForGuild(event);
        audioManager.setSendingHandler(ash);


        // Ensure the bot is connected to a voice channel
        if (!audioManager.isConnected() || audioManager.getConnectedChannel() != null) {
            // Optionally, join the user's current voice channel
            Member member = event.getMember();
            if (member != null && member.getVoiceState() != null && member.getVoiceState().getChannel() != null) {
                VoiceChannel voiceChannel = (VoiceChannel) member.getVoiceState().getChannel();
                audioManager.openAudioConnection(voiceChannel);
            } else {
                event.getChannel().sendMessage("You need to be in a voice channel for me to join!").queue();
                return;
            }
        }

        // Use the MusicHandler to load and play the track
        Bot.getInstance().getMusicHandler().loadItem(event.getGuild().getId(), "Q9USjqb8Om0");
        event.getChannel().sendMessage("Loading track...").queue();
    }*/
}
