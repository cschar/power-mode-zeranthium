
Optimizing :
https://plugins.jetbrains.com/docs/intellij/performance.html
uses this plugin:
https://plugins.jetbrains.com/plugin/15104-ide-perf


Try USING THIS FOR TESTS:
https://stackoverflow.com/a/46533151/5198805
https://github.com/radarsh/gradle-test-logger-plugin


Resources on IntelliJ API:
https://developerlife.com/2021/03/13/ij-idea-plugin-advanced/
http://vladsch.com/blog/3
https://github.com/krasa/StringManipulation/issues/92
links to
[1] https://github.com/krasa/StringManipulation/commit/43a4bc5e99f63663a7b2c5d8d75b1550a3e0a408
[2] https://github.com/krasa/StringManipulation/commit/08422d714ce70126093c051bd5147c8f9dbcdd3f


## TESTS

[ ] - robotUI test: make a test to load settings UI when it is disabled, check if checkboxes are disabled
[x] - robotUI test: test to load packs through UI, prebuilt packs + custom pack
[ ] - Type , pseudo-Disable plugin, Type,  assert when plugin disabled, characters are still typed

## BUGS ?

-- Dyanmic Plugin memory when shutting down
   -- HeapDump debug with VisualVM, start VM with same JDK that Sandbox IDe is running
   -- Use VisualVMPlugin extension tool to start it with same JDK
-- When stopping with CTRL+SHIFT+Z.... particles/dinos still float away instead of immediatly disappearing
-- in Droste, moving caret with arrow keys doesnt resize cover

## upgrade
put docs on astro site
https://docs.astro.build/en/guides/deploy/github/