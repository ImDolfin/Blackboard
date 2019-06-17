package RESTApi;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.json.simple.JSONObject;


class AllBlackboardsTester {

	private JSONObject jsonObject = new JSONObject();
	
	private void buildJsonString(String blackBoardName, String blackBoardText ) {
		jsonObject.put("name", blackBoardName); 								 
		jsonObject.put("text", blackBoardText); 	
	}
	
	@Test
	void createKnownBlackboardShouldReturnError() {
		fail("Not yet implemented");
	}
	
	@Test
	void createNewBlackBoardShouldSucceed() {
		fail("Not yet implemented");
	}
	
	@Test
	void modifyUnknownBlackboardShouldReturnError() {
		fail("Not yet implemented");
	}
	
	@Test
	void modifyKnownBlackboardShouldSucceed() {
		fail("Not yet implemented");
	}
	
	@Test
	void useUmlautsForBlackBoardModificationShouldSucceed() {
		fail("Not yet implemented");
	}
	
	@Test
	void deleteUnknownBlackboardShouldReturnError() {
		fail("Not yet implemented");
	}
	
	@Test
	void deleteKnownBlackboardShouldSucceed() {
		fail("Not yet implemented");
	}
	
	@Test
	void getAllBlackboardsShouldReturnJsonWithBlackboards() {
		fail("Not yet implemented");
	}

}
