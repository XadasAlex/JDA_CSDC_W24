package commands.battleship;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class MakeMove implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var hook = event.getHook();
        event.deferReply().queue();

        switch (BattleshipStartGame.activeGames.get(event.getUser().getId()).MakeMove(event.getOption("move").getAsString(),event)){
            case (-2):
                hook.sendMessage("Someone did not yet set his ships please make sure both of you used /set-ships to set your ships.").queue();
                break;
            case (-1):
                hook.sendMessage("Sorry this is an Illegal move!").queue();
                return;
            case 0:
                hook.sendMessage("You did not hit a ship").queue();
                return;
            case 1:
                hook.sendMessage("You hit a ship").queue();
                break;
            case 2:
                hook.sendMessage("Congratulations "+event.getUser().getName()+" you won the game").queue();
                removeFromGames(event);
                break;
        }
    }
    
    private void removeFromGames(SlashCommandInteractionEvent event){
            var game = BattleshipStartGame.activeGames.get(event.getUser().getId());
            BattleshipStartGame.activeGames.entrySet().removeIf(entry -> entry.getValue().equals(game));
            //Game p1 = Player 1 in DB
            String stat;
            if(game.p2.equals(event.getUser().getId()))
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

    @Override
    public String getName() {
        return "make-move";
    }

    @Override
    public String getDescription() {
        return "Makes a move in your current game of Battleships";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING,"move","The move you wish to perform in your Battleships game",true)
        );
    }
}
