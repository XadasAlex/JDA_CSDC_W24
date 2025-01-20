package commands.battleship;

import commands.ICommand;
import kotlin.Pair;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;



import java.util.HashMap;

import java.util.List;
import java.util.Map;


public class BattleshipStartGame implements ICommand {
    public static Map<String, Game> activeGames = new HashMap<>();
    public static Map<String,Pair<String,Game>> unacceptedGames = new HashMap<>();      //Map<String = opp,Pair<String = challenge creator,Game>>
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var opp = event.getOption("opponent").getAsUser();
        var boardSize = event.getOption("board-size").getAsInt();
        if (activeGames.containsKey(event.getUser().getId()) || activeGames.containsKey(opp.getId())) {
            event.reply("One of you is still in a running game please use /surrender-game to surrender your running game").queue();
            return;
        }
        if(boardSize>25||boardSize<5) {
            event.reply("Number Provided is larger than 25 or smaller than 5").setEphemeral(true).queue();
            return;
        }
        event.reply(opp.getEffectiveName()+" please use /accept-game to accept the game!").queue();

        var gam = new Game(event.getUser().getId(),opp.getId(),boardSize);
        unacceptedGames.put(opp.getId(), new Pair<>(event.getUser().getId(),gam));
    }

    @Override
    public String getName() {
        return "start-new-game-of-battleships";
    }

    @Override
    public String getDescription() {
        return "Start a new game of battleships";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,"opponent","Provide the Opponent for your game of battleships",true),
                new OptionData(OptionType.STRING, "board-size","Provide the size of the Game board (Min is 5 and Max is 25)",true)
        );
    }
}
