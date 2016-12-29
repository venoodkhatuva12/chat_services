Chat Services
=============
There are 2 chat services built so far, which implement a very basic chat function.


Chat Service
------------
Very simply, it takes a message from the user via a restful service, and pushes it onto a queue, using MQTT protocol


Chat Broadcast SSE
------------------
This service accepts a connection, via GET, to a particular channel.
Behind the scenes, this then connects to the queue and begins to send server sent events to all connected clients


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
This will run a very simple python server to serve the flat content on port 8080

Message Queue
-------------

This is a very simple ansible task to install and configure apollo mq to be able to use MQTT (pub/sub topic flavour of queues)
**NOTE: you don't need to use ansible for deployment**
This has simply been chosen to illustrate how the queue is currently configured (it looks dev ready but not production)


The Exercise
============
This exercise is all about finxing the coding that has been "done" but not really "done"".
We need to get the code to live in a healthy, repeatable way.  There are a number of issues currently:

 - There is no CI configured yet to build and release the software
 - No nginx configured yet to reverse proxy the services
    - Thus the Access-Control-Allow-Origin: * header is set for local testing on each service, and the index.html has ports hardcoded.
    - Also, the Chat Broadcast SSE will need to be load balanced (SSEs leave connections open)
 - the message queue isn't really setup for a production release
    - also in ansible, the certs are being checked in
 - Config is hardcoded in each service.
 - no proper logging (only sysout-ing right now)
 - monitoring not thought about
 - Tests are minimal
 - no security, or login integrated
And a few more things in there.


What we'd really like to see is how you read the code, and prioritise the tasks.
There is a whole bunch of stuff to do, and only 2 hours to do it in.
You'll be working alongside one of our people who will be able to provide credentials for AWS, and point you in the right direction.
They're also a great souinding board - we like thinking out loud, as it helps us understand where you are coming from.
We'd also like to to get a bit of an indicator as to how much you think you can do in the time allocated.
Feel free to change any and everything, as long as the core functionality is still there.