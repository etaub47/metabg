package models.metabg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Game
{
    private final Map<String, Resource> resources;
    private final IGameConfig config;
    
    public Game (IGameConfig config) {
        this.resources = new LinkedHashMap<>();
        this.config = config;
    }
    
    public IGameConfig getConfig () { return config; }
    public String getName () { return config.getName(); }
    public Resource getResource (String name) { return resources.get(name); }
    
    public JsonNode getResourcesJson () {
        ObjectNode resourcesJson = Json.newObject();
        for (Resource resource : resources.values()) {
            if (resourcesJson.get(resource.getNameFront()) == null)
                resourcesJson.put(resource.getNameFront(), resource.getImageFront());
            if (resource.hasBack() && resourcesJson.get(resource.getNameBack()) == null)
                resourcesJson.put(resource.getNameBack(), resource.getImageBack());
        }
        return resourcesJson;
    }
    
    public void init () {
        List<Resource> resourceList = new ArrayList<>();
        config.initResources(resourceList);
        for (Resource resource : resourceList)
            this.resources.put(resource.getNameFront(), resource);
    }
}
