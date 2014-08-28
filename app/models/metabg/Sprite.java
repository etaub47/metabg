package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sprite
{
    public enum Side { Front, Back }
    public enum Orientation { Normal, Right, Left, UpsideDown }
    
    private final String id; // must be unique per game
    private final Resource resource;
    private final String tooltip;
    
    private int x;
    private int y;
    private int z;
    private Side side;
    private Orientation orientation;
    private Integer personalPlayerNum;    
    
    private Sprite (String id, Resource resource, String tooltip, int x, int y, int z, Side side, Orientation orientation, 
        Integer personalPlayerNum) 
    {
        this.id = id;
        this.resource = resource;
        this.tooltip = tooltip;
        this.personalPlayerNum = personalPlayerNum;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.orientation = orientation;
    }

    public String getId () { return id; }
    public int getZ () { return z; }    
    public Region createRegion () { return new Region(x, y, resource.getWidth(), resource.getHeight(), id); }
    
    public JsonNode getJson (int playerNum) {
        ObjectNode spriteJson = Json.newObject();
        spriteJson.put("id", id);
        if (!resource.hasBack())
            spriteJson.put("resource", resource.getNameFront());
        else if (personalPlayerNum == null)        
            spriteJson.put("resource", (side == Side.Front) ? resource.getNameFront() : resource.getNameBack());
        else
            spriteJson.put("resource", (personalPlayerNum == playerNum) ? resource.getNameFront() : resource.getNameBack());
        if (tooltip != null && (personalPlayerNum == null || personalPlayerNum == playerNum))
            spriteJson.put("tooltip", tooltip);
        spriteJson.put("x", x);
        spriteJson.put("y", y);
        if (z >= 0)
            spriteJson.put("z", z);
        if (orientation != null && orientation != Orientation.Normal)
            spriteJson.put("orientation", orientation.toString());
        return spriteJson;
    }

    public void move (int x, int y) { this.x = x; this.y = y; }    
    public void flip () { this.side = ((this.side == Side.Front) ? Side.Back : Side.Front); }
    public void orient (Orientation orientation) { this.orientation = orientation; }
    public void updateZ (int z) { this.z = z; }
    public void switchPlayer (int personalPlayerNum) { this.personalPlayerNum = personalPlayerNum; } 
    
    public static class SpriteBuilder
    {
        private final String id; // must be unique per game
        private final Resource resource;
        private final int x;
        private final int y;
        private final int z;

        private String tooltip = null;
        private Side side = Side.Front;
        private Orientation orientation = Orientation.Normal;
        private Integer personalPlayerNum = null;
        
        public SpriteBuilder (String id, Resource resource, int x, int y, int z) { 
            this.id = id; 
            this.resource = resource;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public SpriteBuilder tooltip (String tooltip) { this.tooltip = tooltip; return this; }
        public SpriteBuilder side (Side side) { this.side = side; return this; }
        public SpriteBuilder orientation (Orientation orientation) { this.orientation = orientation; return this; }
        public SpriteBuilder visibleToPlayer (Integer personalPlayerNum) { this.personalPlayerNum = personalPlayerNum; return this; }
        
        public Sprite build () {
            return new Sprite(id, resource, tooltip, x, y, z, side, orientation, personalPlayerNum);
        }
    }
}
