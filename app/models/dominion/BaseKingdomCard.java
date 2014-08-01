package models.dominion;

import models.dominion.ActionFactory.PlayerAction;
import models.dominion.DominionCard.CardType;
import models.dominion.Effects.DrawCardsEffect.DrawCardsEffectType;
import models.dominion.Effects.GainCardEffect.GainCardEffectType;
import models.dominion.Effects.TrashCardEffect.TrashCardEffectType;
import models.dominion.Interfaces.IFunction;
import models.dominion.Interfaces.IPredicate;

public enum BaseKingdomCard implements IDominionCard
{
    Adventurer(new DominionCard.Builder()
        .name("Adventurer").resource("adventurer").cost(6)
        .type(CardType.KingdomCard).type(CardType.ActionCard)        
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.UntilTwoCoins, 0))
        .build()
    ),
    
    Bureaucrat(new DominionCard.Builder()
        .name("Bureaucrat").resource("bureaucrat").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.AttackCard)
        .effect(new Effects.GainCardEffect(true, GainCardEffectType.TopOfDeck, NonKingdomCard.Silver))
        .build()
    ),
    
    Cellar(new DominionCard.Builder()
        .name("Cellar").resource("cellar").cost(2)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(true, PlayerAction.CellarAction, new IPredicate() {
            @Override public boolean apply (Input data) {
                return !data.getActivePlayerState().getHand().isEmpty();
            }            
         }))
        .build()
    ),
    
    Chancellor(new DominionCard.Builder()
        .name("Chancellor").resource("chancellor").cost(3)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.IncreaseCoinsEffect(true, 2))
        .effect(new Effects.TriggerActionEffect(true, PlayerAction.ChancellorAction))
        .build()
    ),
    
    Chapel(new DominionCard.Builder()
        .name("Chapel").resource("chapel").cost(2)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(true, PlayerAction.ChapelAction, new IPredicate() {
            @Override public boolean apply (Input data) {
                return !data.getActivePlayerState().getHand().isEmpty();
            }            
         }))
        .build()
    ),
    
    CouncilRoom(new DominionCard.Builder()
        .name("Council Room").resource("councilroom").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.Standard, 4))
        .effect(new Effects.IncreaseBuysEffect(true, 1))
        .effect(new Effects.DrawCardsEffect(false, DrawCardsEffectType.Standard, 1))
        .build()
    ),
    
    Feast(new DominionCard.Builder()
        .name("Feast").resource("feast").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TrashCardEffect(TrashCardEffectType.PlayedCard))
        .effect(new Effects.TriggerActionEffect(true, PlayerAction.FeastAction))
        .build()
    ),
    
    Festival(new DominionCard.Builder()
        .name("Festival").resource("festival").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.IncreaseActionsEffect(true, 2))
        .effect(new Effects.IncreaseBuysEffect(true, 1))
        .effect(new Effects.IncreaseCoinsEffect(true, 2))
        .build()
    ),

    Gardens(new DominionCard.Builder()
        .name("Gardens").resource("gardens").cost(4)
        .type(CardType.KingdomCard).type(CardType.VictoryCard)
        .points(new IFunction() {
            @Override public Integer apply (Input data) {
                return data.getActivePlayerState().getAllCards().size() / 10;
            }
         })
        .build()
    ),
    
    Laboratory(new DominionCard.Builder()
        .name("Laboratory").resource("laboratory").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.Standard, 2))
        .effect(new Effects.IncreaseActionsEffect(true, 1))
        .build()
    ),
    
    Library(new DominionCard.Builder()
        .name("Library").resource("library").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.UpToPauseAfterAction, 7))
        .build()        
    ),
    
    Market(new DominionCard.Builder()
        .name("Market").resource("market").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.Standard, 1))
        .effect(new Effects.IncreaseActionsEffect(true, 1))
        .effect(new Effects.IncreaseBuysEffect(true, 1))
        .effect(new Effects.IncreaseCoinsEffect(true, 1))
        .build()
    ),
    
    Militia(new DominionCard.Builder()
        .name("Militia").resource("militia").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.AttackCard)
        .effect(new Effects.IncreaseCoinsEffect(true, 2))
        .effect(new Effects.TriggerActionEffect(false, PlayerAction.MilitiaAction, new IPredicate() {
            @Override public boolean apply (Input data) {
                return data.getActivePlayerState().getHand().size() > 3;
            }            
         }))
        .build()
    ),
    
    Mine(new DominionCard.Builder()
        .name("Mine").resource("mine").cost(5)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.TriggerActionEffect(true, PlayerAction.MineTrashAction, new IPredicate() {
            @Override public boolean apply (Input data) {
                for (IDominionCard card : data.getActivePlayerState().getHand())
                    if (card.isTreasureCard()) return true;
                return false;
            }            
         }))
        .build()
    ),
    
    Moat(new DominionCard.Builder()
        .name("Moat").resource("moat").cost(2)
        .type(CardType.KingdomCard).type(CardType.ActionCard).type(CardType.ReactionCard)
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.Standard, 2))
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
        .effect(new Effects.TriggerActionEffect(true, PlayerAction.RemodelTrashAction))
        .build()
    ),
    
    Smithy(new DominionCard.Builder()
        .name("Smithy").resource("smithy").cost(4)
        .type(CardType.KingdomCard).type(CardType.ActionCard)
        .effect(new Effects.DrawCardsEffect(true, DrawCardsEffectType.Standard, 3))
        .build()
    ),
    
    Spy(new DominionCard.Builder().build()),
    Thief(new DominionCard.Builder().build()),
    ThroneRoom(new DominionCard.Builder().build()),
    Village(new DominionCard.Builder().build()),
    Witch(new DominionCard.Builder().build()),
    Woodcutter(new DominionCard.Builder().build()),
    Workshop(new DominionCard.Builder().build());

    private IDominionCard card;
    
    BaseKingdomCard (IDominionCard card) { this.card = card; }

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
