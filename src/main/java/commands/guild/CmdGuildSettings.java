package commands.guild;

import commands.ICommand;
import commands.ICommandPermissions;
import commands.IStringSelectMenu;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import utils.Embedder;
import utils.GuildSettings;

import java.util.List;
import java.util.Set;

public class CmdGuildSettings implements ICommand, IStringSelectMenu, ICommandPermissions {

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Set.of(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return false;
        /*
        if (event.getMember() == null) return false;
        return event.getMember().hasPermission(getRequiredPermissions());
    */
    }


    @Override
    public void executeWithPermission(SlashCommandInteractionEvent event) {
        if (hasPermission(event)) {
            execute(event);
        } else {
            event.replyEmbeds(Embedder.createMissingPermissionEmbed(event.getMember(), getName()).build()).queue();
        }
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Zeige das Dropdown-Menü an
        event.reply("Bitte wähle eine Einstellung aus:")
                .addActionRow(createStringSelectMenu(event))
                .setEphemeral(true)
                .queue();
    }

    @Override
    public String getName() {
        return "guild-settings";
    }

    @Override
    public String getDescription() {
        return "Allows you to change the guild settings";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public StringSelectMenu createStringSelectMenu(SlashCommandInteractionEvent event) {
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        return StringSelectMenu.create("guild:settings:select")
                .addOption("Spam ".concat(String.format("(%s)", gs.isAllowSpam() ? "✅" : "❌")), "allow-spam", "Allow or disallow spam?")
                .addOption("Swear ".concat(String.format("(%s)", gs.isAllowSwear() ? "✅" : "❌")), "allow-swear", "Allow or disallow swearing?")
                .addOption("Welcome Message ".concat(String.format("(%s)", gs.isMemberWelcomeMessage() ? "✅" : "❌")), "welcome-message", "Enable or disable welcome messages?")
                .addOption("Leave Message ".concat(String.format("(%s)", gs.isMemberLeaveMessage() ? "✅" : "❌")), "leave-message", "Enable or disable leave messages?")
                .addOption("Music ".concat(String.format("(%s)", gs.isAllowMusic() ? "✅" : "❌")), "allow-music", "Allow or disallow music bot?")
                .build();
    }

    @Override
    public void onStringSelectionInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("guild:settings:select")) return;

        String selected = event.getSelectedOptions().get(0).getValue();
        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        String response;
        switch (selected) {
            case "allow-spam":
                gs.setAllowSpam(!gs.isAllowSpam()); // Toggle the setting
                response = "Spam-Einstellung wurde auf **" + gs.isAllowSpam() + "** gesetzt!";
                break;
            case "allow-swear":
                gs.setAllowSwear(!gs.isAllowSwear()); // Toggle the setting
                response = "Swear-Einstellung wurde auf **" + gs.isAllowSwear() + "** gesetzt!";
                break;
            case "welcome-message":
                gs.setMemberWelcomeMessage(!gs.isMemberWelcomeMessage()); // Toggle the setting
                response = "Welcome-Message-Einstellung wurde auf **" + gs.isMemberWelcomeMessage() + "** gesetzt!";
                break;
            case "leave-message":
                gs.setMemberLeaveMessage(!gs.isMemberLeaveMessage()); // Toggle the setting
                response = "Leave-Message-Einstellung wurde auf **" + gs.isMemberLeaveMessage() + "** gesetzt!";
                break;
            case "allow-music":
                gs.setAllowMusic(!gs.isAllowMusic()); // Toggle the setting
                response = "Music-Einstellung wurde auf **" + gs.isAllowMusic() + "** gesetzt!";
                break;
            default:
                response = "Ungültige Auswahl!";
                break;
        }

        gs.update();

        event.reply(response).setEphemeral(true).queue();
    }
}