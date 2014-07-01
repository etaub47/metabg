package models.metabg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GameState
{
    public enum Status { WaitingForConnections, InProgress, WaitingForReconnections, GameOver }
    public interface IScore { public String toDisplayString (); }
    
    // these variables should never be manipulated by the game-specific logic
    private String[] playerNames;
    private Set<Integer> disconnectedPlayers;
    
    private Status status;
    private UserInterface userInterface;
    private Set<Action> actions; // should only be at most one action per player
    private Map<String, Sequence> sequences;
    
    public GameState (int numPlayers, int numLayers) {
        userInterface = new UserInterface(numLayers);
        actions = new HashSet<>(numPlayers);
        status = Status.WaitingForConnections;
        playerNames = new String[numPlayers];
        disconnectedPlayers = new HashSet<>(numPlayers);
        sequences = new HashMap<>();
        for (int p = 0; p < numPlayers; p++)
            disconnectedPlayers.add(p);
    }
    
    public Status getStatus () { return status; }
    public UserInterface getUserInterface () { return userInterface; }

    public Action getActionByPlayerNum (int playerNum) {
        for (Action action : actions)
            if (action.getPlayerNum() == playerNum)
                return action;
        return null;
    }
    
    public void addAction (Action action) {
        actions.add(action);
    }
    
    public void removeAction (Action action) {
        actions.remove(action);
    }

    public Sequence getOrCreateSequence (String sequenceId) {
        if (!sequences.containsKey(sequenceId))
            sequences.put(sequenceId, new Sequence(sequenceId));
        return sequences.get(sequenceId);
    }
    
    public void removeSequence (String sequenceId) {
        sequences.remove(sequenceId);
    }    
    
    JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("userInterface", getUserInterfaceJson());
        result.put("actions", getActionsJson());
        result.put("status", status.toString());
        result.put("playerNames", getPlayerNamesJson());
        result.put("disconnected", getDisconnectedPlayersJson());
        return result;
    }
    
    void playerConnected (IGameLogic logic, int seatNum) {
        disconnectedPlayers.remove(seatNum);
        if (disconnectedPlayers.isEmpty()) {
            if (status == Status.WaitingForConnections) {
                logic.initUserInterface(userInterface);
                logic.initActions(actions, sequences);
            }
            if (status == Status.WaitingForConnections || status == Status.WaitingForReconnections)
                status = Status.InProgress;
        }
    }
    
    void playerDisconnected (int seatNum) {
        if (status == Status.InProgress)
            status = Status.WaitingForReconnections;
        disconnectedPlayers.add(seatNum);
    }
    
    void addPlayerName (int seatNum, String playerName) { 
        playerNames[seatNum] = playerName; 
    }
    
    private JsonNode getUserInterfaceJson () {
        return userInterface.getJson();        
    }
    
    private JsonNode getActionsJson () {
        ArrayNode actionsJson = JsonNodeFactory.instance.arrayNode();
        for (Action action : actions)
            actionsJson.add(action.getJson());            
        return actionsJson;
    }
    
    private JsonNode getPlayerNamesJson () {
        ArrayNode playerNamesJson = JsonNodeFactory.instance.arrayNode();
        for (String playerName : playerNames)
            playerNamesJson.add(playerName);            
        return playerNamesJson;        
    }

    private JsonNode getDisconnectedPlayersJson () {
        ArrayNode disconnectedPlayersJson = JsonNodeFactory.instance.arrayNode();
        for (Integer disconnectedPlayer : disconnectedPlayers)
            disconnectedPlayersJson.add(disconnectedPlayer);
        return disconnectedPlayersJson;                     
    }
}
