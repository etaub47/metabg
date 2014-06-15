package models.checkers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import models.metabg.Action;
import models.metabg.Choice;
import models.metabg.GameState;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import models.metabg.Sprites;
import utils.SpriteUtils;

public class CheckersState extends GameState
{
    // prompts
    public static final String PROMPT_SELECT_CHECKER = "Please select a checker to move.";
    
    // each checker has a position 0-31; seat 0 = black; seat 1 = red
    private Set<Integer> redCheckers = new HashSet<>();
    private Set<Integer> redKings = new HashSet<>();
    private Set<Integer> blackCheckers = new HashSet<>();
    private Set<Integer> blackKings = new HashSet<>();
    
    public CheckersState (int numPlayers) {
        super(numPlayers);
    }
    
    @Override
    protected void initSprites () 
    {
        // initialize board sprite
        SpriteUtils utils = SpriteUtils.getInstance();
        int tableX = utils.centerSpriteOnTableX(940), tableY = utils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite("checkerBoard", tableX, tableY, 0, Side.Front, Orientation.Normal, null);
        sprites.addSprite(0, Sprites.BOARD, boardSprite);
        
        // initialize checker sprites
        List<Sprite> blackCheckers = new ArrayList<>(12);
        List<Sprite> redCheckers = new ArrayList<>(12);
        for (int s = 0; s < 12; s++) {
            Sprite blackCheckerSprite = new Sprite("blackChecker", Checkers.toPixelX(tableX, s), Checkers.toPixelY(tableY, s), 1, 
                Side.Front, Orientation.Normal, "black" + s); 
            blackCheckers.add(blackCheckerSprite);
            sprites.addSprite(1, "blackChecker" + s, blackCheckerSprite);
            Sprite redCheckerSprite = new Sprite("redChecker", Checkers.toPixelX(tableX, 20 + s), Checkers.toPixelY(tableY, 20 + s), 1, 
                Side.Front, Orientation.Normal, "red" + s); 
            redCheckers.add(redCheckerSprite);
            sprites.addSprite(1, "redChecker" + s, redCheckerSprite);
        }
    }

    @Override
    protected void initState ()
    {
        // initialize checkers
        for (int s = 0; s < 12; s++) {
            blackCheckers.add(s);
            redCheckers.add(20 + s);    
        }
        
        // initial expected action: red to select checker
        expectedActions.add(new Action(1, PROMPT_SELECT_CHECKER, Choice.Category.TableClick, 1, null, null));
    }
    
}
