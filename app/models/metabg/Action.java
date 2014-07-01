package models.metabg;

import java.util.Arrays;
import java.util.Collections;
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
    
    public Action (int playerNum, String prompt, IEventType eventType, Category category) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = Collections.singleton(new Option(eventType, category));
    }

    public Action (int playerNum, String prompt, IEventType eventType, Category category, Integer layer) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = Collections.singleton(new Option(eventType, category, layer));
    }

    public Action (int playerNum, String prompt, IEventType eventType, Category category, Integer min, Integer max) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = Collections.singleton(new Option(eventType, category, min, max));
    }
    
    public Action (int playerNum, String prompt, Option choice) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = Collections.singleton(choice);
    }

    public Action (int playerNum, String prompt, Set<Option> choices) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = choices;
    }

    public Action (int playerNum, String prompt, Option... choices) {
        this.playerNum = playerNum;
        this.prompt = prompt;          
        this.options = new HashSet<Option>(Arrays.asList(choices));
    }
    
    public int getPlayerNum () { return playerNum; }
    public String getPrompt () { return prompt; }
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

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + playerNum;
        return result;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Action other = (Action) obj;
        if (playerNum != other.playerNum)
            return false;
        return true;
    }
}
