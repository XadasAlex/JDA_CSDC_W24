package commands.guild;

import commands.ICommand;
import utils.GuildCommands;
import utils.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class Kick implements ICommand {
    @Override
    public String getName() {
        return "kick";
    }
    
    public static String getDisplayName() {
        return "Disconnect User From Voice Channel";
    }

    @Override
    public String getDescription() {
        return "kicks a member from their current voice channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "member", "the member you want to be kicked out.", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping memberOption = event.getOption("member");

        assert memberOption != null;

        User user = memberOption.getAsUser();
        Member member = event.getGuild().getMemberById(user.getId());

        if (GuildCommands.kick(event.getGuild(), member)) {
            EmbedBuilder embed = MessageSender.createEmbedBlueprint(event, "Kicked User",
                    getDescription(),
                    "Successfully kicked user: " + member.getEffectiveName());

            event.replyEmbeds(embed.build()).queue();

        } else {
            EmbedBuilder embed = MessageSender.createEmbedBlueprint(event, "Kicking User Failed!",
                    getDescription(),
                    "Couldn't kick user: " + member.getEffectiveName());
            event.replyEmbeds(embed.build()).queue();
        }
    }
}
