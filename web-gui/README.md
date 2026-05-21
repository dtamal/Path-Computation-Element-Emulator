# Web Launcher

The web launcher is a packaged jar containing the Jetty web server and can be
launched as both the server and the client. In order to use the web UIs: follow
the steps below:

## Starting the launchers
Follow the steps below to compile the complete project. Navigate to the project
root directory, and run the commands:
```shell
mvn clean install
```
Once the code is compiled, you can launch the server using the command
```shell
java -jar webgui/target/webgui-0.0.1-SNAPSHOT.jar
```
