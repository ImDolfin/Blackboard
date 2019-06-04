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
import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.apache.commons.io.FileUtils;
import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;

//import com.sun.research.ws.wadl.Response;


@Path("/blackboards")
public class AllBlackboards {
	private static JSONObject jsonObject = new JSONObject();
//	private HttpServletRequest servletRequest;
	private static ArrayList<String> arrayList = new ArrayList<>();
	 Calendar cal = Calendar.getInstance();
	 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
     
	
	 @Context HttpServletRequest servletRequestall; 
	 @Path("/json")
	 @POST
	 @Consumes(MediaType.TEXT_PLAIN)
	 
	 public Response saveBlackboards(String text) {
	  
		
		JSONObject jsonObject = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		try {
			jsonObject = (JSONObject) jsonParser.parse(text);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AllBlackboards.jsonObject = jsonObject;
		
		
		arrayList.add("TIMESTAMP: "+ sdf.format(cal.getTime())+ "   IP-Adresse: "+ servletRequestall.getRemoteAddr() + " hat ein neues Blackboard erstellt </br>");
		
		return Response.ok() //200
				.entity(null)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		
		 
	}
	// 
	
//	 @Context HttpServletRequest servletRequest; 
	 @Context HttpServletRequest servletRequestdel; 
	 @Path("/delete")
	 @POST
	 @Consumes(MediaType.TEXT_PLAIN)
	 //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	// public Response saveBlackboards(@QueryParam("text") String text) {
	 public Response deleteBlackboards(String removekey) {
	 
		arrayList.add("TIMESTAMP: "+ sdf.format(cal.getTime())+ "   IP-Adresse: "+ servletRequestdel.getRemoteAddr() + " hat Blackboard mit dem Namen " + removekey + " geloescht </br>");
		 
		JSONObject jsonObject = new JSONObject(); 
		int size = AllBlackboards.jsonObject.size();
		
		for(int i=0;i<size;i++) {
			
			jsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
			String key = (String) jsonObject.get("name");
			if(removekey.equals(key)) {
				AllBlackboards.jsonObject.remove(Integer.toString(i), jsonObject);
				break;
			}
		}
		
		return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		
		 
	}
	
	
	@Path("/text") 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getText() throws IOException  
	{
	//	File file = null;
		String string = null;
		//String path = new java.io.File("").getAbsolutePath(); 
		
		string = AllBlackboards.jsonObject.toString();
  //      string = this.getClass().getClassLoader().getResource("").getPath();
   //     string = path;
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
	
	
	@Context HttpServletRequest servletRequestget; 
	@Path("/json") 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getBlackboards()
	{
		
		arrayList.add("TIMESTAMP: "+ sdf.format(cal.getTime())+ "   IP-Adresse: "+ servletRequestget.getRemoteAddr() + " hat alle Blackboards abgefragt </br>");
	//	Model model = new Model();
		String string = null;
		
		string = AllBlackboards.jsonObject.toJSONString();
		
		return Response.ok() //200
				.entity(string)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		/*response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");*/

		
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
		
		
		
		
//		return string;
        //return jsonArray.toString();
	}
	
	
	@Path("/log") 
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getIP() throws IOException  
	{
		String string =null;
        if(arrayList.isEmpty()) {
        	string = "empty";
        }
        else {
        	string = arrayList.toString();
        }
        return Response.ok() //200
        		.entity(string)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}
	
	
}
