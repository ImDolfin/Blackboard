 /**
   * Class blackboards for saving loaded json object from api
   **/
class Blackboard {

  constructor() {
    this.urlToSend = 'http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/json';
    this.urlToDelete = 'http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/delete';
  }


  /**
   * Calls readTextFile to request newest state of json (callback -> parseJSON -> setJSONData -> set new JSON to class var)
   **/
  updateJSONData() {
    readTextFile(file, parseJSON);
    console.log("14: updatecomplete");
  }

  /**
   * (Re)Sets JSON
   * After the process of loading a new json from server the function for showing the blackboard names is called
   * @Param jsonData JSON
   */
  setJSONData(jsonData) {
    this.boards = jsonData;
  	console.log("boards is here");
  	this.showAllBlackboardNames();
  	console.log("reload finished");
  }

  /**
   * Sends JSON Data to Server (only necessary content) with @bodyOfData via XMLHttpRequest depending on @type
   * Returns error message from server if action fails
   * After statechange, data is reloaded from server
   * @Param int type
   * @Param String bodyOfData
   **/
  doXmlHttpRequest(type, bodyOfData) {
    let url = "";
    let logProperty = "";

    let xmlHttp = new XMLHttpRequest(); //returns a XMLHttpRequest object

    switch (type) {
      // send
      case 0:
        url = this.urlToSend;
        logProperty = "post";
        break;

      // delete
      case 1:
        url = this.urlToDelete;
        logProperty = "delete";
        break;
    }

    // Initialize xml request propertys
    xmlHttp.open("POST", url, true);
    console.log(logProperty);
    xmlHttp.setRequestHeader("Content-Type", "text/plain");

    // send request
    xmlHttp.send(bodyOfData);

    // register onreadystate eventhandler
    xmlHttp.onreadystatechange = this.xmlHttpOnReadyStateChange(xmlHttp);
  }

  /**
  * Eventhandler for the readystatechange event
  **/
  xmlHttpOnReadyStateChange(xmlHttp){
    if (xmlHttp.readyState === 4 && xmlHttp.status == "200") {
      if(xmlHttp.responseText != ""){
        alert(xmlHttp.responseText);
      }
      this.getAllBlackboardNames();
    }
  }

  /**
   * Sends JSON Data to Server (only necessary content) via XMLHttpRequest
   * @Param String content
   **/
  sendJSONToServer(content) {
    this.doXmlHttpRequest(0, content);
  }

  /**
   * Deletes JSON Data from Server (only necessary key) via XMLHttpRequest
   * @Param String key
   **/
  deleteJSONInServer(key) {
    this.doXmlHttpRequest(1, key);
  }

  /**
   * Returns lenght of class JSON (blackboards)
   * @Return int
   */
  getLenght() {
    //this.updateJSONData();
  	if(this.boards === null){
  		return 0;
  	}
  	else{
  	  return Object.keys(this.boards).length;
  	}
  }

  /**
   * Checks for property equality of either given(@stringToCheck) name or text (@param typ)
   * Returns true if exists, false if not
   * Sets class var position to the position/property in JSON, where the match was true
   * @Param type String
   * @Param stringToCheck String
   * @Return bool
   **/
  checkExistance(type, stringToCheck) {
    let y = false;
    for (let x = 0; x < this.getLenght(); x++) {
      if (this.boards[x][type] === stringToCheck) {
        y = true;
        this.position = x;
      }
    }
    return y;
  }

  /**
   * Creates JSON entry for new Blackboard
   * Check if the name is existent in JSON Object is handled in the server -> gives error message back in function "sendJSONToServer"
   * @Param name String
   * @Param text String
   **/
  createBlackBoard(name, text) {
	  let content = name +  "," + text + ",create";
	  this.sendJSONToServer(content);
  }

