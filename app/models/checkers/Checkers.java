package models.checkers;

import models.metabg.ClickableRegion;
import models.metabg.Game;
import models.metabg.GameState;
import models.metabg.Resource;
import com.google.common.collect.ImmutableList;

public class Checkers extends Game
{
    private static Game instance = new Checkers();
    public static Game getInstance () { return instance; }
    protected Checkers () { }

    // TODO: read from config file?

    @Override public String getName () { return "Checkers"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 2; }
    @Override public GameState createGameState (int numPlayers) { return new CheckersState(numPlayers); }
    
    @Override
    protected void addResources () {
        resources.add(new Resource("checkerBoard", "checkerBoard.png", new ImmutableList.Builder<ClickableRegion>()
            .add(new ClickableRegion(10, 100, 25, 25, "(1, 8)"))
            .add(new ClickableRegion(30, 100, 25, 25, "(2, 8)"))
            .build()));
        resources.add(new Resource("redChecker", "redChecker.png"));
        resources.add(new Resource("blackChecker", "blackChecker.png"));
    }
}
