package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;
import utils.Embedder;
import utils.IconsGuild;

import java.util.List;

public class CmdEmbed implements ICommand {
    @Override
    public String getName() {
        return "embed";
    }

    public static String getDisplayName() {
        return "Embed";
    }


    @Override
    public String getDescription() {
        return "prints a basic embed - for testing purposes";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var embed = Embedder.createBaseEmbed(event.getMember(), IconsGuild.YOUTUBE_ICON, "playing", "playing", "playing");
        embed.setThumbnail("https://tenor.com/view/kickl-fp%C3%B6-gif-2280244404243916118");

        event.replyEmbeds(embed.build()).queue();
        event.getHook().sendMessage("FPÖ Vibes:").queue(msg -> {Helper.deleteAfter(msg, 10);});
        event.getHook().sendMessage("https://tenor.com/view/kickl-fp%C3%B6-gif-2280244404243916118").queue(msg -> {Helper.deleteAfter(msg, 10);});
    }
}
