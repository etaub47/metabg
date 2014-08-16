package models.dominion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import models.metabg.CardStack;
import models.metabg.CardStack.Type;

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
    
    public int countPoints (Input data) {
        int points = 0;
        for (IDominionCard card : getAllCards())
            points = card.getPoints(data);
        return points;
    }
    
    public void drawCardsIntoHand (int numCards) {
        hand.addAll(drawCards(numCards));
    }
    
    public void revealCards (int numCards) {
        revealedCards.addAll(drawCards(numCards));
    }
    
    private List<IDominionCard> drawCards (int numCards) {
        List<IDominionCard> drawnCards = new ArrayList<>(numCards);
        while (drawnCards.size() < numCards) {
            if (deck.isEmpty()) {
                if (discardPile.isEmpty()) 
                    break;
                deck = discardPile;
                discardPile = new CardStack<>(Type.Pile);
                deck.shuffle();                
            }            
            drawnCards.add(deck.drawFromTop());
        }
        return drawnCards;
    }
}
