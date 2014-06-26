package models.metabg;

import models.metabg.Event.IEventType;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Option
{
    public enum Category { TableClick, CardClick, NumberPress, ConfirmPress };

    private final IEventType type;
    private final Category category;
    private final Integer layer; // layer for TableClick; card type for CardClick
    private final Integer min, max; // for NumberPress    

    public Option (IEventType type, Category category) {
        this.type = type;
        this.category = category;
        this.layer = -1;
        this.min = -1;
        this.max = -1;
    }

    public Option (IEventType type, Category category, Integer layer) {
        this.type = type;
        this.category = category;
        this.layer = layer;
        this.min = -1;
        this.max = -1;
    }

    public Option (IEventType type, Category category, Integer min, Integer max) {
        this.type = type;
        this.category = category;
        this.layer = -1;
        this.min = min;
        this.max = max;
    }
    
    public IEventType getType () { return type; }
    public Category getCategory () { return category; }
    public Integer getLayer () { return layer; }
    public Integer getMin () { return min; }
    public Integer getMax () { return max; }
    
    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("category", category.toString());
        result.put("layer", layer);
        result.put("min", min);
        result.put("max", max);
        return result;
    }    
}
