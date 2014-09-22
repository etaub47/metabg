package models.dominion;

import models.dominion.DominionCard.CardType;
import models.dominion.Effects.DrawCardsEffect.DrawCardsEffectType;
import models.dominion.Effects.Effect.AffectsType;
import models.dominion.Effects.GainCardEffect.GainCardEffectType;
import models.dominion.Effects.TrashCardEffect.TrashCardEffectType;
import models.dominion.Interfaces.IFunction;
import models.dominion.Interfaces.IPredicate;

public enum BaseKingdomCard implements IDominionCard
{
    Adventurer(new DominionCard.Builder()
        .name("Adventurer").resource("adventurer").cost(6)
        .type(CardType.KingdomCard).type(CardType.ActionCard)        
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Adventurer, 0))
        .build()
    ),
    
    Bureaucrat(new DominionCard.Builder()
        .name("Bureaucrat").resource("bureaucrat").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.AttackCard)
        .effect(new Effects.GainCardEffect(AffectsType.CurrentPlayer, GainCardEffectType.TopOfDeck, NonKingdomCard.Silver))
        .build()
    ),
    
    Cellar(new DominionCard.Builder()
        .name("Cellar").resource("cellar").cost(2)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.CellarAction, new IPredicate() {
            @Override public boolean apply (DominionGameState state) {
                return !state.getSelectedPlayerData().getHand().isEmpty();
            }            
         }))
        .build()
    ),
    
    Chancellor(new DominionCard.Builder()
        .name("Chancellor").resource("chancellor").cost(3)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.IncreaseCoinsEffect(2))
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.ChancellorAction))
        .build()
    ),
    
    Chapel(new DominionCard.Builder()
        .name("Chapel").resource("chapel").cost(2)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.ChapelAction, new IPredicate() {
            @Override public boolean apply (DominionGameState state) {
                return !state.getSelectedPlayerData().getHand().isEmpty();
            }            
         }))
        .build()
    ),
    
    CouncilRoom(new DominionCard.Builder()
        .name("Council Room").resource("councilroom").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 4))
        .effect(new Effects.IncreaseBuysEffect(1))
        .effect(new Effects.DrawCardsEffect(AffectsType.OtherPlayers, DrawCardsEffectType.Standard, 1))
        .build()
    ),
    
    Feast(new DominionCard.Builder()
        .name("Feast").resource("feast").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TrashCardEffect(TrashCardEffectType.PlayedCard))
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.GainCardAction, 5))
        .build()
    ),
    
    Festival(new DominionCard.Builder()
        .name("Festival").resource("festival").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.IncreaseActionsEffect(2))
        .effect(new Effects.IncreaseBuysEffect(1))
        .effect(new Effects.IncreaseCoinsEffect(2))
        .build()
    ),

    Gardens(new DominionCard.Builder()
        .name("Gardens").resource("gardens").cost(4)
        .type(CardType.KingdomCard).type(CardType.VictoryCard)
        .points(new IFunction() {
            @Override public Integer apply (DominionGameState state) {
                return state.getSelectedPlayerData().getAllCards().size() / 10;
            }
         })
        .build()
    ),
    
    Laboratory(new DominionCard.Builder()
        .name("Laboratory").resource("laboratory").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 2))
        .effect(new Effects.IncreaseActionsEffect(1))
        .build()
    ),
    
    Library(new DominionCard.Builder()
        .name("Library").resource("library").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Library, 7))
        .build()        
    ),
    
    Market(new DominionCard.Builder()
        .name("Market").resource("market").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 1))
        .effect(new Effects.IncreaseActionsEffect(1))
        .effect(new Effects.IncreaseBuysEffect(1))
        .effect(new Effects.IncreaseCoinsEffect(1))
        .build()
    ),
    
    Militia(new DominionCard.Builder()
        .name("Militia").resource("militia").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.AttackCard)
        .effect(new Effects.IncreaseCoinsEffect(2))
        .effect(new Effects.TriggerActionEffect(AffectsType.OtherPlayers, ActionType.MilitiaAction, new IPredicate() {
            @Override public boolean apply (DominionGameState state) {
                return state.getSelectedPlayerData().getHand().size() > 3;
            }            
         }))
        .build()
    ),
    
    Mine(new DominionCard.Builder()
        .name("Mine").resource("mine").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.MineTrashAction, new IPredicate() {
            @Override public boolean apply (DominionGameState state) {
                for (IDominionCard card : state.getSelectedPlayerData().getHand())
                    if (card.isTreasureCard()) return true;
                return false;
            }            
         }))
        .build()
    ),
    
    Moat(new DominionCard.Builder()
        .name("Moat").resource("moat").cost(2)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.ReactionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 2))
        .reaction(new Effects.CancelAttackEffect())
        .build()
    ),

    Moneylender(new DominionCard.Builder()
        .name("Moneylender").resource("moneylender").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.ChainedEffect(
            new Effects.TrashCardEffect(NonKingdomCard.Copper),
            new Effects.IncreaseCoinsEffect(3)))
        .build()
    ),

    Remodel(new DominionCard.Builder()
        .name("Remodel").resource("remodel").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.RemodelAction))
        .build()
    ),
    
    Smithy(new DominionCard.Builder()
        .name("Smithy").resource("smithy").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 3))
        .build()
    ),
    
    Spy(new DominionCard.Builder()
        .name("Spy").resource("spy").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.AttackCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 1))
        .effect(new Effects.IncreaseActionsEffect(1))
        .effect(new Effects.RevealCardsEffect(AffectsType.AllPlayers, 1))
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.SpyAction))
        .build()
    ),
    
    Thief(new DominionCard.Builder().build()),
    
    ThroneRoom(new DominionCard.Builder().build()),
    
    Village(new DominionCard.Builder()
        .name("Village").resource("village").cost(3)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 1))
        .effect(new Effects.IncreaseActionsEffect(2))
        .build()
    ),
    
    Witch(new DominionCard.Builder()
        .name("Witch").resource("witch").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.AttackCard)
        .effect(new Effects.DrawCardsEffect(AffectsType.CurrentPlayer, DrawCardsEffectType.Standard, 2))
        .effect(new Effects.GainCardEffect(AffectsType.OtherPlayers, GainCardEffectType.Standard, NonKingdomCard.Curse))
        .build()
    ),
    
    Woodcutter(new DominionCard.Builder()
        .name("Woodcutter").resource("woodcutter").cost(3)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.IncreaseBuysEffect(1))
        .effect(new Effects.IncreaseCoinsEffect(2))
        .build()
    ),    

    Workshop(new DominionCard.Builder()
        .name("Workshop").resource("workshop").cost(3)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(AffectsType.CurrentPlayer, ActionType.GainCardAction, 4))
        .build()
    );

    private IDominionCard card;
    
    BaseKingdomCard (IDominionCard card) { this.card = card; }

    @Override public String getName () { return card.getName(); }
    @Override public String getResourceKey () { return card.getResourceKey(); }
    @Override public String getSortKey () { return card.getSortKey(); }
    @Override public int getCost () { return card.getCost(); }    
    @Override public boolean isKingdomCard () { return card.isKingdomCard(); }
    @Override public boolean isActionCard () { return card.isActionCard(); }
    @Override public boolean isTreasureCard () { return card.isTreasureCard(); }
    @Override public boolean isVictoryCard () { return card.isVictoryCard(); }
    @Override public boolean isAttackCard () { return card.isAttackCard(); }
    @Override public boolean isReactionCard () { return card.isReactionCard(); }
    @Override public boolean isCurseCard () { return card.isCurseCard(); }
    @Override public void play (DominionGameState state) { card.play(state); }
    @Override public boolean canUndo (DominionGameState state) { return card.canUndo(state); }
    @Override public void undo (DominionGameState state) { card.undo(state); }
    @Override public int getCoins (DominionGameState state) { return card.getCoins(state); }
    @Override public int getPoints (DominionGameState state) { return card.getPoints(state); }
    @Override public boolean react (DominionGameState state) { return card.react(state); }
}
