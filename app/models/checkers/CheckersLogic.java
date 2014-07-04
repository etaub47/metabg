package models.checkers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.metabg.Action;
import models.metabg.Event;
import models.metabg.Event.IEventType;
import models.metabg.GameState;
import models.metabg.IGameLogic;
import models.metabg.Layer;
import models.metabg.Option;
import models.metabg.Region;
import models.metabg.Result;
import models.metabg.Result.ResultType;
import models.metabg.Sequence;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import models.metabg.Step;
import utils.SpriteUtils;

public class CheckersLogic implements IGameLogic
{
    // event types
    public enum EventType implements IEventType { SELECT_CHECKER, SELECT_SQUARE, END_TURN, UNDO_CHECKER, UNDO_SQUARE };

    // layer constants
    public static final int BOARD_LAYER = 0;
    public static final int CHECKERS_LAYER = 1;
    
    // sequence constants
    public static final String SEQUENCE = "Sequence";
    
    // prompts    
    public static final String PROMPT_SELECT_CHECKER = "Please select a checker to move.";
    public static final String PROMPT_SELECT_SQUARE = "Please select a square to move or jump to.";
    public static final String PROMPT_SELECT_SQUARE_OR_END = "Please select another square to jump to, or end your turn.";
    
    // errors
    public static final String ERROR_WRONG_CHECKER = "You may only move one of your own checkers.";
    public static final String ERROR_INVALID_MOVE = "That is not a valid move.";
    
    // state variables
    private Map<String, Checker> checkersById;
    private Map<Integer, Checker> checkersByPosition;
    
    // other instance variables
    private int tableX, tableY;
    
    // constructor
    public CheckersLogic (int numPlayers) {
        checkersById = new HashMap<>();
        checkersByPosition = new HashMap<>();
    }
    
    @Override
    public void init (GameState state) 
    {
        // initialize board
        Layer layer0 = state.getUILayer(BOARD_LAYER);
        SpriteUtils utils = SpriteUtils.getInstance();
        tableX = utils.centerSpriteOnTableX(940); 
        tableY = utils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite("board", "checkerBoard", tableX, tableY, 0, 940, 948, 
            Side.Front, Orientation.Normal);
        layer0.addSprite(boardSprite);        
        for (int s = 0; s < 32; s++)
            layer0.addRegion(new Region(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));
        
        // initialize checkers
        Layer layer1 = state.getUILayer(CHECKERS_LAYER);
        for (int s = 0; s < 12; s++) 
        {
            int position = s;
            Sprite blackCheckerSprite = new Sprite("black" + s, "blackChecker", toPixelX(tableX, position), 
                toPixelY(tableY, position), 1, 90, 93, Side.Front, Orientation.Normal);            
            layer1.addClickableSprite(blackCheckerSprite);
            Checker blackChecker = new Checker("black" + s, Checker.BLACK, position);
            checkersById.put(blackChecker.getId(), blackChecker);
            checkersByPosition.put(blackChecker.getPosition(), blackChecker);

            position = 20 + s;
            Sprite redCheckerSprite = new Sprite("red" + s, "redChecker", toPixelX(tableX, position), 
                toPixelY(tableY, position), 1, 90, 88, Side.Front, Orientation.Normal); 
            layer1.addClickableSprite(redCheckerSprite);
            Checker redChecker = new Checker("red" + s, Checker.RED, position);
            checkersById.put(redChecker.getId(), redChecker);
            checkersByPosition.put(redChecker.getPosition(), redChecker);
        }
        
        // initial expected action: red to select checker
        state.addAction(new Action(Checker.RED, PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, 
            Option.Category.TableClick, CHECKERS_LAYER));
    }
    
    @Override
    public Result processEvent (GameState state, Event event) throws Exception
    {
        // TODO: handle undo and cancel
        
        EventType eventType = (EventType) event.getType();
        switch (eventType) {
            case SELECT_CHECKER: return selectChecker(state, event);                
            case SELECT_SQUARE: return selectSquare(state, event);
            case END_TURN: return endTurn(state, event);
            case UNDO_CHECKER: return undoChecker(state, event);
            case UNDO_SQUARE: return undoSquare(state, event);
            default: throw new IllegalArgumentException("Invalid event type received: " + event);
        }
    }
    
