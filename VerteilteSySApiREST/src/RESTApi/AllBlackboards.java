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
	private static JSONObject jsonObject = new JSONObject();			//Erstellen eines statischen JSON Objekts als Datenhaltung
																		// Das jedes Blackboard im JSONObject hat eine Struktur wie folgt:
																		// {nummer:jsonObjekt} -> Das jsonObjekt hat wiederum jeweis die struktur:
																		// {"name": blackboardname, "text": blackboardcontent}
																		//Ursprünglich JSON File, jedoch ist der Pfad auf Amazon aws nicht ersichtlich
	private static String logstring = "";								//Datenhaltung für das LOGGING
	 Calendar cal = Calendar.getInstance();								//Objekt zur Anzeige des Aktuellen Datums, mit Datumsformat
	 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
     
	 
	 /*
	  * Methode:  saveBlackboards, POST
	  * Pfad:     /rest/blackboards/json
	  * Eingabe:  TEXT
	  * Rückgabe: Response -> Header, Content
	  * Beschreibung: Anhand dieser Methode werden neue Blackboards erstellt sowie der vorhandene Content eines Blackboards verändert
	  * 
	  */
	
	 @Context HttpServletRequest servletRequestall; 					// HttpServletRequest wird benötigt, um die Remote IP Adresse ausgeben zu können
	 @Path("/json")
	 @POST
	 @Consumes(MediaType.TEXT_PLAIN)									//Textplain wird anstatt APPLICATION_JSON verwendet, da ansonsten ein 
	 @Produces(MediaType.TEXT_PLAIN)									//PREFLIGHT-Request ausgelöst wird
	 
	 public Response saveBlackboards(String text) { 					//String beinhaltet "name", "text" eines blackboards, sowie die Angabe
		 																//"create" oder "change", um das blackboard zu erstellen oder zu verändern
		 
		 String[] substrings = text.split(",");							// Aufteilen des Strings in Substrings
		 boolean exists = false;										
		 String error = null;
		 String mode = null;
		 int size = AllBlackboards.jsonObject.size();
		 JSONObject smalljsonObject = new JSONObject();					
		 
		 
		
		JSONObject jsonObject = new JSONObject();						//hier wird ein json objekt erstellt, was ein blackboard representiert
		jsonObject.put("name", substrings[0]);							//damit besteht das blackboard, ist jedoch noch nicht in das static 
		jsonObject.put("text", substrings[1]);							//jsonObject hinzugefügt
		mode = substrings[2];											//hier wird der Modus (create oder change) festgelegt
																		// der Grund, warum das nicht in jeweils einer API Abfrage gefügt wird, ist dass
																		// nur GET und POST funktioniert, ohne, dass ein CORS-Policy Error von Amazon aws 
																		//geworfen wird. Dieser ist für amazon APIs leicht zu beheben, da aber eine .war -
																		//Datei auf den Server geschoben wird, welche selbst die API beinhaltet, haben wir 
																		// es nciht geschafft diesen Fehler zu beheben
		
		// In der folgenden Schleife wird gesucht, ob im statischen JSONObject bereits ein blackboard mit dem angefragen blackkboardnamen existiert
		
		for(int i=0;i<size;i++) {
			smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
			String string = (String) smalljsonObject.get("name");
			if(string.equals(substrings[0])) {
				exists = true;
			 }
		}
		
		//hier werden für die 4 möglichen Fälle (blackboardexistiert bereits und modus create,
											//   blackboardexistiert bereits und modus change,
											//   blackboardexistiert nicht und modus create,
											//   blackboardexistiert nicht und modus change,
		// behandelt und entsprechend ausgegeben	
		
		
		//exist & create: - Ausgabe: Logeintrag: bereits existentes blackboard
		// 				  - Ausgabe: errortext
		if(exists && mode.equals("create"))
		{
			error = "Blackboard already exists :) !";
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>wollte ein neues Blackboards mit dem Namen '" + substrings[0] + "' erstellen. FEHLGESCHLAGEN: BLACKBOARD BEREITS EXISTENT</td></tr>");
			
		}
		
		//exist & change: - Ausgabe: Logeintrag: änderung des blackboardcontens
		// 				  - Aktivität: speichern des blackboards in static jsonObjekt
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
			
		//not exist & create: - Ausgabe: Logeintrag: erstellen eines neuen blackboards
		// 				 	  - Aktivität: speichern des blackboards in static jsonObjekt	
		}
		else if(exists==false && mode.equals("create")) {
			AllBlackboards.jsonObject.put(Integer.toString(size), jsonObject);
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>hat ein neues Blackboard mit dem Namen '" + substrings[0] + "' erstellt</td></tr>");
			
		}
		
		//not exist & change: - Ausgabe: Logeintrag: blackboard nicht existent
		// 				 	  - Ausgabe: errortext
				
		else if(exists==false && mode.equals("change")) {
			error = "Blackboard doesn't exist :) !";
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestall.getRemoteAddr() + "</td><td>wollte den Inhalt des Blackboards mit dem Namen '" + substrings[0] + "' aendern. FEHLGESCHLAGEN - BLACKBOARD NICHT EXISTENT</td></tr>");
			
		}
		
		return Response.ok() //200
				.entity(error)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}

	 
	 /*
	  * Methode:  deleteBlackboards, POST
	  * Pfad:     /rest/blackboards/delete
	  * Eingabe:  TEXT
	  * Rückgabe: Response -> Header, Content
	  * Beschreibung: Anhand dieser Methode wird ein Blackboard gelöscht. Dabei wird entweder ein errortext zurückgegeben, falls das zu
	  * löschende blackbaord nicht existiert, oder das Blackboard wird gelöscht
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
		
		
		// In der folgenden Schleife wird gesucht, ob im statischen JSONObject bereits ein blackboard mit dem angefragen blackkboardnamen existiert
		
		for(int i=0;i<size;i++) {
			smalljsonObject = (JSONObject) AllBlackboards.jsonObject.get(Integer.toString(i));
			String string = (String) smalljsonObject.get("name");
			if(string.equals(removekey)) {
				exists = true;
			 }
		}
		
		
		// In der folgenden Schleife wird, falls das zu löschende Blackboard existiert, dieses aus dem statischen JSONObjekt entfernt
		//Danach werden die Nummern der darüberliegenden Blackboards wieder so herabgesetzt, dass eine zahlenreihe entsteht
		//Beispiel: jsonobjekt: b1,b2,b3,b4,b5
		//			löschen des b3
		//			übrig: b1,b2,b4,b5
		//herabsetzten der zahlen: b1, b2, b3, b4
		// dies erleichert die Clientseitige Abfrage der blackboards, da eine "foreach-funktion" zur Iteration des JSONObjekts nicht zur verfügung steht

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
			
			//sobald das löschen erfolgt ist, wird das logging aufgerufen
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestdel.getRemoteAddr() + "</td><td>hat Blackboard mit dem Namen '" + removekey + "' geloescht</td></tr>");
		}			
		else {//falls das blackboard nciht  existiert wird das logging und ein errortext ausgeworfen
			error = "Blackboard is not existing!";
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestdel.getRemoteAddr() + "</td><td>wollte das Blackboard mit dem Namen '" + removekey + "' loeschen. FEHLGESCHLAGEN - BLACKBOARD NICHT EXISTENT</td></tr>");
		}
		
		return Response.ok() //200
				.entity(error)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		
		 
	}
	
	
	 
	 /*
	  * Methode:  getBlackboards, GET
	  * Pfad:     /rest/blackboards/json
	  * Eingabe:  -
	  * Rückgabe: Response -> Header, Content
	  * Beschreibung: Anhand dieser Methode werden alle im statischen JSONObjekt gespeicherten Blackboards zurückgegeben
	  * Die Rückgabe erfolgt als text_plain und nicht als application_json aus den oben genannten Gründen
	  */
	 
	@Context HttpServletRequest servletRequestget; 
	@Path("/json") 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getBlackboards()
	{
		String string = null;
		
		
		//Fall1: Kein Blackboard vorhanden: - Ausgabe: Logeintrag: kein backboard vorhanden

		if(AllBlackboards.jsonObject.size()==0) {
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestget.getRemoteAddr() + "</td><td>hat alle Blackboards abgefragt! Es wurden " + AllBlackboards.jsonObject.size() + " Blackboards zurueckgegeben</td></tr>");
		}
		//Fall2: Blackboard vorhanden: - Ausgabe: Logeintrag: x backboards zurückgegeben
		else {
			logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestget.getRemoteAddr() + "</td><td>hat alle Blackboards abgefragt! Es wurden " + AllBlackboards.jsonObject.size() + " Blackboards zurueckgegeben</td></tr>");
		}
		
		// es wird das JSONObjekt als String zurückgegeben, auch im Falle eines leeren Objektes -> (leichtere Client-Seitige Verarbeitung)
		string = AllBlackboards.jsonObject.toJSONString();
		
		return Response.ok() //200
				.entity(string)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
		
	}
	
	
	
	 /*
	  * Methode:  getLog, GET
	  * Pfad:     /rest/blackboards/log
	  * Eingabe:  -
	  * Rückgabe: Response -> Header, Content
	  * Beschreibung: Anhand dieser Methode werden alle im statischen logstring gespeicherten Loggings zurückgegeben. Diese bilden zusammen eine Tabelle
	  * Das logging wird angezeigt seit dem der Server gestartet und die Anwendung das letzte mal auf den server geladen wurde
	  */
	
	@Context HttpServletRequest servletRequestlog; 
	@Path("/log") 
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getLog() throws IOException  
	{
		//jedes Logging wirft einen eigenen Log Eintrag
		logstring = logstring.concat("<tr><td>TIMESTAMP (UTC): "+ sdf.format(cal.getTime())+ "</td><td>IP-Adresse: "+ servletRequestget.getRemoteAddr() + "</td><td>hat das Logging abgefragt</td></tr>");
        String returnstring ="<table border='1'>" + logstring + "</table>";
        
        return Response.ok() //200
        		.entity(returnstring)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST")
				.build();
	}
	
	
}