package models.metabg;

public class NumericScore implements IScore
{
    private final int score;
    
    public NumericScore (int score) {
        this.score = score;
    }
    
    @Override
    public String toDisplayString ()
    {
        return String.valueOf(score);
    }
}
