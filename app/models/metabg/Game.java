package models.metabg;

import java.util.ArrayList;
import java.util.List;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Game
{
    private final List<Resource> resources;
    private final IGameConfig config;
    
    public Game (IGameConfig config) {
        this.resources = new ArrayList<>();
        this.config = config;
    }
    
    public IGameConfig getConfig () { return config; }
    public String getName () { return config.getName(); }
    
    public JsonNode getResourcesJson () {
        ObjectNode resourcesJson = Json.newObject();
        for (Resource resource : resources)
            resourcesJson.put(resource.getName(), resource.getJson());
        return resourcesJson;
    }
    
    public void init () {
        config.init(resources);
    }
}
