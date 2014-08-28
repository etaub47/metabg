package models.checkers;

import models.metabg.Event;

public class Selection
{
    private final Event event;
    private final Checker checker;
    
    public Selection (Event event, Checker checker) {
        this.event = event;
        this.checker = checker;
    }

    public Event getEvent () { return event; }
    public Checker getChecker () { return checker; }
}
