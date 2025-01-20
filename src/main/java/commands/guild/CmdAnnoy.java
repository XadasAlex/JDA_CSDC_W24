package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdAnnoy implements ICommand {
    // todo: what happens when user is not in a channel
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping memberOption = event.getOption("member");

        if (memberOption == null) return;

        Member member = memberOption.getAsMember();
        Guild guild = member.getGuild();

        annoy(member, guild);
    }

    private void annoy(Member member, Guild guild) {
        List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
        List<VoiceChannel> allowedChannels = voiceChannels.stream()
                .filter(channel -> member.hasPermission(channel, Permission.VOICE_CONNECT))
                .collect(Collectors.toList());

        // Wiederhole die Kanalliste, bis mindestens 9 Kanäle vorhanden sind
        while (allowedChannels.size() < 9) {
            allowedChannels.addAll(new ArrayList<>(allowedChannels));
        }

        // Stelle sicher, dass der aktuelle Voice-Channel des Members an Position 9 ist
        if (member.getVoiceState() != null && member.getVoiceState().getChannel() != null) {
            VoiceChannel currentChannel = (VoiceChannel) member.getVoiceState().getChannel();
            if (!allowedChannels.contains(currentChannel)) {
                allowedChannels.add(currentChannel);
            }
            allowedChannels.set(9, currentChannel); // Setze den aktuellen Channel an Position 9
        }

        // Begrenze die Liste auf maximal 10 Kanäle
        allowedChannels = allowedChannels.stream().limit(10).collect(Collectors.toList());

        // Verschiebe den Member durch die erlaubten Kanäle
        for (VoiceChannel voiceChannel : allowedChannels) {
            if (member.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                voiceChannel.getGuild().moveVoiceMember(member, voiceChannel).queue();
            }
        }
    }

    @Override
    public String getName() {
        return "annoy";
    }

    @Override
    public String getDescription() {
        return "annoys the member";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "member", "the tagged member will be annoyed", true)
        );
    }
}
