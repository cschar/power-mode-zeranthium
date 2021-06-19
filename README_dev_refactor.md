
Optimizing :
https://plugins.jetbrains.com/docs/intellij/performance.html
uses this plugin:
https://plugins.jetbrains.com/plugin/15104-ide-perf

Plugin components are... deprecated?
https://plugins.jetbrains.com/docs/intellij/plugin-components.html
"Any existing Components should be migrated to services, extensions, or listeners (see below)"

right now pmode3 class is defined as application-component.... this should be stuck in the <applicationService...> tag
like MyApplicatioNService kotlin class.

we are using PersistentStateComponent
https://plugins.jetbrains.com/docs/intellij/persisting-state-of-components.html#using-persistentstatecomponent
In the new guide, they want us to use a service impelemtating that...

I dont think current PowerMode3 class is a service?




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

