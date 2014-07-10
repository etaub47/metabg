package models.metabg;

import java.util.List;

public interface IGameConfig
{
    public String getName ();    
    public int getMinPlayers ();
    public int getMaxPlayers ();
    public int getNumLayers ();
    
    public void init (List<Resource> resources);    
    public Table createTestTable ();
    public IGameLogic createGameLogic (int numPlayers);
}
