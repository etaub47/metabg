package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClickableRegion
{
    private final String id; // must be unique per game
    private final int width;
    private final int height;
    private int x;
    private int y;
    
    public ClickableRegion (int x, int y, int width, int height, String id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
    }
    
    public String getId () { return id; }
    
    public JsonNode getJson () {
        ObjectNode clickableRegionJson = Json.newObject();
        clickableRegionJson.put("id", id);
        clickableRegionJson.put("x", x);
        clickableRegionJson.put("y", y);
        clickableRegionJson.put("width", width);
        clickableRegionJson.put("height", height);
        return clickableRegionJson;
    }
    
    public void move (int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClickableRegion other = (ClickableRegion) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
}
