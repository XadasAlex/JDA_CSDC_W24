package commands.music;

import commands.ICommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import launcher.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class CmdResume implements ICommand {
    private LavalinkClient client;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        client = Bot.getInstance().getLavalinkClient();
        if (client == null) return;
        client.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap(player -> player.setPaused(false))
                .subscribe();

        event.reply("▶️ Music resumed.").queue();
    }

    @Override
    public String getName() { return "resume"; }
    @Override
    public String getDescription() { return "Resumes the paused song"; }
    @Override
    public List<OptionData> getOptions() { return List.of(); }
}
