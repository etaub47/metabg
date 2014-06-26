package models.metabg;

public class Event
{
    public static interface IEventType { };
    
    private final IEventType type;
    private final int playerNum;
    private final String value;
    
    public Event (IEventType type, int playerNum, String value) {
        this.type = type;
        this.playerNum = playerNum;
        this.value = value;
    }

    public IEventType getType () { return type; }
    public int getPlayerNum () { return playerNum; }
    public String getValue () { return value; }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append("Event [type=").append(type).append(", playerNum=").append(playerNum).append(", value=")
            .append(value).append("]");
        return builder.toString();
    }
}
