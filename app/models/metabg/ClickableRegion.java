package models.metabg;

import org.json.JSONException;
import org.json.JSONObject;

public class ClickableRegion
{
    private final int relativeX;
    private final int relativeY;
    private final int width;
    private final int height;
    private final String value;
    
    public ClickableRegion (int relativeX, int relativeY, int width, int height, String value) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.width = width;
        this.height = height;
        this.value = value;
    }
    
    public JSONObject getJson () throws JSONException {
        JSONObject clickableRegion = new JSONObject();
        clickableRegion.put("relativeX", relativeX);
        clickableRegion.put("relativeY", relativeY);
        clickableRegion.put("width", width);
        clickableRegion.put("height", height);
        clickableRegion.put("value", value);
        return clickableRegion;
    }
}
