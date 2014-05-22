package models.metabg;

import java.util.Collections;
import java.util.List;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;

public class Resource
{
    private final String name;
    private final String imageFront;
    private final Optional<String> imageBack;
    private final List<ClickableRegion> clickableRegions;

    public Resource (String name, String imageFront) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.absent();
        this.clickableRegions = Collections.emptyList();
    }    
    
    public Resource (String name, String imageFront, String imageBack) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.of(imageBack);
        this.clickableRegions = Collections.emptyList();
    }    
    
    public Resource (String name, String imageFront, List<ClickableRegion> clickableRegions) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.absent();
        this.clickableRegions = Collections.unmodifiableList(clickableRegions);
    }    
    
    public Resource (String name, String imageFront, String imageBack, List<ClickableRegion> clickableRegions) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.of(imageBack);
        this.clickableRegions = Collections.unmodifiableList(clickableRegions);
    }
    
    public String getName () { return name; }
    
    public JsonNode getJson () {
        ObjectNode resourceJson = Json.newObject();
        resourceJson.put("imageFront", imageFront);
        if (imageBack.isPresent()) resourceJson.put("imageBack", imageBack.get());
        ArrayNode regionsJson = JsonNodeFactory.instance.arrayNode();
        for (ClickableRegion region : clickableRegions)
            regionsJson.add(region.getJson());
        resourceJson.put("clickableRegions", regionsJson);
        return resourceJson;
    }
}
