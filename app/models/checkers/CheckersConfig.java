package models.checkers;

import java.util.List;
import models.metabg.GameState;
import models.metabg.IGameConfig;
import models.metabg.IGameLogic;
import models.metabg.IGameModeFactory;
import models.metabg.IGameModeFactory.IGameMode;
import models.metabg.Resource;
import models.metabg.Table;

public class CheckersConfig implements IGameConfig
{
    private static IGameConfig instance = new CheckersConfig();
    public static IGameConfig getInstance () { return instance; }
    protected CheckersConfig () { }
    
    @Override
    public void init (List<Resource> resources) {
        resources.add(new Resource("checkerBoard", "checkers/checkerBoard.png"));
        resources.add(new Resource("redChecker", "checkers/redChecker.png"));
        resources.add(new Resource("blackChecker", "checkers/blackChecker.png"));
        resources.add(new Resource("redKing", "checkers/redKing.png"));
        resources.add(new Resource("blackKing", "checkers/blackKing.png"));
    }
    
    @Override public String getName () { return "Checkers"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 2; }
    @Override public int getNumLayers () { return 2; }
    
    @Override public Table createTestTable () { return new Table(this, "Testing", 2, IGameModeFactory.DefaultGameMode.Default); } 
    
    @Override public IGameModeFactory createGameModeFactory () { return new IGameModeFactory.DefaultGameModeFactory(); } 
    @Override public IGameLogic createGameLogic (int numPlayers) { return new CheckersLogic(); }
    @Override public GameState createGameState (int numPlayers, IGameMode mode) { 
        return new CheckersGameState(numPlayers, getNumLayers()); 
    }
}
