package RESTApi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.InputStream;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.json.*;

import com.sun.javafx.scene.layout.region.Margins.Converter;

@Path("/blackboards")
public class AllBlackboards {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBlackboards()
	{
		JSONObject blackboards = new JSONObject();
		
		InputStream input = Converter.class.getResourceAsStream("/data/model.json");
		String allBlackboards;
		allBlackboards = input.toString();
		
		
		
		return allBlackboards;//
	}
}
