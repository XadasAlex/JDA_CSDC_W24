package commands.battleship;

import commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.Helper;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class RegisterBattleships implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var hook = event.getHook();
        var id = event.getUser().getId();
        event.deferReply(true).queue();     //gives the bot more time to reply sometimes the interaction takes more than 3 secs for whatever reason

        var stmt = "SELECT COUNT(*) FROM Players WHERE `PlayerID` = ?";
        try (var conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s/battleships.db", Helper.getBaseDBPath()))) {
            if (conn != null) {
                var prep = conn.prepareStatement(stmt);
                prep.setString(1, id);
                var h = prep.executeQuery();
                h.next();
                if (h.getInt("COUNT(*)")==0) {        //Player not in the DB Table
                    System.out.println(h.getInt("COUNT(*)") + " battleships were registered");
                    var insertPlayer = "INSERT INTO Players VALUES (?);";
                    var stat = conn.prepareStatement(insertPlayer);
                    stat.setString(1, id);
                    stat.executeUpdate();
                    hook.sendMessage("Successfully registered for battleships").queue();
                }else{
                    hook.sendMessage("Already registered").queue();
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    @Override
    public String getName() {
        return "register-battleships";
    }

    @Override
    public String getDescription() {
        return "Register yourself for Battleships";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }
}
