package models.checkers;

import java.util.HashMap;
import java.util.Map;
import models.metabg.Action;
import models.metabg.ClickableRegion;
import models.metabg.Event;
import models.metabg.Event.IEventType;
import models.metabg.GameState;
import models.metabg.Layer;
import models.metabg.Option;
import models.metabg.Result;
import models.metabg.Result.ResultType;
import models.metabg.Sequence;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import models.metabg.UserInterface;
import play.Logger;
import utils.SpriteUtils;

public class CheckersState extends GameState
{
    // event types
    public enum EventType implements IEventType { SELECT_CHECKER, SELECT_SQUARE, END_TURN, ERROR };

    // player color constants
    public static final int BLACK = 0;
    public static final int RED = 1;
    
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
    
    // constructor
    public CheckersState (int numPlayers, int numLayers) {
        super(numPlayers, numLayers);
        checkersById = new HashMap<>();
        checkersByPosition = new HashMap<>();
    }
    
    @Override
    protected void initUserInterface () 
    {
        // BOARD_LAYER
        Layer layer0 = userInterface.getLayer(BOARD_LAYER);
        SpriteUtils utils = SpriteUtils.getInstance();
        int tableX = utils.centerSpriteOnTableX(940), tableY = utils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite(UserInterface.BOARD, "checkerBoard", tableX, tableY, 0, 940, 948, 
            Side.Front, Orientation.Normal);
        layer0.addSprite(boardSprite);        
        for (int s = 0; s < 32; s++)
            layer0.addClickableRegion(new ClickableRegion(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));
        
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
    protected void initState ()
    {
        // initialize checkers
        for (int s = 0; s < 12; s++) {
            Checker blackChecker = new Checker("black" + s, BLACK, s);
            checkersById.put(blackChecker.getId(), blackChecker);
            checkersByPosition.put(blackChecker.getPosition(), blackChecker);
            Checker redChecker = new Checker("red" + s, RED, 20 + s);
            checkersById.put(redChecker.getId(), redChecker);
            checkersByPosition.put(redChecker.getPosition(), redChecker);
        }
        
        // initial expected action: red to select checker
        expectedActions.add(new Action(RED, PROMPT_SELECT_CHECKER, EventType.SELECT_CHECKER, Option.Category.TableClick, CHECKERS_LAYER));
    }
    
    @Override
    protected Result processEvent (Event event)
    {
        // TODO: handle undo and cancel
        // TODO: selections
        
        EventType eventType = (EventType) event.getType();
        switch (eventType) {
            case SELECT_CHECKER: return selectChecker(event);                
            case SELECT_SQUARE: return selectSquare(event);
            case END_TURN: return endTurn(event);
            default: 
                Logger.warn("Invalid event received: ", event);
                return new Result(ResultType.DO_NOTHING);
        }
    }
    
    private Result selectChecker (Event event)
    {
        // validate move
        Checker checker = checkersById.get(event.getValue());
        if (checker.getOwner() != event.getPlayerNum())
            return new Result(ResultType.ERROR, ERROR_WRONG_CHECKER);
        
        // nothing much to do yet; let's start a new sequence for now and set up the next logical expected action
        Sequence sequence = getOrCreateSequence("Sequence");
        sequence.addEvent(event);
        expectedActions.add(new Action(event.getPlayerNum(), PROMPT_SELECT_SQUARE, EventType.SELECT_SQUARE, Option.Category.TableClick, BOARD_LAYER));        
        return new Result(ResultType.STATE_CHANGE);
    }
    
    private Result selectSquare (Event event)
    {
        return null; // TODO
    }
    
    private Result endTurn (Event event)
    {
        return null; // TODO
    }

    // utility functions to convert logical/board position to graphical/sprite position
    private int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    private int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }    
}
