package models.dominion;

import models.metabg.ICard;

public interface IDominionCard extends ICard
{
    public boolean isKingdomCard ();
    public boolean isActionCard ();
    public boolean isTreasureCard ();
    public boolean isVictoryCard ();
    public boolean isAttackCard ();
    public boolean isReactionCard ();
    public boolean isCurseCard ();
    
    // only applicable for action cards
    public void play (Input data);
    public boolean canUndo (Input data);
    public void undo (Input data);

    // only applicable for treasure card
    public int getCoins (Input data);

    // only applicable for victory card
    public int getPoints (Input data);
    
    // only applicable for reaction cards
    public boolean react (Input data);
}
