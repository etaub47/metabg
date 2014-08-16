package models.metabg;

import com.google.common.base.Optional;

public class Result
{
    public enum ResultType { DO_NOTHING, STATE_CHANGE, ERROR, GAME_OVER }
    public static final String PREFIX = "MESSAGE: ";
    
    private final ResultType type;
    private final Optional<String> message;
    
    public Result (ResultType type) {
        this.type = type;
        this.message = Optional.absent();
    }

    public Result (ResultType type, String message) {
        this.type = type;
        this.message = Optional.of(PREFIX + message);
    }

    public ResultType getType () { return type; }
    public boolean hasMessage () { return message.isPresent(); }
    public String getMessage () { return message.get(); }
}
