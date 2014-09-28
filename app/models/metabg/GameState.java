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

    private final Game game;
    
    private UserInterface userInterface;    
    private Map<Integer, Action> actions;
    private Status status;
    private String[] playerNames;    
    private Set<Integer> disconnectedPlayers;
    
    public GameState (Game game, int numPlayers, int numLayers) {
        this.game = game;
        userInterface = new UserInterface(numLayers);
        actions = new HashMap<>(numPlayers);
        status = Status.WaitingForConnections;
        playerNames = new String[numPlayers];
        disconnectedPlayers = new HashSet<>(numPlayers);
        for (int p = 0; p < numPlayers; p++)
            disconnectedPlayers.add(p);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    
    public int getNumPlayers () {
        return playerNames.length;
    }
    
    public String getPlayerName (int playerNum) {
        return playerNames[playerNum];
    }
    
    public Layer getUILayer (int layer) { 
        return userInterface.getLayer(layer); 
    }
    
    public boolean hasActions () {
        return !actions.isEmpty();
    }

    public Action getActionByPlayerNum (int playerNum) {
        return actions.get(playerNum);
    }
    
    public void addAction (IActionType actionType, int playerNum) {
        actions.put(playerNum, actionType.createAction(this, playerNum));
    }
    
    public void removeActionByPlayerNum (int playerNum) {
        actions.remove(playerNum);
    }

    public Resource getResource (String name) {
        return game.getResource(name);
    }
    
    protected String getPhase () {
        return "";
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Status getStatus () { return status; }
    
    JsonNode getJson (int playerNum) {
        ObjectNode result = Json.newObject();
        result.put("userInterface", getUserInterfaceJson(playerNum));
        result.put("actions", getActionsJson());
        result.put("status", status.toString());
        result.put("playerNames", getPlayerNamesJson());
        result.put("disconnected", getDisconnectedPlayersJson());
        result.put("phase", getPhase());
        return result;
    }
    
    void playerConnected (IGameLogic logic, int seatNum) {
        disconnectedPlayers.remove(seatNum);
        if (disconnectedPlayers.isEmpty()) {
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
    
    private JsonNode getUserInterfaceJson (int playerNum) {
        return userInterface.getJson(playerNum);        
    }
    
    private JsonNode getActionsJson () {
        ArrayNode actionsJson = JsonNodeFactory.instance.arrayNode();
        for (Action action : actions.values())
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
