package RESTApi;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;

import org.json.simple.JSONObject;



@Path("/blackboards")
public class AllBlackboards {
	private static JSONObject jsonObject = new JSONObject();			//Create a static JSON object as a data store
																		// The structure of each blackboard in the JSONObject is as follows:
																		// {number:jsonObject} -> The jsonObject again has the structure:
																		// {"name": blackboardname, "text": blackboardcontent}
																		//original JSON file, but the path is not visible on Amazon aws
	private static String logstring = "";								//Data storage for LOGGING
	 Calendar cal = Calendar.getInstance();								//Object for displaying the current date, with date format
	 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	 SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
     
	 
	 /*
	  * Method: saveBlackboards, POST
	  * Path: /rest/blackboards/json
	  * Input: TEXT
	  * Returned value: Response -> Header, Content
	  * Description: This method is used to create new blackboards and modify the existing content of a blackboard.
	  * 
	  */

	 @Context HttpServletRequest servletRequestall; 					// HttpServletRequest is required to output the remote IP address
	 @Path("/json")
	 @POST
	 @Consumes(MediaType.TEXT_PLAIN) 									//Textplain is used instead of APPLICATION_JSON, otherwise a 
	 @Produces(MediaType.TEXT_PLAIN) 									//PREFLIGHT-Request is triggered -> we  could not handle this kind of Requests
	 																	//as described later
	 
	 public Response saveBlackboards(String text) { 					//String contains "name", "text" of a blackboard, as well as the information
		 																//"create" or "change" to create or change the blackboard
		 
		 String[] substrings = text.split(","); 						// Split the string into substrings
		 boolean exists = false;										
		 String error = "SUCCESSFUL";
		 String mode = null;
		 int size = AllBlackboards.jsonObject.size();
		 JSONObject smalljsonObject = new JSONObject();					
		 
		 
		
		JSONObject jsonObject = new JSONObject(); 							//a json object is created here, which represents a blackboard
		jsonObject.put("name", substrings[0]); 								//with that the blackboard exists, but is not yet in the static 
		jsonObject.put("text", substrings[1]); 								//jsonObject added
		mode = substrings[2]; 												// here the mode (create or change) is defined
		
																			// the reason why PUT and DELETE is not added to the API querys is, that
																			// only GET and POST works, without any CORS policy error from Amazon aws 
																			//that is thrown. It's easy to fix for amazon APIs, but we have moved a ".war" -
																			//file to the server, which itself contains the API 
																			// we have not managed to fix this error

		// In the following loop it is searched whether a blackboard already exists in the static JSONObject with the requested blackboard name
		
		for(int i=0;i<size;i++) {
			smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
			String string = (String) smalljsonObject.get("name");
			if(string.equals(substrings[0])) {
				exists = true;
			 }
		}
		
		//here  the 4 possible cases (blackboard exists and mode create,
										// blackboard exists and mode change,
										// blackboard does not exist and mode create,
										// blackboard does not exist and mode change,
		// are treated and output accordingly
		
		
		//exist & create: - Output: Log entry: already existing blackboard
						//- Output: errortext	
		if(exists && mode.equals("create"))
		{
			error = "EXISTS_ALREADY";
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>wollte ein neues Blackboards mit dem Namen '" + substrings[0] + "' erstellen. FEHLGESCHLAGEN: BLACKBOARD BEREITS EXISTENT</td></tr>");
			
		}
		
		//exist & change: - Output: Log entry: change of blackboardcontens
						//- Activity: save blackboard in static jsonObject
		else if(exists && mode.equals("change")) {
			for(int i=0;i<size;i++) {
				smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
				String key = (String) smalljsonObject.get("name");
				if(substrings[0].equals(key)) {
					smalljsonObject.put("text", substrings[1]);
					AllBlackboards.jsonObject.put(Integer.toString(i), smalljsonObject);
				}
			}
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>hat den Inhalt des Blackboards mit dem Namen '" + substrings[0] + "' geaendert</td></tr>");
			
			//not exist & create: - Output: Log entry: create a new blackboard
								//- Activity: save blackboard in static jsonObject	
		}
		else if(exists==false && mode.equals("create")) {
			AllBlackboards.jsonObject.put(Integer.toString(size), jsonObject);
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>hat ein neues Blackboard mit dem Namen '" + substrings[0] + "' erstellt</td></tr>");
			
		}
		
		//not exist & change: - Output: Log entry: blackboard does not exist
							//- Output: errortext
		else if(exists==false && mode.equals("change")) {
			error = "NOT_FOUND";
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>wollte den Inhalt des Blackboards mit dem Namen '" + substrings[0] + "' aendern. FEHLGESCHLAGEN - BLACKBOARD NICHT EXISTENT</td></tr>");
			
		}
		
		return Response.ok() //200
				.entity(error)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}

	 
	 /*
	  * Method: deleteBlackboards, POST
	  * Path: /rest/blackboards/delete
	  * Input: TEXT
	  * Returned value: Response -> Header, Content
	  * Description: This method is used to delete a blackboard. Either an error text is returned, if the error text is not
	  * deleting blackbaord does not exist, or the blackboard is deleted
	  */
	
