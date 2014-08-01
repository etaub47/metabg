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
    
    public Collection<IDominionCard> getDeckPlusDiscardPile () {
        Collection<IDominionCard> cards = new ArrayList<>();
        cards.addAll(deck.getCards());
        cards.addAll(discardPile.getCards());
        return cards;
    }
}
