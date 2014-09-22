package models.dominion;

import models.metabg.CardStack;
import models.metabg.Event;
import models.metabg.Event.IEventType;
import models.metabg.GameState;

public enum EventType implements IEventType
{
    PLAY_ACTION_CARD {
        @Override public String validate (GameState state, Event event) {
            DominionGameState gameState = (DominionGameState) state;
            if (!isHand(event.getValue(), event.getPlayerNum()))
                return "You must select a card from your hand, or press CONFIRM to decline.";
            IDominionCard card = gameState.getPlayerData(event.getPlayerNum()).getCardByRegionId(event.getValue());
            if (!card.isActionCard())
                return "You must select an action card from your hand, or press CONFIRM to decline.";            
            return null;
        }
    },
    
    END_RESPONSE,    
    END_ACTION_CARD,
    END_ACTION_PHASE,
    
    BUY_CARD {
        @Override public String validate (GameState state, Event event) {
            DominionGameState gameState = (DominionGameState) state;
            if (!isSupplyPile(event.getValue()))
                return "You must select a supply pile to buy a card, or press CONFIRM to decline.";
            CardStack<IDominionCard> supplyPile = gameState.getSupplyPileByRegionId(event.getValue());
            if (supplyPile.isEmpty())
                return "That supply pile is empty; please select a different pile, or press CONFIRM to decline.";
            if (supplyPile.peekTopCard().getCost() > gameState.getNumCoins())
                return "You do not have enough coins; please select a different pile, or press CONFIRM to decline.";
            return null;
        }
    },
    
    END_BUY_PHASE,

    BUREAUCRAT_REVEAL_VICTORY_CARD, // card from hand, not undoable 
    CELLAR_DISCARD_CARD,            // card from hand, undoable
    CHANCELLOR_DISCARD_DECK,        // 1, not undoable
    CHANCELLOR_DECLINE,             // 2, not undoable
    CHAPEL_TRASH_CARD,              // card from hand, undoable
    GAIN_CARD,                      // supply pile, undoable
    LIBRARY_KEEP_CARD,              // 1, not undoable
    LIBRARY_SET_ASIDE_CARD,         // 2, not undoable
    MILITIA_DISCARD_CARD,           // card from hand, not undoable
    MINE_TRASH_CARD,                // card from hand, undoable
    MINE_GAIN_CARD,                 // supply pile, undoable
    MOAT_REVEAL_REACTION,           // 1, not undoable 
    MOAT_DECLINE_REACTION,          // 2, not undoable
    REMODEL_TRASH_CARD,             // card from hand, undoable
    SPY_DISCARD_CARD,               // 1, not undoable
    SPY_KEEP_CARD,                  // 2, not undoable
    THIEF_CHOOSE_CARD,              // card from revealed, undoable
    THIEF_TRASH_CARD,               // 1, not undoable
    THIEF_TAKE_CARD,                // 2, not undoable
    THRONE_ROOM_PLAY_CARD,          // card from hand, maybe undoable

    UNDO_SELECT_CARD;               // cellar, chapel, militia
    //UNDO_
    
    @Override
    public String validate (GameState state, Event event) { 
        return null; 
    }
    
    private static boolean isSupplyPile (String regionId) { 
        return regionId != null && (regionId.startsWith(DominionGameState.KINGDOM) || regionId.startsWith(DominionGameState.TREASURE) ||
            regionId.startsWith(DominionGameState.VICTORY));
    }
    
    private static boolean isHand (String regionId, int player) { 
        return regionId != null && regionId.startsWith(DominionGameState.HAND + player);
    }
    
    private static boolean isRevealedCard (String regionId, int player) { 
        return regionId != null && regionId.startsWith(DominionGameState.REVEALED + player);
    }
}
