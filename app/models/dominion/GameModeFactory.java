package models.dominion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
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
    
    public Set<? extends IDominionCard> getCardSet (GameMode gameMode) {
        switch (gameMode) {
            case FirstGame: return firstGame;
            default: return null;
        }
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
