package models.metabg;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class UserInterface
{
    public static final String BOARD = "board";
    public static final String SELECTION = "selection";

    private List<Layer> layers;
    
    public UserInterface (int numLayers) {
        this.layers = new ArrayList<>(numLayers);
        for (int l = 0; l < numLayers; l++)
            layers.add(new Layer());
    }
    
    public Layer getLayer (int layer) { return layers.get(layer); }
    
    public JsonNode getJson () {
        ArrayNode uiJson = JsonNodeFactory.instance.arrayNode();
        for (Layer layer : layers)
            uiJson.add(layer.getJson());
        return uiJson;
    }
}
