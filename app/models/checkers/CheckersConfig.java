package models.checkers;

import java.util.List;
import models.metabg.IGameConfig;
import models.metabg.IGameLogic;
import models.metabg.Resource;

public class CheckersConfig implements IGameConfig
{
    private static IGameConfig instance = new CheckersConfig();
    public static IGameConfig getInstance () { return instance; }
    protected CheckersConfig () { }
    
    @Override
    public void init (List<Resource> resources) {
        resources.add(new Resource("checkerBoard", "checkerBoard.png"));
        resources.add(new Resource("redChecker", "redChecker.png"));
        resources.add(new Resource("blackChecker", "blackChecker.png"));
    }
    
    @Override public String getName () { return "Checkers"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 2; }
    @Override public int getNumLayers () { return 2; }
    
    @Override
    public IGameLogic createGameLogic (int numPlayers) {
        return new CheckersLogic(numPlayers);         
    }
}
