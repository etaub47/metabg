package models.dominion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.dominion.ActionFactory.PlayerAction;
import models.dominion.Interfaces.IEffect;
import models.dominion.Interfaces.IPredicate;

public class Effects
{
    private static abstract class Effect implements IEffect 
    {
        protected final boolean self; // false = affects other players
        protected final IPredicate check; // check must be true or effect does not happen
        
        protected Effect () { this.self = true; this.check = null; }
        protected Effect (boolean self) { this.self = self; this.check = null; }
        protected Effect (IPredicate check) { this.self = true; this.check = check; }
        protected Effect (boolean self, IPredicate check) { this.self = self; this.check = check; }
        
        public boolean isSelf () { return self; }
        public boolean check (Input data) { return (check == null) ? true : check.apply(data); }

        @Override public boolean canUndo () { return true; }
    }
    
    private static abstract class PermanentEffect extends Effect implements IEffect
    {
        private PermanentEffect () { super(); }
        private PermanentEffect (boolean self, IPredicate check) { super(self, check); }
        private PermanentEffect (boolean self) { super(self); }
        private PermanentEffect (IPredicate check) { super(check); }
        
        @Override public boolean canUndo () { return false; }
        @Override public void undo (Input data) { }        
    }
    
    public static class DrawCardsEffect extends PermanentEffect implements IEffect
    {
        public enum DrawCardsEffectType { Standard, UntilTwoCoins, UpToPauseAfterAction }

        private final DrawCardsEffectType type;
        private final int numCards;
        
        public DrawCardsEffect (boolean self, DrawCardsEffectType type, int numCards) {
            super(self);
            this.type = type;
            this.numCards = numCards;
        }
        
        @Override 
        public void execute (Input data) {
            // TODO: draw cards
        }
    }
    
    public static class GainCardEffect extends Effect implements IEffect
    {
        public enum GainCardEffectType { Standard, TopOfDeck }

        private final GainCardEffectType type;
        private final IDominionCard card;
        
        public GainCardEffect (boolean self, GainCardEffectType type, IDominionCard card) {
            super(self);
            this.type = type;
            this.card = card;
        }
        
        @Override 
        public void execute (Input data) {
            // TODO: gain card from supply            
        }

        @Override 
        public void undo (Input data) { 
            // TODO: put card back in supply
        }
    }
    
    public static class IncreaseActionsEffect extends Effect implements IEffect
    {
        private final int numActions;
        
        public IncreaseActionsEffect (boolean self, int numActions) {
            super(self);
            this.numActions = numActions; 
        }
        
        @Override
        public void execute (Input data) {
            // TODO: increase actions            
        }

        @Override 
        public void undo (Input data) { 
            // TODO: decrease actions
        }        
    }
    
    public static class IncreaseCoinsEffect extends Effect implements IEffect
    {
        private final int numCoins;
        
        public IncreaseCoinsEffect (int numCoins) {
            this.numCoins = numCoins; 
        }
        
        public IncreaseCoinsEffect (boolean self, int numCoins) {
            super(self);
            this.numCoins = numCoins; 
        }
        
        @Override
        public void execute (Input data) {
            // TODO: increase coins            
        }

        @Override 
        public void undo (Input data) { 
            // TODO: decrease coins
        }        
    }
    
    public static class IncreaseBuysEffect extends Effect implements IEffect
    {
        private final int numBuys;
        
        public IncreaseBuysEffect (boolean self, int numBuys) {
            super(self);
            this.numBuys = numBuys; 
        }
        
        @Override
        public void execute (Input data) {
            // TODO: increase buys            
        }

        @Override 
        public void undo (Input data) { 
            // TODO: decrease buys
        }        
    }
    
    public static class TriggerActionEffect extends Effect implements IEffect
    {
        private final PlayerAction action;
     
        public TriggerActionEffect (boolean self, PlayerAction action) {
            super(self, null);
            this.action = action;
        }

        public TriggerActionEffect (boolean self, PlayerAction action, IPredicate check) {
            super(self, check);
            this.action = action;
        }

        /* TODO
        super(self, new IPredicate() {
            @Override public boolean apply (@Nullable Input input) {
                return !input.getActivePlayerState().getDeckPlusDiscardPile().isEmpty(); 
            }
        });
        */        
        
        @Override
        public void execute (Input data) {
            // TODO: trigger action            
        }
        
        @Override public boolean canUndo () { 
            return self; 
        }
        
        @Override public void undo (Input data) { 
            // TODO: undo trigger action
        }                
    }
    
    public static class TrashCardEffect extends Effect implements IEffect
    {
        public enum TrashCardEffectType { PlayedCard, SpecificCard }

        private final TrashCardEffectType type;
        private final IDominionCard card;
        
        public TrashCardEffect (TrashCardEffectType type) {
            this.type = type;
            this.card = null;
        }
        
        public TrashCardEffect (IDominionCard card) {
            this.type = TrashCardEffectType.SpecificCard;
            this.card = card;
        }
        
        @Override
        public void execute (Input data) {
            // TODO: trash this card            
        }

        @Override public void undo (Input data) { 
            // TODO: undo
        }        
    }
    
    public static class CancelAttackEffect extends PermanentEffect implements IEffect
    {
        @Override
        public void execute (Input data) {
            // TODO: cancel the attack on the active player
        }
    }
    
    public static class ChainedEffect extends Effect implements IEffect
    {
        private final List<IEffect> effects;
        
        public ChainedEffect (IEffect... effects) {
            this.effects = new ArrayList<>(Arrays.asList(effects));
        }
        
        @Override
        public void execute (Input data) {
            // TODO: execute all effects until one fails predicate
        }

        @Override
        public void undo (Input data) {
            // TODO: undo all effects if possible (should this be permanent?)         
        }
    }    
}
