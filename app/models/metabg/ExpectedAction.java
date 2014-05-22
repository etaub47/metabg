package models.metabg;

public class ExpectedAction
{
    public enum Category { TableClick, CardClick, Selection };
    
    private final int playerNum;
    private final String prompt;
    private final Category category;
    private final int layer; // for TableClick; also used for card type in CardClick
    private final int min, max; // for Selection
    
    private boolean fulfilled;
    
    private ExpectedAction (int playerNum, String prompt, Category category, int layer, int min, int max) {
        this.playerNum = playerNum;
        this.prompt = prompt;
        this.category = category;
        this.layer = layer;
        this.min = min;
        this.max = max;
        this.fulfilled = false;
    }
}
