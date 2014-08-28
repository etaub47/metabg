package models.dominion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.dominion.Interfaces.IEffect;
import models.dominion.Interfaces.IPredicate;
import com.google.common.collect.Lists;

public class Effects
{
    public static abstract class Effect implements IEffect 
    {            
        public enum AffectsType { CurrentPlayer, OtherPlayers, AllPlayers }

        private final AffectsType affects; // which player(s) is affected; defaults to current player
        private final IPredicate check; // check must be true or effect does not happen; optional
        
        protected Effect () { this.affects = AffectsType.CurrentPlayer; this.check = null; }
        protected Effect (AffectsType affects) { this.affects = affects; this.check = null; }
        protected Effect (IPredicate check) { this.affects = AffectsType.CurrentPlayer; this.check = check; }
        protected Effect (AffectsType affects, IPredicate check) { this.affects = affects; this.check = check; }

        @Override public boolean check (Input data) { 
            return (check == null) ? true : check.apply(data); 
        }

        @Override 
        public boolean canUndo () { 
            return true; 
        }

        public List<Input> crateInputStates (DominionGameState state) {
            if (affects == AffectsType.CurrentPlayer)
                return Lists.newArrayList(new Input(state, state.getCurrentPlayer()));
            else {            
                List<Input> inputStates = new ArrayList<>();
                for (int p = 0; p < state.getNumPlayers(); p++)
                    if (p != state.getCurrentPlayer() || affects == AffectsType.AllPlayers)
                        inputStates.add(new Input(state, p));
                return inputStates;
            }
        }
        
        protected boolean affectsCurrentPlayer () {
            return affects == AffectsType.CurrentPlayer;
        }
    }
    
    public static abstract class PermanentEffect extends Effect implements IEffect
    {
        private PermanentEffect () { super(); }
        private PermanentEffect (AffectsType affects, IPredicate check) { super(affects, check); }
        private PermanentEffect (AffectsType affects) { super(affects); }
        private PermanentEffect (IPredicate check) { super(check); }
        
        @Override public boolean canUndo () { return false; }
        @Override public void undo (Input data) { }        
    }
    
    public static class DrawCardsEffect extends PermanentEffect implements IEffect
    {
        public enum DrawCardsEffectType { Standard, UntilTwoCoins, PauseAfterAction }

        private final DrawCardsEffectType type;
        private final int numCards;
        
        public DrawCardsEffect (AffectsType affects, DrawCardsEffectType type, int numCards) {
            super(affects);
            this.type = type;
            this.numCards = numCards;
        }
        
        @Override 
        public void execute (Input data) {
            switch (type) {
                case Standard: data.getActivePlayerState().drawCardsIntoHand(numCards); break;
                case UntilTwoCoins: /* TODO */ break;
                case PauseAfterAction: /* TODO */ break;
            }            
        }
        
        // TODO: implicit check: at least one card in deck or discard pile
    }

    public static class RevealCardsEffect extends PermanentEffect implements IEffect
    {
        private final int numCards;
        
        public RevealCardsEffect (AffectsType affects, int numCards) {
            super(affects);
            this.numCards = numCards;
        }
        
        @Override 
        public void execute (Input data) {
            data.getActivePlayerState().revealCards(numCards);
        }
        
        // TODO: implicit check: at least one card in deck or discard pile
    }
    
    
    public static class GainCardEffect extends Effect implements IEffect
    {
        public enum GainCardEffectType { Standard, TopOfDeck }

        private final GainCardEffectType type;
        private final IDominionCard card;
        
        public GainCardEffect (AffectsType affects, GainCardEffectType type, IDominionCard card) {
            super(affects);
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
        
        public IncreaseActionsEffect (int numActions) {
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
        
        public IncreaseBuysEffect (int numBuys) {
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
        private final ActionType actionType;
        private final int value;
     
        public TriggerActionEffect (AffectsType affects, ActionType actionType) {
            super(affects);
            this.actionType = actionType;
            this.value = 0;
        }

        public TriggerActionEffect (AffectsType affects, ActionType actionType, int value) {
            super(affects);
            this.actionType = actionType;
            this.value = value;
        }

        public TriggerActionEffect (AffectsType affects, ActionType actionType, IPredicate check) {
            super(affects, check);
            this.actionType = actionType;
            this.value = 0;
        }

        public TriggerActionEffect (AffectsType affects, ActionType actionType, int value, IPredicate check) {
            super(affects, check);
            this.actionType = actionType;
            this.value = value;
        }

        @Override
        public void execute (Input data) {
            // TODO: trigger action            
        }
        
        @Override 
        public boolean canUndo () { 
            return affectsCurrentPlayer(); 
        }
        
        @Override 
        public void undo (Input data) { 
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

        @Override 
        public void undo (Input data) { 
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
