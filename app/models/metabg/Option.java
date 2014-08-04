package models.metabg;

import models.metabg.Event.IEventType;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Option
{
    public enum Category { TableClick, CardClick, NumberPress, Confirm, Undo };

    private final IEventType type;
    private final Category category;
    private final Integer value; // layer for TableClick; card type for CardClick, value for NumberPress

    public Option (IEventType type, Category category) {
        this.type = type;
        this.category = category;
        this.value = -1;
    }

    public Option (IEventType type, Category category, Integer value) {
        this.type = type;
        this.category = category;
        this.value = value;
    }

    public IEventType getType () { return type; }
    public Category getCategory () { return category; }
    public Integer getValue () { return value; }
    
    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("category", category.toString());
        result.put("value", value);
        return result;
    }    
}
