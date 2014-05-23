package utils;

public class SpriteUtils
{
    public static final int TABLE_WIDTH = 3675;
    public static final int TABLE_HEIGHT = 2450;
    
    private static SpriteUtils instance = new SpriteUtils();
    public static SpriteUtils getInstance () { return instance; }
    protected SpriteUtils () { }
    
    public int centerSpriteOnTableX (int width) { return (TABLE_WIDTH - width) / 2; }
    public int centerSpriteOnTableY (int height) { return (TABLE_HEIGHT - height) / 2; }
}
