"use strict";

var ChatController = (function () {

    function simpleUuid() {
        function s4() { return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1); }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    }

    var userId = simpleUuid(); // would in reality store locally, or in a cookie

    var ChatView = (function () {

        var $chatMessages, $chatBox, $chatButton;

        function paintMessage(message, className) {

            var currentlyScrolledToTheBottom = isViewCurrentlyScrolledToTheBottom();

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

                if (currentlyScrolledToTheBottom) {
                    jumpToPageBottom();
                }

            }
        }

        function isViewCurrentlyScrolledToTheBottom() {
            var currentPanelBottom = Math.round($(window).scrollTop() + $(window).height());
            var currentHeight = Math.round($(document).height());
            return Math.abs(currentHeight - currentPanelBottom) < 10;
        }

        function jumpToPageBottom() {
            $('html, body').scrollTop( $(document).height());
        }

        function clearChatBox() {
            $chatBox.val("");
        }

        function paintMyMessage(message) {
            paintMessage(message, "yours");
        }

        function paintOtherMessage(message) {
            paintMessage(message, "theirs");
        }

        var init = function (chatMessages, chatBox, chatButton) {
            $chatMessages = $("#" + chatMessages);
            $chatBox = $("#" + chatBox);
            $chatButton = $("#" + chatButton);
            attachControllerEventToInput($chatButton);
        };

        function chatBoxValue() {
            return $chatBox.val();
        }

        return {
            "init": init,
            "clearChatBox": clearChatBox,
            "paintMyMessage": paintMyMessage,
            "paintOtherMessage": paintOtherMessage,
            "chatBoxValue": chatBoxValue
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
        var client = new EventSource("http://"+document.location.hostname+":8081/chat/receive/c3743620-cc30-11e6-9d9d-cec0c932ce01"); // TODO: needs to move to a reverse proxy
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
        $.post("http://"+document.location.hostname+":8080/chat/publish/c3743620-cc30-11e6-9d9d-cec0c932ce01/" + userId, payload)// TODO: needs to move to a reverse proxy
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

    var attachControllerEventToInput = function ($chatButton) {
        $chatButton.on("click", function (e) {
            var payload = ChatView.chatBoxValue();
            postPayload(payload);
        });
    };

    var init = function (chatMessages, chatBox, chatButton) {
        ChatView.init(chatMessages, chatBox, chatButton);
        attachControllerServerSideEventToMessageBox();
    };

    return {
        "init": init
    };


})();

