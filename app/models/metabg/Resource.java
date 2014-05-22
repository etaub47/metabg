package models.metabg;

import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.common.base.Optional;

public class Resource
{
    private final String name;
    private final String imageFront;
    private final Optional<String> imageBack;
    private final List<ClickableRegion> clickableRegions;

    public Resource (String name, String imageFront) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.absent();
        this.clickableRegions = Collections.emptyList();
    }    
    
    public Resource (String name, String imageFront, String imageBack) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.of(imageBack);
        this.clickableRegions = Collections.emptyList();
    }    
    
    public Resource (String name, String imageFront, List<ClickableRegion> clickableRegions) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.absent();
        this.clickableRegions = Collections.unmodifiableList(clickableRegions);
    }    
    
    public Resource (String name, String imageFront, String imageBack, List<ClickableRegion> clickableRegions) {
        this.name = name;
        this.imageFront = imageFront;
        this.imageBack = Optional.of(imageBack);
        this.clickableRegions = Collections.unmodifiableList(clickableRegions);
    }
    
    public String getName () { return name; }
    
    public JSONObject getJson () throws JSONException {
        JSONObject resource = new JSONObject();
        resource.put("imageFront", imageFront);
        if (imageBack.isPresent()) resource.put("imageBack", imageBack.get());
        JSONArray regions = new JSONArray();
        for (ClickableRegion region : clickableRegions)
            regions.put(region.getJson());
        resource.put("clickableRegions", clickableRegions);
        return resource;
    }
}
