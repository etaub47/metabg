package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sprite
{
    public enum Side { Front, Back }
    public enum Orientation { Normal, Right, Left, UpsideDown }
    
    private final String resource;
    private final int x;
    private final int y;
    private final int z;
    private final Side side;
    private final Orientation orientation;
    private final String value;
    
    public Sprite (String resource, int x, int y, int z, Side side, Orientation orientation, String value) {
        this.resource = resource;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.orientation = orientation;
        this.value = value;
    }
    
    public JsonNode getJson () {
        ObjectNode spriteJson = Json.newObject();
        spriteJson.put("resource", resource);
        spriteJson.put("x", x);
        spriteJson.put("y", y);
        if (z >= 0)
            spriteJson.put("z", z);
        if (side != Side.Front)
            spriteJson.put("side", side.toString());
        if (orientation != Orientation.Normal)
            spriteJson.put("orientation", orientation.toString());
        if (value != null)
            spriteJson.put("value", value);
        return spriteJson;
    }
}