	 @Context HttpServletRequest servletRequestdel; 
	 @Path("/delete")
	 @POST
	 @Consumes(MediaType.TEXT_PLAIN)
	 @Produces(MediaType.TEXT_PLAIN)
	 public Response deleteBlackboards(String removekey) {
	 
		JSONObject jsonObject = new JSONObject(); 
		int size = AllBlackboards.jsonObject.size();
		int repl;
		boolean once = false;
		boolean exists = false;
		String error = null;
		JSONObject smalljsonObject = new JSONObject();
		
		
		// In the following loop it is searched whether a blackboard already exists in the static JSONObject with the requested blackkboard name
		
		for(int i=0;i<size;i++) {
			smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
			String string = (String) smalljsonObject.get("name");
			if(string.equals(removekey)) {
				exists = true;
			 }
		}
		
		
		// In the following loop, if the blackboard to be deleted exists, it is removed from the static JSON object
		//After that the numbers of the blackboards above are lowered again in such a way that a series of numbers is created.
		//Example: jsonobjekt: b1,b2,b3,b4,b5
		// delete the b3
		// left: b1,b2,b4,b5
		//set the numbers: b1, b2, b3, b4
		// this facilitates the client-side query of the blackboards, since a "foreach function" for iterating the JSON object is not available


		if(exists) {
			for(int i=0;i<size;i++) {
				
				jsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
				String key = (String) jsonObject.get("name");
				if(once) {
					repl = i-1;
					AllBlackboards.jsonObject.put(Integer.toString(repl), jsonObject);
					AllBlackboards.jsonObject.remove(Integer.toString(i), jsonObject);
				}
				else if(removekey.equals(key)) {
					AllBlackboards.jsonObject.remove(Integer.toString(i), jsonObject);
					once=true;
				}
			}
			
			//as soon as the deletion is done, the logging is called 
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestdel.getRemoteAddr() + "</td><td>hat Blackboard mit dem Namen '" + removekey + "' geloescht</td></tr>");
		}			
		else {//if the blackboard does not exist the logging and error text will be ejected
			error = "Blackboard is not existing!";
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestdel.getRemoteAddr() + "</td><td>wollte das Blackboard mit dem Namen '" + removekey + "' loeschen. FEHLGESCHLAGEN - BLACKBOARD NICHT EXISTENT</td></tr>");
		}
		
		return Response.ok() //200
				.entity(error)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		
		 
	}
	
	
	 
	 /*
	  * Method: getBlackboards, GET
	  * Path: /rest/blackboards/json
	  * Input: -
	  * Returned value: Response -> Header, Content
	  * Description: This method returns all blackboards stored in the static JSON object.
	  * The return is as method type: text_plain and not as application_json for the reasons mentioned above.
	  */
	 
	@Context HttpServletRequest servletRequestget; 
	@Path("/json") 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getBlackboards()
	{
		String string = null;
		
		
		// Case 1: No blackboard available: - Output: Log entry: no backboard available
		
		if(AllBlackboards.jsonObject.size()==0) {
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestget.getRemoteAddr() + "</td><td>hat alle Blackboards abgefragt! Es wurden " + AllBlackboards.jsonObject.size() + " Blackboards zurueckgegeben</td></tr>");
		}
		// Case 2: Blackboard present: - output: log entry: x backboards returned
		else {
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestget.getRemoteAddr() + "</td><td>hat alle Blackboards abgefragt! Es wurden " + AllBlackboards.jsonObject.size() + " Blackboards zurueckgegeben</td></tr>");
		}
		
		// the JSON object is returned as string, also in case of an empty object -> (easier client-side processing)
		string = AllBlackboards.jsonObject.toJSONString();
		
		return Response.ok() //200
				.entity(string)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		
	}
	
	
	
	 /*
	  * Method: getLog, GET
	  * Path: /rest/blackboards/log
	  * Input: -
	  * Returned value: Response -> Header, Content
	  * Description: This method returns all loggings stored in the static logstring. Together, these form a table
	  * The logging is displayed since the server was started and the application was last uploaded to the server.
	  */
	
	@Context HttpServletRequest servletRequestlog; 
	@Path("/log") 
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getLog() throws IOException  
	{
		//each logging throws its own log entry
		logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestget.getRemoteAddr() + "</td><td>hat das Logging abgefragt</td></tr>");
        String returnstring ="<table border='1'>" + logstring + "</table>";
        
        return Response.ok() //200
        		.entity(returnstring)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}
	
	
}