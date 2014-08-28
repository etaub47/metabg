package models.metabg;

import java.util.Collection;
import models.metabg.IGameModeFactory.IGameMode;

public interface IGameConfig
{
    public String getName ();    
    public int getMinPlayers ();
    public int getMaxPlayers ();
    public int getNumLayers ();
    public int getInitialZoom ();
    public int getInitialX ();
    public int getInitialY ();
    
    public void initResources (Collection<Resource> resources);
    public Table createTestTable (Game game);
    public IGameModeFactory createGameModeFactory ();    
    public IGameLogic createGameLogic (int numPlayers);
    public GameState createGameState (Game game, int numPlayers, IGameMode gameMode);
}
