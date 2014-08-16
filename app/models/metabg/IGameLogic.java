package models.metabg;

public interface IGameLogic 
{
    public Result processEvent (GameState state, Event event) throws Exception;
}
