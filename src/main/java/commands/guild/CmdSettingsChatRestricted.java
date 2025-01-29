package commands.guild;

import commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.IconsGuild;
import utils.Embedder;
import utils.GuildSettings;
import utils.Helper;

import java.util.List;

public class CmdSettingsChatRestricted implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping memberOption = event.getOption("member");
        OptionMapping chatRestrictionOption = event.getOption("chat-restricted");
        if (memberOption == null || chatRestrictionOption == null) return;

        Member member = memberOption.getAsMember();

        if (event.getGuild().getSelfMember().getId().equals(member.getId())) {
            event.reply("nice try").queue(Helper::deleteAfter15);
        }

        boolean chatRestricted = chatRestrictionOption.getAsBoolean();
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        if (gs == null) return;

        EmbedBuilder embed = null;

        if (chatRestricted) {
            gs.addMemberChatRestriction(member.getId());
            embed = Embedder.createBaseEmbed(event.getMember(), IconsGuild.SETTINGS_ICON_URL,
                    getName(),
                    "Chat Restriction",
                    String.format("%s has been added to the members who are chat restricted.",
                            member.getEffectiveName())
            );
        } else {
            gs.removeMemberChatRestriction(member.getId());
             embed = Embedder.createBaseEmbed(event.getMember(), IconsGuild.SETTINGS_ICON_URL,
                    getName(),
                    "Chat Restriction",
                    String.format("%s has been removed from the members who are chat restricted.",
                            member.getEffectiveName())
            );
        }

        event.replyEmbeds(embed.build()).queue();
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
