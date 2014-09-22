package models.dominion;

import java.util.List;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class Interfaces
{
    public interface IFunction extends Function<DominionGameState, Integer>
    {
    }

    public interface IPredicate extends Predicate<DominionGameState>
    {
    }
    
    public interface IEffect {
        public List<Integer> getSelectedPlayers (DominionGameState state);        
        public boolean check (DominionGameState state);
        public void execute (DominionGameState state);
        public boolean canUndo ();
        public void undo (DominionGameState state);
    }
}
