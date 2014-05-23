package models.metabg;

import java.util.ArrayList;
import java.util.List;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Game
{
    protected List<Resource> resources = new ArrayList<>();
    protected Sprites sprites = new Sprites(3); // TODO: make configurable by game?
    
    public void init () { addResources(); initSprites(); }
    
    public abstract String getName ();    
    public abstract int getMinPlayers ();
    public abstract int getMaxPlayers ();

    public JsonNode getResourcesJson () {
        ObjectNode resourcesJson = Json.newObject();
        for (Resource resource : resources)
            resourcesJson.put(resource.getName(), resource.getJson());
        return resourcesJson;
    }
    
    public JsonNode getSpritesJson () {
        return sprites.getJson();        
    }
    
    protected abstract void addResources ();
    protected abstract void initSprites ();
}
