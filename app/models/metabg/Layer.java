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
    
    public void addSprite (Sprite sprite) { sprites.add(sprite); }    
    public void addClickableRegion (ClickableRegion region) { clickableRegions.add(region); }    
    public void addClickableSprite (Sprite sprite) {
        sprites.add(sprite);
        clickableRegions.add(sprite.getClickableRegion());
    }
    
    public Sprite getSprite (String id) {
        for (Sprite sprite : sprites)
            if (sprite.getId().equals(id))
                return sprite;
        return null;
    }
    
    public ClickableRegion getRegion (String id) {
        for (ClickableRegion region : clickableRegions)
            if (region.getId().equals(id))
                return region;
        return null;
    }
    
    public void moveSprite (String id, int x, int y) {
        Sprite sprite = getSprite(id);
        if (sprite != null)
            sprite.move(x, y);
        ClickableRegion region = getRegion(id);
        if (region != null)
            region.move(x, y);
    }
    
    public void removeSprite (String id) {
        Iterator<Sprite> iter = sprites.iterator();
        while (iter.hasNext()) {
            Sprite sprite = iter.next();
            if (sprite.getId() == id)
                iter.remove();
        }
        removeClickableRegion(id);
    }
    
    public void removeClickableRegion (String id) {
        Iterator<ClickableRegion> iter2 = clickableRegions.iterator();
        while (iter2.hasNext()) {
            ClickableRegion region = iter2.next();
            if (region.getId() == id)
                iter2.remove();
        }
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
