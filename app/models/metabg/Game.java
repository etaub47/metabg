package models.metabg;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Game
{
    protected List<Resource> resources = new ArrayList<>();
    
    public void init () { addResources(); }
    
    public abstract String getName ();    
    public abstract int getMinPlayers ();
    public abstract int getMaxPlayers ();

    public JSONObject getResourcesJson () throws JSONException {
        JSONObject resourcesJson = new JSONObject();
        for (Resource resource : resources)
            resourcesJson.put(resource.getName(), resource.getJson());
        return resourcesJson;
    }
    
    protected abstract void addResources ();
}
