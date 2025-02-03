package commands.music;

import commands.ICommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import launcher.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.GuildSettings;

import java.util.List;

public class CmdPause implements ICommand {
    private LavalinkClient client;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());
        if (!gs.isAllowMusic()) {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), getName() , "Music isnt allowed on the server!").build()).queue();
            return;
        }
        client = Bot.getInstance().getLavalinkClient();
        if (client == null) return;
        client.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap(player -> player.setPaused(true))
                .subscribe();

        event.reply("⏸️ Music paused.").queue();
    }

    @Override
    public String getName() { return "pause"; }
    @Override
    public String getDescription() { return "Pauses the currently playing song"; }
    @Override
    public List<OptionData> getOptions() { return List.of(); }
}
