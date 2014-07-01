package models.metabg;

import java.util.ArrayList;
import java.util.List;

public class Sequence
{
    private final String id;
    private final List<Event> events;
    
    public Sequence (String id) {
        this.id = id;
        this.events = new ArrayList<>();
    }
    
    public String getId () { return id; }
    public List<Event> getEvents () { return events; }
    public Event getFirstEvent () { return events.isEmpty() ? null : events.get(0); }
    public Event getLastEvent () { return events.isEmpty() ? null : events.get(events.size() - 1); }
    
    public void addEvent (Event event) { events.add(event); }
}
