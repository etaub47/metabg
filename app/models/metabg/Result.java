package models.metabg;

import com.google.common.base.Optional;

public class Result
{
    public enum ResultType { DO_NOTHING, STATE_CHANGE, ERROR, GAME_OVER }
    public static final String MESSAGE_PREFIX = "MESSAGE: ";
    public static final String ERROR_PREFIX = "ERROR: ";
    
    private final ResultType type;
    private final Optional<String> message;
    
    public Result (ResultType type) {
        this.type = type;
        this.message = Optional.absent();
    }

    public Result (ResultType type, String message) {
        this.type = type;
        if (type == ResultType.ERROR)
            this.message = Optional.of(ERROR_PREFIX + message);
        else
            this.message = Optional.of(MESSAGE_PREFIX + message);
    }

    public ResultType getType () { return type; }
    public boolean hasMessage () { return message.isPresent(); }
    public String getMessage () { return message.get(); }
}
