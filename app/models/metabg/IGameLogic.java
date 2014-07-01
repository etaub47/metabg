package models.metabg;

import java.util.Map;
import java.util.Set;

public interface IGameLogic 
{
    public void initUserInterface (UserInterface userInterface);
    public void initActions (Set<Action> expectedActions, Map<String, Sequence> sequences);
    
    public Result processEvent (GameState state, Event event);
}
