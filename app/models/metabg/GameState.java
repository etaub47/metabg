package models.metabg;

import java.util.HashSet;
import java.util.Set;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class GameState
{
    // TODO: do we need a status field to tell us if somebody won?
    protected Sprites sprites;
    protected Set<Action> expectedActions;
    
    public GameState () {
        sprites = new Sprites(3); // TODO: make number of layers configurable by game?
        expectedActions = new HashSet<>(); // TODO: limit hash set size to number of players?
    }
    
    public void init () {
        initSprites();
        initState();
    }
    
    protected abstract void initSprites ();
    protected abstract void initState ();

    public JsonNode getJson () {
        ObjectNode result = Json.newObject();
        result.put("sprites", getSpritesJson());
        result.put("expectedActions", getExpectedActionsJson());
        return result;
    }
    
    protected JsonNode getSpritesJson () {
        return sprites.getJson();        
    }
    
    protected JsonNode getExpectedActionsJson () {
        ArrayNode expectedActionsJson = JsonNodeFactory.instance.arrayNode();
        for (Action expectedAction : expectedActions) {
            expectedActionsJson.add(expectedAction.getJson());            
        }
        return expectedActionsJson;
    }
}
