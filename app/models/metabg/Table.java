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
    public enum Status { WaitingForPlayers, InProgress }
    
    private final String name;
    private final Date createDate;
    private final Seat[] seats; // TODO: LinkedHashMap?
    
    private Status status;
    private GameState state;
    
    public Table (String name, int numPlayers, GameState state) {
        this.name = name;
        this.createDate = new Date();
        this.seats = new Seat[numPlayers];
        this.status = Status.WaitingForPlayers;
        this.state = state; 
    }
    
    public String getName () { return name; }
    public Date getCreateDate () { return createDate; }
    public Seat[] getSeats () { return seats; }
    public Status getStatus () { return status; }
    
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
    }
    
    public synchronized void connectPlayer (int seatNum, WebSocket.In<String> inboundConnection, WebSocket.Out<String> outboundConnection) {
        seats[seatNum].connect(inboundConnection, outboundConnection);
        boolean foundDisconnected = false;
        for (Seat seat : seats)
            if (seat == null || seat.getStatus() == Seat.Status.Disconnected)
                foundDisconnected = true;
        if (!foundDisconnected) {
            status = Status.InProgress;
            state.init();
            for (Seat seat : seats)
                seat.sendState(state);
        }
    }
}
