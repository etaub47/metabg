package models.metabg;

import java.util.Collections;
import java.util.Set;
import models.metabg.Choice.Category;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Action
{
    private final int playerNum;
    private final String prompt;
    private final Set<Choice> choices;
    
    public Action (int playerNum, String prompt, Category category, Integer layerType, Integer min, Integer max) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.choices = Collections.singleton(new Choice(category, layerType, min, max));
    }
    
    public Action (int playerNum, String prompt, Choice choice) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.choices = Collections.singleton(choice);
    }

    public Action (int playerNum, String prompt, Set<Choice> choices) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.choices = choices;
    }
    
    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("player", playerNum);
        result.put("prompt", prompt);
        ArrayNode choicesJson = JsonNodeFactory.instance.arrayNode();
        for (Choice choice : choices)
            choicesJson.add(choice.getJson());
        result.put("choices", choicesJson);
        return result;             
    }    
}