  /**
   * Deletes specific Blackboard
   * Check if the name is existent in JSON Object is handled in the server -> gives error message back in function "sendJSONToServer"
   * @Param name String
   **/
  deleteBlackBoard(name) {
	  this.deleteJSONInServer(name);
    console.log("117:" + name);
    console.log("119: gelöscht");
  }

  /**
   * Updates Blackboard content
   * Check if the name is existent in JSON Object is handled in the server -> gives error message back in function "sendJSONToServer"
   * @Param name String
   * @Param text String
   **/
  updateBlackboardContent(name, text) {
	  let content = name  + "," + text + ",change";
    this.sendJSONToServer(content);
  }

  /**
   * Return Div that contains content of a given Blackboard(name)
   * -> Depending on checkIfEmpty the Div contains either the content itself, or just the result of the check if there is a content or not
   * Returns false, if no Blackboard for specified name
   * @Param name String
   * @Param checkIfEmpty bool
   * @Return bool
   * @Return Div
   **/
  getBlackboardContent(name, checkIfEmpty) {
    if (this.checkExistance("name", name) === true) {
      let blackboardsDiv = document.createElement("div");
      blackboardsDiv.id = 'allBLackboards';
      let textForBlackboard = document.createElement("p");

      // only get if empty or not
      if (checkIfEmpty === true) {
        ((this.boards[this.position].text).lenght === 0) ? textForBlackboard.innerHTML = "Leere Notiz": textForBlackboard.innerHTML = "Notiz mit Inhalt";
      }
      // Get content of boards
      else {
        textForBlackboard.innerHTML = this.boards[this.position].text;
        console.log(this.boards[this.position].text);
      }

      blackboardsDiv.appendChild(textForBlackboard);
      return blackboardsDiv;
    }
    // not element exists -> return false
    else {
      return false;
    }
  }

  /**
   * Deletes Blackboard content
   * Check if the name is existent in JSON Object is handled in the server -> gives error message back in function "sendJSONToServer"
   * @Param name String
   **/
  clearBlackboardContent(name) {
	  let content = name + ","  + ",change";
    this.sendJSONToServer(content);
  }

  /**
   * Calls updateJSONData function -> this calls json object with all blackboards from the server over the API
   * Afterwards function showAllBlackboardNames is called, when data is received from API
   **/
  getAllBlackboardNames() {
    this.updateJSONData();
  }

   /**
   * Afterwards function showAllBlackboardNames is called, when data is received from API
   * The function creates an HTML div and creates for each blackbaord an HTML Element with a css style
   * Than the existing divs will be deleted and the new one appended
   **/
  showAllBlackboardNames() {
    let blackboardsDiv = document.createElement("div");
    blackboardsDiv.id = 'allBLackboards';
    let lenght = this.getLenght();

    if (lenght >0) {
      for (let i = 0; i < lenght; i++) {
        let nameOfBlackboard = document.createElement("h3");
        nameOfBlackboard.innerHTML = this.boards[i].name;
        nameOfBlackboard.classList.add("kacheln");
        blackboardsDiv.appendChild(nameOfBlackboard);
      }
      console.log(this.boards);
      clearResultDiv();
      document.body.appendChild(blackboardsDiv);
    }
  }
}

/**
 * Makes HTTP Request for @file
 * Calls @callback if request sucessfull (http 200)
 * @Param file String
 * @Param callback Operation
 **/
function readTextFile(file, callback) {
  let rawFile = new XMLHttpRequest();
  rawFile.overrideMimeType("application/json");
  rawFile.open("GET", file, true);
  console.log("get");
  rawFile.onreadystatechange = function() {
    if (rawFile.readyState === 4 && rawFile.status == "200") {
	  clearResultDiv();
	  if(rawFile.responseText == ""){
		alert("Keine Blackboards vorhanden!");
	  }
	callback(rawFile.responseText);
    }
  }
  rawFile.send(null);

}


/**
 * Parses JSON and Calls setter of Blackboard
 * @Param text JSON
 **/
