package commands.chat;

import commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import utils.Helper;
import utils.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;

public class CmdAssignTeams implements ICommand {

    @Override
    public String getName() {
        return "teams";
    }

    @Override
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return false;
    }

    @Override
    public String getDescription() {
        return "Assembles a team with the given players";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "list-users", "List of users to create a team with (player count >= teams)", true),
                new OptionData(OptionType.INTEGER, "teams", "how many teams should be created? Default is 2.", false),
                new OptionData(OptionType.BOOLEAN, "ccm", "[Create Channels & Move] the players to their team if they were tagged before", false)
        );
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Set.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping optionPlayers = event.getOption("list-users");
        OptionMapping optionTeamCount = event.getOption("teams");

        if (optionPlayers == null) return;

        int teamCount = (optionTeamCount == null || optionTeamCount.getAsInt() <= 1) ? 2: optionTeamCount.getAsInt();

        String players = optionPlayers.getAsString();

        List<Member> result = Helper.getTaggedMembers(event.getGuild(), players);

        if (result == null || result.size() < teamCount) return;

        List<Member> taggedPlayers = new ArrayList<>(result);

        Collections.shuffle(taggedPlayers);

        List<List<Member>> teams = new ArrayList<>();

        for (int i = 0; i < teamCount; i++) {
            teams.add(new ArrayList<>());
        }

        int teamIndex = 0;
        for (Member player : taggedPlayers) {
            teams.get(teamIndex).add(player);
            teamIndex = (teamIndex + 1) % teamCount;
        }

        EmbedBuilder teamEmbed = MessageSender.
                createEmbedBlueprint(event, "Shuffled Players into teams", "Teams",
                        String.format("Created %s Team(s) for %s Players", teamCount, taggedPlayers.size()));
        teamIndex = 1;
        for (List<Member> team : teams) {
            teamEmbed.addField(String.format("Team %d: ", teamIndex), String.format("%d Players", team.size()), true);
            int playerIndex = 1;
            for (Member player : team) {
                teamEmbed.addField("(" + playerIndex + "):", player.getEffectiveName(), true);
                playerIndex++;
            }
            teamIndex++;
        }

        event.replyEmbeds(teamEmbed.build()).queue();
    }

    @Override
    public void executeWithPermission(SlashCommandInteractionEvent event) {
        ICommand.super.executeWithPermission(event);
    }
}
