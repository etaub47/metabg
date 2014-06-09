package models.metabg;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    
    private GameState state;
    
    public Table (String name, int numPlayers, GameState state) {
        this.name = name;
        this.createDate = new Date();
        this.seats = new Seat[numPlayers];
        this.state = state; 
    }
    
    public String getName () { return name; }
    public Date getCreateDate () { return createDate; }
    public Seat[] getSeats () { return seats; }
    
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
        state.playerConnected(seatNum);
        if (state.getStatus() != GameState.Status.WaitingForConnections)
            for (Seat seat : seats)
                seat.sendState(state);
    }
    
    public synchronized void disconnectPlayer (int seatNum) {
        state.playerDisconnected(seatNum);
        if (state.getStatus() != GameState.Status.WaitingForConnections)
            for (Seat seat : seats)
                seat.sendState(state);        
    }
}