function parseJSON(text) {
  b.setJSONData(JSON.parse(text));
  console.log("269: " + text);
}


/*
====================================================================================================================================
*/
const blackboardName = document.getElementById('newBlackboardName');
const blackboardText = document.getElementById('newBlackboardText');
const createBlackboardButton = document.getElementById("createNewBlackboard");
const deleteBlackboardButton = document.getElementById("deleteBlackboard");
const updateContentButton = document.getElementById("updateBlackboardContent");
const showContentButton = document.getElementById('showBlackboardContent');
const clearContentButton = document.getElementById('clearBlackboardContent');
const checkEmptyButton = document.getElementById('checkEmptyBlackboardContent'); //alles abchecken
const showBlackboardsButton = document.getElementById('showAllBlackboards'); // warum 2x aufrufen bis geht?

const file = "http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/json";
//const file = "blackboards.json";
var selectedBlackboard = null;

var b = new Blackboard();
readTextFile(file, parseJSON);

/**
 * Remove allBlackboards div form DOM, if it exist
 **/
function clearResultDiv() {
  if (document.getElementById('allBLackboards')) {
    document.getElementById('allBLackboards').remove();
  }
}



//---------------------------------------
// In this section, eventListeners are added to the specific Buttons


//checks whether inputs are valid -> if not, an alert is thrown
createBlackboardButton.addEventListener("click", function() {
  let name = blackboardName.value;
  let text = blackboardText.value;
  if (name != "") {
    b.createBlackBoard(name, text);
  } else {
    alert("Sie müssen einen Namen vor dem Erstellen eines Blackboards eingeben!");
  }
})

//checks whether inputs are valid -> if not, an alert is thrown
deleteBlackboardButton.addEventListener("click", function() {
  let name = blackboardName.value;
  if (name != "") {
    b.deleteBlackBoard(name);
  } else {
    alert("Sie müssen einen Namen vor dem Erstellen eines Blackboards eingeben!");
  }
})

//checks whether inputs are valid -> if not, an alert is thrown
updateContentButton.addEventListener("click", function() {
  let name = blackboardName.value;
  let text = blackboardText.value;
  if (name != "" && text != "") {
    b.updateBlackboardContent(name, text);
  } else {
    alert("Sie müssen den Namen des Blackboards sowie eine Nachricht angeben!");
  }
})

//checks whether inputs are valid -> if not, an alert is thrown
clearContentButton.addEventListener("click", function() {
  let name = blackboardName.value;
  if (name != "") {
    b.clearBlackboardContent(name);
  } else {
    alert("Sie müssen den Namen des Blackboards angeben!");
  }
})

//checks whether inputs are valid -> if not, an alert is thrown
// if result of getBlackboardContent with param "false", which means - get the content - is not false, than the content of a specific blackboard is shown in the browser
showContentButton.addEventListener("click", function() {
  let name = blackboardName.value;
  if (name != "") {
    let res = b.getBlackboardContent(name, false);
    if (res === false) {
      alert("Kein Blackboard mit diesem Namen vorhanden!");
    } else {
      clearResultDiv();
      document.body.appendChild(res);
    }
  } else {
    alert("Sie müssen den Namen des Blackboards eingeben!");
  }
})

//checks whether inputs are valid -> if not, an alert is thrown
// if result of getBlackboardContent with param "true", which means - check whether there is content - is not false, than the content of a specific blackboard is shown in the browser
checkEmptyButton.addEventListener("click", function() {
  let name = blackboardName.value;
  if (name != "") {
    let res = b.getBlackboardContent(name, true);
    if (res === false) {
      alert("Kein Blackboard mit diesem Namen vorhanden!");
    } else {
      clearResultDiv();
      document.body.appendChild(res);
    }
  } else {
    alert("Sie müssen den Namen des Blackboards eingeben!");
  }
})

//calls function getAllBlackboardNames
showBlackboardsButton.addEventListener("click", function() {
   b.getAllBlackboardNames();
})
