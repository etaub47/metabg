package models.dominion;

import models.metabg.Event;
import models.metabg.GameState;
import models.metabg.IGameLogic;
import models.metabg.Result;

public class DominionLogic implements IGameLogic
{
    @Override
    public Result processEvent (GameState state, Event event) throws Exception
    {
        DominionGameState gameState = (DominionGameState) state; 
        EventType eventType = (EventType) event.getType();
        
        switch (eventType) {
            // TODO
        }
        
        return null;
    }
}
