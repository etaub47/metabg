package models.metabg;

import java.util.HashSet;
import java.util.Set;
import models.metabg.Event.IEventType;
import models.metabg.Option.Category;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Action
{
    private final int playerNum;
    private final String prompt;
    private final Set<Option> options;
    
    private Action (int playerNum, String prompt, Set<Option> choices) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = choices;
    }

    public int getPlayerNum () { 
        return playerNum; 
    }
    
    public Option getOptionByCategory (Category category) {
        for (Option option : options)
            if (option.getCategory() == category)
                return option;
        return null;        
    }
    
    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("player", playerNum);
        result.put("prompt", prompt);
        ArrayNode optionsJson = JsonNodeFactory.instance.arrayNode();
        for (Option option : options)
            optionsJson.add(option.getJson());
        result.put("options", optionsJson);
        return result;             
    }
    
    public static class Builder 
    {
        private int playerNum;
        private String prompt;
        private Set<Option> options = new HashSet<>();
        
        public Builder () { }        
        public Builder player (int playerNum) { this.playerNum = playerNum; return this; }
        public Builder prompt (String prompt) { this.prompt = prompt; return this; }
        
        public Builder option (IEventType type, Category category) { 
            this.options.add(new Option(type, category)); 
            return this; 
        }
        
        public Builder option (IEventType type, Category category, Integer value) { 
            this.options.add(new Option(type, category, value)); 
            return this; 
        }
        
        public Action build () { return new Action(playerNum, prompt, options); }        
    }
}
