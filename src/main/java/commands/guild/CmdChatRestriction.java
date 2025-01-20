package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Embedder;
import utils.GuildSettings;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;

public class CmdChatRestriction implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            event.replyEmbeds(Embedder.createErrorMessage(event.getMember(), getName(), "Only Admins can use this command.").build()).queue();
            return;
        }

        Member member = Objects.requireNonNull(event.getOption("member")).getAsMember();
        boolean chatRestricted = Objects.requireNonNull(event.getOption("chat-restricted")).getAsBoolean();

        assert member != null;

        Guild guild = event.getGuild();

        assert guild != null;

        GuildSettings gs = GuildSettings.load(guild.getId());

        assert gs != null;

        if (chatRestricted) {
            gs.addMemberChatRestriction(member.getId());
        } else {
            gs.removeMemberChatRestriction(member.getId());
        }
        gs.update();
    }

    @Override
    public String getName() {
        return "chat-restrict";
    }

    @Override
    public String getDescription() {
        return "disable the mentioned members ability to send messages in this server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "member", "member executed on", true),
                new OptionData(OptionType.BOOLEAN, "chat-restricted", "chat restricted true/false", true)
        );
    }
}
