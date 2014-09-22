package models.dominion;

import models.dominion.DominionGameState.GamePhase;
import models.metabg.CardStack;
import models.metabg.Event;
import models.metabg.GameState;
import models.metabg.IGameLogic;
import models.metabg.Result;
import models.metabg.Result.ResultType;

public class DominionLogic implements IGameLogic
{
    @Override
    public Result processEvent (GameState state, Event event) throws Exception
    {
        DominionGameState gameState = (DominionGameState) state; 
        EventType eventType = (EventType) event.getType();
        
        switch (eventType) {
            case PLAY_ACTION_CARD: playActionCard(gameState, event);
            case BUY_CARD: return buyCard(gameState, event); 
            default: throw new IllegalArgumentException("Invalid event type received: " + event);
        }
    }
    
    private Result playActionCard (DominionGameState gameState, Event event) throws Exception
    {
        // determine the action card selected
        PlayerState player = gameState.getPlayerData(event.getPlayerNum());
        IDominionCard card = player.getCardByRegionId(event.getValue());
        
        // decrement the available actions, and move the card from the player's hand to the player cards
        gameState.decrementActions(1);
        player.getHand().remove(card);
        gameState.getPlayedCards().add(card);
        
        // now let's see which action card was played and do the appropriate thing
        card.play(gameState);
        
        // if the action card itself didn't set up the next expected action, then it is our responsibility
        if (!gameState.hasActions())
            determineNextAction(gameState);        
        
        // update the user interface and the players' state along with what just happened
        gameState.updateUserInterface();
        String whatHappened = gameState.getPlayerName(event.getPlayerNum()) + " plays a " + card.getName();        
        return new Result(ResultType.STATE_CHANGE, whatHappened);        
    }
    
    private Result buyCard (DominionGameState gameState, Event event) throws Exception
    {
        // determine the supply card pile and draw the top card
        CardStack<IDominionCard> supplyPile = gameState.getSupplyPileByRegionId(event.getValue());
        IDominionCard card = supplyPile.drawFromTop();
        
        // decrement buys and coins available; add the purchased card to the player's discard pile
        gameState.decrementBuys(1);
        gameState.decrementCoins(card.getCost());        
        gameState.getPlayerData(event.getPlayerNum()).getDiscardPile().addToTop(card);
        
        // determine the next action
        determineNextAction(gameState);        
        
        // update the user interface and the players' state along with what just happened
        gameState.updateUserInterface();
        String whatHappened = gameState.getPlayerName(event.getPlayerNum()) + " buys a " + card.getName();
        return new Result(ResultType.STATE_CHANGE, whatHappened);        
    }
    
    private void determineNextAction (DominionGameState gameState) {
        while (true) {
            switch (gameState.getCurrentPhase()) 
            {
                case ActionPhase:
                    if (gameState.getNumActions() == 0)
                        gameState.setCurrentPhase(GamePhase.BuyPhase);
                    else {
                        gameState.actionPhase();
                        return;
                    }

                case BuyPhase:
                    if (gameState.getNumBuys() == 0)
                        gameState.setCurrentPhase(GamePhase.CleanupPhase);
                    else {
                        gameState.buyPhase();                        
                        return;                    
                    }

                case CleanupPhase:
                    gameState.cleanupPhase();
                    if (!gameState.isGameOver())
                        gameState.nextPlayerTurn();
                    else {
                        // TODO: game over
                        return;
                    }
            }
        }
    }    
}
