package models.checkers;

public class Checker
{
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
    
    public void move (int position) { this.position = position; }
    public void promoteToKing () { this.king = true; }
}
