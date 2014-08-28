package models.metabg;

import models.metabg.Option.Category;
import com.fasterxml.jackson.databind.JsonNode;

@Deprecated
public interface IAction
{    
    public int getPlayerNum ();
    public Option getOptionByCategory (Category category);    
    public JsonNode getJson ();
}
