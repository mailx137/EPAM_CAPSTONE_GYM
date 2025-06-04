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