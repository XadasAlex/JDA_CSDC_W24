package commands.music;

import audio.TrackScheduler;
import commands.ICommand;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.IconsGuild;

import java.util.List;

public class CmdQueueClear implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var audio = Bot.getInstance().getAudioGuildManagerById(event.getGuild().getIdLong());

        OptionMapping stopOption = event.getOption("stop");

        TrackScheduler trackScheduler = audio.getTrackScheduler();

        if (trackScheduler.isQueueEmpty()) {
            return;
        }

        trackScheduler.clearQueue();

        if (stopOption != null && stopOption.getAsBoolean()) {
            audio.getLink().updatePlayer(playerUpdateBuilder -> {
                playerUpdateBuilder.setTrack(null);
            }).subscribe();
        }


        EmbedBuilder embed = Embedder.createBaseEmbed(event.getMember(), IconsGuild.YOUTUBE_ICON, getName(), "Cleared the queue", "Stopped the playing.");
        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "queue-clear";
    }

    @Override
    public String getDescription() {
        return "clears the queue";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.BOOLEAN, "stop", "stop playback aswell?", false)
        );
    }
}
