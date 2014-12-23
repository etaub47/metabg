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
import utils.Utils;

public class GameModeFactory implements IGameModeFactory
{
    public static GameModeFactory getInstance () { return instance; }
    private static GameModeFactory instance = new GameModeFactory();
    protected GameModeFactory () { }
    
    public enum GameMode implements IGameMode { FirstGame, BigMoney, Interaction, SizeDistortion, VillageSquare, Random }
    
    private Set<? extends IDominionCard> firstGame = EnumSet.of(BaseKingdomCard.Cellar, BaseKingdomCard.Village,
        BaseKingdomCard.Workshop, BaseKingdomCard.Remodel, BaseKingdomCard.Market, BaseKingdomCard.Moat,
        BaseKingdomCard.Woodcutter, BaseKingdomCard.Militia, BaseKingdomCard.Smithy, BaseKingdomCard.Mine);
    private Set<? extends IDominionCard> bigMoney = EnumSet.of(BaseKingdomCard.Adventurer, BaseKingdomCard.Bureaucrat,
        BaseKingdomCard.Chancellor, BaseKingdomCard.Chapel, BaseKingdomCard.Feast, BaseKingdomCard.Laboratory,
        BaseKingdomCard.Market, BaseKingdomCard.Mine, BaseKingdomCard.Moneylender, BaseKingdomCard.ThroneRoom);
    private Set<? extends IDominionCard> interaction = EnumSet.of(BaseKingdomCard.Bureaucrat, BaseKingdomCard.Chancellor,
        BaseKingdomCard.CouncilRoom, BaseKingdomCard.Festival, BaseKingdomCard.Library, BaseKingdomCard.Militia,
        BaseKingdomCard.Moat, BaseKingdomCard.Spy, BaseKingdomCard.Thief, BaseKingdomCard.Village);
    private Set<? extends IDominionCard> sizeDistortion = EnumSet.of(BaseKingdomCard.Cellar, BaseKingdomCard.Chapel,
        BaseKingdomCard.Feast, BaseKingdomCard.Gardens, BaseKingdomCard.Laboratory, BaseKingdomCard.Thief,
        BaseKingdomCard.Village, BaseKingdomCard.Witch, BaseKingdomCard.Woodcutter, BaseKingdomCard.Workshop);
    private Set<? extends IDominionCard> villageSquare = EnumSet.of(BaseKingdomCard.Bureaucrat, BaseKingdomCard.Cellar,
        BaseKingdomCard.Festival, BaseKingdomCard.Library, BaseKingdomCard.Market, BaseKingdomCard.Remodel,
        BaseKingdomCard.Smithy, BaseKingdomCard.ThroneRoom, BaseKingdomCard.Village, BaseKingdomCard.Woodcutter);

    private Set<? extends IDominionCard> treasureCards = EnumSet.of(NonKingdomCard.Copper, 
        NonKingdomCard.Silver, NonKingdomCard.Gold);
    private Set<? extends IDominionCard> victoryCards = EnumSet.of(NonKingdomCard.Estate, 
        NonKingdomCard.Duchy, NonKingdomCard.Province, NonKingdomCard.Curse);
    
    private List<IDominionCard> getRandomCardSet ()
    {
        List<IDominionCard> cardSet = new ArrayList<>();
        BaseKingdomCard cardType;
        for (int c = 0; c < 10; c++) {            
            do { cardType = BaseKingdomCard.values()[Utils.getRandomInt(BaseKingdomCard.values().length)]; } 
                while(!cardSet.contains(cardType));
            cardSet.add(cardType);
        }
        return cardSet;
    }
    
    public Collection<? extends IDominionCard> getCardSet (GameMode gameMode) 
    {
        List<IDominionCard> cardSet;
        switch (gameMode) {
            case FirstGame: cardSet = new ArrayList<>(firstGame); break;
            case BigMoney: cardSet = new ArrayList<>(bigMoney); break;
            case Interaction: cardSet = new ArrayList<>(interaction); break;
            case SizeDistortion: cardSet = new ArrayList<>(sizeDistortion); break;
            case VillageSquare: cardSet = new ArrayList<>(villageSquare); break;
            case Random: cardSet = getRandomCardSet(); break;
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
