package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Choice
{
    public enum Category { TableClick, CardClick, Selection };

    protected final Category category;
    protected final Integer layerType; // layer for TableClick; card type for CardClick
    protected final Integer min, max; // for Selection (0 = enter)    

    public Choice (Category category, Integer layerType, Integer min, Integer max) {
        this.category = category;
        this.layerType = layerType;
        this.min = min;
        this.max = max;
    }

    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("category", category.toString());
        result.put("layerType", layerType);
        result.put("min", min);
        result.put("max", max);
        return result;
    }    
}