    private Result selectChecker (GameState state, Event event) throws Exception
    {
        // validate move
        Checker checker = checkersById.get(event.getValue());
        if (checker.getOwner() != event.getPlayerNum())
            return new Result(ResultType.ERROR, ERROR_WRONG_CHECKER);
        
        // start a new sequence to keep track of the selected checker 
        Sequence sequence = state.getOrCreateSequence(SEQUENCE);
        sequence.addStep(event, checker);
        
        // highlight the selected checker for both players to see
        state.getUILayer(CHECKERS_LAYER).getRegion(event.getValue()).setHighlightColor("white");
        
        // add the next expected action: the same player must now select a square to move to or undo their selection
        Option option1 = new Option(EventType.SELECT_SQUARE, Option.Category.TableClick, BOARD_LAYER);
        Option option2 = new Option(EventType.UNDO_CHECKER, Option.Category.Undo);
        state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE, option1, option2));

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result selectSquare (GameState state, Event event) throws Exception
    {
        // pull out some data we are going to need from the existing sequence and the current event
        Sequence sequence = state.getOrCreateSequence(SEQUENCE);
        Event firstEvent = sequence.getFirstStep().getEvent(), lastEvent = sequence.getLastStep().getEvent();
        Checker checker = checkersById.get(firstEvent.getValue()), jumpedChecker = null;
        int currentPosition, newPosition = Integer.valueOf(event.getValue());
        boolean mustJump = false, validMove = false;

        // determine the current position of the selected checker and a few other useful pieces of information
        EventType eventType = (EventType) lastEvent.getType();
        switch (eventType) {
            case SELECT_CHECKER: currentPosition = checkersById.get(lastEvent.getValue()).getPosition(); break;
            case SELECT_SQUARE: currentPosition = Integer.valueOf(lastEvent.getValue()); mustJump = true; break;
            default: throw new IllegalStateException("Unexpected event found in sequence");
        }
        boolean rowACEG = (currentPosition / 4) % 2 == 0; // the checker is in row A, C, E, or G
        int moveValue = newPosition - currentPosition;
        int rowMoveValue = (newPosition / 4) - (currentPosition / 4);
        boolean destinationEmpty = (checkersByPosition.get(newPosition) == null);
        
        // validate the move
        if (rowMoveValue == -1 && !mustJump && (checker.isRed() || checker.isKing()) && destinationEmpty &&
            (moveValue == -4 || (moveValue == -3 && rowACEG) || (moveValue == -5 && !rowACEG))) 
        {
            // move one square toward the top of the board
            validMove = true;            
        }
        else if (rowMoveValue == 1 && !mustJump && (checker.isBlack() || checker.isKing()) && destinationEmpty &&
                 (moveValue == 4 || (moveValue == 5 && rowACEG) || (moveValue == 3 && !rowACEG)))
        {
            // move one square toward the bottom of the board
            validMove = true;            
        }
        else if (rowMoveValue == -2 && (checker.isRed() || checker.isKing()) && destinationEmpty && 
                 (moveValue == -7 || moveValue == -9))
        {
            // jump toward the top of the board
            int jumpedCheckerRelativePosition = (rowACEG && moveValue == -7) ? -3 : (!rowACEG && moveValue == -9) ? -5 : -4;
            jumpedChecker = checkersByPosition.get(currentPosition + jumpedCheckerRelativePosition);
            validMove = (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }
        else if (rowMoveValue == 2 && (checker.isBlack() || checker.isKing()) && destinationEmpty &&
                 (moveValue == 7 || moveValue == 9))
        {
            // jump toward the bottom of the board
            int jumpedCheckerRelativePosition = (rowACEG && moveValue == 9) ? 5 : (!rowACEG && moveValue == 7) ? 3 : 4;
            jumpedChecker = checkersByPosition.get(currentPosition + jumpedCheckerRelativePosition);
            validMove = (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }        
        if (!validMove)
            return new Result(ResultType.ERROR, ERROR_INVALID_MOVE);
        
        // if this is not a jump, the player's turn is over: remove the sequence and highlight, move the checker, and change turns
        if (jumpedChecker == null)
        {
            state.removeSequence(SEQUENCE);
            state.getUILayer(CHECKERS_LAYER).getRegion(checker.getId()).clearHighlightColor();
            moveChecker(checker, newPosition, state.getUILayer(CHECKERS_LAYER));
            checkForKingPromotion(checker, state.getUILayer(CHECKERS_LAYER));
            int nextPlayerTurn = event.getPlayerNum() == Checker.RED ? Checker.BLACK : Checker.RED;
            state.addAction(new Action(nextPlayerTurn, PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, 
                Option.Category.TableClick, CHECKERS_LAYER));
        }
        else // a jump is not the end of the turn; let's update the sequence, highlight the grid square, and move on
        {
            sequence.addStep(event, jumpedChecker);
            state.getUILayer(BOARD_LAYER).getRegion(event.getValue()).setHighlightColor("yellow");
            Option option1 = new Option(EventType.SELECT_SQUARE, Option.Category.TableClick, BOARD_LAYER);
            Option option2 = new Option(EventType.END_TURN, Option.Category.Confirm);
            Option option3 = new Option(EventType.UNDO_SQUARE, Option.Category.Undo);
            state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE_OR_END, option1, option2, option3));
        }
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result endTurn (GameState state, Event event)
    {
        // get the steps from the sequence and determine the moving checker
        Sequence sequence = state.getOrCreateSequence(SEQUENCE);
        List<Step> steps = sequence.getSteps();
        Checker movingChecker = (Checker) sequence.getFirstStep().getData();
        
        // remove the sequence and the highlighting around the moving checker's initial location
        state.removeSequence(SEQUENCE);
        state.getUILayer(CHECKERS_LAYER).getRegion(movingChecker.getId()).clearHighlightColor();
        
        // move the checker being moved
        int finalPosition = Integer.valueOf(sequence.getLastStep().getEvent().getValue());
        moveChecker(movingChecker, finalPosition, state.getUILayer(CHECKERS_LAYER));
        checkForKingPromotion(movingChecker, state.getUILayer(CHECKERS_LAYER));
        
        // for each jump, remove the jumped checker and remove the relevant highlighted square
        for (Step step : steps) {
            if (step.getEvent().getType() == EventType.SELECT_SQUARE) {
                removeChecker((Checker)step.getData(), state.getUILayer(CHECKERS_LAYER));
                state.getUILayer(BOARD_LAYER).getRegion(step.getEvent().getValue()).clearHighlightColor();
            }
        }
        
        // check for victory condition
        boolean foundOpponentChecker = false;
        for (Checker checker : checkersById.values())
            if (checker.getOwner() != event.getPlayerNum())
                foundOpponentChecker = true;
        if (!foundOpponentChecker) {            
            String gameOverMessage = (event.getPlayerNum() == Checker.BLACK ? "Black wins!" : "Red wins!");
            return new Result(ResultType.GAME_OVER, gameOverMessage);
        }

        // set up the next player's turn
        int nextPlayerTurn = event.getPlayerNum() == Checker.RED ? Checker.BLACK : Checker.RED;
        state.addAction(new Action(nextPlayerTurn, PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, 
            Option.Category.TableClick, CHECKERS_LAYER));

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result undoChecker (GameState state, Event event) throws Exception
    {
        // remove the existing highlighting, and remove the sequence altogether 
        String undoCheckerId = state.getOrCreateSequence(SEQUENCE).getFirstStep().getEvent().getValue();
        state.getUILayer(CHECKERS_LAYER).getRegion(undoCheckerId).clearHighlightColor();
        state.removeSequence(SEQUENCE);
        
        // prompt the player to select a different checker
        state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, 
            Option.Category.TableClick, CHECKERS_LAYER));

        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }

    private Result undoSquare (GameState state, Event event) throws Exception
    {
        // remove the existing highlighted square and the most recent sequence step
        Sequence sequence = state.getOrCreateSequence(SEQUENCE);
        String undoSquareId = sequence.getLastStep().getEvent().getValue();
        state.getUILayer(BOARD_LAYER).getRegion(undoSquareId).clearHighlightColor();
        sequence.removeLastStep();
        
        // prompt the player to complete his/her turn
        Option option1 = new Option(EventType.SELECT_SQUARE, Option.Category.TableClick, BOARD_LAYER);
        if (sequence.getSteps().size() == 1) {
            Option option2 = new Option(EventType.UNDO_CHECKER, Option.Category.Undo);
            state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE, option1, option2));            
        }
        else {
            Option option2 = new Option(EventType.END_TURN, Option.Category.Confirm);
            Option option3 = new Option(EventType.UNDO_SQUARE, Option.Category.Undo);
            state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE_OR_END, option1, option2, option3));            
        }
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }

    // helper function to move a checker
    private void moveChecker (Checker checker, int newPosition, Layer uiLayer) {
        checkersByPosition.remove(checker.getPosition());
        checker.move(newPosition);
        checkersByPosition.put(checker.getPosition(), checker);
        uiLayer.moveSprite(checker.getId(), toPixelX(tableX, newPosition), toPixelY(tableY, newPosition));
    }

    // helper function to remove a checker
    private void removeChecker (Checker checker, Layer uiLayer) {
        checkersById.remove(checker.getId());
        checkersByPosition.remove(checker.getPosition());
        uiLayer.removeSprite(checker.getId());
    }
    
    // helper function to promote a checker to king
    private void checkForKingPromotion (Checker checker, Layer uiLayer) {
        int position = checker.getPosition();
        if (!checker.isKing() && ((checker.isBlack() && (position / 4) == 7) || (checker.isRed() && (position / 4) == 0))) {
            checker.promoteToKing();
            uiLayer.removeSprite(checker.getId());
            String resource = checker.isBlack() ? "blackKing" : "redKing"; 
            int height = checker.isBlack() ? 93 : 88;
            Sprite kingSprite = new Sprite(checker.getId(), resource, toPixelX(tableX, position), toPixelY(tableY, position), 
                1, 90, height, Side.Front, Orientation.Normal);
            uiLayer.addClickableSprite(kingSprite);
        }
    }
    
    // utility functions to convert logical/board position to graphical/sprite position
    private int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    private int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }    
}
