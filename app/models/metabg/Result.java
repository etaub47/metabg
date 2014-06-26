package models.metabg;

public class Result
{
    public enum ResultType { DO_NOTHING, STATE_CHANGE, ERROR, GAME_OVER }
    
    private final ResultType type;
    private final String message;
    
    public Result (ResultType type) {
        this.type = type;
        this.message = null;
    }

    public Result (ResultType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ResultType getType () { return type; }
    public String getMessage () { return message; }
}
