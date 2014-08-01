package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.metabg.Sprite;

public class SpriteUtils
{
    public static final int TABLE_WIDTH = 3675;
    public static final int TABLE_HEIGHT = 2450;
    
    public static int centerSpriteOnTableX (int width) { return (TABLE_WIDTH - width) / 2; }
    public static int centerSpriteOnTableY (int height) { return (TABLE_HEIGHT - height) / 2; }  
    
    public static void sortSprites (List<Sprite> sprites) { 
        Collections.sort(sprites, new Comparator<Sprite>() {
            @Override public int compare (Sprite sprite1, Sprite sprite2) {
                return sprite1.getZ() - sprite2.getZ();
            }
        }); 
    }
}
