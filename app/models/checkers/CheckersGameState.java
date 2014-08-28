package models.checkers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.metabg.Event;
import models.metabg.Game;
import models.metabg.GameState;
import models.metabg.Layer;
import models.metabg.Region;
import models.metabg.Resource;
import models.metabg.Sprite;
import utils.SpriteUtils;

public class CheckersGameState extends GameState
{
    // layer constants
    public static final int BOARD_LAYER = 0;
    public static final int CHECKERS_LAYER = 1;

    private final int tableX, tableY;

    private Map<String, Checker> checkersById = new HashMap<>();
    private Map<Integer, Checker> checkersByPosition = new HashMap<>();    
    private List<Selection> selections = new ArrayList<>();

    public CheckersGameState (Game game, int numPlayers, int numLayers)
    {
        super(game, numPlayers, numLayers);

        // initialize board
        Layer layer0 = getUILayer(BOARD_LAYER);
        tableX = SpriteUtils.centerSpriteOnTableX(940); 
        tableY = SpriteUtils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite.SpriteBuilder("board", getResource("checkerBoard"), tableX, tableY, 0).build();
        layer0.addSprite(boardSprite);        
        for (int s = 0; s < 32; s++)
            layer0.addRegion(new Region(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));
        
        // initialize checkers
        Layer layer1 = getUILayer(CHECKERS_LAYER);
        for (int s = 0; s < 12; s++) 
        {
            int position = s;
            Sprite blackCheckerSprite = new Sprite.SpriteBuilder("black" + s, getResource("blackChecker"), 
                toPixelX(tableX, position), toPixelY(tableY, position), 1).build();            
            layer1.addClickableSprite(blackCheckerSprite);
            Checker blackChecker = new Checker("black" + s, Checker.BLACK, position);
            checkersById.put(blackChecker.getId(), blackChecker);
            checkersByPosition.put(blackChecker.getPosition(), blackChecker);

            position = 20 + s;
            Sprite redCheckerSprite = new Sprite.SpriteBuilder("red" + s, getResource("redChecker"),
                toPixelX(tableX, position), toPixelY(tableY, position), 1).build(); 
            layer1.addClickableSprite(redCheckerSprite);
            Checker redChecker = new Checker("red" + s, Checker.RED, position);
            checkersById.put(redChecker.getId(), redChecker);
            checkersByPosition.put(redChecker.getPosition(), redChecker);
        }
        
        // initial expected action: red to select checker
        addAction(ActionType.SelectChecker, Checker.RED);
    }
    
    public Checker getCheckerById (String id) { return checkersById.get(id); }
    public Checker getCheckerByPosition (int position) { return checkersByPosition.get(position); }
    public Collection<Checker> getAllCheckers () { return checkersById.values(); }
    
    public List<Selection> getSelections () { return selections; }
    public Selection getFirstSelection () { return selections.isEmpty() ? null : selections.get(0); }
    public Selection getLastSelection () { return selections.isEmpty() ? null : selections.get(selections.size() - 1); }
    
    public void addSelection (Event event) { selections.add(new Selection(event, null)); }
    public void addSelection (Event event, Checker checker) { selections.add(new Selection(event, checker)); }
    public void removeLastSelection () { if (!selections.isEmpty()) selections.remove(selections.size() - 1); }
    public void removeAllSelections () { selections.clear(); }    

    public boolean isMoveValid (Checker checker, int currentPosition, int newPosition, boolean mustJump)
    {
        boolean rowACEG = (currentPosition / 4) % 2 == 0; // the checker is in row A, C, E, or G
        int moveValue = newPosition - currentPosition;
        int rowMoveValue = (newPosition / 4) - (currentPosition / 4);
        boolean destinationEmpty = (getCheckerByPosition(newPosition) == null);
        
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
            Checker jumpedChecker = determineJumpedChecker(currentPosition, newPosition); 
            return (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }
        else if (rowMoveValue == 2 && (checker.isBlack() || checker.isKing()) && destinationEmpty &&
                 (moveValue == 7 || moveValue == 9))
        {
            // jump toward the bottom of the board
            Checker jumpedChecker = determineJumpedChecker(currentPosition, newPosition); 
            return (jumpedChecker != null && jumpedChecker.getOwner() != checker.getOwner());
        }
        else
        {
            return false;
        }
    }
    
    // helper function to determine the checker being jumped
    public Checker determineJumpedChecker (int currentPosition, int newPosition) 
    {
        boolean rowACEG = (currentPosition / 4) % 2 == 0; // the checker is in row A, C, E, or G
        int moveValue = newPosition - currentPosition;
        
        switch (moveValue) {
            case -7: return getCheckerByPosition(currentPosition + (rowACEG ? -3 : -4));
            case -9: return getCheckerByPosition(currentPosition + (rowACEG ? -4 : -5));
            case  7: return getCheckerByPosition(currentPosition + (rowACEG ?  4 :  3));
            case  9: return getCheckerByPosition(currentPosition + (rowACEG ?  5 :  4));
            default: return null;
        }
    }

    // helper function to determine if a checker should be promoted to a king
    public void checkForKingPromotion (Checker checker) {
        int position = checker.getPosition();
        if (!checker.isKing() && ((checker.isBlack() && (position / 4) == 7) || (checker.isRed() && (position / 4) == 0)))
            promoteCheckerToKing(checker);
    }
    
    // helper function to determine if a particular checker has at least one valid move it can make
    public boolean hasValidMove (Checker checker) {
        int[] movesToCheck = new int[]{ 3, 4, 5, 7, 9, -3, -4, -5, -7, -9 };
        for (int m = 0; m < movesToCheck.length; m++)
            if (isMoveValid(checker, checker.getPosition(), checker.getPosition() + movesToCheck[m], false))
                return true;        
        return false;
    }
    
    // helper function to determine if a player has at least one valid move he/she can make
    public boolean hasAnyValidMoves (int playerNum) {
        for (Checker checker : getAllCheckers())
            if (checker.getOwner() == playerNum && hasValidMove(checker))
                return true;
        return false;        
    }
    
    public void moveChecker (Checker checker, int newPosition) {
        Layer uiLayer = getUILayer(CHECKERS_LAYER);
        checkersByPosition.remove(checker.getPosition());
        checker.move(newPosition);
        checkersByPosition.put(checker.getPosition(), checker);        
        uiLayer.moveSprite(checker.getId(), toPixelX(tableX, newPosition), toPixelY(tableY, newPosition));
    }
    
    public void promoteCheckerToKing (Checker checker) {
        Layer uiLayer = getUILayer(CHECKERS_LAYER);
        checker.promoteToKing();
        uiLayer.removeSprite(checker.getId());
        Resource resource = getResource(checker.isBlack() ? "blackKing" : "redKing"); 
        Sprite kingSprite = new Sprite.SpriteBuilder(checker.getId(), resource, toPixelX(tableX, checker.getPosition()), 
            toPixelY(tableY, checker.getPosition()), 1).build();
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
