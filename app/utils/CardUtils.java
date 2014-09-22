package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.metabg.ICard;

public class CardUtils
{
    public static void sortCards (List<? extends ICard> cards) { 
        Collections.sort(cards, new Comparator<ICard>() {
            @Override public int compare (ICard card1, ICard card2) {
                return card1.getSortKey().compareTo(card2.getSortKey());
            }
        }); 
    }
}
