package models.dominion;

import models.metabg.ICard;

public interface IDominionCard extends ICard
{
    public int getCost (); 
    
    public boolean isKingdomCard ();
    public boolean isActionCard ();
    public boolean isTreasureCard ();
    public boolean isVictoryCard ();
    public boolean isAttackCard ();
    public boolean isReactionCard ();
    public boolean isCurseCard ();
    
    // only applicable for action cards
    public void play (DominionGameState state);
    public boolean canUndo (DominionGameState state);
    public void undo (DominionGameState state);

    // only applicable for treasure card
    public int getCoins (DominionGameState state);

    // only applicable for victory card
    public int getPoints (DominionGameState state);
    
    // only applicable for reaction cards
    public boolean react (DominionGameState state);
}
