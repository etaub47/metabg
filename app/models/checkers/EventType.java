package models.checkers;

import models.metabg.Event.IEventType;

public enum EventType implements IEventType 
{ 
    SELECT_CHECKER, 
    SELECT_SQUARE, 
    END_TURN, 
    UNDO_CHECKER, 
    UNDO_SQUARE 
}
