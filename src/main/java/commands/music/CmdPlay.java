package commands.music;

import commands.ICommand;
import audio.GuildMusicManager;
import audio.MusicHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Embedder;
import utils.Helper;

import java.util.List;

public class CmdPlay implements ICommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            event.reply("Du musst in einem Sprachkanal sein!").setEphemeral(true).queue();
            return;
        }
        if (event.getOption("url") == null) {
            event.reply("Bitte gib eine URL an!").setEphemeral(true).queue();
            return;
        }
        String trackUrl = event.getOption("url").getAsString();

        // Hole den VoiceChannel des Nutzers
        VoiceChannel voiceChannel = (VoiceChannel) member.getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();

        // Hol den GuildMusicManager über den MusicHandler-Singleton, anhand der Guild
        GuildMusicManager musicManager = MusicHandler.getInstance().getGuildMusicManager(event.getGuild());

        // Falls der Bot noch nicht verbunden ist, wird die Verbindung zum Sprachkanal hergestellt
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceChannel);
            audioManager.setSendingHandler(musicManager.getSendHandler());
        }

        // Aufruf der loadAndPlay-Methode – hier wird direkt die Guild übergeben
        MusicHandler.getInstance().loadAndPlay(event.getGuild(), trackUrl);

        event.replyEmbeds(
                Embedder.createBaseEmbed(
                        event.getMember(),
                        "https://example.com/music_icon.png", // Beispiel-Icon, anpassen
                        getName(),
                        "Track wird geladen:",
                        trackUrl
                ).build()
        ).queue(Helper::deleteAfter300);
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Spielt einen Track ab. Übergib die URL des Tracks als Option.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(
                net.dv8tion.jda.api.interactions.commands.OptionType.STRING,
                "url",
                "Die URL des Tracks",
                true
        ));
    }
}
