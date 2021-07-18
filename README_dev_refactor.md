
Optimizing :
https://plugins.jetbrains.com/docs/intellij/performance.html
uses this plugin:
https://plugins.jetbrains.com/plugin/15104-ide-perf


Try using this for UI building:
https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl.html#example

Try USING THIS FOR TESTS:
https://stackoverflow.com/a/46533151/5198805
https://github.com/radarsh/gradle-test-logger-plugin


interesting blog post on figuring out openAPI:
http://vladsch.com/blog/3

https://github.com/krasa/StringManipulation/issues/92
links to
[1] https://github.com/krasa/StringManipulation/commit/43a4bc5e99f63663a7b2c5d8d75b1550a3e0a408
[2] https://github.com/krasa/StringManipulation/commit/08422d714ce70126093c051bd5147c8f9dbcdd3f

[1] uses a callback..
[2] seems to be a more 'standard way', also detailed in
https://www.plugin-dev.com/intellij/general/plugin-initial-load/


## TESTS

- robotUI test: make a test to load settings UI when it is disabled, check if checkboxes are disabled

## BUGS ?


-- Run the "plugin" task, then close the project wihtout hitting red 'stop' icon in launcher IDE...
console shows a Memory leak error....

-- When stopping with CTRL+SHIFT+Z.... particles still float away instead of immediatly disappearing\

-- in Droste, moving caret with arrow keys doesnt resize cover

