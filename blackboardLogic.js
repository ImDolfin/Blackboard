/**var array = string.split(",");
alert(array[0]);
 * Class blackboards for saving loaded json object from api
 **/
class Blackboard {

  constructor() {

  }

  /**
   * Calls getJSONFromServer to request newest state of json (callback -> parseJSON -> setJSONData -> set new JSON to class var)
   **/
  updateJSONData() {
    this.getJSONFromServer();
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
   * Sends JSON Data to Server (only necessary content) with @bodyOfData using @httpRequestType via XMLHttpRequest depending on @apiOperation
   * Returns error message from server if action fails
   * After statechange, data is reloaded from server
   * @Param String apiOperation
   * @Param String httpRequestType
   * @Param String bodyOfData
   **/
  doXmlHttpRequest(apiOperation, httpRequestType, bodyOfData) {
    let url = "";
    let logProperty = "";

    let xmlHttp = new XMLHttpRequest(); //returns a XMLHttpRequest object

    // setup Request depending on wanted Api operation
    switch (apiOperation) {
      case "send":
        url = 'http://localhost:8080/VerteilteSySApiREST/rest/blackboards/json';
        //url = 'http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/json';
        logProperty = "HTTP:POST; Operation:sendJSON";
        break;
      case "delete":
        url = 'http://localhost:8080/VerteilteSySApiREST/rest/blackboards/delete';
        //url = 'http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/delete';
        logProperty = "HTTP:POST; Operation:deleteJSON";
        break;
      case "get":
        url = 'http://localhost:8080/VerteilteSySApiREST/rest/blackboards/json';
        //url = "http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/json";
        xmlHttp.overrideMimeType("application/json"); //brauchen wir das?
        logProperty = "HTTP:GET; Operation:getJSON";
        break;
    }

    // Initialize xml request propertys
    xmlHttp.open(httpRequestType, url, true);
    console.log(logProperty);
    xmlHttp.setRequestHeader("Content-Type", "text/plain");

    // send request
    xmlHttp.send(bodyOfData);

    // register onreadystate eventhandler by creating a function that calls the function containing the logic
    // (because of asynchronous callstructure - no direct call possible)
    xmlHttp.onreadystatechange = function() {
      xmlHttpOnReadyStateChange(xmlHttp, httpRequestType);
    }
  }

  /**
   *
   **/
  getJSONFromServer() {
    /* bodyOfData has to be null because no body is send for this GET operation in the XHR request.
    Not adding null would throw an exception on older browsers */
    let bodyOfData = null;

    this.doXmlHttpRequest("get", "GET", bodyOfData);
  }

  /**
   * Sends JSON Data to Server (only necessary content) via XMLHttpRequest
   * @Param String content
   **/
  sendJSONToServer(content) {
    this.doXmlHttpRequest("send", "POST", content);
  }

  /**
   * Deletes JSON Data from Server (only necessary key) via XMLHttpRequest
   * @Param String key
   **/
  deleteJSONInServer(key) {
    this.doXmlHttpRequest("delete", "POST", key);
  }

  /**
   * Returns lenght of class JSON (blackboards)
   * @Return int
   */
  getLenght() {
    //this.updateJSONData();
    if (this.boards === null) {
      return 0;
    } else {
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
    let content = name + "," + text + ",create";
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
    let content = name + "," + text + ",change";
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
        //check for empty string redudant, bc sometimes browser wont get the lenght 0 even if the lenght is 0
        ((this.boards[this.position].text).lenght == 0 || this.boards[this.position].text === "") ? textForBlackboard.innerHTML = "Leere Notiz": textForBlackboard.innerHTML = "Notiz mit Inhalt";
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
    let content = name + "," + ",change";
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

    if (lenght > 0) {
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
 * Eventhandler for the readystatechange event
 * Had to be solved as a global function because of asynchronous callbacks of xmlHttpOnReadyStateChange()
 * Differentiate between HTTP Method (@httpMethod)
 * @Param XMLHttpRequest xmlHttp
 * @Param String httpMethod
 **/
function xmlHttpOnReadyStateChange(xmlHttp, httpMethod) {
if (xmlHttp.readyState === 4) {
  if (httpMethod === "GET") {
    // TODO: handle this version of the setTextFile()
    if (xmlHttp.status == "200") {
      clearResultDiv();
    }
    // parseJSON(xmlHttp.responseText);
    parseJSON(xmlHttp.responseText);
    console.log("GET: " + xmlHttp.responseText);


  } else if (httpMethod === "POST") {
    console.log(xmlHttp.status);
    if (xmlHttp.status == "200") {
      /*switch(xmlHttp.responseText){
        case "SUCCESSFUL":
          alert("Activity successfully finished"); // only commented, because it is only useful for debugging
          break;
        case "NOT_FOUND":
          alert("Blackboard does not exist!");
          break;
        case "EXISTS_ALREADY":
          alert("Blackboard exists already!");
          break;
      }*/
      if (xmlHttp.responseText != "") {
        alert(xmlHttp.responseText);
      }
      // } else if (xmlHttp.status == "404" || xmlHttp.status == "400") {
      //   alert(xmlHttp.responseText);

    }
    console.log("POST 282: " + xmlHttp.responseText);
    b.getAllBlackboardNames();
  }
} else {
  console.log("Warten auf XmlHttpRequest readyState 4; Aktueller State: " + xmlHttp.readyState);
}
}

/**
 * TODO: JSON.parse throws diffrent errors, however without adapting the code worked like this without errors. If the retrned @text gets loged and hardcoded in, no errors are thrown. Maybe API bug?
 * Parses JSON and Calls setter of Blackboard
 * @Param text JSON
 **/
function parseJSON(text) {
  //  let t = "" + text;  // b.setJSONData(JSON.parse('{"0":{"name":"jhv","text":""}}'));
  //  let tex = '{"0":{"name":"jhv","text":""}}';
  console.log("269: " + text);
  b.setJSONData(JSON.parse(text));
  console.log("269: " + text);
  text
}
