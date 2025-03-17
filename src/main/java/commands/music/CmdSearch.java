package commands.music;

import commands.ICommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.SearchResult;
import dev.arbjerg.lavalink.client.player.Track;
import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.GuildSettings;

import java.util.List;

public class CmdSearch implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!GuildSettings.isMusicAllowedInGuild(event.getGuild().getId())) {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), getName() , "Music isnt allowed on the server!").build()).queue();
            return;
        }

        OptionMapping queryOption = event.getOption("query");
        if (queryOption == null) return;

        String searchQuery = queryOption.getAsString();

        if (searchQuery.contains("http")) {
            event.reply("Warum suchst du nach einer URL du Dödl!").queue();
            return;
        }

        LavalinkClient client = Bot.getInstance().getLavalinkClient();

        long guildId = event.getGuild().getIdLong();
        Link link = client.getOrCreateLink(guildId);

        link.loadItem(String.format("ytsearch:%s", searchQuery)).subscribe(lavalinkLoadResult -> {
            if (lavalinkLoadResult instanceof SearchResult searchResult) {
                List<Track> tracks = searchResult.getTracks();
                EmbedBuilder embed = Embedder.createSearchResultEmbed(event, tracks, searchQuery);
                event.replyEmbeds(embed.build()).queue();
                return;
            }

            event.reply(String.format("No results for: %s", searchQuery)).queue();
        });
    }

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "returns search results based on the provided query that are playable.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "query", "the search query", true)
        );
    }
}
