package models.metabg;

import java.util.HashSet;
import java.util.Set;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class GameState
{
    public enum Status { WaitingForConnections, InProgress, WaitingForReconnections, GameOver }
    
    protected UserInterface userInterface;
    protected Set<Action> expectedActions;
    protected Status status;
    protected String[] playerNames;
    protected int[] scores;
    protected Set<Integer> disconnectedPlayers;
    
    public GameState (int numPlayers, int numLayers) {
        userInterface = new UserInterface(numLayers);
        expectedActions = new HashSet<>(numPlayers);
        status = Status.WaitingForConnections;
        playerNames = new String[numPlayers];
        scores = new int[numPlayers];
        disconnectedPlayers = new HashSet<>(numPlayers);
        for (int p = 0; p < numPlayers; p++)
            disconnectedPlayers.add(p);
    }
    
    public Status getStatus () { return status; }
    
    public void playerConnected (int seatNum) {
        disconnectedPlayers.remove(seatNum);
        if (disconnectedPlayers.isEmpty()) {
            if (status == Status.WaitingForConnections)
                init();
            if (status == Status.WaitingForConnections || status == Status.WaitingForReconnections)
                status = Status.InProgress;
        }
    }
    
    public void playerDisconnected (int seatNum) {
        if (status == Status.InProgress)
            status = Status.WaitingForReconnections;
        disconnectedPlayers.add(seatNum);
    }
    
    public void addPlayerName (int seatNum, String playerName) { 
        playerNames[seatNum] = playerName; 
    }
    
    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("userInterface", getUserInterfaceJson());
        result.put("expectedActions", getExpectedActionsJson());
        result.put("status", status.toString());
        result.put("playerNames", getPlayerNamesJson());
        result.put("scores", getScoresJson());
        result.put("disconnected", getDisconnectedPlayersJson());
        return result;
    }

    protected void init () { initUserInterface(); initState(); }
    protected abstract void initUserInterface ();
    protected abstract void initState ();
    
    private JsonNode getUserInterfaceJson () {
        return userInterface.getJson();        
    }
    
    private JsonNode getExpectedActionsJson () {
        ArrayNode expectedActionsJson = JsonNodeFactory.instance.arrayNode();
        for (Action expectedAction : expectedActions)
            expectedActionsJson.add(expectedAction.getJson());            
        return expectedActionsJson;
    }
    
    private JsonNode getPlayerNamesJson () {
        ArrayNode playerNamesJson = JsonNodeFactory.instance.arrayNode();
        for (String playerName : playerNames)
            playerNamesJson.add(playerName);            
        return playerNamesJson;        
    }

    private JsonNode getScoresJson () {
        ArrayNode scoresJson = JsonNodeFactory.instance.arrayNode();
        for (int score : scores)
            scoresJson.add(score);            
        return scoresJson;             
    }

    private JsonNode getDisconnectedPlayersJson () {
        ArrayNode disconnectedPlayersJson = JsonNodeFactory.instance.arrayNode();
        for (Integer disconnectedPlayer : disconnectedPlayers)
            disconnectedPlayersJson.add(disconnectedPlayer);
        return disconnectedPlayersJson;                     
    }
}
