
Optimizing :
https://plugins.jetbrains.com/docs/intellij/performance.html
uses this plugin:
https://plugins.jetbrains.com/plugin/15104-ide-perf


Try using this for UI building:
https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl.html#example

Try USING THIS FOR TESTS:
https://stackoverflow.com/a/46533151/5198805
https://github.com/radarsh/gradle-test-logger-plugin



- SettingsSavingComponent ?

interesting blog post on figuring out openAPI:
http://vladsch.com/blog/3

BINGO
https://github.com/krasa/StringManipulation/issues/92
links to
[1] https://github.com/krasa/StringManipulation/commit/43a4bc5e99f63663a7b2c5d8d75b1550a3e0a408
[2] https://github.com/krasa/StringManipulation/commit/08422d714ce70126093c051bd5147c8f9dbcdd3f

[1] uses a callback..
[2] seems to be a more 'standard way', also detailed in
https://www.plugin-dev.com/intellij/general/plugin-initial-load/


## CURRENT BUGS

- The PostStartupActivity is called each time a project is opened.. instad of when application is loaded.
------ Not a bug... seems to still only be called once... each EditorActionHandler is unique to each project.
  
-- Run the "plugin" task, then close the project wihtout hitting red 'stop' icon in launcher IDE...
console shows a Memory leak error....

-- When stopping with CTRL+SHIFT+Z.... particles still float away instead of immediatly disappearing\


--- gradlew :buildSearchableOptions.....  HANGS WHEN A BACKGROUND THREAD IS RUNNING In configurable
  
#### Tests

- Load .xml file from a fixture, assert the settings configured, are whats in the file
--- This will allow us to then change Enum structure, create a 'migration' of settings
  



## BUG when uprading to v2021

ered in Disposer but wasn't disposed.
Register it with a proper parentDisposable or ensure that it's always disposed by direct Disposer.dispose call.
See https://jetbrains.org/intellij/sdk/docs/basics/disposers.html for more details.
The corresponding Disposer.register() stacktrace is shown as the cause:

java.lang.RuntimeException: Memory leak detected: 'com.cschar.pmode3.PowerMode3$4@107c8977' of class com.cschar.pmode3.PowerMode3$4 is registered in Disposer but wasn't disposed.
Register it with a proper parentDisposable or ensure that it's always disposed by direct Disposer.dispose call.
See https://jetbrains.org/intellij/sdk/docs/basics/disposers.html for more details.
The corresponding Disposer.register() stacktrace is shown as the cause:

