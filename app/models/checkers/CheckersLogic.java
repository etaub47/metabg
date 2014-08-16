package models.checkers;

import java.util.List;
import models.metabg.Action;
import models.metabg.Event;
import models.metabg.GameState;
import models.metabg.IGameLogic;
import models.metabg.Option;
import models.metabg.Result;
import models.metabg.Result.ResultType;
import models.metabg.Sequence;
import models.metabg.Step;

public class CheckersLogic implements IGameLogic
{
    @Override
    public Result processEvent (GameState state, Event event) throws Exception
    {
        CheckersGameState gameState = (CheckersGameState) state; 
        EventType eventType = (EventType) event.getType();
        
        switch (eventType) {
            case SELECT_CHECKER: return selectChecker(gameState, event);                
            case SELECT_SQUARE: return selectSquare(gameState, event);
            case END_TURN: return endTurn(gameState, event);
            case UNDO_CHECKER: return undoChecker(gameState, event);
            case UNDO_SQUARE: return undoSquare(gameState, event);
            default: throw new IllegalArgumentException("Invalid event type received: " + event);
        }
    }
    
    private Result selectChecker (CheckersGameState state, Event event) throws Exception
    {
        // validate move
        Checker checker = state.getCheckerById(event.getValue());
        if (checker.getOwner() != event.getPlayerNum())
            return new Result(ResultType.ERROR, CheckersConstants.ERROR_WRONG_CHECKER);
        
        // start a new sequence to keep track of the selected checker 
        Sequence sequence = state.createSequence(CheckersConstants.SEQUENCE_ID, CheckersConstants.SEQUENCE_TYPE);
        sequence.addStep(event, checker);
        
        // highlight the selected checker for both players to see
        state.getUILayer(CheckersConstants.CHECKERS_LAYER).getRegion(event.getValue()).setHighlightColor("white");
        
        // add the next expected action: the same player must now select a square to move to or undo their selection
        state.addAction(new Action.Builder().player(event.getPlayerNum()).prompt(CheckersConstants.PROMPT_SELECT_SQUARE)
            .option(EventType.SELECT_SQUARE, Option.Category.TableClick, CheckersConstants.BOARD_LAYER)
            .option(EventType.UNDO_CHECKER, Option.Category.Undo)            
            .build());

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result selectSquare (CheckersGameState state, Event event) throws Exception
    {
        // pull out some data we are going to need from the existing sequence and the current event
        Sequence sequence = state.getSequence(CheckersConstants.SEQUENCE_ID);
        Event firstEvent = sequence.getFirstStep().getEvent(), lastEvent = sequence.getLastStep().getEvent();
        Checker checker = state.getCheckerById(firstEvent.getValue());
        int currentPosition, newPosition;
        boolean mustJump = false;

        // determine the current position of the selected checker
        EventType eventType = (EventType) lastEvent.getType();
        switch (eventType) {
            case SELECT_CHECKER: currentPosition = state.getCheckerById(lastEvent.getValue()).getPosition(); break;
            case SELECT_SQUARE: currentPosition = Integer.valueOf(lastEvent.getValue()); mustJump = true; break;
            default: throw new IllegalStateException("Unexpected event found in sequence");
        }

        // validate the move
        newPosition = Integer.valueOf(event.getValue());
        if (!isMoveValid(state, checker, currentPosition, newPosition, mustJump))
            return new Result(ResultType.ERROR, CheckersConstants.ERROR_INVALID_MOVE);

        // now that the move has been validated, a jumped checker indicates this is a valid jump (null is a non-jump move)
        Checker jumpedChecker = determineJumpedChecker(state, currentPosition, newPosition);
        if (jumpedChecker == null)
        {
            // remove the sequence and the highlighting around the moving checker's initial location            
            state.removeSequence(CheckersConstants.SEQUENCE_ID);
            state.getUILayer(CheckersConstants.CHECKERS_LAYER).getRegion(checker.getId()).clearHighlightColor();
            
            // move the checker being moved and check for king promotion
            state.moveChecker(checker, newPosition);
            checkForKingPromotion(state, checker);
            
            // check for victory condition            
            int nextPlayerTurn = (event.getPlayerNum() == Checker.BLACK) ? Checker.RED : Checker.BLACK;
            if (!hasAnyValidMoves(state, nextPlayerTurn))
                return new Result(ResultType.GAME_OVER, event.getPlayerNum() == Checker.BLACK ? "Black wins!" : "Red wins!");
            
            // set up the next player's turn 
            state.addAction(new Action.Builder().player(nextPlayerTurn).prompt(CheckersConstants.PROMPT_SELECT_CHECKER)
                .option(EventType.SELECT_CHECKER, Option.Category.TableClick, CheckersConstants.CHECKERS_LAYER)
                .build());
        }
        else
        {
            // update the sequence
            sequence.addStep(event, jumpedChecker);
            
            // highlight the selected grid square
            state.getUILayer(CheckersConstants.BOARD_LAYER).getRegion(event.getValue()).setHighlightColor("yellow");
            
            // set up the next player's turn                        
            state.addAction(new Action.Builder().player(event.getPlayerNum()).prompt(CheckersConstants.PROMPT_SELECT_SQUARE_OR_END)
                .option(EventType.SELECT_SQUARE, Option.Category.TableClick, CheckersConstants.BOARD_LAYER)
                .option(EventType.END_TURN, Option.Category.Confirm)
                .option(EventType.UNDO_SQUARE, Option.Category.Undo)
                .build());
        }
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }

    private Result endTurn (CheckersGameState state, Event event)
    {
        // get the steps from the sequence and determine the moving checker
        Sequence sequence = state.getSequence(CheckersConstants.SEQUENCE_ID);
        List<Step> steps = sequence.getSteps();
        Checker movingChecker = (Checker) sequence.getFirstStep().getData();
        
        // remove the sequence and the highlighting around the moving checker's initial location
        state.removeSequence(CheckersConstants.SEQUENCE_ID);
        state.getUILayer(CheckersConstants.CHECKERS_LAYER).getRegion(movingChecker.getId()).clearHighlightColor();
        
        // move the checker being moved and check for king promotion
        int finalPosition = Integer.valueOf(sequence.getLastStep().getEvent().getValue());
        state.moveChecker(movingChecker, finalPosition);
        checkForKingPromotion(state, movingChecker);
        
        // for each jump, remove the jumped checker and remove the relevant highlighted square
        for (Step step : steps) {
            if (step.getEvent().getType() == EventType.SELECT_SQUARE) {
                Checker jumpedChecker = (Checker)step.getData();
                state.removeChecker(jumpedChecker);
                state.getUILayer(CheckersConstants.CHECKERS_LAYER).removeSprite(jumpedChecker.getId());
                state.getUILayer(CheckersConstants.BOARD_LAYER).getRegion(step.getEvent().getValue()).clearHighlightColor();
            }
        }
        
        // check for victory condition
        int nextPlayerTurn = (event.getPlayerNum() == Checker.BLACK) ? Checker.RED : Checker.BLACK;
        if (!hasAnyValidMoves(state, nextPlayerTurn))
            return new Result(ResultType.GAME_OVER, event.getPlayerNum() == Checker.BLACK ? "Black wins!" : "Red wins!");

        // set up the next player's turn
        state.addAction(new Action.Builder().player(nextPlayerTurn).prompt(CheckersConstants.PROMPT_SELECT_CHECKER)
            .option(EventType.SELECT_CHECKER, Option.Category.TableClick, CheckersConstants.CHECKERS_LAYER)
            .build());

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result undoChecker (CheckersGameState state, Event event) throws Exception
    {
        // remove the existing highlighting, and remove the sequence altogether 
        String undoCheckerId = state.getSequence(CheckersConstants.SEQUENCE_ID).getFirstStep().getEvent().getValue();
        state.getUILayer(CheckersConstants.CHECKERS_LAYER).getRegion(undoCheckerId).clearHighlightColor();
        state.removeSequence(CheckersConstants.SEQUENCE_ID);
        
        // prompt the player to select a different checker
        state.addAction(new Action.Builder().player(event.getPlayerNum()).prompt(CheckersConstants.PROMPT_SELECT_CHECKER)
            .option(EventType.SELECT_CHECKER, Option.Category.TableClick, CheckersConstants.CHECKERS_LAYER)
            .build());

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }

    private Result undoSquare (CheckersGameState state, Event event) throws Exception
    {
        // remove the existing highlighted square and the most recent sequence step
        Sequence sequence = state.getSequence(CheckersConstants.SEQUENCE_ID);
        String undoSquareId = sequence.getLastStep().getEvent().getValue();
        state.getUILayer(CheckersConstants.BOARD_LAYER).getRegion(undoSquareId).clearHighlightColor();
        sequence.removeLastStep();
        
        // prompt the player to complete his/her turn
        if (sequence.getSteps().size() == 1) {
            state.addAction(new Action.Builder().player(event.getPlayerNum()).prompt(CheckersConstants.PROMPT_SELECT_SQUARE)
                .option(EventType.SELECT_SQUARE, Option.Category.TableClick, CheckersConstants.BOARD_LAYER)
                .option(EventType.UNDO_CHECKER, Option.Category.Undo)
                .build());            
        }
        else {
            state.addAction(new Action.Builder().player(event.getPlayerNum()).prompt(CheckersConstants.PROMPT_SELECT_SQUARE_OR_END)
                .option(EventType.SELECT_SQUARE, Option.Category.TableClick, CheckersConstants.BOARD_LAYER)
                .option(EventType.END_TURN, Option.Category.Confirm)
                .option(EventType.UNDO_SQUARE, Option.Category.Undo)
                .build());                        
        }
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private boolean isMoveValid (CheckersGameState state, Checker checker, int currentPosition, int newPosition, boolean mustJump)
    {
        boolean rowACEG = (currentPosition / 4) % 2 == 0; // the checker is in row A, C, E, or G
        int moveValue = newPosition - currentPosition;
        int rowMoveValue = (newPosition / 4) - (currentPosition / 4);
        boolean destinationEmpty = (state.getCheckerByPosition(newPosition) == null);
        
        if (rowMoveValue == -1 && !mustJump && (checker.isRed() || checker.isKing()) && destinationEmpty &&
            (moveValue == -4 || (moveValue == -3 && rowACEG) || (moveValue == -5 && !rowACEG))) 
        {
            // move one square toward the top of the board
            return true;            
        }
        else if (rowMoveValue == 1 && !mustJump && (checker.isBlack() || checker.isKing()) && destinationEmpty &&
                 (moveValue == 4 || (moveValue == 5 && rowACEG) || (moveValue == 3 && !rowACEG)))
        {
            // move one square toward the bottom of the board
            return true;            
        }
        else if (rowMoveValue == -2 && (checker.isRed() || checker.isKing()) && destinationEmpty && 
                 (moveValue == -7 || moveValue == -9))
        {
            // jump toward the top of the board
            Checker jumpedChecker = determineJumpedChecker(state, currentPosition, newPosition); 
            return (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }
        else if (rowMoveValue == 2 && (checker.isBlack() || checker.isKing()) && destinationEmpty &&
                 (moveValue == 7 || moveValue == 9))
        {
            // jump toward the bottom of the board
            Checker jumpedChecker = determineJumpedChecker(state, currentPosition, newPosition); 
            return (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }
        else
        {
            return false;
        }
    }
    
    // helper function to determine the checker being jumped
    private Checker determineJumpedChecker (CheckersGameState state, int currentPosition, int newPosition) 
    {
        boolean rowACEG = (currentPosition / 4) % 2 == 0; // the checker is in row A, C, E, or G
        int moveValue = newPosition - currentPosition;
        
        switch (moveValue) {
            case -7: return state.getCheckerByPosition(currentPosition + (rowACEG ? -3 : -4));
            case -9: return state.getCheckerByPosition(currentPosition + (rowACEG ? -4 : -5));
            case  7: return state.getCheckerByPosition(currentPosition + (rowACEG ?  4 :  3));
            case  9: return state.getCheckerByPosition(currentPosition + (rowACEG ?  5 :  4));
            default: return null;
        }
    }

    // helper function to determine if a checker should be promoted to a king
    private void checkForKingPromotion (CheckersGameState state, Checker checker) {
        int position = checker.getPosition();
        if (!checker.isKing() && ((checker.isBlack() && (position / 4) == 7) || (checker.isRed() && (position / 4) == 0)))
            state.promoteCheckerToKing(checker);
    }
    
    // helper function to determine if a particular checker has at least one valid move it can make
    private boolean hasValidMove (CheckersGameState state, Checker checker) {
        int[] movesToCheck = new int[]{ 3, 4, 5, 7, 9, -3, -4, -5, -7, -9 };
        for (int m = 0; m < movesToCheck.length; m++)
            if (isMoveValid(state, checker, checker.getPosition(), checker.getPosition() + movesToCheck[m], false))
                return true;        
        return false;
    }
    
    // helper function to determine if a player has at least one valid move he/she can make
    private boolean hasAnyValidMoves (CheckersGameState state, int playerNum) {
        for (Checker checker : state.getAllCheckers())
            if (checker.getOwner() == playerNum && hasValidMove(state, checker))
                return true;
        return false;        
    }
}
