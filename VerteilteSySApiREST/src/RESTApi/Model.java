package RESTApi;

import org.json.simple.JSONObject;

public class Model {
	private static JSONObject jsonObject = new JSONObject();

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		Model.jsonObject = jsonObject;
	}
	
	
}
