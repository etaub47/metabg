package models.metabg;

import models.metabg.Option.Category;
import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

public class Seat
{    
    public enum Status { Connected, Disconnected }
   
    private final int playerNum;
    private final String playerName;
    private final Table table;
    private WebSocket.Out<String> outboundConnection;
    private Status status;
    
    public Seat (int playerNum, String playerName, Table table) {
        this.playerNum = playerNum;
        this.playerName = playerName;
        this.table = table;
        this.status = Status.Disconnected;
    }
    
    public String getPlayerName () { return playerName; }
    public Status getStatus () { return status; }
    
    public void connect (WebSocket.In<String> inboundConnection, WebSocket.Out<String> outboundConnection) {
        this.outboundConnection = outboundConnection;
        this.status = Status.Connected;
        inboundConnection.onMessage(new Callback<String>(){ public void invoke(String event) { incomingMessage(event); }});
        inboundConnection.onClose(new Callback0(){ public void invoke() { closeConnection(); }});
        this.status = Status.Connected;        
    }
    
    public void incomingMessage (String message) 
    {
        if (message == null || !message.contains("|")) {
            Logger.warn("Invalid message received from player " + playerNum + ": " + message);
            return;
        }
        
        String[] messageParts = message.split("\\|");
        Category category = null;
        
        try { category = Category.valueOf(messageParts[0]); }
        catch (Exception e) {
            Logger.warn("Invalid message category received from player " + playerNum + ": " + message);
            return;            
        }
        
        String value = messageParts[1];
        table.processIncomingMessage(playerNum, category, value);
    }

    public void closeConnection () {
        this.outboundConnection = null;
        this.status = Status.Disconnected;
        table.disconnectPlayer(playerNum);
    }
    
    public void sendState (GameState state) {
        if (outboundConnection != null && state != null)
            outboundConnection.write(state.getJson().toString());
    }
    
    public void sendMessage (String message) {
        if (outboundConnection != null)
            outboundConnection.write(message);
    }
}
