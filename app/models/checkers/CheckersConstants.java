package models.checkers;

public class CheckersConstants
{
    // layer constants
    public static final int BOARD_LAYER = 0;
    public static final int CHECKERS_LAYER = 1;
    
    // sequence constants
    public static final String SEQUENCE_ID = "CurrentSequence";
    public static final String SEQUENCE_TYPE = "StandardTurn";
    
    // prompts    
    public static final String PROMPT_SELECT_CHECKER = "Please select a checker to move.";
    public static final String PROMPT_SELECT_SQUARE = "Please select a square to move or jump to.";
    public static final String PROMPT_SELECT_SQUARE_OR_END = "Please select another square to jump to, or end your turn.";
    
    // errors
    public static final String ERROR_WRONG_CHECKER = "You may only move one of your own checkers.";
    public static final String ERROR_INVALID_MOVE = "That is not a valid move.";
}
