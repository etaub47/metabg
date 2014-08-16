package models.metabg;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class CardStack<T extends ICard>
{
    public enum Type { Deck, Pile };

    private final Type type;
    private final Deque<T> cards = new ArrayDeque<>();
    
    public CardStack (Type type) { this.type = type; }

    public int getCount () { return this.cards.size(); }
    public Collection<T> getCards () { return this.cards; }
    public boolean isEmpty () { return this.cards.isEmpty(); }
    public void clear () { cards.clear(); }
    
    public Type getType () { return type; }
    public boolean isDeck () { return type == Type.Deck; }
    public boolean isPile () { return type == Type.Pile; }

    public T peekTopCard () { return this.cards.peekFirst(); }
    public T peekBottomCard () { return this.cards.peekLast(); }
    
    public void addToTop (T card) { this.cards.addFirst(card); }
    public void addToTop (Collection<T> cards) { for (T card : cards) addToTop(card); }
    public void addToTop (CardStack<T> stack) { addToTop(stack.getCards()); stack.clear(); }

    public void addToBottom (T card) { this.cards.addLast(card); }
    public void addToBottom (Collection<T> cards) { for (T card : cards) addToBottom(card); }
    public void addToBottom (CardStack<T> stack) { addToBottom(stack.getCards()); stack.clear(); }

    public T drawFromTop () { return this.cards.removeFirst(); }
    public T drawFromBottom () { return this.cards.removeLast(); }
    
    public void drawFromTop (List<T> hand, int quantity) {
        quantity = quantity <= cards.size() ? quantity : cards.size();
        for (int idx = 0; idx < quantity; idx++)
            hand.add(cards.removeFirst());
    }
    
    public void shuffle () {
        List<T> randomAccessList = new ArrayList<>(this.cards);
        Collections.shuffle(randomAccessList);
        clear();
        addToTop(randomAccessList);        
    }    
}
