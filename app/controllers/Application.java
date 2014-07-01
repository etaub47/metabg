package controllers;

import java.util.List;
import models.metabg.Game;
import models.metabg.GameManager;
import models.metabg.Seat;
import models.metabg.Table;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller 
{
    // GET     /metabg
    public static Result games () {
        List<Game> games = GameManager.getInstance().getCatalog();
        return ok(views.html.metabg.render(games));
    }

    // GET     /metabg/:game
    public static Result tables (String gameName) {
        if (gameName == null) 
            return badRequest();        
        Game game = GameManager.getInstance().getGame(gameName);
        List<Table> tables = GameManager.getInstance().getTables(gameName);
        if (game == null || tables == null) 
            return null;
        ArrayNode tablesJson = JsonNodeFactory.instance.arrayNode();
        for (Table table : tables)
            tablesJson.add(table.getJson());
        ObjectNode gameJson = Json.newObject();
        gameJson.put("tables", tablesJson);
        gameJson.put("minPlayers", game.getConfig().getMinPlayers());
        gameJson.put("maxPlayers", game.getConfig().getMaxPlayers());
        return ok(gameJson);
    }
    
    // GET     /metabg/:game/:table
    public static Result seats (String gameName, String tableName) {
        Table table = GameManager.getInstance().getTable(gameName, tableName);
        if (table == null) 
            return badRequest();
        Seat[] seats = table.getSeats();
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        for (Seat seat : seats)
            result.add((seat == null) ? "Empty" : seat.getPlayerName());
        return ok(result);
    }
    
    // POST    /metabg/:game/:table    
    public static Result createTable (String gameName, String tableName, Integer numPlayers) {
        if (gameName == null || tableName == null || numPlayers == null)
            return badRequest();
        Game game = GameManager.getInstance().getGame(gameName);
        if (game == null || numPlayers < game.getConfig().getMinPlayers() || numPlayers > game.getConfig().getMaxPlayers())
            return badRequest();            
        Table table = new Table(game.getConfig(), tableName, numPlayers);
        boolean success = GameManager.getInstance().addTable(gameName, table);
        return success ? ok() : status(CONFLICT);
    }
    
    // DELETE  /metabg/:game/:table
    public static Result removeTable (String gameName, String tableName) {
        if (gameName == null || tableName == null)
            return badRequest();
        Game game = GameManager.getInstance().getGame(gameName);
        Table table = GameManager.getInstance().getTable(gameName, tableName);
        if (game == null || table == null)
            return badRequest();        
        GameManager.getInstance().removeTable(gameName, tableName);
        return ok();        
    }
    
    // GET     /metabg/:game/:table/:seat
    public static Result playGame (String gameName, String tableName, Integer seat, String player) {
        player = player.equals("default") ? "Player " + (seat + 1) : player;
        Game game = GameManager.getInstance().getGame(gameName);
        Table table = GameManager.getInstance().getTable(gameName, tableName);
        if (game == null || table == null)
            return badRequest();
        table.seatPlayer(seat, player);
        ObjectNode result = Json.newObject();
        result.put("resources", game.getResourcesJson());
        return ok(views.html.canvas.render(result.toString()));
    }
    
    // GET     /resources/js/routes
    public static Result jsRoutes () {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes", routes.javascript.Application.tables(),
            routes.javascript.Application.seats(), routes.javascript.Application.createTable()));
    }
    
    // GET     /metabg/:game/:table/:seat/connect
    public static WebSocket<String> connect (final String gameName, final String tableName, final Integer seat) {
        return new WebSocket<String>() {            
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                Table table = GameManager.getInstance().getTable(gameName, tableName);
                if (table != null) table.connectPlayer(seat, in, out);
            }
        };
    }
}
