package models.checkers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import models.metabg.UserInterface;
import play.Logger;
import utils.SpriteUtils;

public class CheckersLogic implements IGameLogic
{
    // event types
    public enum EventType implements IEventType { SELECT_CHECKER, SELECT_SQUARE, END_TURN, ERROR };

    // layer constants
    public static final int BOARD_LAYER = 0;
    public static final int CHECKERS_LAYER = 1;
    
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
    public void initUserInterface (UserInterface userInterface) 
    {
        // BOARD_LAYER
        Layer layer0 = userInterface.getLayer(BOARD_LAYER);
        SpriteUtils utils = SpriteUtils.getInstance();
        tableX = utils.centerSpriteOnTableX(940); 
        tableY = utils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite(UserInterface.BOARD, "checkerBoard", tableX, tableY, 0, 940, 948, 
            Side.Front, Orientation.Normal);
        layer0.addSprite(boardSprite);        
        for (int s = 0; s < 32; s++)
            layer0.addRegion(new Region(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));
        
        // CHECKERS_LAYER
        Layer layer1 = userInterface.getLayer(CHECKERS_LAYER);
        for (int s = 0; s < 12; s++) {
            Sprite blackCheckerSprite = new Sprite("black" + s, "blackChecker", toPixelX(tableX, s), 
                toPixelY(tableY, s), 1, 90, 93, Side.Front, Orientation.Normal);            
            layer1.addClickableSprite(blackCheckerSprite);
            Sprite redCheckerSprite = new Sprite("red" + s, "redChecker", toPixelX(tableX, 20 + s), 
                toPixelY(tableY, 20 + s), 1, 90, 88, Side.Front, Orientation.Normal); 
            layer1.addClickableSprite(redCheckerSprite);
        }
    }

    @Override
    public void initActions (Set<Action> actions, Map<String, Sequence> sequences)
    {
        // initialize checkers
        for (int s = 0; s < 12; s++) {
            Checker blackChecker = new Checker("black" + s, Checker.BLACK, s);
            checkersById.put(blackChecker.getId(), blackChecker);
            checkersByPosition.put(blackChecker.getPosition(), blackChecker);
            Checker redChecker = new Checker("red" + s, Checker.RED, 20 + s);
            checkersById.put(redChecker.getId(), redChecker);
            checkersByPosition.put(redChecker.getPosition(), redChecker);
        }
        
        // initial expected action: red to select checker
        actions.add(new Action(Checker.RED, PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, 
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
            default: 
                Logger.warn("Invalid event received: ", event);
                return new Result(ResultType.DO_NOTHING);
        }
    }
    
    private Result selectChecker (GameState state, Event event) throws Exception
    {
        // validate move
        Checker checker = checkersById.get(event.getValue());
        if (checker.getOwner() != event.getPlayerNum())
            return new Result(ResultType.ERROR, ERROR_WRONG_CHECKER);
        
        // start a new sequence and keep track of the selected checker 
        Sequence sequence = state.getOrCreateSequence("Sequence");
        sequence.addEvent(event);
        
        // highlight the selected checker for both players to see
        state.getUserInterface().getLayer(CHECKERS_LAYER).getRegion(event.getValue()).setHighlightColor("white");
        
        // add the next logical expected action: the same player must now select a square to move to
        state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE, EventType.SELECT_SQUARE, 
            Option.Category.TableClick, BOARD_LAYER));
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result selectSquare (GameState state, Event event) throws Exception
    {
        // pull out some data we are going to need from the existing sequence and the current event
        Sequence sequence = state.getOrCreateSequence("Sequence");
        Event firstEvent = sequence.getFirstEvent(), lastEvent = sequence.getLastEvent();
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
        
        // validate the move
        if (rowMoveValue == -1 && !mustJump && (checker.isRed() || checker.isKing()) &&
            (moveValue == -4 || (moveValue == -3 && rowACEG) || (moveValue == -5 && !rowACEG))) 
        {
            // move one square toward the top of the board
            validMove = true;            
        }
        else if (rowMoveValue == 1 && !mustJump && (checker.isBlack() || checker.isKing()) &&
            (moveValue == 4 || (moveValue == 5 && rowACEG) || (moveValue == 3 && !rowACEG)))
        {
            // move one square toward the bottom of the board
            validMove = true;            
        }
        else if (rowMoveValue == -2 && (checker.isRed() || checker.isKing()) && (moveValue == -7 || moveValue == -9))
        {
            // jump toward the top of the board
            int jumpedCheckerRelativePosition = (rowACEG && moveValue == -7) ? -3 : (!rowACEG && moveValue == -9) ? -5 : -4;
            jumpedChecker = checkersByPosition.get(currentPosition + jumpedCheckerRelativePosition);
            validMove = (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }
        else if (rowMoveValue == 2 && (checker.isBlack() || checker.isKing()) && (moveValue == 7 || moveValue == 9))
        {
            // jump toward the bottom of the board
            int jumpedCheckerRelativePosition = (rowACEG && moveValue == 9) ? 5 : (!rowACEG && moveValue == 7) ? 3 : 4;
            jumpedChecker = checkersByPosition.get(currentPosition + jumpedCheckerRelativePosition);
            validMove = (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }        
        if (!validMove)
            return new Result(ResultType.ERROR, ERROR_INVALID_MOVE);
        
        // if this is not a jump, the player's turn is over: move the checker, change the player's turn, and clean up
        if (jumpedChecker == null)
        {
            checker.move(newPosition);
            state.getUserInterface().getLayer(CHECKERS_LAYER).moveSprite(checker.getId(), 
                toPixelX(tableX, newPosition), toPixelY(tableY, newPosition));            
            state.removeSequence("Sequence");
            state.getUserInterface().getLayer(CHECKERS_LAYER).getRegion(checker.getId()).clearHighlightColor();
            int nextPlayerTurn = event.getPlayerNum() == Checker.RED ? Checker.BLACK : Checker.RED;
            state.addAction(new Action(nextPlayerTurn, PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, 
                Option.Category.TableClick, CHECKERS_LAYER));
        }
        else // a jump is not the end of the turn; let's update the sequence, highlight the grid square, and move on
        {
            sequence.addEvent(event);
            state.getUserInterface().getLayer(CHECKERS_LAYER).getRegion(event.getValue()).setHighlightColor("yellow");
            Option option1 = new Option(EventType.SELECT_SQUARE, Option.Category.TableClick, BOARD_LAYER);
            Option option2 = new Option(EventType.END_TURN, Option.Category.ConfirmPress);
            state.addAction(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE_OR_END, option1, option2));
        }
        
        // update the players' state
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result endTurn (GameState state, Event event)
    {
        return null; // TODO
    }

    // utility functions to convert logical/board position to graphical/sprite position
    private int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    private int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }    
}
