package models.checkers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import models.metabg.Action;
import models.metabg.GameState;
import models.metabg.Layer;
import models.metabg.Option;
import models.metabg.Region;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import utils.SpriteUtils;

public class CheckersGameState extends GameState
{
    private Map<String, Checker> checkersById = new HashMap<>();
    private Map<Integer, Checker> checkersByPosition = new HashMap<>();

    private int tableX, tableY;

    public CheckersGameState (int numPlayers, int numLayers)
    {
        super(numPlayers, numLayers);

        // initialize board
        Layer layer0 = getUILayer(CheckersConstants.BOARD_LAYER);
        tableX = SpriteUtils.centerSpriteOnTableX(940); 
        tableY = SpriteUtils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite("board", "checkerBoard", tableX, tableY, 0, 940, 948, 
            Side.Front, Orientation.Normal);
        layer0.addSprite(boardSprite);        
        for (int s = 0; s < 32; s++)
            layer0.addRegion(new Region(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));
        
        // initialize checkers
        Layer layer1 = getUILayer(CheckersConstants.CHECKERS_LAYER);
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
        addAction(new Action.Builder().player(Checker.RED).prompt(CheckersConstants.PROMPT_SELECT_CHECKER)
            .option(EventType.SELECT_CHECKER, Option.Category.TableClick, CheckersConstants.CHECKERS_LAYER)
            .build());
    }
    
    public Checker getCheckerById (String id) { return checkersById.get(id); }
    public Checker getCheckerByPosition (int position) { return checkersByPosition.get(position); }
    public Collection<Checker> getAllCheckers () { return checkersById.values(); }
    
    public void moveChecker (Checker checker, int newPosition) {
        Layer uiLayer = getUILayer(CheckersConstants.CHECKERS_LAYER);
        checkersByPosition.remove(checker.getPosition());
        checker.move(newPosition);
        checkersByPosition.put(checker.getPosition(), checker);        
        uiLayer.moveSprite(checker.getId(), toPixelX(tableX, newPosition), toPixelY(tableY, newPosition));
    }
    
    public void promoteCheckerToKing (Checker checker) {
        Layer uiLayer = getUILayer(CheckersConstants.CHECKERS_LAYER);
        checker.promoteToKing();
        uiLayer.removeSprite(checker.getId());
        String resource = checker.isBlack() ? "blackKing" : "redKing"; 
        int height = checker.isBlack() ? 93 : 88;
        Sprite kingSprite = new Sprite(checker.getId(), resource, toPixelX(tableX, checker.getPosition()), 
            toPixelY(tableY, checker.getPosition()), 1, 90, height, Side.Front, Orientation.Normal);
        uiLayer.addClickableSprite(kingSprite);        
    }
    
    public void removeChecker (Checker checker) {
        checkersById.remove(checker.getId());
        checkersByPosition.remove(checker.getPosition());
    }
    
    // utility functions to convert logical/board position to graphical/sprite position
    private int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    private int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }    
}
