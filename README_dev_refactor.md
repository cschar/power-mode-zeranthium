
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


### TODO:

-- add custom config path... save location in settings, so when we reopen, its saved fo ruser
// i nc ustom config path add a label showing current loaded path...

[x] - ensure we Save custom pack basepath in settings
[ ] - make tooltip explaining what 'speed' level is in linker
[x] - better squashed UI on packRow component list
[x] - catch error when we try to download a project, close settings, reopen settings and click 'download' again
[x] - catch download cancelled exception
[ ] - show preview if preview.png in pack directory
[ ] - write script to convert folder of images to best compression using quant program
        https://askubuntu.com/questions/1011550/how-can-i-convert-all-video-files-in-nested-folders-batch-conversion
  ### OR JUST USE PNGoo .... write to same folder structure etc...

## TESTS

[ ] - robotUI test: make a test to load settings UI when it is disabled, check if checkboxes are disabled
[x] - robotUI test: test to load packs through UI, prebuilt packs + custom pack


## BUGS ?


-- When stopping with CTRL+SHIFT+Z.... particles still float away instead of immediatly disappearing\

-- in Droste, moving caret with arrow keys doesnt resize cover

