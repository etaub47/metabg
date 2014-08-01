package models.dominion;

import models.metabg.Event.IEventType;

public enum EventType implements IEventType
{
    PLAY_ACTION_CARD, // card from hand, maybe undoable
    END_RESPONSE,     // confirm, not undoable (militia)
    END_ACTION_CARD,  // confirm, maybe undoable    
    END_ACTION_PHASE, // confirm, not undoable
    BUY_CARD,         // supply pile, undoable
    END_BUY_PHASE,    // confirm, not undoable

    BUREAUCRAT_REVEAL_VICTORY_CARD, // card from hand, not undoable 
    CELLAR_DISCARD_CARD,            // card from hand, undoable
    CHANCELLOR_DISCARD_DECK,        // 1, not undoable
    CHANCELLOR_DECLINE,             // 2, not undoable
    CHAPEL_TRASH_CARD,              // card from hand, undoable
    FEAST_GAIN_CARD,                // supply pile, undoable
    LIBRARY_SET_ASIDE_CARD,         // 1, not undoable
    LIBRARY_KEEP_CARD,              // 2, not undoable
    MILITIA_DISCARD_CARD,           // card from hand, not undoable
    MINE_TRASH_CARD,                // card from hand, undoable
    MINE_GAIN_CARD,                 // supply pile, undoable
    MOAT_REVEAL_REACTION,           // 1, not undoable 
    MOAT_DECLINE_REACTION,          // 2, not undoable
    REMODEL_TRASH_CARD,             // card from hand, undoable
    REMODEL_GAIN_CARD,              // supply pile, undoable
    SPY_DISCARD_CARD,               // 1, not undoable
    SPY_KEEP_CARD,                  // 2, not undoable
    THIEF_CHOOSE_CARD,              // card from revealed, undoable
    THIEF_TRASH_CARD,               // 1, not undoable
    THIEF_TAKE_CARD,                // 2, not undoable
    THRONE_ROOM_PLAY_CARD,        // card from hand, maybe undoable
    WORKSHOP_GAIN_CARD,             // supply pile, undoable

    UNDO_SELECT_CARD                // cellar, chapel, militia
    //UNDO_
}
