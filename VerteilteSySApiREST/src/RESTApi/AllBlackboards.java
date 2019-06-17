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
	// Create a static JSON object as a data store
	// The structure of each blackboard in the JSONObject is as follows:
	// {number:jsonObject} -> The jsonObject again has the structure:
	// {"name": blackboard name, "text": blackboard content}
	// original JSON file, but the path is not visible on Amazon aws	
	private static JSONObject jsonObject = new JSONObject();		
	
	private const String NOT_FOUND = "Blackboard does not exist!"; 
	private const String EXISTS_ALREADY = "Blackboard already exists :) !",
		
	// Data storage for LOGGING
	private static String logstring = "";								
	// Object for displaying the current date, with date format
	private Calendar cal = Calendar.getInstance();								
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
     
	
	/* the reason why PUT and DELETE is not added to the API querys is, that
	 * only GET and POST works, without any CORS policy error from Amazon aws 
	 * that is thrown. It's easy to fix for amazon APIs, but we have moved a ".war" -
	 * file to the server, which itself contains the API 
	 * we have not managed to fix this error
	 */
	 
	
	
	/**
	 * Method: saveBlackboards, POST
	 * Path: /rest/blackboards/json
	 * HttpServletRequest is required to output the remote IP address
	 * Textplain is used instead of APPLICATION_JSON, otherwise a 																	
	 * PREFLIGHT-Request is triggered -> we  could not handle this kind of Requests
	 * as described later
	 * Description: This method is used to create new blackboards and modify the existing content of a blackboard.
	 * @param: text String contains "name", "text" of a blackboard, as well as the information
	 * 		"create" or "change" to create or change the blackboard
	 * @return Response -> Header, Content
	 */
	@Context HttpServletRequest servletRequestAll; 					
	@Path("/json")
	@POST
	@Consumes(MediaType.TEXT_PLAIN) 									
	@Produces(MediaType.TEXT_PLAIN) 	
	public Response saveBlackboards(String text) { 

		// Split the string into substrings
		String[] substrings = text.split(","); 						
		boolean exists = false;										
		String error = null;
		String mode = null;
		int size = AllBlackboards.jsonObject.size();
		JSONObject smalljsonObject = new JSONObject();					
		//a json object is created here, which represents a blackboard
		JSONObject jsonObject = new JSONObject(); 							
		//with that the blackboard exists, but is not yet added in the static jsonObject 
		jsonObject.put("name", substrings[0]); 								 
		jsonObject.put("text", substrings[1]); 								
		// here the mode (create or change) is defined
		mode = substrings[2]; 												

		// In the following loop it is searched whether a blackboard already 
		// exists in the static JSONObject with the requested blackboard name
		exists = checkForObjectsExistence(substrings[0]);
		
		/*
		 * here  the 4 possible cases ( blackboard exists and mode create,
		 *								blackboard exists and mode change,
		 *								blackboard does not exist and mode create,
		 *								blackboard does not exist and mode change,
		 * are treated and output accordingly
		 */
		
		// exist & create: - Output: Log entry: already existing blackboard
						//- Output: errortext	
		if(exists && mode.equals("create"))
		{
			error = EXISTS_ALREADY;
			createLogEntry(servletRequestAll.getRemoteAddr(),
					"wollte ein neues Blackboards mit dem Namen '" + substrings[0] + 
					"' erstellen. FEHLGESCHLAGEN: BLACKBOARD BEREITS EXISTENT");
		}
		
		// exist & change: - Output: Log entry: change of blackboardcontens
		//				   - Activity: save blackboard in static jsonObject
		else if(exists && mode.equals("change")) {
			for(int i=0;i<size;i++) {
				smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
				String key = (String) smalljsonObject.get("name");
				if(substrings[0].equals(key)) {
					smalljsonObject.put("text", substrings[1]);
					AllBlackboards.jsonObject.put(Integer.toString(i), smalljsonObject);
				}
			}
			createLogEntry(servletRequestAll.getRemoteAddr(),
					"hat den Inhalt des Blackboards mit dem Namen '" + 
					substrings[0] + "' geaendert");
		}
		// not exist & create: - Output: Log entry: create a new blackboard
		//					   - Activity: save blackboard in static jsonObject	
		else if(!exists && mode.equals("create")) {
			AllBlackboards.jsonObject.put(Integer.toString(size), jsonObject);
			createLogEntry(servletRequestAll.getRemoteAddr(), 
					"hat ein neues Blackboard mit dem Namen '" + 
					substrings[0] + "' erstellt");			
		}
		// not exist & change: - Output: Log entry: blackboard does not exist
		//					   - Output: errortext
		else if(!exists && mode.equals("change")) {
			error = NOT_FOUND;
			createLogEntry(servletRequestAll.getRemoteAddr(),
					"wollte den Inhalt des Blackboards mit dem Namen '" + 
					substrings[0] + 
					"' aendern. FEHLGESCHLAGEN - BLACKBOARD NICHT EXISTENT");		
		}
		Response.Status status;
		if(error == EXISTS_ALREADY) {
			status = Response.Status.CONFLICT; //409
		}
		else if(error == NOT_FOUND) {
			status = Response.Status.NOT_FOUND; //404

		}
		else {
			status = Response.Status.OK; //200
		}
		
		return Response.status(status)
				.entity(error)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}
	 
	/**
	 * Method: deleteBlackboards, POST
	 * Path: /rest/blackboards/delete
	 * Description: This method is used to delete a blackboard. 
	 * Either the error text "Blackboard is not existing!" 
	 * is returned on fail, or the blackboard is deleted.
	 * @param removekey
	 * @return Response -> Header, Content
	 */
	@Context HttpServletRequest servletRequestDelete; 
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteBlackboards(String removekey) {
	 
		JSONObject jsonObject = new JSONObject(); 
		int repl;
		boolean once = false;
		boolean exists = false;
		String error = null;
		
		// In the following loop it is searched whether a blackboard already exists 
		// in the static JSONObject with the requested blackkboard name		
		exists = checkForObjectsExistence(removekey);
		
		/* 	
		 * If the blackboard that shall be deleted exists, 
		 * it is removed from the static JSON object in the following loop.
		 * After that the numbers of the blackboards above are lowered 
		 * again in such a way that a continuous series of numbers is created.
		 * Example: jsonobject: b1,b2,b3,b4,b5
		 * delete the b3
		 * left: b1,b2,b4,b5
		 * set the numbers: b1, b2, b3, b4
		 * this facilitates the client-side query of the blackboards, 
		 * since a "foreach function" for iterating the JSON object is not available
		 */
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
			
			// as soon as the deletion is done, the logging is called 
			createLogEntry(servletRequestDelete.getRemoteAddr(), 
					"hat Blackboard mit dem Namen '" + 
					removekey + "' geloescht");
		}		
		// if the blackboard does not exist the logging and error text will be ejected
		else {
			error = NOT_FOUND;
			createLogEntry(servletRequestDelete.getRemoteAddr(),
					"wollte das Blackboard mit dem Namen '" + 
					removekey + "' loeschen. FEHLGESCHLAGEN - BLACKBOARD NICHT EXISTENT");
		}
		Response.Status status;
		if(error = NOT_FOUND) {
			status = Response.Status.NOT_FOUND; //404
		}
		else {
			status = Response.Status.OK; //200
		}
		return Response.status(status)
				.entity(error)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();	 
	}
	
	/**
	 * Method: getBlackboards, GET
	 * Path: /rest/blackboards/json
	 * Description: This method returns all blackboards stored in the static JSON object.
	 * The return is as method type: text_plain and not 
	 * as application_json for the reasons mentioned above.
	 * @return: Response -> Header, Content
	 */
	@Context HttpServletRequest servletRequestGet; 
	@Path("/json") 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getBlackboards()
	{
		String string = null;
	
		// Case 1: No blackboard available: - Output: Log entry: no blackboard available	
		if(AllBlackboards.jsonObject.size() == 0) {
			createLogEntry(servletRequestGet.getRemoteAddr(),
					"hat alle Blackboards abgefragt! Es wurden " + 
					AllBlackboards.jsonObject.size() + " Blackboards zurueckgegeben");
		}
		// Case 2: Blackboard present: - output: log entry: x blackboards returned
		else {
			createLogEntry(servletRequestGet.getRemoteAddr(), 
					"hat alle Blackboards abgefragt! Es wurden " + 
					AllBlackboards.jsonObject.size() + " Blackboards zurueckgegeben");
		}
		
		// the JSON object is returned as string, also in case of 
		// an empty object -> (easier client-side processing)
		string = AllBlackboards.jsonObject.toJSONString();		
		return Response.ok() //200
				.entity(string)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();	
	}
		
	/**
	 * Method: getLog, GET
	 * Path: /rest/blackboards/log
	 * Description: This method returns all loggings stored in the static logstring. 
	 * Together, they form a table. The logging is displayed from when the server was 
	 * started and the application was last uploaded to the server.
	 * @return Response -> Header, Content
	 */
	@Context HttpServletRequest servletRequestLog; 
	@Path("/log") 
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getLog() throws IOException  
	{
		// each logging throws its own log entry
		createLogEntry(servletRequestLog.getRemoteAddr(), 
				"hat das Logging abgefragt");
		
        String returnString ="<table border='1'>" + logstring + "</table>";
        
        return Response.ok() // 200
        		.entity(returnString)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}
	
	
	/**
	 * Checks if the blackBoard of the given name exists already
	 * @param blackboardName name of the black board which will be searched for
	 * @return boolean indicating if the object exists ->true means it exists
	 * 													 false means it does not exist
	 */
	private boolean checkForObjectsExistence(String blackboardName) {
		int size = AllBlackboards.jsonObject.size();
		JSONObject smalljsonObject = new JSONObject();
		boolean exists = false;
		for(int i=0;i<size;i++) {
			smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
			String string = (String) smalljsonObject.get("name");
			if(string.equals(blackboardName)) {
				exists = true;
			 }
		}
		return exists;
	}
	
	/**
	 * Creates a log entry and appends it to the logString class parameter
	 * @param ipAddress address of the api method caller
	 * @param textMessage message which should be appended to the log entry
	 */
	private void createLogEntry(String ipAddress, String textMessage) {
		if (textMessage == null)
			textMessage = "";
		logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): " + 
				sdfdate.format(cal.getTime()) + " - " + sdf.format(cal.getTime()) + 
				"</td><td>IP-Adresse: "+ ipAddress + 
				"</td><td>" + textMessage + "</td></tr>");
	}
}