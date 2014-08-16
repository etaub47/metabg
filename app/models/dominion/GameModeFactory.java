package models.dominion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import models.metabg.IGameModeFactory;

public class GameModeFactory implements IGameModeFactory
{
    public static GameModeFactory getInstance () { return instance; }
    private static GameModeFactory instance = new GameModeFactory();
    protected GameModeFactory () { }
    
    public enum GameMode implements IGameMode { FirstGame }
    
    private Set<? extends IDominionCard> firstGame = EnumSet.of(BaseKingdomCard.Cellar, BaseKingdomCard.Village,
        BaseKingdomCard.Workshop, BaseKingdomCard.Remodel, BaseKingdomCard.Market, BaseKingdomCard.Moat,
        BaseKingdomCard.Woodcutter, BaseKingdomCard.Militia, BaseKingdomCard.Smithy, BaseKingdomCard.Mine);

    private Set<? extends IDominionCard> treasureCards = EnumSet.of(NonKingdomCard.Copper, 
        NonKingdomCard.Silver, NonKingdomCard.Gold);
    private Set<? extends IDominionCard> victoryCards = EnumSet.of(NonKingdomCard.Estate, 
        NonKingdomCard.Duchy, NonKingdomCard.Province, NonKingdomCard.Curse);
    
    public Collection<? extends IDominionCard> getCardSet (GameMode gameMode) 
    {
        List<IDominionCard> cardSet;
        switch (gameMode) {
            case FirstGame: cardSet = new ArrayList<>(firstGame); break;
            default: return null;
        }
        
        Collections.sort(cardSet, new Comparator<IDominionCard>(){
            @Override public int compare (IDominionCard card1, IDominionCard card2) {
                return card1.getCost() - card2.getCost();
            }            
        });
        
        return cardSet;
    }
    
    public Set<? extends IDominionCard> getTreasureCards () { 
        return new LinkedHashSet<IDominionCard>(treasureCards); 
    }
    
    public Set<? extends IDominionCard> getVictoryCards () { 
        return new LinkedHashSet<IDominionCard>(victoryCards);
    }

    @Override
    public List<? extends IGameMode> getAllModes () {
        return new ArrayList<>(Arrays.asList(GameMode.values()));
    }

    @Override
    public IGameMode getMode (String mode) {
        return GameMode.valueOf(mode);
    }
}
