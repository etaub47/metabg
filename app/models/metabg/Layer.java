package models.metabg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import play.libs.Json;
import utils.SpriteUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Layer
{
    private List<Sprite> sprites;
    private List<Region> regions;
    
    public Layer () {
        sprites = new ArrayList<>();
        regions = new ArrayList<>();
    }
    
    public void addSprite (Sprite sprite) { sprites.add(sprite); }    
    public void addRegion (Region region) { regions.add(region); }
    public void addClickableSprite (Sprite sprite) {
        sprites.add(sprite);
        regions.add(sprite.createRegion());
    }
    
    public Sprite getSprite (String id) {
        for (Sprite sprite : sprites)
            if (sprite.getId().equals(id))
                return sprite;
        return null;
    }
    
    public Region getRegion (String id) {
        for (Region region : regions)
            if (region.getId().equals(id))
                return region;
        return null;
    }
    
    public void moveSprite (String id, int x, int y) {
        Sprite sprite = getSprite(id);
        if (sprite != null)
            sprite.move(x, y);
        Region region = getRegion(id);
        if (region != null)
            region.move(x, y);
    }
    
    public void removeSprite (String id) {
        Iterator<Sprite> iter = sprites.iterator();
        while (iter.hasNext()) {
            Sprite sprite = iter.next();
            if (sprite.getId().equals(id))
                iter.remove();
        }
        removeRegion(id);
    }
    
    public void removeRegion (String id) {
        Iterator<Region> iter2 = regions.iterator();
        while (iter2.hasNext()) {
            Region region = iter2.next();
            if (region.getId().equals(id))
                iter2.remove();
        }
    }
    
    public void sortSprites () {
        SpriteUtils.sortSprites(sprites);
    }
    
    public JsonNode getJson (int playerNum) {
        ObjectNode levelJson = Json.newObject();
        ArrayNode spritesJson = JsonNodeFactory.instance.arrayNode();
        for (Sprite sprite : sprites)
            spritesJson.add(sprite.getJson(playerNum));
        levelJson.put("sprites", spritesJson);                
        ArrayNode regionsJson = JsonNodeFactory.instance.arrayNode();
        for (Region region : regions)
            regionsJson.add(region.getJson());
        levelJson.put("regions", regionsJson);
        return levelJson;
    }
}
