package models.dominion;

import java.util.Collection;
import models.metabg.Game;
import models.metabg.GameState;
import models.metabg.IGameConfig;
import models.metabg.IGameLogic;
import models.metabg.IGameModeFactory;
import models.metabg.IGameModeFactory.IGameMode;
import models.metabg.Resource;
import models.metabg.Table;

public class DominionConfig implements IGameConfig
{
    private static IGameConfig instance = new DominionConfig();
    public static IGameConfig getInstance () { return instance; }
    protected DominionConfig () { }
    
    @Override public String getName () { return"Dominion"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 4; }
    @Override public int getNumLayers () { return 2; }
    @Override public int getInitialZoom () { return 1; }
    @Override public int getInitialX () { return 2; }
    @Override public int getInitialY () { return 0; }    
    
    @Override
    public void initResources (Collection<Resource> resources) {
        resources.add(new Resource("adventurer", "dominion/adventurer.jpg", 200, 300, "deck", "dominion/deck.jpg"));
        resources.add(new Resource("bureaucrat", "dominion/bureaucrat.jpg", 200, 300, "deck"));
        resources.add(new Resource("cellar", "dominion/cellar.jpg", 200, 300, "deck"));
        resources.add(new Resource("chancellor", "dominion/chancellor.jpg", 200, 300, "deck"));
        resources.add(new Resource("chapel", "dominion/chapel.jpg", 200, 300, "deck"));
        resources.add(new Resource("copper", "dominion/copper.jpg", 200, 300, "deck"));
        resources.add(new Resource("councilroom", "dominion/councilroom.jpg", 200, 300, "deck"));
        resources.add(new Resource("curse", "dominion/curse.jpg", 200, 300, "deck"));
        resources.add(new Resource("duchy", "dominion/duchy.jpg", 200, 300, "deck"));
        resources.add(new Resource("estate", "dominion/estate.jpg", 200, 300, "deck"));
        resources.add(new Resource("feast", "dominion/feast.jpg", 200, 300, "deck"));
        resources.add(new Resource("festival", "dominion/festival.jpg", 200, 300, "deck"));
        resources.add(new Resource("gardens", "dominion/gardens.jpg", 200, 300, "deck"));
        resources.add(new Resource("gold", "dominion/gold.jpg", 200, 300, "deck"));
        resources.add(new Resource("laboratory", "dominion/laboratory.jpg", 200, 300, "deck"));
        resources.add(new Resource("library", "dominion/library.jpg", 200, 300, "deck"));
        resources.add(new Resource("market", "dominion/market.jpg", 200, 300, "deck"));
        resources.add(new Resource("militia", "dominion/militia.jpg", 200, 300, "deck"));
        resources.add(new Resource("mine", "dominion/mine.jpg", 200, 300, "deck"));
        resources.add(new Resource("moat", "dominion/moat.jpg", 200, 300, "deck"));
        resources.add(new Resource("moneylender", "dominion/moneylender.jpg", 200, 300, "deck"));
        resources.add(new Resource("province", "dominion/province.jpg", 200, 300, "deck"));
        resources.add(new Resource("remodel", "dominion/remodel.jpg", 200, 300, "deck"));
        resources.add(new Resource("silver", "dominion/silver.jpg", 200, 300, "deck"));
        resources.add(new Resource("smithy", "dominion/smithy.jpg", 200, 300, "deck"));
        resources.add(new Resource("spy", "dominion/spy.jpg", 200, 300, "deck"));
        resources.add(new Resource("thief", "dominion/thief.jpg", 200, 300, "deck"));
        resources.add(new Resource("throneroom", "dominion/throneroom.jpg", 200, 300, "deck"));
        resources.add(new Resource("trash", "dominion/trash.jpg", 200, 300, "deck"));
        resources.add(new Resource("village", "dominion/village.jpg", 200, 300, "deck"));
        resources.add(new Resource("witch", "dominion/witch.jpg", 200, 300, "deck"));
        resources.add(new Resource("woodcutter", "dominion/woodcutter.jpg", 200, 300, "deck"));
        resources.add(new Resource("workshop", "dominion/workshop.jpg", 200, 300, "deck"));
    }

    @Override public Table createTestTable (Game game) { 
        return new Table(game, "Testing", 2, GameModeFactory.GameMode.FirstGame); 
    }
    
    @Override public IGameModeFactory createGameModeFactory () { 
        return new GameModeFactory(); 
    }
    
    @Override public IGameLogic createGameLogic (int numPlayers) { 
        return new DominionLogic(); 
    }
    
    @Override public GameState createGameState (Game game, int numPlayers, IGameMode mode) { 
        return new DominionGameState(game, numPlayers, getNumLayers(), mode); 
    }
}
