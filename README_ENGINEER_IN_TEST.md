The Engineer in Test Challenge
==============================

Please write some appropriate automated tests for this code.
As we expect test engineers to also change production code, so if you see anything wrong, feel free to refactor any of the code wherever you see fit.  
And if you really feel like it, check in some configuration with run instructions in order to setup a continuous integration environment.
You can use any combination of test framework you want. 
If you want more guidance than that on choice, we believe in Affinitas that the tests should be written in the language of the code, as it makes it easier for developers to also write tests and read them when there are failures.

But there are a number of issues currently:

 - There is no CI configured yet to build and release the software
 - Configuration needs to be done properly in the code (config is currently hardcoded for both services)
 - The services are on different ports, and no reverse proxy exists for the services
    - Thus the Access-Control-Allow-Origin: * header is set for local testing on each service, and the index.html in the chat_client_demo module has ports hardcoded.
 - Id's haven't really been thought about on the DOM
 - Testing between 2 clients and a server can be tricky
 - Unit tests are minimal
 - No user login currently integrated (deferred till later)
And a few more things in there.

We'd also like to see some quick notes on what you'd teach the developers who have made the original code in order to fix some obvious stuff they are doing.
