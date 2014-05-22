package models.metabg;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

public class Seat
{
    public enum Status { Connected, Disconnected }
    
    private final int num;
    private final String playerName;
    private final Table table;
    private final WebSocket.Out<String> outboundConnection;
    private Status status;
    
    public Seat (int num, String playerName, Table table, WebSocket.In<String> inboundConnection, WebSocket.Out<String> outboundConnection) {
        this.num = num;
        this.playerName = playerName;
        this.table = table;
        this.outboundConnection = outboundConnection;
        this.status = Status.Connected;
        inboundConnection.onMessage(new Callback<String>(){ public void invoke(String event) { incomingMessage(event); }});
        inboundConnection.onClose(new Callback0(){ public void invoke() { closeConnection(); }});
    }
        
    public String getPlayerName () { return playerName; }
    public Status getStatus () { return status; }
    public WebSocket.Out<String> getConnection () { return outboundConnection; }
    
    public void incomingMessage (String message) {
        // TODO
    }

    public void closeConnection () {
        // TODO
    }
}
