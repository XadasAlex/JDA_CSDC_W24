package bot.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class ChannelActions {
    public static void joinChannel(AudioManager am, AudioChannel ac) {
        am.openAudioConnection(ac);
    }

    public static void leaveChannel(AudioManager am) {
        am.closeAudioConnection();
    }

    public static Channel getMembersChannel(Member member) {
        Channel channel = null;

        try {

            channel = member.getVoiceState().getChannel();


        } catch (NullPointerException e) { e.printStackTrace(); }

        return channel;
    }
}
