package models.checkers;

import java.util.List;
import models.metabg.Event;
import models.metabg.GameState;
import models.metabg.IGameLogic;
import models.metabg.Result;
import models.metabg.Result.ResultType;

public class CheckersLogic implements IGameLogic
{
    @Override
    public Result processEvent (GameState state, Event event) throws Exception
    {
        CheckersGameState gameState = (CheckersGameState) state; 
        EventType eventType = (EventType) event.getType();
        
        switch (eventType) {
            case CheckerSelected: return selectChecker(gameState, event);                
            case SquareSelected: return selectSquare(gameState, event);
            case TurnEnded: return endTurn(gameState, event);
            case UndoCheckerRequested: return undoChecker(gameState, event);
            case UndoSquareRequested: return undoSquare(gameState, event);
            default: throw new IllegalArgumentException("Invalid event type received: " + event);
        }
    }

    private Result selectChecker (CheckersGameState state, Event event) throws Exception
    {
        // keep track of the selected checker 
        Checker checker = state.getCheckerById(event.getValue());
        state.addSelection(event, checker);
        
        // highlight the selected checker for both players to see
        state.getUILayer(CheckersGameState.CHECKERS_LAYER).getRegion(event.getValue()).setHighlightColor("white");
        
        // add the next expected action: the same player must now select a square to move to or undo their selection
        state.addAction(ActionType.SelectSquare, event.getPlayerNum());

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result selectSquare (CheckersGameState state, Event event) throws Exception
    {
        // pull out some data we are going to need from the game state and the current event
        Event firstEvent = state.getFirstSelection().getEvent(), lastEvent = state.getLastSelection().getEvent();
        Checker checker = state.getCheckerById(firstEvent.getValue());
        int currentPosition, newPosition = Integer.valueOf(event.getValue());

        // determine the current position of the selected checker
        EventType eventType = (EventType) lastEvent.getType();
        switch (eventType) {
            case CheckerSelected: currentPosition = state.getCheckerById(lastEvent.getValue()).getPosition(); break;
            case SquareSelected: currentPosition = Integer.valueOf(lastEvent.getValue()); break;
            default: throw new IllegalStateException("Unexpected event found in game state");
        }

        // a jumped checker indicates this is a jump (a null jumped checker indicates a non-jump move)
        Checker jumpedChecker = state.determineJumpedChecker(currentPosition, newPosition);
        if (jumpedChecker == null)
        {
            // remove the highlighting around the moving checker's initial location            
            state.getUILayer(CheckersGameState.CHECKERS_LAYER).getRegion(checker.getId()).clearHighlightColor();
            
            // move the checker being moved and check for king promotion
            state.moveChecker(checker, newPosition);
            state.checkForKingPromotion(checker);
            
            // check for victory condition            
            int nextPlayerTurn = (event.getPlayerNum() == Checker.BLACK) ? Checker.RED : Checker.BLACK;
            if (!state.hasAnyValidMoves(nextPlayerTurn))
                return new Result(ResultType.GAME_OVER, event.getPlayerNum() == Checker.BLACK ? "Black wins!" : "Red wins!");
            
            // set up the next player's turn 
            state.addAction(ActionType.SelectChecker, nextPlayerTurn);
        }
        else
        {
            // update the game state with the jump
            state.addSelection(event, jumpedChecker);
            
            // highlight the selected grid square
            state.getUILayer(CheckersGameState.BOARD_LAYER).getRegion(event.getValue()).setHighlightColor("yellow");
            
            // set up the next player's turn                        
            state.addAction(ActionType.SelectSquareOrEnd, event.getPlayerNum());
        }
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }

    private Result endTurn (CheckersGameState state, Event event)
    {
        // get the selections from the game state and determine the moving checker
        List<Selection> selections = state.getSelections();
        Checker movingChecker = selections.get(0).getChecker();
        
        // remove the highlighting around the moving checker's initial location
        state.getUILayer(CheckersGameState.CHECKERS_LAYER).getRegion(movingChecker.getId()).clearHighlightColor();
        
        // move the checker being moved and check for king promotion
        int finalPosition = Integer.valueOf(state.getLastSelection().getEvent().getValue());
        state.moveChecker(movingChecker, finalPosition);
        state.checkForKingPromotion(movingChecker);
        
        // for each jump, remove the jumped checker and remove the relevant highlighted square
        for (Selection selection : selections) {
            if (selection.getEvent().getType() == EventType.SquareSelected) {
                Checker jumpedChecker = selection.getChecker();
                state.removeChecker(jumpedChecker);
                state.getUILayer(CheckersGameState.CHECKERS_LAYER).removeSprite(jumpedChecker.getId());
                state.getUILayer(CheckersGameState.BOARD_LAYER).getRegion(selection.getEvent().getValue()).clearHighlightColor();
            }
        }
        
        // check for victory condition
        int nextPlayerTurn = (event.getPlayerNum() == Checker.BLACK) ? Checker.RED : Checker.BLACK;
        if (!state.hasAnyValidMoves(nextPlayerTurn))
            return new Result(ResultType.GAME_OVER, event.getPlayerNum() == Checker.BLACK ? "Black wins!" : "Red wins!");

        // set up the next player's turn
        state.addAction(ActionType.SelectChecker, nextPlayerTurn);

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result undoChecker (CheckersGameState state, Event event) throws Exception
    {
        // remove the existing highlighting 
        String undoCheckerId = state.getFirstSelection().getEvent().getValue();
        state.getUILayer(CheckersGameState.CHECKERS_LAYER).getRegion(undoCheckerId).clearHighlightColor();
        
        // prompt the player to select a different checker
        state.addAction(ActionType.SelectChecker, event.getPlayerNum());

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }

    private Result undoSquare (CheckersGameState state, Event event) throws Exception
    {
        // remove the existing highlighted square and the most recent selection
        String undoSquareId = state.getLastSelection().getEvent().getValue();
        state.getUILayer(CheckersGameState.BOARD_LAYER).getRegion(undoSquareId).clearHighlightColor();
        state.removeLastSelection();
        
        // prompt the player to complete his/her turn
        if (state.getSelections().size() == 1)
            state.addAction(ActionType.SelectSquare, event.getPlayerNum());
        else
            state.addAction(ActionType.SelectSquareOrEnd, event.getPlayerNum());
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
}
