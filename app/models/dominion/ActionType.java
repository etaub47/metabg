package models.dominion;

import models.metabg.Action;
import models.metabg.GameState;
import models.metabg.IActionType;
import models.metabg.Option;

public enum ActionType implements IActionType
{
    SelectAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Select an action card to play, or press CONFIRM to decline and skip to the buy phase.")
                .option(EventType.PLAY_ACTION_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .option(EventType.END_ACTION_PHASE, Option.Category.Confirm)
                .build();
        }        
    }, 
    
    BuyAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Select a supply pile to buy a card, or press CONFIRM to end your turn.")
                .option(EventType.BUY_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .option(EventType.END_BUY_PHASE, Option.Category.Confirm)
                .build();
        }        
    }, 
    
    CellarAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Select a card to discard, or press CONFIRM when you are done.")
                .option(EventType.CELLAR_DISCARD_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .option(EventType.END_ACTION_CARD, Option.Category.Confirm)
                .option(EventType.UNDO_SELECT_CARD, Option.Category.Undo)
                .build();
        }        
    }, 
    
    ChancellorAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Press 1 to put your deck into your discard pile, or press 2 to decline.")
                .option(EventType.CHANCELLOR_DISCARD_DECK, Option.Category.NumberPress, 1)
                .option(EventType.CHANCELLOR_DECLINE, Option.Category.NumberPress, 2)
                .build();
        }        
    }, 
    
    ChapelAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Select a card to discard, or press CONFIRM if you are done.")
                .option(EventType.CHAPEL_TRASH_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .option(EventType.END_ACTION_CARD, Option.Category.Confirm)
                .option(EventType.UNDO_SELECT_CARD, Option.Category.Undo)
                .build();
        }        
    }, 
    
    GainCardAction {
        @Override public Action createAction (GameState state, int playerNum) {
            DominionGameState gameState = (DominionGameState) state; 
            return new Action.Builder().player(playerNum)
                .prompt("Gain a card costing up to " + gameState.getCurrentValue() + ".")
                .option(EventType.GAIN_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .build();
        }        
    },
    
    MilitiaAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Discard down to 3 cards in your hand.")
                .option(EventType.MILITIA_DISCARD_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .build();
        }        
    }, 
    
    MineTrashAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Trash a treasure card from your hand.")
                .option(EventType.MINE_TRASH_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .build();
        }        
    }, 
    
    MineGainAction {
        @Override public Action createAction (GameState state, int playerNum) {
            DominionGameState gameState = (DominionGameState) state; 
            return new Action.Builder().player(playerNum)
                .prompt("Gain a treasure card costing up to " + gameState.getCurrentValue() + "; put it into your hand.")
                .option(EventType.MINE_GAIN_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .build();
        }        
    }, 
    
    RemodelAction {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Trash a card from your hand.")
                .option(EventType.REMODEL_TRASH_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                .build();
        }        
    }, 
    
    SpyAction {
        @Override public Action createAction (GameState state, int playerNum) {
            DominionGameState gameState = (DominionGameState) state; 
            return new Action.Builder().player(playerNum)
                .prompt("Press 1 to have " + gameState.getSelectedPlayerName() + " discard the revealed card, or press 2 to put it back.")
                .option(EventType.SPY_DISCARD_CARD, Option.Category.NumberPress, 1)
                .option(EventType.SPY_KEEP_CARD, Option.Category.NumberPress, 2)
                .build();
        }        
    };
    
    @Override
    public abstract Action createAction (GameState state, int playerNum);    
}
