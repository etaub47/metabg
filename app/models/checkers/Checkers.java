package models.checkers;

import java.util.ArrayList;
import java.util.List;
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
        resources.add(new Resource("redChecker", "redChecker.png"));
        resources.add(new Resource("blackChecker", "blackChecker.png"));
    }
    
    @Override
    protected void initSprites () {
        SpriteUtils utils = SpriteUtils.getInstance();
        int tableX = utils.centerSpriteOnTableX(940), tableY = utils.centerSpriteOnTableY(948);
        Sprite boardSprite = new Sprite("checkerBoard", tableX, tableY, 0, Side.Front, Orientation.Normal, null);
        sprites.addSprite(0, Sprites.BOARD, boardSprite);
        List<Sprite> blackCheckers = new ArrayList<>(12);
        List<Sprite> redCheckers = new ArrayList<>(12);
        for (int s = 0; s < 12; s++) {
            Sprite blackCheckerSprite = new Sprite("blackChecker", toPixelX(tableX, s), toPixelY(tableY, s), 1, 
                Side.Front, Orientation.Normal, "black" + s); 
            blackCheckers.add(blackCheckerSprite);
            sprites.addSprite(1, "blackChecker" + s, blackCheckerSprite);
            Sprite redCheckerSprite = new Sprite("redChecker", toPixelX(tableX, 20 + s), toPixelY(tableY, 20 + s), 1, 
                Side.Front, Orientation.Normal, "red" + s); 
            redCheckers.add(redCheckerSprite);
            sprites.addSprite(1, "redChecker" + s, redCheckerSprite);
        }
    }
    
    private int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    private int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }
}
