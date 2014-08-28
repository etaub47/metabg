package models.checkers;

import models.metabg.Event;
import models.metabg.Event.IEventType;
import models.metabg.GameState;

public enum EventType implements IEventType
{ 
    CheckerSelected {
        @Override public String validate (GameState state, Event event) {
            CheckersGameState gameState = (CheckersGameState) state;
            Checker checker = gameState.getCheckerById(event.getValue());
            if (checker.getOwner() != event.getPlayerNum())
                return "You may only move one of your own checkers.";
            return null;
        }
    },
    
    SquareSelected {
        @Override public String validate (GameState state, Event event) 
        {
            // pull out some data we are going to need from the game state and the current event
            CheckersGameState gameState = (CheckersGameState) state;
            Event firstEvent = gameState.getFirstSelection().getEvent(), lastEvent = gameState.getLastSelection().getEvent();
            Checker checker = gameState.getCheckerById(firstEvent.getValue());
            int currentPosition, newPosition = Integer.valueOf(event.getValue());
            boolean mustJump = false;

            // determine the current position of the selected checker
            EventType eventType = (EventType) lastEvent.getType();
            switch (eventType) {
                case CheckerSelected: currentPosition = gameState.getCheckerById(lastEvent.getValue()).getPosition(); break;
                case SquareSelected: currentPosition = Integer.valueOf(lastEvent.getValue()); mustJump = true; break;
                default: throw new IllegalStateException("Unexpected event found in game state");
            }

            // validate the move
            if (!gameState.isMoveValid(checker, currentPosition, newPosition, mustJump))
                return "That is not a valid move.";
            return null;
        }
    },
    
    TurnEnded, 
    UndoCheckerRequested, 
    UndoSquareRequested;

    @Override
    public String validate (GameState state, Event event) {
        return null;
    } 
}
