# Connect 5 Game 

This is a Java / Maven / Spring Boot application which provides RESTful services for client and server connection for Connect 5 Game.

## Installation Instructions
- I have have created 3 Maven projects to complete this connect 5 game. 
	- connect-5-common
	- connect-5-server
	- connect-5-client
- Common project used for both client and server projects, have included the common project dependency in pom.xml. So please build the common project before executing build for client and server.
- Server project is a Spring Boot application with Rest Controller to receive the request from clients and its configured with port 8888.
- Run the Maven clean install for Client and Server project.
- Please follow the below order to run the applicaiton.
	- Run the Spring boot applicaiton in Server project.
	- Clone the client as two seprate projects for two players.
	- Run the Java Main method in Connect5GamePlayer class under client.
	

## What you'll need

    JDK 11
    Maven
    spring-boot 2.4.4

