

The Exercise
============
This exercise is all about finxing the coding that has been "done" but not really "done".
We need to get the code to live in a healthy, repeatable way.  There are a number of issues currently:

 - There is no CI configured yet to build and release the software
 - No nginx configured yet to reverse proxy the services
    - Thus the Access-Control-Allow-Origin: * header is set for local testing on each service, and the index.html has ports hardcoded.
    - Also, the Chat Broadcast SSE will need to be load balanced (SSEs leave connections open)
 - the message queue isn't really setup for a production release
    - also in ansible, the SSL certs are being checked in for the message queue
 - Config is hardcoded for both services.
 - no proper logging (only sysout-ing right now)
 - monitoring not even been thought about
 - Tests are minimal
 - no user login currently integrated (deferred till later)
And a few more things in there.


What we'd really like to see is how you read the code, and prioritise the tasks.
We'd also like to to get a bit of an indicator as to how much you think you can do in the time allocated.
There is a whole bunch of stuff to do, and only 2 hours to do it in.
You'll be working alongside one of our people who will be able to provide credentials for AWS, and point you in the right direction for other stuff.
They're also a great sounding board - we like thinking out loud, as it helps us understand where you are coming from.
Feel free to change any-and-everything, as long as the core functionality is still there.


What Is In This Repo?
=====================

Chat Services
=============
There are 2 chat services built so far, which implement a very basic chat function. 
One service currently accepts a message, and the other broadcasts it to whatever client is connected.


Chat Service
------------
Very simply, it takes a message from the user via a restful interface, and pushes it onto a queue, which uses MQTT protocol (a lightweight pub-sub queue)
There are plans later to add a "history" feature to the ChatService so the client can see the chat history, which will require adding a datastore of some description.


Chat Broadcast SSE
------------------
This service accepts a connection, via GET, to a particular channel.
Behind the scenes, this then connects to the queue and begins to send _server sent events_ to all connected clients


The Other Bits
==============

There are 2 more modules in the project. 

Chat Client Demo
----------------

To illustrate how both services interacts, we have included a small client example.
To be able to see this page, you need to:
```
cd chat_client_service
./run.sh
```
This will run a very simple python server to serve the flat content on port 8000
*Note, you need both services running for the page to work*

Message Queue
-------------

This is a very simple ansible task to install and configure apollo mq to be able to use MQTT (pub/sub-like topics message queue)
**NOTE: you don't need to use ansible**
This has simply been chosen to illustrate how the queue is currently configured (it looks dev ready but not production)
