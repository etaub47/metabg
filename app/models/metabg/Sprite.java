package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sprite
{
    public enum Side { Front, Back }
    public enum Orientation { Normal, Right, Left, UpsideDown }
    
    private final String id;
    private final String resource;
    private final int x;
    private final int y;
    private final int z;
    private final int width;
    private final int height;
    private final Side side;
    private final Orientation orientation;
    
    public Sprite (String id, String resource, int x, int y, int z, int width, int height, Side side, Orientation orientation) {
        this.id = id;
        this.resource = resource;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.side = side;
        this.orientation = orientation;
    }
    
    public String getId () { return id; }
    public int getZ () { return z; }    
    public ClickableRegion getClickableRegion () {
        return new ClickableRegion(x, y, width, height, id);
    }
    
    public JsonNode getJson () {
        ObjectNode spriteJson = Json.newObject();
        spriteJson.put("id", id);
        spriteJson.put("resource", resource);
        spriteJson.put("x", x);
        spriteJson.put("y", y);
        if (z >= 0)
            spriteJson.put("z", z);
        if (side != Side.Front)
            spriteJson.put("side", side.toString());
        if (orientation != Orientation.Normal)
            spriteJson.put("orientation", orientation.toString());
        return spriteJson;
    }
}
