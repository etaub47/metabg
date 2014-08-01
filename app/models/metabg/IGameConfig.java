package models.metabg;

import java.util.List;
import models.metabg.IGameModeFactory.IGameMode;

public interface IGameConfig
{
    public String getName ();    
    public int getMinPlayers ();
    public int getMaxPlayers ();
    public int getNumLayers ();
    
    public void init (List<Resource> resources);    
    public Table createTestTable ();
    public IGameModeFactory createGameModeFactory ();    
    public IGameLogic createGameLogic (int numPlayers);
    public GameState createGameState (int numPlayers, IGameMode gameMode);
}
