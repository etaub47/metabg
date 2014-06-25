package models.checkers;

import java.util.HashMap;
import java.util.Map;
import models.metabg.Action;
import models.metabg.Choice;
import models.metabg.ClickableRegion;
import models.metabg.GameState;
import models.metabg.Layer;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import models.metabg.UserInterface;
import utils.SpriteUtils;

public class CheckersState extends GameState
{
    // prompts
    public static final String PROMPT_SELECT_CHECKER = "Please select a checker to move.";
    
    private Map<String, Checker> checkersById;
    private Map<Integer, Checker> checkersByPosition;
    
    public CheckersState (int numPlayers, int numLayers) {
        super(numPlayers, numLayers);
        checkersById = new HashMap<>();
        checkersByPosition = new HashMap<>();
    }
    
    @Override
    protected void initUserInterface () 
    {
        // layer 0
        Layer layer0 = userInterface.getLayer(0);
        SpriteUtils utils = SpriteUtils.getInstance();
        int tableX = utils.centerSpriteOnTableX(940), tableY = utils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite(UserInterface.BOARD, "checkerBoard", tableX, tableY, 0, 940, 948, 
            Side.Front, Orientation.Normal);
        layer0.addSprite(boardSprite);        
        for (int s = 0; s < 32; s++)
            layer0.addClickableRegion(new ClickableRegion(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));
        
        // layer 1
        Layer layer1 = userInterface.getLayer(1);
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
            Checker blackChecker = new Checker("black" + s, s);
            checkersById.put(blackChecker.getId(), blackChecker);
            checkersByPosition.put(blackChecker.getPosition(), blackChecker);
            Checker redChecker = new Checker("red" + s, 20 + s);
            checkersById.put(redChecker.getId(), redChecker);
            checkersByPosition.put(redChecker.getPosition(), redChecker);
        }
        
        // initial expected action: red to select checker
        expectedActions.add(new Action(1, PROMPT_SELECT_CHECKER, Choice.Category.TableClick, 1, null, null));
    }
    
    // utility functions to convert logical/board position to graphical/sprite position
    private int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    private int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }    
}
