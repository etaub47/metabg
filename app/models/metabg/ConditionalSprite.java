package models.metabg;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

public class ConditionalSprite extends Sprite
{
    private Map<Integer, Sprite> sprites = new HashMap<>();

    public ConditionalSprite (String id, String resource, int x, int y, int z, int width, int height, 
        Side side, Orientation orientation) 
    {
        super(id, resource, x, y, z, width, height, side, orientation);        
    }
    
    public void addAlternateSprite (int playerNum, Sprite sprite) {
        sprites.put(playerNum, sprite);
    }
    
    public JsonNode getJson (int playerNum) {
        if (sprites.containsKey(playerNum))
            return sprites.get(playerNum).getJson(playerNum);
        else
            return super.getJson(playerNum);
    }
}
