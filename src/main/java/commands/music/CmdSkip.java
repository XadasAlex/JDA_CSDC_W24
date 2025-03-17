package commands.music;

import audio.AudioGuildManager;
import commands.ICommand;
import dev.arbjerg.lavalink.client.player.Track;
import launcher.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.GuildSettings;
import utils.IconsGuild;

import java.util.List;

public class CmdSkip implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!GuildSettings.isMusicAllowedInGuild(event.getGuild().getId())) {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), getName() , "Music isnt allowed on the server!").build()).queue();
            return;
        }

        AudioGuildManager audio = Bot.getInstance().getAudioGuildManagerById(event.getGuild().getIdLong());

        if (audio == null) {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), "Skip", "Audio manager not found!").build()).queue();
            return;
        }

        // Nächsten Track abrufen und starten
        Track nextTrack = audio.getTrackScheduler().playNextTrack();

        if (nextTrack != null) {
            event.replyEmbeds(Embedder.createBaseEmbed(event.getMember(), IconsGuild.YOUTUBE_ICON, "Skip", "You skipped to the next track", String.format("Now playing: %s", nextTrack.getInfo().getTitle())).build()).queue();
        } else {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), "Skip", "No more tracks in queue!").build()).queue();
        }
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the current song.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
