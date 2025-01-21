package listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.Embedder;
import utils.GuildSettings;

public class GuildSettingsListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        assert gs != null;

        if (gs.isMemberChatRestricted(event.getMember().getId())) {
            event.getMessage().delete().queue();
            TextChannel channel = (TextChannel) event.getChannel();
            EmbedBuilder embed = Embedder.createErrorMessage(event.getMember(), "Chat Restricted", "You are not allowed to chat at the time.");
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
