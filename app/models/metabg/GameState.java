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

    private String[] playerNames;
    private Set<Integer> disconnectedPlayers;
    private Status status;
    private UserInterface userInterface;    
    private Map<Integer, IAction> actions;
    private Map<String, Sequence> sequences;
    
    public GameState (int numPlayers, int numLayers) {
        userInterface = new UserInterface(numLayers);
        actions = new HashMap<>(numPlayers);
        status = Status.WaitingForConnections;
        playerNames = new String[numPlayers];
        disconnectedPlayers = new HashSet<>(numPlayers);
        sequences = new HashMap<>();
        for (int p = 0; p < numPlayers; p++)
            disconnectedPlayers.add(p);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    
    public Layer getUILayer (int layer) { 
        return userInterface.getLayer(layer); 
    }

    public IAction getActionByPlayerNum (int playerNum) {
        return actions.get(playerNum);
    }
    
    public void addAction (IAction action) {
        actions.put(action.getPlayerNum(), action);
    }
    
    public void removeActionByPlayerNum (int playerNum) {
        actions.remove(playerNum);
    }

    public Sequence getSequence (String sequenceId) {
        return sequences.get(sequenceId);
    }

    public Sequence createSequence (String sequenceId, String sequenceType) {
        Sequence sequence = new Sequence(sequenceId, sequenceType);
        sequences.put(sequenceId, sequence);
        return sequence;
    }
    
    public void removeSequence (String sequenceId) {
        sequences.remove(sequenceId);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Status getStatus () { return status; }
    
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
            if (status == Status.WaitingForConnections)
                logic.init(this);
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
    
    void endGame () {
        status = Status.GameOver;
    }
    
    private JsonNode getUserInterfaceJson () {
        return userInterface.getJson();        
    }
    
    private JsonNode getActionsJson () {
        ArrayNode actionsJson = JsonNodeFactory.instance.arrayNode();
        for (IAction action : actions.values())
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
