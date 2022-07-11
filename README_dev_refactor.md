
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

--- CATCH ERROR when file is named improperly... e.g IMAGE0001.png instead of 0001.png


-- When stopping with CTRL+SHIFT+Z.... particles still float away instead of immediatly disappearing\

-- in Droste, moving caret with arrow keys doesnt resize cover


java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
at java.base/java.util.Objects.checkIndex(Objects.java:372)
at java.base/java.util.ArrayList.get(ArrayList.java:459)
at com.cschar.pmode3.config.common.SpriteDataAnimated.setImageAnimated(SpriteDataAnimated.java:153)
at com.cschar.pmode3.config.common.SpriteDataAnimated.<init>(SpriteDataAnimated.java:61)
at com.cschar.pmode3.config.common.SpriteDataAnimated.fromJsonObjectString(SpriteDataAnimated.java:285)
at com.cschar.pmode3.PowerMode3.deserializeSpriteDataAnimated(PowerMode3.java:456)
at com.cschar.pmode3.PowerMode3.loadConfigDataAsync(PowerMode3.java:396)
at com.cschar.pmode3.PowerMode3$5.run(PowerMode3.java:366)
at com.intellij.openapi.progress.impl.CoreProgressManager.startTask(CoreProgressManager.java:442)
at com.intellij.openapi.progress.impl.ProgressManagerImpl.startTask(ProgressManagerImpl.java:114)
at com.intellij.openapi.progress.impl.CoreProgressManager.lambda$runProcessWithProgressAsynchronously$5(CoreProgressManager.java:493)
at com.intellij.openapi.progress.impl.ProgressRunner.lambda$submit$3(ProgressRunner.java:252)
at com.intellij.openapi.progress.impl.CoreProgressManager.lambda$runProcess$2(CoreProgressManager.java:188)
at com.intellij.openapi.progress.impl.CoreProgressManager.lambda$executeProcessUnderProgress$12(CoreProgressManager.java:608)
at com.intellij.openapi.progress.impl.CoreProgressManager.registerIndicatorAndRun(CoreProgressManager.java:683)
at com.intellij.openapi.progress.impl.CoreProgressManager.computeUnderProgress(CoreProgressManager.java:639)
at com.intellij.openapi.progress.impl.CoreProgressManager.executeProcessUnderProgress(CoreProgressManager.java:607)
at com.intellij.openapi.progress.impl.ProgressManagerImpl.executeProcessUnderProgress(ProgressManagerImpl.java:60)
at com.intellij.openapi.progress.impl.CoreProgressManager.runProcess(CoreProgressManager.java:175)
at com.intellij.openapi.progress.impl.ProgressRunner.lambda$submit$4(ProgressRunner.java:252)
at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1700)
at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
at java.base/java.util.concurrent.Executors$PrivilegedThreadFactory$1$1.run(Executors.java:668)
at java.base/java.util.concurrent.Executors$PrivilegedThreadFactory$1$1.run(Executors.java:665)
at java.base/java.security.AccessController.doPrivileged(Native Method)
at java.base/java.util.concurrent.Executors$PrivilegedThreadFactory$1.run(Executors.java:665)
at java.base/java.lang.Thread.run(Thread.java:829)
