package models.metabg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import play.Play;

public class GameManager
{
    private static GameManager instance = new GameManager();
    public static GameManager getInstance () { return instance; }
    protected GameManager () { registerGames(); }
    
    private Map<String, Game> catalog = new LinkedHashMap<>();    
    private Map<String, Map<String, Table>> tables = new LinkedHashMap<>();
    
    private void registerGame (Game game) {
        game.init();
        catalog.put(game.getName(), game);
        tables.put(game.getName(), new LinkedHashMap<String, Table>());
        Table testTable = game.getConfig().createTestTable(game);
        if (testTable != null)
            addTable(game.getName(), testTable);
    }
    
    private void registerGames () {
        String[] gameConfigNames = Play.application().configuration().getString("games").split(",");        
        for (String gameConfigName : gameConfigNames)
            try { registerGame(new Game((IGameConfig) ((Class.forName(gameConfigName)).getMethod("getInstance")).invoke(null))); }
            catch (Exception e) { }
    }
    
    public List<Game> getCatalog () { return new ArrayList<>(catalog.values()); }
    public Game getGame (String gameName) { return catalog.get(gameName); }
    
    public List<Table> getTables (String gameName) { 
        return tables.containsKey(gameName) ? new ArrayList<>(tables.get(gameName).values()) : null; 
    }
    
    public Table getTable (String gameName, String tableName) { 
        return tables.get(gameName) == null ? null : tables.get(gameName).get(tableName); 
    }
    
    public boolean addTable (String gameName, Table table) {
        if ((tables.get(gameName) == null) || (tables.get(gameName).get(table.getName()) != null))
            return false;
        tables.get(gameName).put(table.getName(), table);
        return true;
    }
    
    public boolean removeTable (String gameName, String tableName) {
        if ((tables.get(gameName) == null) || (tables.get(gameName).get(tableName) == null))
            return false;
        tables.get(gameName).remove(tableName); 
        return true;
    }
}
