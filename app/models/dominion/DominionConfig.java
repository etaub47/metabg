package models.dominion;

import java.util.List;
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
    
    @Override
    public void init (List<Resource> resources) {
        resources.add(new Resource("adventurer","dominion/adventurer.jpg"));
        resources.add(new Resource("bureaucrat","dominion/bureaucrat.jpg"));
        resources.add(new Resource("cellar","dominion/cellar.jpg"));
        resources.add(new Resource("chancellor","dominion/chancellor.jpg"));
        resources.add(new Resource("chapel","dominion/chapel.jpg"));
        resources.add(new Resource("copper","dominion/copper.jpg"));
        resources.add(new Resource("councilroom","dominion/councilroom.jpg"));
        resources.add(new Resource("curse","dominion/curse.jpg"));
        resources.add(new Resource("deck","dominion/deck.jpg"));
        resources.add(new Resource("duchy","dominion/duchy.jpg"));
        resources.add(new Resource("estate","dominion/estate.jpg"));
        resources.add(new Resource("feast","dominion/feast.jpg"));
        resources.add(new Resource("festival","dominion/festival.jpg"));
        resources.add(new Resource("gardens","dominion/gardens.jpg"));
        resources.add(new Resource("gold","dominion/gold.jpg"));
        resources.add(new Resource("laboratory","dominion/laboratory.jpg"));
        resources.add(new Resource("library","dominion/library.jpg"));
        resources.add(new Resource("market","dominion/market.jpg"));
        resources.add(new Resource("militia","dominion/militia.jpg"));
        resources.add(new Resource("mine","dominion/mine.jpg"));
        resources.add(new Resource("moat","dominion/moat.jpg"));
        resources.add(new Resource("moneylender","dominion/moneylender.jpg"));
        resources.add(new Resource("province","dominion/province.jpg"));
        resources.add(new Resource("remodel","dominion/remodel.jpg"));
        resources.add(new Resource("silver","dominion/silver.jpg"));
        resources.add(new Resource("smithy","dominion/smithy.jpg"));
        resources.add(new Resource("spy","dominion/spy.jpg"));
        resources.add(new Resource("thief","dominion/thief.jpg"));
        resources.add(new Resource("throneroom","dominion/throneroom.jpg"));
        resources.add(new Resource("trash","dominion/trash.jpg"));
        resources.add(new Resource("village","dominion/village.jpg"));
        resources.add(new Resource("witch","dominion/witch.jpg"));
        resources.add(new Resource("woodcutter","dominion/woodcutter.jpg"));
        resources.add(new Resource("workshop","dominion/workshop.jpg"));
    }

    @Override public String getName () { return"Dominion"; }
    @Override public int getMinPlayers () { return 2; }
    @Override public int getMaxPlayers () { return 4; }
    @Override public int getNumLayers () { return 2; }
    
    @Override public Table createTestTable () { return new Table(this, "Testing", 2, GameModeFactory.GameMode.FirstGame); }
    
    @Override public IGameModeFactory createGameModeFactory () { return new GameModeFactory(); } 
    @Override public IGameLogic createGameLogic (int numPlayers) { return new DominionLogic(); }
    @Override public GameState createGameState (int numPlayers, IGameMode mode) { 
        return new DominionGameState(numPlayers, getNumLayers(), mode); 
    }
}
