package commands.music;

import audio.AudioGuildManager;
import commands.ICommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.*;
import launcher.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;

import java.util.List;
import java.util.Objects;

public class CmdPlay implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        AudioGuildManager audio = Bot.getInstance().getAudioGuildManagerById(event.getGuild().getIdLong());

        String query = Objects.requireNonNull(event.getOption("query")).getAsString();

        if (!query.contains("https")) {
            query = "ytsearch:" + query;
        }

        if (query.isEmpty()) {
            event.reply("❌ You must provide a search query or URL!").setEphemeral(true).queue();
            return;
        }

        Member member = event.getMember();

        if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            event.reply("❌ You must be in a voice channel!").queue();
            return;
        }

        VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();

        if (!event.getGuild().getAudioManager().isConnected()) {
            event.getGuild().getAudioManager().openAudioConnection(channel);
        }

        audio.play(query, event, channel);
    }


    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays a song from a given URL or search query";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "query", "The URL or search term of the song", true));
    }
}
