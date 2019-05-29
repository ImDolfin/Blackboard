package RESTApi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


@Path("/blackboards")
public class AllBlackboards {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBlackboards() throws ParseException
	{
		//JSONObject blackboards = new JSONObject();
		
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray;
        JSONObject jsonObject = null;
        String input =null;
         
        //try (FileReader reader = new FileReader("/data/model.json"))
        try (FileReader reader = new FileReader("C:\\Users\\U51210\\git\\verteiltsys\\VerteilteSySApiREST\\model.json"))
        {
            //Read JSON file
        	// jsonArray = (JSONArray) jsonParser.parse(reader);
        	
        	

        	 // for (Object o : jsonArray)
        	 // {
        	    jsonObject = (JSONObject) jsonParser.parse(reader);
        	  //}
            //Iterate over employee array
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (ParseException e) {
            e.printStackTrace();
        }*/
		/*
        File f = new File("scores.txt");
    	f.getAbsolutePath();
    	input = f.getAbsolutePath();*/
		
		/*
		InputStream input = Converter.class.getResourceAsStream("/data/model.json");
		String allBlackboards;
		allBlackboards = input.toString();
		*/
		
		
		//return jsonObject.toString();//
        return jsonObject.toString();
	}
}
