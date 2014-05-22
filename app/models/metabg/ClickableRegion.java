package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClickableRegion
{
    private final int relativeX;
    private final int relativeY;
    private final int width;
    private final int height;
    private final String value;
    
    public ClickableRegion (int relativeX, int relativeY, int width, int height, String value) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.width = width;
        this.height = height;
        this.value = value;
    }
    
    public JsonNode getJson () {
        ObjectNode clickableRegionJson = Json.newObject();
        clickableRegionJson.put("relativeX", relativeX);
        clickableRegionJson.put("relativeY", relativeY);
        clickableRegionJson.put("width", width);
        clickableRegionJson.put("height", height);
        clickableRegionJson.put("value", value);
        return clickableRegionJson;
    }
}
