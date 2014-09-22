package models.dominion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.dominion.Interfaces.IEffect;
import models.dominion.Interfaces.IPredicate;
import models.metabg.CardStack;
import com.google.common.collect.Lists;

public class Effects
{
    public static abstract class Effect implements IEffect 
    {            
        public enum AffectsType { CurrentPlayer, OtherPlayers, AllPlayers }

        protected AffectsType affects; // which player(s) is affected; defaults to current player
        protected IPredicate check; // check must be true or effect does not happen; optional
        
        protected Effect () { this.affects = AffectsType.CurrentPlayer; this.check = null; }
        protected Effect (AffectsType affects) { this.affects = affects; this.check = null; }
        protected Effect (IPredicate check) { this.affects = AffectsType.CurrentPlayer; this.check = check; }
        protected Effect (AffectsType affects, IPredicate check) { this.affects = affects; this.check = check; }

        @Override public boolean check (DominionGameState state) { return (check == null) ? true : check.apply(state); }
        @Override public boolean canUndo () { return true; }

        @Override 
        public List<Integer> getSelectedPlayers (DominionGameState state) {
            if (affects == AffectsType.CurrentPlayer)
                return Lists.newArrayList(state.getCurrentPlayer());
            else {            
                List<Integer> selectedPlayers = new ArrayList<>();
                for (int p = 0; p < state.getNumPlayers(); p++)
                    if (p != state.getCurrentPlayer() || affects == AffectsType.AllPlayers)
                        selectedPlayers.add(p);
                return selectedPlayers;
            }
        }
    }
    
    public static abstract class PermanentEffect extends Effect implements IEffect
    {
        private PermanentEffect () { super(); }
        private PermanentEffect (AffectsType affects, IPredicate check) { super(affects, check); }
        private PermanentEffect (AffectsType affects) { super(affects); }
        private PermanentEffect (IPredicate check) { super(check); }
        
        @Override public boolean canUndo () { return false; }
        @Override public void undo (DominionGameState state) { }        
    }
    
    public static class DrawCardsEffect extends PermanentEffect implements IEffect
    {
        public enum DrawCardsEffectType { Standard, Adventurer, Library }

        private final DrawCardsEffectType type;
        private final int numCards;
        
        public DrawCardsEffect (AffectsType affects, DrawCardsEffectType type, int numCards) {
            super(affects);
            this.type = type;
            this.numCards = numCards;
        }
        
        @Override 
        public void execute (DominionGameState state) 
        {
            PlayerState player = state.getSelectedPlayerData();
            switch (type) {
                case Standard: player.drawCardsIntoHand(numCards); break;
                case Adventurer: drawAdventurer(state, player); break;
                case Library: drawLibrary(state, player); break;
            }            
        }
        
        private void drawAdventurer (DominionGameState state, PlayerState player) {
            IDominionCard card;
            int numTreasureCardsFound = 0;
            while (numTreasureCardsFound < 2) {
                card = player.drawCard();
                if (card == null) 
                    break;
                player.getHand().add(card);
                if (card.isTreasureCard())
                    numTreasureCardsFound++;
            }
        }
        
        private void drawLibrary (DominionGameState state, PlayerState player) {
            IDominionCard card;
            while (player.getHand().size() < 7) {
                card = player.drawCard();
                if (card == null) 
                    break;
                player.getHand().add(card);
                if (card.isActionCard()) {
                    state.addAction(ActionType.LibraryAction, state.getSelectedPlayer());
                    break;
                }
            }            
        }        
    }

    public static class RevealCardsEffect extends PermanentEffect implements IEffect
    {
        private final int numCards;
        
        public RevealCardsEffect (AffectsType affects, int numCards) {
            super(affects);
            this.numCards = numCards;
        }
        
        @Override 
        public void execute (DominionGameState state) {
            state.getSelectedPlayerData().revealCards(numCards);
        }
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
        public void execute (DominionGameState state) {
            CardStack<IDominionCard> supply = state.getSupplyFromCard(card);
            if (supply != null) {
                IDominionCard actualCard = supply.drawFromTop();
                switch (type) {
                    case Standard: state.getSelectedPlayerData().getDiscardPile().addToTop(actualCard); break;
                    case TopOfDeck: state.getSelectedPlayerData().getDeck().addToTop(actualCard); break;
                    default:
                }
            }
        }

        @Override 
        public void undo (DominionGameState state) { 
            // TODO: put card back in supply (use state.mode to determine pile in case empty)
        }
    }
    
    public static class IncreaseActionsEffect extends Effect implements IEffect
    {
        private final int numActions;
        
        public IncreaseActionsEffect (int numActions) {
            this.numActions = numActions; 
        }
        
        @Override
        public void execute (DominionGameState state) {
            state.incrementActions(numActions);
        }

        @Override 
        public void undo (DominionGameState state) { 
            state.decrementActions(numActions);
        }        
    }
    
    public static class IncreaseCoinsEffect extends Effect implements IEffect
    {
        private final int numCoins;
        
        public IncreaseCoinsEffect (int numCoins) {
            this.numCoins = numCoins; 
        }
        
        @Override
        public void execute (DominionGameState state) {
            state.incrementCoins(numCoins);            
        }

        @Override 
        public void undo (DominionGameState state) { 
            state.decrementCoins(numCoins);
        }        
    }
    
    public static class IncreaseBuysEffect extends Effect implements IEffect
    {
        private final int numBuys;
        
        public IncreaseBuysEffect (int numBuys) {
            this.numBuys = numBuys; 
        }
        
        @Override
        public void execute (DominionGameState state) {
            state.incrementBuys(numBuys);            
        }

        @Override 
        public void undo (DominionGameState state) { 
            state.decrementBuys(numBuys);
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
        public void execute (DominionGameState state) {
            state.setCurrentValue(value);
            state.addAction(actionType, state.getSelectedPlayer());
        }
        
        @Override 
        public boolean canUndo () { 
            return affects == AffectsType.CurrentPlayer; 
        }
        
        @Override 
        public void undo (DominionGameState state) { 
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
        
        public TrashCardEffect (final IDominionCard card) {
            this.type = TrashCardEffectType.SpecificCard;
            this.card = card;
            this.check = new IPredicate() {
                @Override public boolean apply (DominionGameState state) {
                    return state.getSelectedPlayerData().getHand().contains(card);
                }
            };
        }
        
        @Override
        public void execute (DominionGameState state) {
            IDominionCard cardToTrash;
            switch (type) {
                case PlayedCard: 
                    cardToTrash = state.getPlayedCards().get(state.getPlayedCards().size() - 1);
                    state.getPlayedCards().remove(state.getPlayedCards().size() - 1);
                    state.getTrashPile().addToTop(cardToTrash);
                    break;
                case SpecificCard:
                    state.getSelectedPlayerData().getHand().remove(card);
                    state.getTrashPile().addToTop(card);
                    break;
            }
        }

        @Override 
        public void undo (DominionGameState state) { 
            // TODO: undo
        }        
    }
    
    public static class CancelAttackEffect extends PermanentEffect implements IEffect
    {
        @Override
        public void execute (DominionGameState state) {
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
        public void execute (DominionGameState state) {
            for (IEffect effect : effects)
                if (effect.check(state))
                    effect.execute(state);
        }

        @Override
        public void undo (DominionGameState state) {
            // TODO: undo all effects if possible (should this be permanent?)         
        }
    }    
}
