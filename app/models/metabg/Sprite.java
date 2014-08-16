package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sprite
{
    public enum Side { Front, Back }
    public enum Orientation { Normal, Right, Left, UpsideDown }
    
    private final String id; // must be unique per game
    private final String resource;
    private final int width;
    private final int height;
    private int x;
    private int y;
    private int z;
    private Side side;
    private Orientation orientation;
    
    public Sprite (String id, String resource, int x, int y, int z, int width, int height, Side side, 
        Orientation orientation) 
    {
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
    public Region createRegion () { return new Region(x, y, width, height, id); }
    
    public JsonNode getJson (int playerNum) {
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

    public void move (int x, int y) { this.x = x; this.y = y; }    
    public void flip () { this.side = ((this.side == Side.Front) ? Side.Back : Side.Front); }
    public void orient (Orientation orientation) { this.orientation = orientation; }
    public void updateZ (int z) { this.z = z; }    
}
