package models.metabg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import play.libs.Json;
import utils.SpriteComparator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Layer
{
    private List<Sprite> sprites;
    private List<ClickableRegion> clickableRegions;
    
    public Layer () {
        sprites = new ArrayList<>();
        clickableRegions = new ArrayList<>();
    }
    
    public void addSprite (Sprite sprite) {
        sprites.add(sprite);
    }
    
    public void addClickableRegion (ClickableRegion region) {
        clickableRegions.add(region);
    }
    
    public void addClickableSprite (Sprite sprite) {
        sprites.add(sprite);
        clickableRegions.add(sprite.getClickableRegion());
    }
    
    public void removeSprite (String id) {
        Iterator<Sprite> iter = sprites.iterator();
        while (iter.hasNext()) {
            Sprite sprite = iter.next();
            if (sprite.getId() == id)
                iter.remove();
        }
        Iterator<ClickableRegion> iter2 = clickableRegions.iterator();
        while (iter2.hasNext()) {
            ClickableRegion region = iter2.next();
            if (region.getValue() == id)
                iter2.remove();
        }
    }
    
    public void removeClickableRegion (ClickableRegion region) {
        clickableRegions.remove(region);
    }
    
    public void sortSprites () {
        Collections.sort(sprites, new SpriteComparator());
    }
    
    public JsonNode getJson () {
        ObjectNode levelJson = Json.newObject();
        ArrayNode spritesJson = JsonNodeFactory.instance.arrayNode();
        for (Sprite sprite : sprites)
            spritesJson.add(sprite.getJson());
        levelJson.put("sprites", spritesJson);                
        ArrayNode regionsJson = JsonNodeFactory.instance.arrayNode();
        for (ClickableRegion region : clickableRegions)
            regionsJson.add(region.getJson());
        levelJson.put("clickableRegions", regionsJson);
        return levelJson;
    }
}
