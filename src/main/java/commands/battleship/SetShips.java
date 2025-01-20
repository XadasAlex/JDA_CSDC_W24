package commands.battleship;

import commands.ICommand;
import kotlin.Pair;
import kotlin.Triple;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SetShips implements ICommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if(!BattleshipStartGame.activeGames.containsKey(event.getUser().getId())) {
            event.reply("You currently dont have any active games!").setEphemeral(true).queue();
            return;
        }

        var shipPositions = event.getOption("ship-positions").getAsString();

        var parts = shipPositions.split(" ");

        List<Triple<Pair<Integer, Integer>, Integer, Integer>> ships = new ArrayList<>();

        for(var part : parts) {
            var h = part.toCharArray();
            Integer orient = switch (h[2]) {
                case 'N' -> 1;
                case 'E' -> 2;
                case 'S' -> 3;
                case 'W' -> 4;
                default -> null;
            };
            ships.add(new Triple<>(new Pair<>(Integer.parseInt(String.valueOf(h[0])), Integer.parseInt(String.valueOf(h[1]))), orient, Integer.parseInt(String.valueOf(h[3]))));
        }

        if(BattleshipStartGame.activeGames.get(event.getUser().getId()).setShips(null,event)){
            event.reply("You have successfully set all your ships!").setEphemeral(true).queue();
        }else {
            event.reply("Something went wrong with setting your ships please ensure that your ships are not out of bounds and you have the correct amount of ships. Your previous ships were: " + shipPositions).setEphemeral(true).queue();
        }
    }

    @Override
    public String getName() {
        return "set-ships";
    }

    @Override
    public String getDescription() {
        return "Set the Ships for your current game of Battleships";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "ship-positions", "The Positions of your ships in the format xyOrientation(NESW)Length seperated by spaces for each ship", true)
        );
    }
}
