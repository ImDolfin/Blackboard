class apiInteractions {
  constructor(){

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

        let responsearray = xmlHttp.responseText.split(";");
        switch(responsearray[0]){   // responsearray[0] = custom HTTP API statuscode
          case "SUCCESSFUL":
            alert("Activity successfully finished: " + responsearray[1] +" " + responsearray[2]); // responsearray[1] = timestamp, responsearray[2] = time
            break;
          case "NOT_FOUND":
            alert("Blackboard does not exist!: " + responsearray[1] +" " + responsearray[2]);
            break;
          case "EXISTS_ALREADY":
            alert("Blackboard exists already!: " + responsearray[1] +" " + responsearray[2]);
            break;
        }
      }
      console.log("POST 282: " + xmlHttp.responseText);
      b.getAllBlackboardNames();
    }
  } else {
    console.log("Warten auf XmlHttpRequest readyState 4; Aktueller State: " + xmlHttp.readyState);
  }
}
