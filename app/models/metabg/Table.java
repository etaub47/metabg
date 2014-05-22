package models.metabg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Table
{
    public enum Status { WaitingForPlayers, InProgress, Completed }
    
    private final String name;
    private final Date createDate;
    private final Seat[] seats; // TODO: LinkedHashMap?
    
    private Status status;
    private Set<ExpectedAction> expectedActions;
    
    public Table (String name, int numPlayers) {
        this.name = name;
        this.createDate = new Date();
        this.seats = new Seat[numPlayers];
        this.status = Status.WaitingForPlayers;
        this.expectedActions = new HashSet<>();
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
}
