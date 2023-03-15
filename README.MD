PROJECT TITLE: Covid London Data Set Loader\
PURPOSE OF PROJECT: To load the Covid London data set from the provided csv file into a Java data structure
HOW TO START THIS PROJECT: run DataViewer.java as javafx file\
AUTHORS: KCL Informatics, PPA, 3b1w

## Setting up On VSCode
> For development: use github desktop to log into github enterprise account in working directory

**NOTE: when `JAVAFX_PATH` is referenced below, replace it with the path to where you have downloaded javafx-sdk ie. /Users/user1/Documents/javafx-sdk-19.0.2.1**
1.  `git clone https://github.kcl.ac.uk/k22009721/CW4-Covid.git`
2. under Java Projects -> Referenced Libraries -> + icon (add libraries)
    - add the jar files for commons and openCSV from +libs
    - add the jar files from JAVAFX_PATH/lib
3. Go into `.vscode/launch.json (if it exists)` or `"Go to run and debug" on the side panel -> click 'create launch.json' `
4. copy paste the text below: (remember to replace `JAVAFX_PATH` with where you have downloaded javafx-sdk
```
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [        
        {
            "type": "java",
            "name": "App",
            "request": "launch",
            "mainClass": "DataViewer",
            "vmArgs": "--module-path /JAVAFX_PATH/lib --add-modules javafx.controls,javafx.fxml"
            // eg   : "--module-path /Users/harshpatel/Documents/Java-Libraries/javafx-sdk-19.0.2.1/lib --add-modules javafx.controls,javafx.fxml"
        }
    ]
}
```
5. press (fn) f5 to run program.
