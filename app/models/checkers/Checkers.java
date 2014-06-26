package models.checkers;

import models.metabg.Game;
import models.metabg.GameState;
import models.metabg.Resource;

public class Checkers extends Game
{
    private static Game instance = new Checkers();
    public static Game getInstance () { return instance; }
    protected Checkers () { }
    
    @Override
    public void init () {
        resources.add(new Resource("checkerBoard", "checkerBoard.png"));
        resources.add(new Resource("redChecker", "redChecker.png"));
        resources.add(new Resource("blackChecker", "blackChecker.png"));
    }
    
    @Override public String getName () { return "Checkers"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 2; }
    @Override public int getNumLayers () { return 2; }
    @Override public GameState createGameState (int numPlayers, int numLayers) { 
        return new CheckersState(numPlayers, numLayers); 
    }
}
