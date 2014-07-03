package models.metabg;

public class Step
{
    private final Event event;
    private final Object data;
    
    public Step (Event event, Object data) {
        this.event = event;
        this.data = data;
    }

    public Event getEvent () { return event; }
    public Object getData () { return data; }
}
