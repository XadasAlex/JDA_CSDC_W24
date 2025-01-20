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
        var hook = event.getHook();
        event.deferReply().setEphemeral(true).queue();

        var shipPositions = event.getOption("ship-positions").getAsString();

        var parts = shipPositions.split(" ");

        Triple<Pair<Integer, Integer>, Integer, Integer>[] ships = new Triple[parts.length];
        int counter = 0;

        try {

            for (var part : parts) {
                var h = part.split("-");


                Integer orient = switch (h[2].toCharArray()[0]) {
                    case 'N' -> 1;
                    case 'E' -> 2;
                    case 'S' -> 3;
                    case 'W' -> 4;
                    default -> null;
                };
                ships[counter] = new Triple<>(new Pair<>(Integer.parseInt(String.valueOf(h[0])), Integer.parseInt(String.valueOf(h[1]))), orient, Integer.parseInt(String.valueOf(h[3])));
                counter++;
            }
        }catch (Exception e) {
            hook.sendMessage("Something went wrong while setting your ships Your previous ships were: " + shipPositions).setEphemeral(true).queue();
            return;
        }

        if(BattleshipStartGame.activeGames.get(event.getUser().getId()).setShips( ships,event)){
            hook.sendMessage("You have successfully set all your ships!").setEphemeral(true).queue();
        }else {
            hook.sendMessage("Something went wrong with setting your ships. Your previous ships were: " + shipPositions).setEphemeral(true).queue();
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
                new OptionData(OptionType.STRING, "ship-positions", "Format x-y-Orientation(NESW)-Length seperated by spaces for each ship", true)
        );
    }
}
