package commands.music;

import commands.ICommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import launcher.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class CmdStop implements ICommand {
    private LavalinkClient client;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        client = Bot.getInstance().getLavalinkClient();
        if (client == null) return;
        client.getOrCreateLink(event.getGuild().getIdLong())
                .updatePlayer(update -> update.setTrack(null))
                .subscribe();

        event.reply("⏹️ Stopped playing.").queue();
    }

    @Override
    public String getName() { return "stop"; }
    @Override
    public String getDescription() { return "Stops the currently playing song"; }
    @Override
    public List<OptionData> getOptions() { return List.of(); }
}