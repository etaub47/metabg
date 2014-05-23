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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller 
{
    // GET     /metabg                     controllers.Application.games()
    public static Result games () {
        List<Game> games = GameManager.getInstance().getCatalog();
        return ok(views.html.metabg.render(games));
    }

    // GET     /metabg/:game               controllers.Application.tables(game: String)
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
        gameJson.put("minPlayers", game.getMinPlayers());
        gameJson.put("maxPlayers", game.getMaxPlayers());
        return ok(gameJson);
    }
    
    // GET     /metabg/:game/:table        controllers.Application.seats(game: String, table: String)
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
    
    // POST    /metabg/:game/:table        controllers.Application.createTable(game: String, table: String, numPlayers: Integer ?= 2)    
    public static Result createTable (String gameName, String tableName, Integer numPlayers) {
        if (gameName == null || tableName == null || numPlayers == null)
            return badRequest();
        Game game = GameManager.getInstance().getGame(gameName);
        if (game == null || numPlayers < game.getMinPlayers() || numPlayers > game.getMaxPlayers())
            return badRequest();            
        Table table = new Table(tableName, numPlayers);
        boolean success = GameManager.getInstance().addTable(gameName, table);
        return success ? ok() : status(CONFLICT);
    }
    
    // DELETE  /metabg/:game/:table        controllers.Application.removeTable(game: String, table: String)
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
    
    // GET     /metabg/:game/:table/:seat  controllers.Application.playGame(game: String, table: String, seat: Integer, player: String ?= "default")
    public static Result playGame (String gameName, String tableName, Integer seat, String player) {
        player = player.equals("default") ? "Player " + (seat + 1) : player;
        Game game = GameManager.getInstance().getGame(gameName);
        Table table = GameManager.getInstance().getTable(gameName, tableName);
        if (game == null || table == null)
            return badRequest();
        ObjectNode result = Json.newObject();
        result.put("resources", game.getResourcesJson());
        result.put("sprites", game.getSpritesJson());
        return ok(views.html.canvas.render(result.toString()));
    }
    
    // GET     /resources/js/routes        controllers.Application.jsRoutes()
    public static Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes", routes.javascript.Application.tables(),
            routes.javascript.Application.seats(), routes.javascript.Application.createTable()));
    }
    
    /*
    public static WebSocket<String> index() {
        return new WebSocket<String>() {
            
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                
                in.onMessage(new Callback<String>() {
                    public void invoke (String event) {
                        Logger.info(event);
                    }                                        
                });
                
                in.onClose(new Callback0() {
                    public void invoke () {
                        Logger.info("Disconnected");
                    }
                });
                
                out.write("Hello!");
                
            }
        };
    }
    
    public static Result wstest() {
        return ok(views.html.wstest.render());
    }
    */
    
}
