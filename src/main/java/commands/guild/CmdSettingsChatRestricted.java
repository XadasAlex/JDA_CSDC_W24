package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.GuildSettings;

import java.util.List;

public class CmdSettingsChatRestricted implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping memberOption = event.getOption("member");
        OptionMapping chatRestrictionOption = event.getOption("chat-restricted");
        if (memberOption == null || chatRestrictionOption == null) return;

        Member member = memberOption.getAsMember();
        boolean chatRestricted = chatRestrictionOption.getAsBoolean();
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        if (gs == null) return;

        if (chatRestricted) {
            gs.addMemberChatRestriction(member.getId());
        } else {
            gs.removeMemberChatRestriction(member.getId());
        }
    }

    @Override
    public String getName() {
        return "chat-restrict";
    }

    @Override
    public String getDescription() {
        return "chat restricts one mentioned member from this guild";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "member", "the member to be chat restricted.", true),
                new OptionData(OptionType.BOOLEAN, "chat-restricted", "chat restriction true -> add false -> remove", true)
        );
    }
}
