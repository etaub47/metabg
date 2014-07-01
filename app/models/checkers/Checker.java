package models.checkers;

public class Checker
{
    // player color constants
    public static final int BLACK = 0;
    public static final int RED = 1;

    private final String id;
    private final int owner;
    private int position;
    private boolean king;
    
    public Checker (String id, int owner, int position) {
        this.id = id;
        this.owner = owner;
        this.position = position;
        this.king = false;
    }
    
    public final String getId () { return id; }
    public final int getOwner () { return owner; }
    public final int getPosition () { return position; }
    public final boolean isKing () { return king; }
    public final boolean isBlack () { return owner == BLACK; }
    public final boolean isRed () { return owner == RED; }
    
    public void move (int position) { this.position = position; }
    public void promoteToKing () { this.king = true; }
}
