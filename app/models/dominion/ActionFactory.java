package models.dominion;

import models.metabg.Action;
import models.metabg.IAction;
import models.metabg.Option;

public class ActionFactory
{
    // layer constants
    public static final int BOARD_LAYER = 0;
    public static final int CARD_LAYER = 1;
    
    public enum PlayerAction { CellarAction, ChancellorAction, ChapelAction, FeastAction, MilitiaAction,
        MineTrashAction, MineGainAction, RemodelTrashAction }
    
    private static ActionFactory instance = new ActionFactory();
    public static ActionFactory getInstance () { return instance; }
    protected ActionFactory () { }
    
    public IAction createAction (PlayerAction action, int playerNum) {
        return createAction(action, playerNum, -1);
    }
    
    public IAction createAction (PlayerAction action, int playerNum, int value) {
        switch (action) 
        {            
            case CellarAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Select a card to discard, or press CONFIRM when you are done.")
                    .option(EventType.CELLAR_DISCARD_CARD, Option.Category.TableClick, CARD_LAYER)
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
                    .option(EventType.CHAPEL_TRASH_CARD, Option.Category.TableClick, CARD_LAYER)
                    .option(EventType.END_ACTION_CARD, Option.Category.Confirm)
                    .option(EventType.UNDO_SELECT_CARD, Option.Category.Undo)
                    .build();
                
            case FeastAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Gain a card costing up to 5.")
                    .option(EventType.FEAST_GAIN_CARD, Option.Category.TableClick, CARD_LAYER)
                    .build();
                
            case MilitiaAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Discard down to 3 cards in your hand.")
                    .option(EventType.MILITIA_DISCARD_CARD, Option.Category.TableClick, CARD_LAYER)
                    .build();
                
            case MineTrashAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Trash a treasure card from your hand.")
                    .option(EventType.MINE_TRASH_CARD, Option.Category.TableClick, CARD_LAYER)
                    .build();
            
            case MineGainAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Gain a treasure card costing up to " + value + "; put it into your hand.")
                    .option(EventType.MINE_GAIN_CARD, Option.Category.TableClick, CARD_LAYER)
                    .build();
                
            case RemodelTrashAction:
                return new Action.Builder().player(playerNum)
                    .prompt("Trash a card from your hand.")
                    .option(EventType.REMODEL_TRASH_CARD, Option.Category.TableClick, CARD_LAYER)
                    .build();
            
            default: 
                return null;
        }
    }
    
}
