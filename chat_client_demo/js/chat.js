"use strict";

var ChatController = (function () {

    function simpleUuid() {
        function s4() { return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1); }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    }

    var userId = simpleUuid(); // would in reality store locally, or in a cookie

    var ChatView = (function () {

        var $chatMessages, $chatBox;

        function paintMessage(message, className) {
            var id = message.id;
            if ($("_" + id).length === 0) {
                var $chatBubble = $("<div>");
                var $chatText = $("<div>");
                $chatText.addClass("talktext");
                $chatText.text(message.message);
                $chatBubble.addClass("talk-bubble round");
                $chatBubble.addClass(className);
                $chatBubble.append($chatText);
                $chatMessages.append($chatBubble);
            }
        }

        function clearChatBox() {
            $chatBox.val("");
        }

        function paintMyMessage(message) {
            paintMessage(message, "tri-right right-in right-align");
        }

        function paintOtherMessage(message) {
            paintMessage(message, "tri-left left-in left-align");
        }

        var init = function (chatMessages, chatBox) {
            $chatMessages = $("#" + chatMessages);
            $chatBox = $("#" + chatBox);
            attachControllerEventToInput($chatBox);
        };

        return {
            "init": init,
            "clearChatBox": clearChatBox,
            "paintMyMessage": paintMyMessage,
            "paintOtherMessage": paintOtherMessage
        };
    })();


    function drawMessageInContainer(message) {
        var incomingUserId = message.userId;
        if (incomingUserId == userId) {
            ChatView.paintMyMessage(message);
        } else {
            ChatView.paintOtherMessage(message);
        }
    }

    var attachControllerServerSideEventToMessageBox = function () {
        var client = new EventSource("http://localhost:8081/chat/receive/c3743620-cc30-11e6-9d9d-cec0c932ce01"); // needs to move to a reverse proxy
        client.onopen = function (event) {
            console.log(event);
        };
        client.onmessage = function (event) {
            var message = event.data;
            console.log("Received message: " + message);
            drawMessageInContainer(JSON.parse(message));
        };
        return client;

    };

    function postPayload(payload) {
        $.post("http://localhost:8080/chat/publish/c3743620-cc30-11e6-9d9d-cec0c932ce01/" + userId, payload) // needs to move to a reverse proxy
            .done(function () {
                console.log("success");
                ChatView.clearChatBox();
            })
            .fail(function () {
                console.log("error");
            })
            .always(function () {
                console.log("finished");
            });
    }

    var attachControllerEventToInput = function ($chatBox) {
        var ENTER = 13;
        $chatBox.on("keyup", function (e) {
            if (e.keyCode == ENTER) {
                var payload = $chatBox.val();
                postPayload(payload);
            }
        });
    };

    var init = function (chatMessages, chatBox) {
        ChatView.init(chatMessages, chatBox);
        attachControllerServerSideEventToMessageBox();
    };

    return {
        "init": init
    };


})();

