
What Is In This Repo?
=====================

Chat Services
=============
There are 2 chat services built so far, which implement a very basic chat function. 
One service currently accepts a message, and the other broadcasts it to whatever client is connected.


Chat Service
------------
Very simply, it takes a message from the user via a restful interface, and pushes it onto a message queue.
The queue uses MQTT protocol (a lightweight pub-sub queue - like a topic)
There are plans later to add a "history" feature to the ChatService so the client can see the chat history, which will require adding a datastore of some description.


Chat Broadcast SSE
------------------
This service accepts a connection, via GET, to a particular message queue channel.  
This then holds a connection open to a queue on the backend and begins to send _server sent events_ to all connected clients


The Other Parts
===============

There are 2 more modules in the project. 

Message Queue
-------------

This is a very simple ansible task to install and configure apollo mq to be able to use MQTT (pub/sub-like topics message queue)
**NOTE: you don't need to use ansible**
This has simply been chosen to illustrate how the queue is currently configured (it looks dev ready but not production)

To install the message queue locally, you can use:
```ansible-playbook -i local/hosts.ini install.yml --ask-sudo-pass -vv```

Chat Client Demo
----------------

To illustrate how the services and queue interacts, we have included a small client example.
To be able to see this page, you need to:
```
cd chat_client_service
./run.sh
```
This will run a very simple python server to serve the flat content on port 8000

*Note, you need both services running and the message queue up for the page to work*

To go to a particular channel, open 2 browser windows pointing at the following url:

```
http://localhost:8000?c=c3743620-cc30-11e6-9d9d-cec0c932ce01
(Any valid UUID will currently create a new message queue in the background)
```

