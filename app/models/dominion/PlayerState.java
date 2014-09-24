package models.dominion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import models.metabg.CardStack;
import models.metabg.CardStack.Type;
import utils.CardUtils;

public class PlayerState
{
    private CardStack<IDominionCard> deck = new CardStack<>(Type.Deck);
    private CardStack<IDominionCard> discardPile = new CardStack<>(Type.Pile);
    private List<IDominionCard> hand = new ArrayList<>();
    private List<IDominionCard> revealedCards = new ArrayList<>();
    
    public PlayerState () {
        for (int c = 0; c < 7; c++)        
            deck.addToBottom(NonKingdomCard.Copper);
        for (int c = 0; c < 3; c++)        
            deck.addToBottom(NonKingdomCard.Estate);
        deck.shuffle();
        drawCardsIntoHand(5);
        CardUtils.sortCards(hand);
    }
    
    public CardStack<IDominionCard> getDeck () { return deck; }
    public CardStack<IDominionCard> getDiscardPile () { return discardPile; }
    public List<IDominionCard> getHand () { return hand; }
    public List<IDominionCard> getRevealedCards () { return revealedCards; }
    
    public Collection<IDominionCard> getAllCards () {
        Collection<IDominionCard> cards = new ArrayList<>();
        cards.addAll(deck.getCards());
        cards.addAll(discardPile.getCards());
        cards.addAll(hand);
        cards.addAll(revealedCards);
        return cards;
    }
    
    public int countPoints (DominionGameState state) {
        int points = 0;
        for (IDominionCard card : getAllCards())
            points = card.getPoints(state);
        return points;
    }
    
    public void drawCardsIntoHand (int numCards) {
        hand.addAll(drawCards(numCards));
    }
    
    public void discardHand () {
        discardPile.addToTop(hand);
        hand.clear();
    }
    
    public IDominionCard getCardByRegionId (String regionId) {
        if (regionId.startsWith(DominionGameState.HAND))
            return hand.get(Integer.valueOf(regionId.substring(DominionGameState.HAND.length()).substring(2, 3)));
        else if (regionId.startsWith(DominionGameState.REVEALED)) 
            return revealedCards.get(Integer.valueOf(regionId.substring(DominionGameState.REVEALED.length()).substring(2, 3)));
        else return null;
    }
    
    public void revealCards (int numCards) {
        revealedCards.addAll(drawCards(numCards));
    }
    
    public List<IDominionCard> drawCards (int numCards) {
        List<IDominionCard> drawnCards = new ArrayList<>(numCards);
        while (drawnCards.size() < numCards) {
            IDominionCard card = drawCard();
            if (card == null)
                break;
            drawnCards.add(card);
        }
        return drawnCards;
    }
    
    public IDominionCard drawCard () {
        if (deck.isEmpty()) {
            if (discardPile.isEmpty()) 
                return null;
            deck.addAnywhere(discardPile);
            deck.shuffle();                
        }
        return deck.drawFromTop();        
    }

    public boolean hasActionCard () {
        for (IDominionCard card : hand)
            if (card.isActionCard()) return true;
        return false;        
    }
    
    public boolean hasTreasureCard () {
        for (IDominionCard card : hand)
            if (card.isTreasureCard()) return true;
        return false;        
    }
}
