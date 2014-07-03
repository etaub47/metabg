package models.metabg;

public interface IGameLogic 
{
    public void init (GameState state);
    public Result processEvent (GameState state, Event event) throws Exception;
}
