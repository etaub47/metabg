package utils;

import java.util.Comparator;
import models.metabg.Sprite;

public class SpriteComparator implements Comparator<Sprite>
{
    @Override
    public int compare (Sprite sprite1, Sprite sprite2) {
        return sprite1.getZ() - sprite2.getZ();
    }
}
