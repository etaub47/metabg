package models.metabg;

import java.util.ArrayList;
import java.util.List;

public class Sequence
{
    private final String id;
    private final String type;
    private final List<Step> steps;
    
    public Sequence (String id, String type) {
        this.id = id;
        this.type = type;
        this.steps = new ArrayList<>();
    }
    
    public String getId () { return id; }
    public String getType () { return type; }
    public List<Step> getSteps () { return steps; }
    public Step getFirstStep () { return steps.isEmpty() ? null : steps.get(0); }
    public Step getLastStep () { return steps.isEmpty() ? null : steps.get(steps.size() - 1); }
    
    public void addStep (Event event) { steps.add(new Step(event, null)); }
    public void addStep (Event event, Object data) { steps.add(new Step(event, data)); }
    public void removeLastStep () { if (!steps.isEmpty()) steps.remove(steps.size() - 1); }
    public void removeAllSteps () { steps.clear(); }
}
