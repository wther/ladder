Snakes & Ladders Game
=====================

This is a GIT repository for an online, multiplayer Snakes and Ladders game developed for a university project. Find more info on the game at: http://en.wikipedia.org/wiki/Snakes_and_Ladders

### How to compile backend?

    mvn clean install
    
This creates the WAR file in the **target** directory, runs all integration and unit tests.
    
### How to run back-end?

    mvn spring-boot:run
    
This starts the web-backend at **localhost:8080**. Any static content (HTML, CSS, JS) will automatically be reloaded.