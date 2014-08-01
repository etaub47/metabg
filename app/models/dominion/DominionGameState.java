package models.dominion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import models.dominion.GameModeFactory.GameMode;
import models.metabg.CardStack;
import models.metabg.CardStack.Type;
import models.metabg.GameState;
import models.metabg.IGameModeFactory.IGameMode;

public class DominionGameState extends GameState
{
    public DominionGameState (int numPlayers, int numLayers, IGameMode mode) {
        super(numPlayers, numLayers);
        Set<? extends IDominionCard> cardSet = GameModeFactory.getInstance().getCardSet((GameMode)mode);
        // TODO: set up the supply piles based on card sets 
    }

    private List<CardStack<IDominionCard>> kingdomCardSupply = new ArrayList<>(10);
    private List<CardStack<IDominionCard>> treasureCardSupply = new ArrayList<>(3);
    private List<CardStack<IDominionCard>> victoryCardSupply = new ArrayList<>(4);
    private CardStack<IDominionCard> trashPile = new CardStack<>(Type.Pile);
    
    private List<PlayerState> playerData = new ArrayList<>();
    
    private List<IDominionCard> playedCards = new ArrayList<>();
    private CardStack<IDominionCard> setAsideCards = new CardStack<>(Type.Pile);
    
    private int currentPlayer;
    private IDominionCard currentAction;
    private int throneRooms;
    
    private int numActions;
    private int numBuys;
    private int numCoins;
    
    public PlayerState getPlayerData (int playerNum) { return playerData.get(playerNum); }
    public PlayerState getCurrentPlayerData () { return playerData.get(currentPlayer); }
}
