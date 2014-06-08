package models.metabg;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Action
{
    public enum Category { TableClick, CardClick, Selection };
    
    private final int playerNum;
    private final String prompt;
    private final Category category;
    private final Integer layerType; // layer for TableClick; card type for CardClick
    private final Integer min, max; // for Selection
    
    private boolean fulfilled;
    
    public Action (int playerNum, String prompt, Category category, Integer layerType, Integer min, Integer max) {
        this.playerNum = playerNum;
        this.prompt = prompt;
        this.category = category;
        this.layerType = layerType;
        this.min = min;
        this.max = max;
        this.fulfilled = false;
    }
    
    public JsonNode getJson () {
        ObjectNode actionJson = Json.newObject();
        actionJson.put("player", playerNum);
        actionJson.put("prompt", prompt);
        actionJson.put("category", category.toString());
        actionJson.put("layerType", layerType);
        actionJson.put("min", min);
        actionJson.put("max", max);
        return actionJson;
    }
}
