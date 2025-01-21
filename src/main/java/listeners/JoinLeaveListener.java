package listeners;

import launcher.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.CommandIcons;
import utils.Embedder;
import utils.GuildSettings;

public class JoinLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        if (!gs.isMemberWelcomeMessage()) return;

        // Sende eine DM an den Benutzer
        event.getUser().openPrivateChannel().queue(channel -> {
            EmbedBuilder embed = Embedder.createBaseEmbed(
                    event.getMember(),
                    CommandIcons.COMMUNITY_ICON_URL,
                    "Welcome!",
                    String.format("Hey %s, nice to see you!", event.getMember().getEffectiveName()),
                    String.format("Welcome to the server: %s. Enjoy your stay.", event.getGuild().getName())
            );
            channel.sendMessageEmbeds(embed.build()).queue();
        }, error -> {
            System.err.println("Could not send a welcome DM to " + event.getUser().getAsTag());
        });
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        if (!gs.isMemberLeaveMessage()) return;

        // Sende eine DM an den Benutzer
        event.getUser().openPrivateChannel().queue(channel -> {
            EmbedBuilder embed = Embedder.createBaseEmbed(
                    null, // Kein Member-Objekt mehr verfügbar
                    CommandIcons.COMMUNITY_ICON_URL,
                    "Goodbye!",
                    String.format("Bye %s, sad to see you go!", event.getUser().getName()),
                    "We will forever miss you, or until your return."
            );
            channel.sendMessageEmbeds(embed.build()).queue();
        }, error -> {
            System.err.println("Could not send a goodbye DM to " + event.getUser().getAsTag());
        });
    }
}
