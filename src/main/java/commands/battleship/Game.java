package commands.battleship;

import kotlin.Pair;
import kotlin.Triple;
import launcher.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game {
    public String GameID;

    public String p1;
    public String p2;

    public String Moves;

    private boolean[][] p1Ships;
    private boolean[][] p2Ships;

    private boolean[][] p1Shots;
    private boolean[][] p2Shots;

    private boolean p1SetShips;
    private boolean p2SetShips;

    private boolean turnPlayer;             //true = p1's turn false = p2's turn

    /*private Map<Integer,Integer> shipSizes = Map.ofEntries(
            Map.entry(1,2),
            Map.entry(2,3),
            Map.entry(3,3),
            Map.entry(4,2),
            Map.entry(5,1)
    );*/

    private Map<Integer,Integer> shipSizes = Map.ofEntries(
            Map.entry(1,2)
    );

    public Game(String p1, String p2,Integer boardSize) {
        this.p1 = p1;
        this.p2 = p2;
        this.p1Ships = new boolean[boardSize][boardSize];
        this.p2Ships = new boolean[boardSize][boardSize];
        this.p1Shots = new boolean[boardSize][boardSize];
        this.p2Shots = new boolean[boardSize][boardSize];

        Moves = "";
    }

    public Integer MakeMove(String move, SlashCommandInteractionEvent player) {      //a=97 z=122
        if(!p1SetShips&&!p2SetShips)
            return -2;

        if(turnPlayer && !(player.getUser().getId().equals(p1))){
            return -3;
        }

        if(!turnPlayer && !(player.getUser().getId().equals(p2))){
            return -3;
        }

        if(move.length()<2 || move.length()>4)
            return -1;          //-1 = error

        var parts = move.split(" ");

        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);

        boolean hit = false;

        if(player.getUser().getId().equals(p1)) {
            if(p1Shots[x][y])
                return -1;
            p1Shots[x][y] = true;
            if(p2Ships[x][y])
                hit = true;
        }else {
            if(p2Shots[x][y])
                return -1;
            p2Shots[x][y] = true;
            if(p1Ships[x][y])
                hit = true;
        }

        Moves+=(move+"-");

        turnPlayer = !turnPlayer;

        if(checkIfWon(player.getUser().getId().equals(p1)))
            return 2;
        if(hit)
            return 1;
        return 0;
    }

    public boolean setShips (Triple<Pair<Integer, Integer>, Integer, Integer>[] ships, SlashCommandInteractionEvent player){

        Map<Integer,Integer> seenSizes = new HashMap<>();

        for(Triple<Pair<Integer, Integer>, Integer, Integer> ship : ships){

            if(!seenSizes.containsKey(ship.component3()))
                seenSizes.put(ship.component3(), 1);
            else
                seenSizes.put(ship.component3(), seenSizes.get(ship.component3()) + 1);
        }

        if(!seenSizes.equals(shipSizes)) {
            return false;
        }

        if(player.getUser().getId().equals(p1)) {
            var h = populateBoard(ships, p1Ships);
            if(h){
                p1SetShips = true;
                if(p1SetShips&&p2SetShips){
                    turnPlayer = new Random().nextBoolean();
                    player.getChannel().sendMessage("Both Players registered their ships. The first person to make a move is: " + Bot.getInstance().getJda().getUserById(turnPlayer ? p1 : p2).getName()).queue();
                }
                return true;
            }else
                return false;
        }else {
            var h = populateBoard(ships, p2Ships);
            if(h){
                p2SetShips = true;
                return true;
            }else
                return false;
        }

    }

    private boolean populateBoard(Triple<Pair<Integer, Integer>, Integer, Integer>[] ships, boolean[][] board) {    //comp1 Pair is X Y comp2 is orientation 0=N, 1=E, 2=S, 3=W comp3 is length of ship
        for (Triple<Pair<Integer, Integer>, Integer, Integer> triple : ships){
            switch (triple.component2()){
                case 0:     //N
                    if(triple.component1().component1()+triple.component3() > board.length)
                        return false;

                    for (int i = 0; i<triple.component3();i++){
                        board[triple.component1().component1()+i][triple.component1().component2()] = true;
                    }
                    break;
                case 1:     //E
                    if(triple.component1().component2()+triple.component3() > board.length)
                        return false;

                    for (int i = 0; i<triple.component3();i++){
                        board[triple.component1().component1()][triple.component1().component2()+i] = true;
                    }
                    break;
                case 2:     //S
                    if(triple.component1().component2()-triple.component3() <0)
                        return false;
                    for (int i = 0; i<triple.component3();i++){
                        board[triple.component1().component1()-i][triple.component1().component2()] = true;
                    }
                    break;
                case 3:     //W
                    if(triple.component1().component2()-triple.component3() <0)
                        return false;
                    for (int i = 0; i<triple.component3();i++){
                        board[triple.component1().component1()][triple.component1().component2()-i] = true;
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private boolean checkIfWon(boolean shotPlayer) {
        if(shotPlayer){
            for (int i = 0; i<p1Ships.length; i++){
                for (int j = 0; j<p1Ships.length; j++){
                    if(p2Ships[i][j]){
                        if(!p1Shots[i][j])
                            return false;
                    }

                }
            }
            return true;
        }else {
            for (int i = 0; i<p2Ships.length; i++){
                for (int j = 0; j<p2Ships.length; j++){
                    if(p1Ships[i][j]){
                        if(!p2Shots[i][j])
                            return false;
                    }
                }
            }
            return true;
        }
    }

    int getBit(int n, int k) {
        return (n >> k) & 1;
    }
}
