package models.metabg;

import java.text.SimpleDateFormat;
import java.util.Date;
import models.metabg.Option.Category;
import models.metabg.Result.ResultType;
import play.Logger;
import play.libs.Json;
import play.mvc.WebSocket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Table
{
    private final String name;
    private final Date createDate;
    private final Seat[] seats;    
    private final GameState state;
    private final IGameLogic logic;
    
    public Table (IGameConfig config, String name, int numPlayers) {
        this.name = name;
        this.createDate = new Date();
        this.seats = new Seat[numPlayers];
        this.state = new GameState(numPlayers, config.getNumLayers());
        this.logic = config.createGameLogic(numPlayers);
    }
    
    public String getName () { return name; }
    public Date getCreateDate () { return createDate; }
    public Seat[] getSeats () { return seats; }
    public GameState getState () { return state; }
    
    public JsonNode getJson () {
        ObjectNode table = Json.newObject();
        table.put("name", name);
        table.put("created", new SimpleDateFormat("MMM dd, yyyy h:mm a").format(createDate));
        ArrayNode seatsJson = JsonNodeFactory.instance.arrayNode();
        for (Seat seat : seats)
            seatsJson.add((seat == null) ? "Empty" : seat.getPlayerName());
        table.put("seats", seatsJson);
        return table;
    }
    
    public void seatPlayer (int seatNum, String playerName) {
        seats[seatNum] = new Seat(seatNum, playerName, this);
        state.addPlayerName(seatNum, playerName);
    }
    
    public synchronized void connectPlayer (int seatNum, WebSocket.In<String> inboundConnection, WebSocket.Out<String> outboundConnection) {
        seats[seatNum].connect(inboundConnection, outboundConnection);
        state.playerConnected(logic, seatNum);
        if (state.getStatus() != GameState.Status.WaitingForConnections)
            sendState();
    }
    
    public synchronized void disconnectPlayer (int seatNum) {
        state.playerDisconnected(seatNum);
        if (state.getStatus() != GameState.Status.WaitingForConnections)
            sendState();
    }
    
    public void processIncomingMessage (int playerNum, Category category, String value) 
    {
        // make sure game is in progress first
        if (state.getStatus() != GameState.Status.InProgress) {
            Logger.info("Ignoring message while game is not in progress from player ", playerNum);
            return;
        }
        
        // determine the action based on player num 
        Action selectedAction = state.getActionByPlayerNum(playerNum);
        if (selectedAction == null) {
            Logger.warn("Unexpected message received from player ", playerNum, ": ", category, "|", value);
            return;
        }
        
        // determine the option based on category
        Option selectedOption = selectedAction.getOptionByCategory(category);
        if (selectedOption == null) {
            Logger.warn("Unexpected message category received from player ", playerNum, ": ", category, "|", value);
            return;
        }
                
        // non-numeric message or value out of range
        if (category == Category.NumberPress) {
            int number;
            try { number = Integer.parseInt(value); }
            catch (Exception e) { 
                Logger.warn("Non-numeric message received from player " + playerNum + ": " + category + "|" + value);
                return;
            }
            if (number < selectedOption.getMin() || number > selectedOption.getMax()) {
                Logger.warn("Out of range message received from player " + playerNum + ": " + category + "|" + value);
                return;
            }
        }
        
        // remove the action now that it has been fulfilled
        state.removeAction(selectedAction);

        // process the event (game-specific)
        Result result = null;
        try { result = logic.processEvent(state, new Event(selectedOption.getType(), playerNum, value)); }
        catch (Exception e) {
            Logger.warn("An exception occurred in processEvent(" + selectedOption.getType() + ", " + playerNum + ", " +
                value + "): " + e.getMessage());
            e.printStackTrace();
            result = new Result(ResultType.ERROR, "An unknown error occurred.");
        }

        // process the result
        switch (result.getType()) {
            case STATE_CHANGE: sendState(); break;
            case ERROR: sendError(playerNum, result.getMessage()); break;
            case GAME_OVER: break; // TODO
            default: break;
        }
    }
    
    private void sendState () { for (Seat seat : seats) seat.sendState(state); }    
    private void sendError (int playerNum, String message) { seats[playerNum].sendError(message); }
}
