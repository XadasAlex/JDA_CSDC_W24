package commands.battleship;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class AcceptBattleshipGame implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if(!BattleshipStartGame.unacceptedGames.containsKey(event.getUser().getId())) {
            event.reply("You dont have an active game challenge please use /start-new-game-of-battleships to challenge someone to a game!").setEphemeral(true).queue();
            return;
        }

        event.reply("You accepted the challenge please use /set-ships to set the ships for your game!").queue();

        var p = BattleshipStartGame.unacceptedGames.get(event.getUser().getId());

        var stat = "Insert into Games values ((select max(GameID)+1 from Games),?,0,0,?);";
        try (var conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s/battleships.db", Helper.getBaseDBPath()))) {
            if (conn != null) {
                var st = conn.prepareStatement(stat);
                st.setString(2, event.getUser().getId());
                st.setString(1, p.component1());
                st.executeUpdate();

                BattleshipStartGame.activeGames.put(event.getUser().getId(), p.component2());
                BattleshipStartGame.activeGames.put(p.component1(), p.component2());
                BattleshipStartGame.unacceptedGames.remove(event.getUser().getId());
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "accept-game";
    }

    @Override
    public String getDescription() {
        return "join the Battleship game you were challenged to";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
