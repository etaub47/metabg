package models.dominion;

import models.dominion.DominionCard.CardType;

public enum NonKingdomCard implements IDominionCard
{
    Copper(new DominionCard.Builder().name("Copper").resource("copper").cost(0).type(CardType.TreasureCard).coins(1).build()),
    Silver(new DominionCard.Builder().name("Silver").resource("silver").cost(3).type(CardType.TreasureCard).coins(2).build()),
    Gold(new DominionCard.Builder().name("Gold").resource("gold").cost(6).type(CardType.TreasureCard).coins(3).build()),

    Estate(new DominionCard.Builder().name("Estate").resource("estate").cost(2).type(CardType.VictoryCard).points(1).build()),
    Duchy(new DominionCard.Builder().name("Duchy").resource("duchy").cost(5).type(CardType.VictoryCard).points(3).build()),
    Province(new DominionCard.Builder().name("Province").resource("province").cost(8).type(CardType.VictoryCard).points(6).build()),
    
    Curse(new DominionCard.Builder().name("Curse").resource("curse").cost(0).type(CardType.CurseCard).points(-1).build());
    
    private IDominionCard card;
    
    NonKingdomCard (IDominionCard card) { this.card = card; }

    @Override public String getName () { return card.getName(); }
    @Override public String getResourceKey () { return card.getResourceKey(); }
    @Override public String getSortKey () { return card.getSortKey(); }
    @Override public boolean isKingdomCard () { return card.isKingdomCard(); }
    @Override public boolean isActionCard () { return card.isActionCard(); }
    @Override public boolean isTreasureCard () { return card.isTreasureCard(); }
    @Override public boolean isVictoryCard () { return card.isVictoryCard(); }
    @Override public boolean isAttackCard () { return card.isAttackCard(); }
    @Override public boolean isReactionCard () { return card.isReactionCard(); }
    @Override public boolean isCurseCard () { return card.isCurseCard(); }
    @Override public void play (Input data) { card.play(data); }
    @Override public boolean canUndo (Input data) { return card.canUndo(data); }
    @Override public void undo (Input data) { card.undo(data); }
    @Override public int getCoins (Input data) { return card.getCoins(data); }
    @Override public int getPoints (Input data) { return card.getPoints(data); }
    @Override public boolean react (Input data) { return card.react(data); }    
}
