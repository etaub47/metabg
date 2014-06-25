package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClickableRegion
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String value;
    
    public ClickableRegion (int x, int y, int width, int height, String value) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.value = value;
    }
    
    public String getValue () { return value; }
    
    public JsonNode getJson () {
        ObjectNode clickableRegionJson = Json.newObject();
        clickableRegionJson.put("x", x);
        clickableRegionJson.put("y", y);
        clickableRegionJson.put("width", width);
        clickableRegionJson.put("height", height);
        clickableRegionJson.put("value", value);
        return clickableRegionJson;
    }
}
