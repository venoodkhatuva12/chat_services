

The DevOps Exercise
===================
This exercise is all about building a continuous deployment pipeline for the code checked into this repo, and where appropriate, fixing some code and making it ready for production.

We need to get the code built and deployed to live in a healthy, repeatable way. 

You can choose any tools you deem appropriate.  We are using the netflix stack and Jenkins for our continuous deployment stuff internally, but are open to other ways of doing stuff.

But there are a number of issues currently:

 - There is no CI configured yet to build and release the software
 - Load balancing needs to be setup
 - Message queue needs to be setup
 - Configuration needs to be done properly in the code (config is currently hardcoded for both services)
 - The services are on different ports, and no reverse proxy exists for the services
    - Thus the Access-Control-Allow-Origin: * header is set for local testing on each service, and the index.html in the chat_client_demo module has ports hardcoded.
 - The message queue isn't really setup for a production release (passwords, live SSL certs needed etc)
    - also in the ansible config, the SSL certs are being checked in for the message queue
 - No proper logging (only sysout-ing right now)
 - Monitoring needs to be implemented
 - Tests are minimal
 - No user login currently integrated (deferred till later)
And a few more things in there.


What we'd really like to see is how you prioritise the tasks.

We'd also like to to get a bit of an indicator as to how much you think you can do in the time allocated.
There is a whole bunch of stuff to do, and only 2 hours 45 mins to do it all in.
You'll be working alongside one of our people who can point you in the right direction.
They're also a great sounding board - we like thinking out loud, as it helps us understand where you are coming from.
Feel free to change any-and-everything, as long as the core functionality is still there.

After the interview we're also interested in hearing what advice you'd give to the devs who wrote the code.
