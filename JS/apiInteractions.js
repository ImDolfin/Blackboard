class apiInteractions() {
  constructor() {
    this.xmlHttp = new XMLHttpRequest(); //returns a XMLHttpRequest object
    this.url = "";
    this.logProperty = "";
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

    // setup Request depending on wanted Api operation
    switch (apiOperation) {
      case "send":
        this.url = 'http://localhost:8080/VerteilteSySApiREST/rest/blackboards/json';
        //this.url = 'http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/json';
        this.logProperty = "HTTP:POST; Operation:sendJSON";
        break;
      case "delete":
        this.url = 'http://localhost:8080/VerteilteSySApiREST/rest/blackboards/delete';
        //this.url = 'http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/delete';
        this.logProperty = "HTTP:POST; Operation:deleteJSON";
        break;
      case "get":
        this.url = 'http://localhost:8080/VerteilteSySApiREST/rest/blackboards/json';
        //this.url = "http://blackboardproject.us-east-2.elasticbeanstalk.com/rest/blackboards/json";
        this.xmlHttp.overrideMimeType("application/json"); //brauchen wir das?
        this.logProperty = "HTTP:GET; Operation:getJSON";
        break;
    }

    // Initialize xml request propertys
    this.xmlHttp.open(httpRequestType, this.url, true);
    console.log(this.logProperty);
    this.xmlHttp.setRequestHeader("Content-Type", "text/plain");

    // send request
    this.xmlHttp.send(bodyOfData);

    // register onreadystate eventhandler by creating a function that calls the function containing the logic
    // (because of asynchronous callstructure - no direct call possible)
    this.xmlHttp.onreadystatechange = function() {
      xmlHttpOnReadyStateChange(this.xmlHttp, httpRequestType);
    }
  }

  /**
   *
   **/
  getJSONFromServer() {
    /* bodyOfData has to be null because no body is send for this GET operation in the XHR request.
    Not adding null would throw an exception on older browsers */
    this.doXmlHttpRequest("get", "GET", null);
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
    }
    else if (httpMethod === "POST") {
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
