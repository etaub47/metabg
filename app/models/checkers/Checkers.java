package models.checkers;

import models.metabg.ClickableRegion;
import models.metabg.Game;
import models.metabg.Resource;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import models.metabg.Sprites;
import utils.SpriteUtils;
import com.google.common.collect.ImmutableList;

public class Checkers extends Game
{
    private static Game instance = new Checkers();
    public static Game getInstance () { return instance; }
    protected Checkers () { }

    // TODO: read from config file?

    @Override public String getName () { return "Checkers"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 2; }
    
    @Override
    protected void addResources () {
        resources.add(new Resource("checkerBoard", "checkerBoard.png", new ImmutableList.Builder<ClickableRegion>()
            .add(new ClickableRegion(10, 100, 25, 25, "(1, 8)"))
            .add(new ClickableRegion(30, 100, 25, 25, "(2, 8)"))
            .build()));
    }
    
    @Override
    protected void initSprites () {        
        SpriteUtils utils = SpriteUtils.getInstance();
        Sprite boardSprite = new Sprite("checkerBoard", 50, 50,
            0, Side.Front, Orientation.Normal, null); 
        /*
        Sprite boardSprite = new Sprite("checkerBoard", utils.centerSpriteOnTableX(940), utils.centerSpriteOnTableY(948),
            0, Side.Front, Orientation.Normal, null);
        */ 
        sprites.addSprite(0, Sprites.BOARD, boardSprite);        
    }
}
