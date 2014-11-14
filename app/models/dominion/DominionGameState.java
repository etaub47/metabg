package models.dominion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import models.dominion.GameModeFactory.GameMode;
import models.metabg.CardStack;
import models.metabg.CardStack.Type;
import models.metabg.Game;
import models.metabg.GameState;
import models.metabg.IGameModeFactory.IGameMode;
import models.metabg.Layer;
import models.metabg.Sprite;
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
    public static final String HAND = "Hand_";
    public static final String REVEALED = "Revealed_";
    
    public static enum GamePhase { Action, Buy, Cleanup }
    
    private final IGameMode mode;
    
    private List<CardStack<IDominionCard>> kingdomCardSupply = new ArrayList<>(10);
    private List<CardStack<IDominionCard>> treasureCardSupply = new ArrayList<>(3);
    private List<CardStack<IDominionCard>> victoryCardSupply = new ArrayList<>(4);
    private CardStack<IDominionCard> trashPile = new CardStack<>(Type.Pile);
    
    private List<PlayerState> playerData = new ArrayList<>();
    
    private List<IDominionCard> playedCards = new ArrayList<>();
    private CardStack<IDominionCard> setAsideCards = new CardStack<>(Type.Pile);
        
    private GamePhase currentPhase;    
    private int currentPlayer; // which player is taking their turn now
    private int selectedPlayer; // which player are we talking about now
    
    private IDominionCard currentAction = null;
    private int currentValue = 0;
    
    private int numThroneRooms = 0;    
    private int numActions = 1;
    private int numBuys = 1;
    private int numCoins = 0;

    public DominionGameState (Game game, int numPlayers, int numLayers, IGameMode mode) {
        super(game, numPlayers, numLayers);
        this.mode = mode;
    }
    
    public void init ()
    {        
        GameModeFactory gameModeFactory = GameModeFactory.getInstance();        
        Collection<? extends IDominionCard> cardSet = gameModeFactory.getCardSet((GameMode)mode);
        int numVictoryCards = ((getNumPlayers() == 2) ? 8 : 12);
        
        // set up kingdom card supply piles
        for (IDominionCard card : cardSet) {
            CardStack<IDominionCard> supplyPile = new CardStack<>(CardStack.Type.Pile);
            int pileSize = card.isVictoryCard() ? numVictoryCards : 10;
            for (int c = 0; c < pileSize; c++) 
                supplyPile.addToTop(card);            
            kingdomCardSupply.add(supplyPile);      
        }

        // set up treasure card supply piles        
        for (IDominionCard card : gameModeFactory.getTreasureCards()) {
            CardStack<IDominionCard> supplyPile = new CardStack<>(CardStack.Type.Pile);
            for (int c = 0; c < 60; c++) 
                supplyPile.addToTop(card);            
            treasureCardSupply.add(supplyPile);
        }

        // set up victory card supply piles        
        for (IDominionCard card : gameModeFactory.getVictoryCards()) {
            CardStack<IDominionCard> supplyPile = new CardStack<>(CardStack.Type.Pile);
            for (int c = 0; c < numVictoryCards; c++) 
                supplyPile.addToTop(card);            
            victoryCardSupply.add(supplyPile);
        }
        
        // set up players, initial phase, and user interface
        currentPlayer = Utils.getRandomInt(getNumPlayers());        
        for (int p = 0; p < getNumPlayers(); p++)
            playerData.add(new PlayerState());
        buyPhase();
        updateUserInterface();
    }

    public List<CardStack<IDominionCard>> getKingdomCardSupply () { return kingdomCardSupply; }
    public List<CardStack<IDominionCard>> getTreasureCardSupply () { return treasureCardSupply; }
    public List<CardStack<IDominionCard>> getVictoryCardSupply () { return victoryCardSupply; }
    public CardStack<IDominionCard> getTrashPile () { return trashPile; }
    public CardStack<IDominionCard> getSupplyFromCard (IDominionCard card) {
        for (CardStack<IDominionCard> supplyPile : kingdomCardSupply)
            if (!supplyPile.isEmpty() && supplyPile.peekTopCard().equals(card))
                return supplyPile;
        for (CardStack<IDominionCard> supplyPile : treasureCardSupply)
            if (!supplyPile.isEmpty() && supplyPile.peekTopCard().equals(card))
                return supplyPile;
        for (CardStack<IDominionCard> supplyPile : victoryCardSupply)
            if (!supplyPile.isEmpty() && supplyPile.peekTopCard().equals(card))
                return supplyPile;
        return null;
    }
    
    public PlayerState getPlayerData (int playerNum) { return playerData.get(playerNum); }
    
    public List<IDominionCard> getPlayedCards () { return playedCards; }
    public CardStack<IDominionCard> getSetAsideCards () { return setAsideCards; }
    
    public GamePhase getCurrentPhase () { return currentPhase; }
    public void setCurrentPhase (GamePhase currentPhase) { this.currentPhase = currentPhase; }
    
    public int getCurrentPlayer () { return currentPlayer; }
    public PlayerState getCurrentPlayerData () { return playerData.get(currentPlayer); }
    public String getCurrentPlayerName () { return getPlayerName(currentPlayer); } 
    public void setCurrentPlayer (int currentPlayer) { this.currentPlayer = currentPlayer; }

    public int getSelectedPlayer () { return selectedPlayer; } 
    public PlayerState getSelectedPlayerData () { return playerData.get(selectedPlayer); }
    public String getSelectedPlayerName () { return getPlayerName(selectedPlayer); } 
    public void setSelectedPlayer (int selectedPlayer) { this.selectedPlayer = selectedPlayer; }
    
    public IDominionCard getCurrentAction () { return currentAction; }
    public int getCurrentValue () { return currentValue; }
    public void setCurrentValue (int currentValue) { this.currentValue = currentValue; }
    
    public int getNumThroneRooms () { return numThroneRooms; } 
    
    public int getNumActions () { return numActions; } 
    public void incrementActions (int amount) { numActions += amount; }
    public void decrementActions (int amount) { numActions -= amount; }

    public int getNumBuys () { return numBuys; }
    public void incrementBuys (int amount) { numBuys += amount; }
    public void decrementBuys (int amount) { numBuys -= amount; }

    public int getNumCoins () { return numCoins; }    
    public void incrementCoins (int amount) { numCoins += amount; }
    public void decrementCoins (int amount) { numCoins -= amount; }
    
    @Override
    protected String getPhase () {
        String phase = currentPhase + " Phase: ";
        switch (currentPhase) {
            case Action: phase += numActions + " action" + ((numActions > 1) ? "s" : ""); break;
            case Buy: phase += numBuys + " buy" + ((numBuys > 1) ? "s, " : ", ") + numCoins + " coin" + ((numCoins > 1) ? "s" : ""); break;
            case Cleanup:
        }        
        return phase;
    }
    
    public CardStack<IDominionCard> getSupplyPileByRegionId (String regionId) {
        if (regionId.startsWith(KINGDOM)) 
            return kingdomCardSupply.get(Integer.valueOf(regionId.substring(KINGDOM.length())));
        else if (regionId.startsWith(TREASURE)) 
            return treasureCardSupply.get(Integer.valueOf(regionId.substring(TREASURE.length())));
        else if (regionId.startsWith(VICTORY)) 
            return victoryCardSupply.get(Integer.valueOf(regionId.substring(VICTORY.length())));
        else return null;
    }
    
    public void actionPhase () {
        setCurrentPhase(GamePhase.Action);
        addAction(ActionType.SelectAction, currentPlayer);        
    }
    
    public void buyPhase () {
        setCurrentPhase(GamePhase.Buy);
        addAction(ActionType.BuyAction, currentPlayer);
        Iterator<IDominionCard> cardIter = getCurrentPlayerData().getHand().iterator();
        while (cardIter.hasNext()) {
            IDominionCard card = cardIter.next();
            if (card.isTreasureCard()) {
                cardIter.remove();
                getPlayedCards().add(card);
                numCoins += card.getCoins(this);
            }            
        }
    }
    
    public void cleanupPhase () {
        setCurrentPhase(GamePhase.Cleanup);
        PlayerState currentPlayerData = getCurrentPlayerData();
        currentPlayerData.getDiscardPile().addToTop(playedCards);
        playedCards.clear();
        currentPlayerData.discardHand();
        currentPlayerData.drawCardsIntoHand(5);
    }
    
    public void nextPlayerTurn () {
        currentPhase = GamePhase.Action;         
        currentPlayer++;
        if (currentPlayer == getNumPlayers())
            currentPlayer = 0;        
        currentAction = null;
        numThroneRooms = 0;
        numActions = 1;
        numBuys = 1;
        numCoins = 0;        
    }
    
    public int countEmptySupplyPiles () {
        int counter = 0;
        for (CardStack<IDominionCard> pile : kingdomCardSupply)
            if (pile.isEmpty()) counter++;
        for (CardStack<IDominionCard> pile : victoryCardSupply)
            if (pile.isEmpty()) counter++;
        return counter;        
    }
    
    public boolean isGameOver () {
        return victoryCardSupply.get(2).isEmpty() || countEmptySupplyPiles() >= 3;
    }
    
    public void updateUserInterface () 
    {
        Layer layer = getUILayer(CARD_LAYER);
        int startX = SpriteUtils.centerSpriteOnTableX(2000);
        int startY = 75, s = 0, p = 0, c = 0, handSize;
        int numPlayers = playerData.size();
        
        // remove all sprites; we will regenerate everything from scratch in this method
        layer.removeAllSprites();        
        
        // supply piles
        for (CardStack<IDominionCard> supplyPile : kingdomCardSupply)
            layer.addClickableSprite(new Sprite.SpriteBuilder(KINGDOM + s, getResource(supplyPile.peekTopCard().getResourceKey()), 
                startX + (s % 5) * 220, startY + (s++ / 5) * 320, 0).build());
        s = 0;
        for (CardStack<IDominionCard> supplyPile : treasureCardSupply)
            layer.addClickableSprite(new Sprite.SpriteBuilder(TREASURE + s, getResource(supplyPile.peekTopCard().getResourceKey()), 
                startX + 1140 + s++ * 220, startY, 0).build());            
        s = 0;
        for (CardStack<IDominionCard> supplyPile : victoryCardSupply)
            layer.addClickableSprite(new Sprite.SpriteBuilder(VICTORY + s, getResource(supplyPile.peekTopCard().getResourceKey()), 
                startX + 1140 + s++ * 220, startY + 320, 0).build());
        layer.addSprite(new Sprite.SpriteBuilder("trash", getResource("trash"), startX + 1800, startY, 0).build());

        // players
        for (PlayerState player : playerData) {
            startX = getPlayerStartX(numPlayers, p);
            startY = getPlayerStartY(numPlayers, p);
            handSize = player.getHand().size();
            if (!player.getDeck().isEmpty())            
                layer.addSprite(new Sprite.SpriteBuilder("Deck_" + p, getResource(player.getDeck().peekTopCard().getResourceKey()), 
                    startX, startY, 0).side(Side.Back).build());
            if (!player.getDiscardPile().isEmpty())
                layer.addSprite(new Sprite.SpriteBuilder("Discard_" + p, getResource(player.getDiscardPile().peekTopCard().getResourceKey()), 
                    startX + 220, startY, 0).build());            
            c = 0;
            if (!player.getRevealedCards().isEmpty())
                for (IDominionCard card : player.getRevealedCards())
                    layer.addClickableSprite(new Sprite.SpriteBuilder(REVEALED + p + "_" + c, getResource(card.getResourceKey()), 
                        startX + 440 + (220 * c), startY, c++).tooltip(card.getName()).build());                
            startY += 320; c = 0;
            for (IDominionCard card : player.getHand())
                layer.addClickableSprite(new Sprite.SpriteBuilder(HAND + p + "_" + c, getResource(card.getResourceKey()), 
                    startX + ((800 / handSize - 1) * c), startY, c++).tooltip(card.getName()).visibleToPlayer(p).build());
            p++;
        }
        
        // played cards
        c = 0;
        for (IDominionCard card : playedCards)
            layer.addSprite(new Sprite.SpriteBuilder("Played_" + c, getResource(card.getResourceKey()), 
                SpriteUtils.centerSpriteOnTableX(200 * playedCards.size()) + (200 * c++), 710, 0).tooltip(card.getName()).build());   
    }
    
    private int getPlayerStartX (int numPlayers, int playerNum) {
        switch (playerNum) {
            case 0:
                if (numPlayers == 2 || numPlayers == 4 || numPlayers == 5)
                    return SpriteUtils.centerSpriteOnTableX(2100);
                else
                    return SpriteUtils.centerSpriteOnTableX(3200);
            case 1:
                if (numPlayers == 2 || numPlayers == 4 || numPlayers == 5)
                    return SpriteUtils.centerSpriteOnTableX(2100) + 1100;
                else
                    return SpriteUtils.centerSpriteOnTableX(3200) + 1100;
            case 2:
                if (numPlayers == 3 || numPlayers == 6)
                    return SpriteUtils.centerSpriteOnTableX(3200) + 2200;
                else if (numPlayers == 4)
                    return SpriteUtils.centerSpriteOnTableX(2100);
                else
                    return SpriteUtils.centerSpriteOnTableX(3200);
            case 3:
                if (numPlayers == 4)
                    return SpriteUtils.centerSpriteOnTableX(2100) + 1100;
                else if (numPlayers == 5)
                    return SpriteUtils.centerSpriteOnTableX(3200) + 1100;
                else
                    return SpriteUtils.centerSpriteOnTableX(3200);
            case 4:
                if (numPlayers == 5)
                    return SpriteUtils.centerSpriteOnTableX(3200) + 2200;
                else
                    return SpriteUtils.centerSpriteOnTableX(3200) + 1100;
            case 5:
                return SpriteUtils.centerSpriteOnTableX(3200) + 2200;
            default:
                return 0;
        }
    }
    
    private int getPlayerStartY (int numPlayers, int playerNum) {
        switch (playerNum) {
            case 0: return 1025;
            case 1: return 1025;
            case 2: return (numPlayers == 3 || numPlayers == 6) ? 1025 : 1665;
            case 3: return 1665;
            case 4: return 1665;
            case 5: return 1665;
            default: return 0;
        }
    }   
 }
