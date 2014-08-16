package models.dominion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import models.dominion.GameModeFactory.GameMode;
import models.metabg.CardStack;
import models.metabg.CardStack.Type;
import models.metabg.GameState;
import models.metabg.IGameModeFactory.IGameMode;
import models.metabg.Layer;
import models.metabg.Sprite;
import models.metabg.Sprite.Orientation;
import models.metabg.Sprite.Side;
import utils.SpriteUtils;
import utils.Utils;

public class DominionGameState extends GameState
{
    // layer constants
    public static final int CARD_LAYER = 0;
    
    // clickable region constants
    public static final String KINGDOM = "Kingdom_";
    public static final String TREASURE = "Treasure_";
    public static final String VICTORY = "Victory_";
    
    private List<CardStack<IDominionCard>> kingdomCardSupply = new ArrayList<>(10);
    private List<CardStack<IDominionCard>> treasureCardSupply = new ArrayList<>(3);
    private List<CardStack<IDominionCard>> victoryCardSupply = new ArrayList<>(4);
    private CardStack<IDominionCard> trashPile = new CardStack<>(Type.Pile);
    
    private List<PlayerState> playerData = new ArrayList<>();
    
    private List<IDominionCard> playedCards = new ArrayList<>();
    private CardStack<IDominionCard> setAsideCards = new CardStack<>(Type.Pile);
    
    private int currentPlayer;
    private IDominionCard currentAction = null;
    
    private int numThroneRooms = 0;    
    private int numActions = 0;
    private int numBuys = 0;
    private int numCoins = 0;

    public DominionGameState (int numPlayers, int numLayers, IGameMode mode) 
    {
        super(numPlayers, numLayers);

        GameModeFactory factory = GameModeFactory.getInstance();        
        Collection<? extends IDominionCard> cardSet = factory.getCardSet((GameMode)mode);
        int numVictoryCards = ((numPlayers == 2) ? 8 : 12);
        Layer layer = getUILayer(CARD_LAYER);
        int startX = SpriteUtils.centerSpriteOnTableX(2000); 
        int startY = 50;        
        
        // set up kingdom card supply piles
        int s = 0;
        for (IDominionCard card : cardSet) {
            CardStack<IDominionCard> supplyPile = new CardStack<>(CardStack.Type.Pile);
            int pileSize = card.isVictoryCard() ? numVictoryCards : 10;
            for (int c = 0; c < pileSize; c++) 
                supplyPile.addToTop(card);            
            kingdomCardSupply.add(supplyPile);      
            layer.addClickableSprite(new Sprite(KINGDOM + s, card.getResourceKey(), 
                startX + (s % 5) * 220, startY + (s / 5) * 320, 0, 200, 300, Side.Front, Orientation.Normal));
            s++;
        }

        // set up treasure card supply piles        
        s = 0;
        for (IDominionCard card : factory.getTreasureCards()) {
            CardStack<IDominionCard> supplyPile = new CardStack<>(CardStack.Type.Pile);
            for (int c = 0; c < 60; c++) 
                supplyPile.addToTop(card);            
            treasureCardSupply.add(supplyPile);
            layer.addClickableSprite(new Sprite(TREASURE + s, card.getResourceKey(), 
                startX + 1140 + s * 220, startY, 0, 200, 300, Side.Front, Orientation.Normal));            
            s++;
        }

        // set up victory card supply piles        
        s = 0;
        for (IDominionCard card : factory.getVictoryCards()) {
            CardStack<IDominionCard> supplyPile = new CardStack<>(CardStack.Type.Pile);
            for (int c = 0; c < numVictoryCards; c++) 
                supplyPile.addToTop(card);            
            victoryCardSupply.add(supplyPile);            
            layer.addClickableSprite(new Sprite(VICTORY + s, card.getResourceKey(), 
                startX + 1140 + s * 220, startY + 320, 0, 200, 300, Side.Front, Orientation.Normal));
            s++;
        }
        
        // set up players
        for (int p = 0; p < numPlayers; p++) {
            playerData.add(new PlayerState());
            // TODO: graphics
        }
        
        currentPlayer = Utils.getRandomInt(numPlayers);        
        layer.addSprite(new Sprite("trash", "trash", startX + 1800, startY, 0, 200, 300, Side.Front, Orientation.Normal));
    }

    public List<CardStack<IDominionCard>> getKingdomCardSupply () { return kingdomCardSupply; }
    public List<CardStack<IDominionCard>> getTreasureCardSupply () { return treasureCardSupply; }
    public List<CardStack<IDominionCard>> getVictoryCardSupply () { return victoryCardSupply; }
    public CardStack<IDominionCard> getTrashPile () { return trashPile; }
    
    public PlayerState getPlayerData (int playerNum) { return playerData.get(playerNum); }
    public PlayerState getCurrentPlayerData () { return playerData.get(currentPlayer); }
    
    public List<IDominionCard> getPlayedCards () { return playedCards; }
    public CardStack<IDominionCard> getSetAsideCards () { return setAsideCards; }
    
    public int getCurrentPlayer () { return currentPlayer; } 
    public IDominionCard getCurrentAction () { return currentAction; }
    
    public int getNumThroneRooms () { return numThroneRooms; }    
    public int getNumActions () { return numActions; } 
    public int getNumBuys () { return numBuys; }
    public int getNumCoins () { return numCoins; }
    
    public void nextPlayerTurn () {
        // TODO
        currentAction = null;
        numThroneRooms = 0;
        numActions = 0;
        numBuys = 0;
        numCoins = 0;        
    }
    
    private void updateUserInterface () {
    }
}
