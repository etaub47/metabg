package models.dominion;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import models.dominion.Interfaces.IEffect;
import models.dominion.Interfaces.IFunction;

public class DominionCard implements IDominionCard
{
    public enum CardType { KingdomCard, ActionCard, TreasureCard, VictoryCard, AttackCard, ReactionCard, CurseCard }
    
    private final String name;
    private final String resourceKey;
    private final String sortKey;
    private final int cost;
    
    private final Set<CardType> types;
    private final List<IEffect> effects;
    private final List<IEffect> reactions;
    private final IFunction points;
    private final IFunction coins;
    
    private DominionCard (String name, String resourceKey, String sortKey, int cost, Set<CardType> types, List<IEffect> effects,
        List<IEffect> reactions, IFunction points, IFunction coins)
    {
        this.name = name;
        this.resourceKey = resourceKey;
        this.sortKey = sortKey;
        this.cost = cost;
        this.types = types;
        this.effects = effects;
        this.reactions = reactions;
        this.points = points;
        this.coins = coins;
    }
    
    @Override public String getName () { return name; }
    @Override public String getResourceKey () { return resourceKey; }
    @Override public String getSortKey () { return sortKey; }
    @Override public int getCost () { return cost; }
    
    @Override public boolean isKingdomCard () { return types.contains(CardType.KingdomCard); }
    @Override public boolean isActionCard () { return types.contains(CardType.ActionCard); }
    @Override public boolean isTreasureCard () { return types.contains(CardType.TreasureCard); }
    @Override public boolean isVictoryCard () { return types.contains(CardType.VictoryCard); }
    @Override public boolean isAttackCard () { return types.contains(CardType.AttackCard); }
    @Override public boolean isReactionCard () { return types.contains(CardType.ReactionCard); }
    @Override public boolean isCurseCard () { return types.contains(CardType.CurseCard); }
    
    @Override public int getPoints (Input data) { return points.apply(data); }
    @Override public int getCoins (Input data) { return coins.apply(data); }
    
    public void play (Input data) {
        // TODO: play all effects
    }
    
    public boolean canUndo (Input data) {
        // TODO: consider all effects
        return true;
    }
    
    public void undo (Input data) {
        // TODO: undo all effects
    }

    public boolean react (Input data) {
        // TODO
        return true;
    }
    
    public static class Builder
    {
        private String name;
        private String resourceKey;
        private String sortKey;
        
        private Set<CardType> types = EnumSet.noneOf(CardType.class);
        private int cost;
        
        private List<IEffect> effects = new ArrayList<>();
        private List<IEffect> reactions = new ArrayList<>();
        private IFunction points;
        private IFunction coins;
        
        public Builder name (String name) { this.name = name; return this; }
        public Builder resource (String resourceKey) { this.resourceKey = resourceKey; return this; }
        public Builder cost (int cost) { this.cost = cost; return this; }
        public Builder type (CardType type) { this.types.add(type); return this; }
        
        public Builder effect (IEffect effect) { this.effects.add(effect); return this; }        
        public Builder reaction (IEffect effect) { this.reactions.add(effect); return this; }        
        public Builder points (IFunction points) { this.points = points; return this; }
        public Builder coins (IFunction coins) { this.coins = coins; return this; }
        
        public Builder points (final int points) {
            this.points = new IFunction() {
                @Override public Integer apply (Input data) {
                    return points;
                }
            };
            return this;
        }

        public Builder coins (final int coins) {
            this.points = new IFunction() {
                @Override public Integer apply (Input data) {
                    return coins;
                }
            };
            return this;
        }
        
        public IDominionCard build () {
            String sortPrefix = (types.contains(CardType.ActionCard)) ? "A_" : (types.contains(CardType.TreasureCard)) ? "B_" : "C_";
            this.sortKey = sortPrefix + name;
            return new DominionCard(name, resourceKey, sortKey, cost, types, effects, reactions, points, coins);
        }
    }
}
