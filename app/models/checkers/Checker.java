package models.checkers;

public class Checker
{
    private String id;
    private int position;
    private boolean king;
    
    public Checker (String id, int position) {
        this.id = id;
        this.position = position;
        this.king = false;
    }
    
    public String getId () { return id; }
    public int getPosition () { return position; }
    public boolean isKing () { return king; }
    
    public void move (int position) { this.position = position; }
    public void promoteToKing () { this.king = true; }
}
