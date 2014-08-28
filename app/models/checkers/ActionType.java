package models.checkers;

import models.metabg.Action;
import models.metabg.GameState;
import models.metabg.IActionType;
import models.metabg.Option;

public enum ActionType implements IActionType 
{
    SelectChecker {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Please select a checker to move.")
                .option(EventType.CheckerSelected, Option.Category.TableClick, CheckersGameState.CHECKERS_LAYER)
                .build();                
        }
    },

    SelectSquare {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Please select a square to move or jump to.")
                .option(EventType.SquareSelected, Option.Category.TableClick, CheckersGameState.BOARD_LAYER)
                .option(EventType.UndoCheckerRequested, Option.Category.Undo)
                .build();
        }
    }, 

    SelectSquareOrEnd {
        @Override public Action createAction (GameState state, int playerNum) {
            return new Action.Builder().player(playerNum)
                .prompt("Please select another square to jump to, or end your turn.")
                .option(EventType.SquareSelected, Option.Category.TableClick, CheckersGameState.BOARD_LAYER)
                .option(EventType.TurnEnded, Option.Category.Confirm)
                .option(EventType.UndoSquareRequested, Option.Category.Undo)
                .build();
        }            
    };

    @Override
    public abstract Action createAction (GameState state, int playerNum);
}
