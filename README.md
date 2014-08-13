HTTPServer
==========

HTTPServer is simple netty (http://netty.io/) application which allow client to make simple requests to the server
and display the trafic statistic.
____________________________________________________________________________________________________________________________
Project implementation features:

1. Classes, which represent URL-handlers, based on the MVC ideology (Model View Controller).
    This concept is implemented using specific "@Mapping" annotations for such classes.
    
    Current classes was placed in the same separate package and retrieving using "Reflections" library
    (code.google.com/p/reflections) during server bootstrapping to create those instances.

2.  In "ChannelPipeline" was added "readTimeoutHandler" to close the connection after 70 seconds without client activity.
     After 70 seconds, ReadTimeoutException will be "throw" and handled in "ServerHandler" in "exceptionCaught" method. 
     Until that time connection steel persistent. 

3. For counting traffic to the "ChannelPipeline" was added "ChannelTrafficShapingHandler" to compute traffic per channel.

4. Also, the statistic storage represents by "Statistic.class" which has static methods for required computations.
____________________________________________________________________________________________________________________________

- "127.0.0.1:8080/hello"  displays "Hello World" after 10 seconds;
- "127.0.0.1:8080/redirect?url=/redirect_page" redirects to the 
" 127.0.0.1:8080/redirect_page" and this page displays "Page was redirected";
- "127.0.0.1:8080/status" displays all statistic*.
____________________________________________________________________________________________________________________________

 Project build   and "jar"-package  can be done using IDE or Maven (because of  use Maven support in current project).

To make a "jar" with all dependencies we must define in "pom" file special "Maven Shade Plugin", which grabs all dependencies.
Next:
  - go to project the folder on command prompt/terminal;
  - type maven life/cycle command: "mvn clean package"
where "StartServer" - is the class where "main method" of our app placed.

After this, IT IS NECESSARY TO ADD to MANIFEST.MF file from "jar" package next line:  "Main-Class: StartServer "
  
 "package" phase recursively invokes previews phases: "validate" ,"compile" and "test". Therefore, the project will be build 
 and all unit-test will run (but we haven't any at this moment).
____________________________________________________________________________________________________________________________
 To run application, we can use our IDE or ran "main" method from the command prompt. To do the second thing:
 - go to folder where "jar" file is placed and type next:   "java -jar HTTPServer-1.0-SNAPSHOT.jar"
 where "HTTPServer-1.0-SNAPSHOT" is the name of our "jar"

To stop process type "Ctrl+C"
____________________________________________________________________________________________________________________________

* The table with logs from "status" page doesn't display speed (b/s). Using "ChannelTrafficShapingHandler.class" to compute
traffic I've faced the problem to retrieve real "lastWriteThroughput"/"lastReadThroughput" from TrafficCounter.
 It was always "0" in all circumstances. Unfortunately, I haven't resolved this issue yet.

The required command "ab –c 100 –n 10000 ..." was transformed to "ab -k –c 100 –n 10000 ..." to support persistent connections 
with "-k" switch. 
