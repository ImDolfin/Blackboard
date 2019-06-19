describe("Blackboard", function() {

    let blackboard;

    beforeEach(function() {
        blackboard = new Blackboard();
    });

    it("2.1 check update of json", function(){
        spyOn(blackboard.apiInteractions, 'getJSONFromServer').and.callFake(function(){});
        blackboard.updateJSONData();
        expect(blackboard.apiInteractions.getJSONFromServer).toHaveBeenCalled();
    });

    it("2.2 check setJSONData", function(){
        spyOn(blackboard, 'showAllBlackboardNames').and.callFake(function(){});
        boardsJSON = {
            0: {
                "name":"board1",
                "text":"text1"
            }
        };
        blackboard.setJSONData(boardsJSON);
        expect(blackboard.boards).toEqual(boardsJSON);
    });

    describe("get lenght of blackboards", function(){        
        it("2.6 should return 0 when blackboards.boards null ", function() {
            blackboard.boards = null;
            expect(blackboard.getLenght()).toEqual(0);
        });
        it("2.7 should return lenght of blackboards.boards", function() {
            blackboard.boards = {
                0: {
                    "name":"board1",
                    "text":"text1"
                }
            }
            expect(blackboard.getLenght()).toEqual(1);
        });
    });

    describe ("check checkExistance of blackboard",function(){
        beforeEach(function(){
            blackboard.boards = {
                0: {
                    "name":"board1",
                    "text":"text1"
                }
            }
        });
        it("2.8 check Existance of existing board", function(){
            expect (blackboard.checkExistance("name", "board1")).toBe(true);
        });
        it("2.9 check Existance of non-existing board", function(){
            expect (blackboard.checkExistance("name", "board2")).toBe(false);
        });
    });

    //Expected spy sendJSONToServer to have been called with [ 'name,text,create' ] but it was never called.
    it("2.10 check creation of blackboards", function(){
        // mock/stub api ajax call and set dummyobject to class variables
        spyOn(blackboard.apiInteractions, 'sendJSONToServer').and.callFake(function(){});
        blackboard.createBlackBoard("name", "text");
        expect(blackboard.apiInteractions.sendJSONToServer).toHaveBeenCalledWith("name,text,create"); // checkt obs aufgerufen wurde 
    });

    it("2.11 check deletion of blackboard", function(){
        spyOn(blackboard.apiInteractions, 'deleteJSONInServer').and.callFake(function(){});
        blackboard.deleteBlackBoard("name");
        expect(blackboard.apiInteractions.deleteJSONInServer).toHaveBeenCalledWith("name");
    });

    it("2.12 check update of blackboard", function(){
        spyOn(blackboard.apiInteractions, 'sendJSONToServer').and.callFake(function(){});
        blackboard.updateBlackboardContent("name", "text");
        expect(blackboard.apiInteractions.sendJSONToServer).toHaveBeenCalledWith("name,text,change");
    });

    it("2.13 check clear of blackboard", function(){
        spyOn(blackboard.apiInteractions, 'sendJSONToServer').and.callFake(function(){});
        blackboard.clearBlackboardContent("name");
        expect(blackboard.apiInteractions.sendJSONToServer).toHaveBeenCalledWith("name,,change");
    });

    describe ("check get content of blackboard",function(){
        it("2.14 if no board exists it should return false", function(){
            spyOn(blackboard, 'checkExistance').and.callFake(function(){
                return false;
            });
            expect(blackboard.getBlackboardContent("name", true)).toBe(false);
            expect(blackboard.checkExistance).toHaveBeenCalledWith("name", "name");
        });

        it("2.15 if board exists and is filled", function(){
            blackboard.boards = {
                0: {
                    "name":"board1",
                    "text":"text1"
                }
            }

            // create the return value like it should do in the getBlackboardContent() method
            let textForBlackboard = document.createElement("p");
            textForBlackboard.innerHTML = "text1";
            let content = document.createElement("div");
            content.id = 'allBLackboards';
            content.appendChild(textForBlackboard);

            expect(blackboard.getBlackboardContent("board1", false)).toEqual(content);
        });
    });


    //Expected spy open to have been called.

//  Error: <toHaveBeenCalled> : Expected a spy, but got [object XMLHttpRequest].
//  Usage: expect(<spyObj>).toHaveBeenCalled()
    // describe("check doXmlHttpRequest", function(){
    //     it("check get", function (){
    //         xmlHttp = new XMLHttpRequest();
    //         spyOn(xmlHttp,'open').and.callFake(function(){});
    //         blackboard.doXmlHttpRequest("get", "GET", null)
    //         expect(xmlHttp.open).toHaveBeenCalled();
    //     }) 
    // });

    
});

describe("ApiInteractions", function() {
    let api

    beforeEach(function(){
        api = new apiInteractions();
    });

    it("2.3 check get json from server", function(){
        spyOn(api, 'doXmlHttpRequest').and.callFake(function(){});
        api.getJSONFromServer();
        expect(api.doXmlHttpRequest).toHaveBeenCalledWith("get","GET",null);
    });

    it("2.4 check send json to server", function(){
        spyOn(api, 'doXmlHttpRequest').and.callFake(function(){});
        let content = "board1,text1,change";
        api.sendJSONToServer(content);
        expect(api.doXmlHttpRequest).toHaveBeenCalledWith("send","POST",content);
    });

    it("2.5 check delete json from server", function(){
        spyOn(api, 'doXmlHttpRequest').and.callFake(function(){});
        let key = "board1"
        api.deleteJSONInServer(key);
        expect(api.doXmlHttpRequest).toHaveBeenCalledWith("delete","POST",key);
    });
});
