package models.metabg;

import java.text.SimpleDateFormat;
import java.util.Date;
import models.metabg.IGameModeFactory.IGameMode;
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
    
    public Table (Game game, String name, int numPlayers, IGameMode mode) {
        this.name = name;
        this.createDate = new Date();
        this.seats = new Seat[numPlayers];
        this.state = game.getConfig().createGameState(game, numPlayers, mode);
        this.logic = game.getConfig().createGameLogic(numPlayers);
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
    
    public synchronized void processIncomingMessage (int playerNum, Category category, String value) 
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
            if (number < 1 || number > selectedOption.getValue()) {
                Logger.warn("Out of range message received from player " + playerNum + ": " + category + "|" + value);
                return;
            }
        }
        
        Event event = new Event(selectedOption.getType(), playerNum, value);
        Result result = null;
        
        // check for game-specific validation errors based on the event type        
        String validationError = null;        
        try {
            validationError = event.getType().validate(state, event); 
            if (validationError != null)
                result = new Result(ResultType.ERROR, validationError);
        }
        catch (Exception e) {
            Logger.error("An exception occurred in validate", e);
            result = new Result(ResultType.ERROR, "An unexpected error occurred.");
        }        
        
        // process the event
        if (result == null) 
        {
            // remove the action now that we are fairly certain it will be processed
            // if an application-level exception occurs, we do not try to recover, since this usually means the program has a bug 
            state.removeActionByPlayerNum(playerNum);
            
            try { result = logic.processEvent(state, event); }
            catch (Exception e) {
                Logger.error("An exception occurred in processEvent", e);
                result = new Result(ResultType.ERROR, "An unexpected error occurred.");
            }
        }

        // process the result
        switch (result.getType())
        {
            case DO_NOTHING:
                break;
            
            case STATE_CHANGE:
                if (result.hasMessage())
                    sendMessage(result.getMessage());
                sendState(); 
                break;
                
            case ERROR:
                sendMessage(playerNum, result.getMessage()); 
                break;
                
            case GAME_OVER:
                state.endGame();
                sendState(); 
                sendMessage(result.getMessage());
                break;
        }
    }
    
    private void sendState () { for (Seat seat : seats) seat.sendState(state); }    
    private void sendMessage (int playerNum, String message) { seats[playerNum].sendMessage(message); }
    private void sendMessage (String message) { for (Seat seat : seats) seat.sendMessage(message); }
}
