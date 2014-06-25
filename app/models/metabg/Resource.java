package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;

public class Resource
{
    private final String name;
    private final String imageFront;
    private final Optional<String> imageBack;

    public Resource (String name, String imageFront) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.absent();
    }    
    
    public Resource (String name, String imageFront, String imageBack) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.of(imageBack);
    }    
    
    public String getName () { return name; }
    
    public JsonNode getJson () {
        ObjectNode resourceJson = Json.newObject();
        resourceJson.put("imageFront", imageFront);
        if (imageBack.isPresent()) resourceJson.put("imageBack", imageBack.get());
        return resourceJson;
    }
}
