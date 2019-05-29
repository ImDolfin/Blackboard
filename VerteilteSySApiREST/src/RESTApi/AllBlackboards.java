package RESTApi;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Application;
import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;

import com.sun.research.ws.wadl.Response;


@Path("/blackboards")
public class AllBlackboards {
	
	
	
	 @Path("{sku}")
	 @PUT
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void saveBlackboards(@PathParam("sku") String sku) throws IOException {
	  
		 
		 try {
			 FileWriter fileWriter = new FileWriter("C:\\Users\\U51210\\git\\verteiltsys\\VerteilteSySApiREST\\model.json");
			//JSONParser jsonParser = new JSONParser();
			// jsonParser.parse(sku);
			 fileWriter.write(sku);
			 fileWriter.flush(); 
			 fileWriter.close(); 
		 }catch (IOException e) {
			e.printStackTrace();
		}
		 
	    }
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBlackboards() throws ParseException
	{
		//JSONObject blackboards = new JSONObject();
		
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
         
        //try (FileReader reader = new FileReader("/data/model.json"))
        try (FileReader reader = new FileReader("C:\\Users\\U51210\\git\\verteiltsys\\VerteilteSySApiREST\\model.json"))
        {
        	
        	jsonArray = (JSONArray) jsonParser.parse(reader);
        	
        	
        	
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        return jsonArray.toString();
	}
}
