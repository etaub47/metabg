package models.metabg;

import java.util.ArrayList;
import java.util.List;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Game
{
    protected List<Resource> resources = new ArrayList<>();
    
    public void init () { addResources(); }
    
    public abstract String getName ();    
    public abstract int getMinPlayers ();
    public abstract int getMaxPlayers ();
    public abstract GameState createGameState ();

    public JsonNode getResourcesJson () {
        ObjectNode resourcesJson = Json.newObject();
        for (Resource resource : resources)
            resourcesJson.put(resource.getName(), resource.getJson());
        return resourcesJson;
    }

    protected abstract void addResources ();
}
