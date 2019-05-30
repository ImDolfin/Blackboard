package RESTApi;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Application;

import org.apache.commons.io.FileUtils;
import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;

//import com.sun.research.ws.wadl.Response;


@Path("/blackboards")
public class AllBlackboards {
	
	
	
	 @Path("/puttext")
	 @PUT
	 @Consumes(MediaType.APPLICATION_JSON)
	 //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	// public Response saveBlackboards(@QueryParam("text") String text) {
	 public void saveBlackboards(String text) {
	  
		Model model = new Model();
		 
		JSONObject jsonObject = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		try {
			jsonObject = (JSONObject) jsonParser.parse(text);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.setJsonObject(jsonObject);
		
		 /*
		  
		 try {
			
			 FileWriter fileWriter = new FileWriter("C:\\Users\\U51210\\git\\verteiltsys\\VerteilteSySApiREST\\model.json");
			//JSONParser jsonParser = new JSONParser();
			// jsonParser.parse(sku);
			 fileWriter.write(text);
			 fileWriter.flush(); 
			 fileWriter.close(); 
		 }catch (IOException e) {
			e.printStackTrace();
		}*/
		 
	        //return Response.status(200).build();
		 
	}
	 
	@Path("/text") 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getText() throws IOException  
	{
		File file = null;
		String string = null;
		String path = new java.io.File("").getAbsolutePath(); 
		
        file = new File("C:\\Users\\U51210\\git\\verteiltsys\\VerteilteSySApiREST\\model.txt");
        string = this.getClass().getClassLoader().getResource("").getPath();
        string = path;
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
        return string;
	}
	
	@Path("/json") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBlackboards()
	{
		Model model = new Model();
		JSONObject jsonObject = new JSONObject();
		String string = null;
		
		jsonObject = model.getJsonObject();
		string = jsonObject.toJSONString();
		
		/* JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
         
        
        try (FileReader reader = new FileReader("C:\\Users\\U51210\\git\\verteiltsys\\VerteilteSySApiREST\\model.json"))
        {
        	
        	jsonArray = (JSONArray) jsonParser.parse(reader);
        	
        	
        	
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
		
		
		
		
		return string;
        //return jsonArray.toString();
	}
}
