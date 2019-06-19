/*
===================================================================
Operations for DOM
===================================================================
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

var selectedBlackboard = null;

var b = new Blackboard();
b.getAllBlackboardNames();

/**
 * Remove allBlackboards div form DOM, if it exist
 **/
function clearResultDiv() {
  if (document.getElementById('allBLackboards')) {
    document.getElementById('allBLackboards').remove();
  }
}

// TODO: test this implementation
/**
 * Wrapper to handle return of overloaded getBlackboardContent()
 * Checks whether inputs are valid -> if not, alerts are thrown
 * (if everthing is valid) depending on @checkIfEmpty a div either containing the content or the state (empty/note empty) for given blackboard(@name) is returned
 * @Param String name
 * @Param bool checkIfEmpty
 **/
function showContentWrapper(name, checkIfEmpty) {
  if (name != "") {
    let res = b.getBlackboardContent(name, checkIfEmpty);
    if (res === false) {
      alert("Kein Blackboard mit diesem Namen vorhanden!");
    } else {
      clearResultDiv();
      document.body.appendChild(res);
    }
  } else {
    alert("Sie müssen den Namen des Blackboards eingeben!");
  }
}

/*-----------------------------------------------------------------
In this section eventListeners are added to the specific buttons
They call the respective opertions of the Blackboard class and handle the Returns
These return value(s) and the userinputs of the InputTexts are handled and alerts are thrown in the DOM they are not valid
*/

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

//Calls the showContentWrapper()
showContentButton.addEventListener("click", function() {
  showContentWrapper(blackboardName.value, false);
})

//Calls the showContentWrapper()
checkEmptyButton.addEventListener("click", function() {
  showContentWrapper(blackboardName.value, true);
})

//calls function getAllBlackboardNames
showBlackboardsButton.addEventListener("click", function() {
  b.getAllBlackboardNames();
})
