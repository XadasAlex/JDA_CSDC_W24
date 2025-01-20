package commands.battleship;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SurrenderGame implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var hook = event.getHook();
        event.deferReply(true).queue();
        if(!BattleshipStartGame.activeGames.containsKey(event.getUser().getId())) {
            hook.sendMessage("You are not currently in a game!").queue();
        }
        else {
            hook.sendMessage("You Surrendered!").queue();
            var game = BattleshipStartGame.activeGames.get(event.getUser().getId());
            BattleshipStartGame.activeGames.entrySet().removeIf(entry -> entry.getValue().equals(game));
            //Game p1 = Player 1 in DB
            String stat;
            if(game.p1.equals(event.getUser().getId()))
                stat = "UPDATE GAMES SET PlayerTwoWins = 1 WHERE PlayerOne = ? AND PlayerTwo = ?";
            else
                stat = "UPDATE GAMES SET PlayerOneWins = 1 WHERE PlayerOne = ? AND PlayerTwo = ?";
            try (var conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s/battleships.db", Helper.getBaseDBPath()))) {
                if (conn != null) {
                    var st = conn.prepareStatement(stat);
                    st.setString(1, game.p1);
                    st.setString(2, game.p2);
                    st.executeUpdate();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public String getName() {
        return "surrender-game";
    }

    @Override
    public String getDescription() {
        return "Surrenders your current game of Battleships";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
