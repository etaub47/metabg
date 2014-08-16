package models.dominion;

import models.metabg.Action;
import models.metabg.IAction;
import models.metabg.Option;

public class ActionFactory
{
    public enum PlayerAction { SelectAction, CellarAction, ChancellorAction, ChapelAction, GainCardAction, MilitiaAction,
        MineTrashAction, MineGainAction, RemodelAction, SpyAction }
    
    private DominionGameState state;
    
    public ActionFactory (DominionGameState state) {
        this.state = state;
    }
    
    public IAction createAction (PlayerAction action, int playerNum) {
        return createAction(action, playerNum, -1);
    }
    
    public IAction createAction (PlayerAction action, int playerNum, int value) {
        switch (action) 
        {
            case SelectAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Select an action card to play, or press CONFIRM to decline and skip to the buy phase.")
                    .option(EventType.PLAY_ACTION_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .option(EventType.END_ACTION_PHASE, Option.Category.Confirm)
                    .build();
            
            case CellarAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Select a card to discard, or press CONFIRM when you are done.")
                    .option(EventType.CELLAR_DISCARD_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .option(EventType.END_ACTION_CARD, Option.Category.Confirm)
                    .option(EventType.UNDO_SELECT_CARD, Option.Category.Undo)
                    .build();
            
            case ChancellorAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Press 1 to put your deck into your discard pile, or press 2 to decline.")
                    .option(EventType.CHANCELLOR_DISCARD_DECK, Option.Category.NumberPress, 1)
                    .option(EventType.CHANCELLOR_DECLINE, Option.Category.NumberPress, 2)
                    .build();

            case ChapelAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Select a card to discard, or press CONFIRM if you are done.")
                    .option(EventType.CHAPEL_TRASH_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .option(EventType.END_ACTION_CARD, Option.Category.Confirm)
                    .option(EventType.UNDO_SELECT_CARD, Option.Category.Undo)
                    .build();
                
            case GainCardAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Gain a card costing up to " + value + ".")
                    .option(EventType.GAIN_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .build();
                
            case MilitiaAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Discard down to 3 cards in your hand.")
                    .option(EventType.MILITIA_DISCARD_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .build();
                
            case MineTrashAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Trash a treasure card from your hand.")
                    .option(EventType.MINE_TRASH_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .build();
            
            case MineGainAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Gain a treasure card costing up to " + value + "; put it into your hand.")
                    .option(EventType.MINE_GAIN_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .build();
                
            case RemodelAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Trash a card from your hand.")
                    .option(EventType.REMODEL_TRASH_CARD, Option.Category.TableClick, DominionGameState.CARD_LAYER)
                    .build();
                
            case SpyAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Press 1 to have " + state.getPlayerName(value) + " discard the revealed card, or press 2 to put it back.")
                    .option(EventType.SPY_DISCARD_CARD, Option.Category.NumberPress, 1)
                    .option(EventType.SPY_KEEP_CARD, Option.Category.NumberPress, 2)
                    .build();
            
            default: 
                return null;
        }
    }
    
}
