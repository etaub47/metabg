package models.metabg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sprites
{
    public static final String BOARD = "board";
    public static final String SELECTION = "selection";

    private final int numLevels;
    private List<Map<String, Sprite>> levels;
    
    public Sprites (int numLevels) {
        this.numLevels = numLevels;
        this.levels = new ArrayList<>(numLevels);
        for (int l = 0; l < numLevels; l++) {
            Map<String, Sprite> level = new HashMap<>();
            levels.add(l, level);
        }
    }
    
    public void addSprite (int level, String id, Sprite sprite) {
        levels.get(level).put(id, sprite);
    }
    
    public void removeSprite (int level, String id) {
        levels.get(level).remove(id);
    }
    
    // TODO: sort sprites by z-index
    
    public JsonNode getJson () {
        ArrayNode spritesJson = JsonNodeFactory.instance.arrayNode();
        for (int l = 0; l < numLevels; l++) {
            ObjectNode levelJson = Json.newObject();
            for (Map.Entry<String, Sprite> sprite : levels.get(l).entrySet())
                levelJson.put(sprite.getKey(), sprite.getValue().getJson());
            spritesJson.add(levelJson);            
        }
        return spritesJson;
    }
}
