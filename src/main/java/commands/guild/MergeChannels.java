package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import utils.GuildCommands;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MergeChannels implements ICommand {
    @Override
    public String getName() {
        return "mergec";
    }

    public static String getDisplayName() {
        return "Channel Merger";
    }

    @Override
    public String getDescription() {
        return "merges all the members in the mentioned channels into one channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.CHANNEL, "channels", "the channels for the merging process", true),
                new OptionData(OptionType.CHANNEL, "channels", "the channels for the merging process", true),
                new OptionData(OptionType.CHANNEL, "channels", "the channels for the merging process", false),
                new OptionData(OptionType.CHANNEL, "channels", "the channels for the merging process", false),
                new OptionData(OptionType.CHANNEL, "channels", "the channels for the merging process", false)
        );
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Set.of();
    }

    @Override
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<VoiceChannel> channels = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            OptionMapping channel = event.getOption("channels");
            channels.add((VoiceChannel) channel);
        }

        GuildCommands.createVoiceChannel(event.getGuild(), "MergedChannel");

        for (VoiceChannel channel : channels) {
            for (Member member : channel.getMembers()) {

            }
        }
    }

    @Override
    public void executeWithPermission(SlashCommandInteractionEvent event) {
        ICommand.super.executeWithPermission(event);
    }
}
