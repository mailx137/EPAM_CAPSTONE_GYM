## Setup the application
At first you need to clone application.properties.example to application.properties and set the your properties accordingly.

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
## Run the application
Windows: ```mvn clean package; mvn jetty:run ``` <br/>
Linux/Osx: ```mvn clean package && mvn jetty:run```

if you want to run the application in debug mode, you need to set environment variable before running jetty.

Windows: ```$env:MAVEN_OPTS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:5005'```<br/>
Linux/Osx: ```export MAVEN_OPTS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:5005'```


### VSCODE launch.json for Remote Debugging
```
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Attach to Remote JVM",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005
        }
    ]
}
```