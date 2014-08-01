package models.metabg;

import java.util.List;
import com.google.common.collect.Lists;

public interface IGameModeFactory
{
    public static interface IGameMode { }
    
    public List<? extends IGameMode> getAllModes();
    public IGameMode getMode (String mode);
    
    public enum DefaultGameMode implements IGameMode { Default }    
    public static class DefaultGameModeFactory implements IGameModeFactory {
        @Override public List<? extends IGameMode> getAllModes () { return Lists.newArrayList(DefaultGameMode.Default); }
        @Override public IGameMode getMode (String mode) { return DefaultGameMode.Default; }        
    }
}
