package commands.music;

import audio.AudioGuildManager;
import commands.ICommand;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.IconsGuild;

import java.util.List;

public class CmdLoop implements ICommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        AudioGuildManager audio = Bot.getInstance().getAudioGuildManagerById(event.getGuild().getIdLong());
        audio.getTrackScheduler().toggleLoop();

        boolean isLooped = audio.getTrackScheduler().isLooped();

        EmbedBuilder embed = Embedder.createBaseEmbed(event.getMember(),
                IconsGuild.YOUTUBE_ICON,
                getName(),
                isLooped ? "Loop enabled" : "Loop disabled",
                "this is a toggle command. Write /loop again to" + (isLooped ? "unloop" : "loop"));

        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public String getDescription() {
        return "loops the current track on this server.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
