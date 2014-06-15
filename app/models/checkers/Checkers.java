package models.checkers;

import models.metabg.ClickableRegion;
import models.metabg.Game;
import models.metabg.GameState;
import models.metabg.Resource;
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
    @Override public GameState createGameState (int numPlayers) { return new CheckersState(numPlayers); }
    
    @Override
    protected void addResources () {
        SpriteUtils utils = SpriteUtils.getInstance(); // TODO: if this stays here, change name from relativeX to x...
        int tableX = utils.centerSpriteOnTableX(940), tableY = utils.centerSpriteOnTableY(948);
        ImmutableList.Builder<ClickableRegion> builder = new ImmutableList.Builder<>();
        for (int s = 0; s < 32; s++)
            builder.add(new ClickableRegion(toPixelX(tableX, s), toPixelY(tableY, s), 107, 107, String.valueOf(s)));        
        resources.add(new Resource("checkerBoard", "checkerBoard.png", builder.build()));
        resources.add(new Resource("redChecker", "redChecker.png"));
        resources.add(new Resource("blackChecker", "blackChecker.png"));
    }
    
    // utility functions to convert logical/board position to graphical/sprite position
    public static int toPixelX (int tableX, int pos) { return tableX + 152 + (214 * (pos % 4)) - (107 * ((pos / 4) % 2)); }
    public static int toPixelY (int tableY, int pos) { return tableY + 50 + (pos / 4) * 107; }    
}
