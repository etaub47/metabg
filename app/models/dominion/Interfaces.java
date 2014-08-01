package models.dominion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class Interfaces
{
    public interface IFunction extends Function<Input, Integer>
    {
    }

    public interface IPredicate extends Predicate<Input>
    {
    }
    
    public interface IEffect {
        public boolean check (Input data);
        public void execute (Input data);
        public boolean canUndo ();
        public void undo (Input data);
    }
}
