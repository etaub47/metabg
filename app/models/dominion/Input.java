package models.dominion;

public class Input
{
    private final DominionGameState gameState;
    private final int activePlayer;
    
    private Input (DominionGameState gameState, int activePlayer) {
        this.gameState = gameState;
        this.activePlayer = activePlayer;
    }
    
    public DominionGameState getGameState () { return gameState; }
    public PlayerState getActivePlayerState () { return gameState.getPlayerData(activePlayer); }
}
