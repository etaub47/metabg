package models.checkers;

import java.util.Collection;
import models.metabg.Game;
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
    
    @Override public String getName () { return "Checkers"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 2; }
    @Override public int getNumLayers () { return 2; }
    @Override public int getInitialZoom () { return 4; }
    @Override public int getInitialX () { return 10; }
    @Override public int getInitialY () { return 10; }    
    
    @Override
    public void initResources (Collection<Resource> resources) {
        resources.add(new Resource("checkerBoard", "checkers/checkerBoard.png", 940, 948));
        resources.add(new Resource("redChecker", "checkers/redChecker.png", 90, 88));
        resources.add(new Resource("blackChecker", "checkers/blackChecker.png", 90, 93));
        resources.add(new Resource("redKing", "checkers/redKing.png", 90, 88));
        resources.add(new Resource("blackKing", "checkers/blackKing.png", 90, 93));
    }
    
    @Override public Table createTestTable (Game game) { 
        return new Table(game, "Testing", 2, IGameModeFactory.DefaultGameMode.Default); 
    } 
    
    @Override public IGameModeFactory createGameModeFactory () { 
        return new IGameModeFactory.DefaultGameModeFactory(); 
    }
    
    @Override public IGameLogic createGameLogic (int numPlayers) { 
        return new CheckersLogic(); 
    }
    
    @Override public GameState createGameState (Game game, int numPlayers, IGameMode mode) { 
        return new CheckersGameState(game, numPlayers, getNumLayers()); 
    }
}
