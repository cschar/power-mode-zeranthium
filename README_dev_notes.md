

# Deploying new version

```
 1. Change versions
    - 1.1. make actual change in gradle.properties:
        - 1.1.1  change pluginVersion
        - 1.1.2  update pluginSinceBuild & pluginUntilBuild
                  ex: to cover 2023.2 -> 2024.1.*
                  pluginSinceBuild = 232
                  pluginUntilBuild = 241.*
                  
        - 1.1.3  update pluginVerifierIdeVersions


 2. Update change notes plugin in build.gradle
    - update CHANGELOG.md
 
 3. build  
        ./gradlew :buildPlugin --info
 
 4. run 
        ./gradlew :runPlugin
        
 5. test 
        export POWERMODE_ZERANTHIUM_TESTS="true"
        ./gradlew :test --info
 
 . publish  
        ./gradlew :publishPlugin --no-configuration-cache

```



## when setting up on a new computer...

step 1. in File -> project structure -> SDKs ... '+' icon ---> add the IntelliJP Platform Plugin SDK
step 2. set that SDK as the project SDK to have the external library defined to access all intellij.openapi stuff


## testing 

`./gradlew :test --info`
- run a single test
  `./gradlew :test --tests "com.cschar.pmode3.uitest.WriteTextJavaTest.writeSomeText"`
  `./gradlew :test --tests "com.cschar.pmode3.GSONTest"`
- `./gradlew :test --tests "com.cschar.pmode3.PowerMode3Test"`

## testing ui

- in 1 terminal run
  `./gradlew :clean :runIdeForUiTests`
  `./gradlew :clean :runIdeForUiTests --args="C:\\Users\\codywin\\IdeaProjects\\untitled13\\src\\App22336.kt"`

  then open `http://localhost:8082/` to check UI structure

- in another terminal
  all tests: `TEST_TYPE=UI ./gradlew :test`
  single test class:
- `TEST_TYPE=UI ./gradlew :test --tests "com.cschar.pmode3.uitest.OpenSettingsJavaTest"`
  single testcase
- `TEST_TYPE=UI ./gradlew :test --info --tests "com.cschar.pmode3.uitest.OpenSettingsJavaTest.opens_project"`
-

#### testing the demo repo for ui-robot test

terminal 1:
./gradlew ui-test-example:clean ui-test-example:runIdeForUiTests &
`./gradlew ui-test-example:clean ui-test-example:runIdeForUiTests --args="C:\\Users\\codywin\\IdeaProjects\\untitled13\\src\\App22336.kt"`
terminal2
./gradlew ui-test-example:test --tests "SayHelloJavaTest"


## for example reference of test code
https://github.com/JetBrains/gradle-intellij-plugin/tree/master/examples/simple-plugin/src/test/java/org/intellij/examples/simple/plugin
and maybe..
https://github.com/JetBrains/intellij-sdk-code-samples/


# to delete ide-sandbox settings
rm `./build/idea-sandbox/config/options/power.mode.3.Zeranthium.xml`

# dropping down to debug level logging when in sandbox
add in Help > diagnostic Tools > Debug Log Settings
#com.cschar.pmode3:all     (including # at start)




----------------------------------------------------------------

# Troubleshooting

```
- If serialized options are messed up
--- delete  build/idea-sandbox/config/options/power.mode3.xml

```

## runIdeForUiTest gotcha
make sure no file is open inside the ./build folder, :clean and :runIdeForUiTest will break


## Gradle dev env
in intellij run configurations make an empty gradle config with these args to intended project file
runIde --args="C:\\path\\to\\my\\project\\file\\App22336.kt"

## Upgrading Gradle
When upgrading gradle... (to make ./gradlew use a diff version on commadnline...)
go into gradle/wrapper/gradle-wrapper.properties, and change the URL from which it downloads the version


----------------------------------------------------------------


# logo design

-- SVG editor for logo here:
https://editor.method.ac/

# any new images added, make sure to run through quant image compression
https://pngquant.org/

# LICENSING ...

audio code is LGPL:
https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java
https://opensource.stackexchange.com/questions/5175/including-untouched-lgpl-library-in-a-mit-licenced-project?rq=1
https://opensource.stackexchange.com/questions/7904/can-your-mit-library-use-an-lgpl-library?rq=1

https://dzone.com/articles/the-lgpl-license ON LGPL:
If you distribute software using the library, you must offer to supply the source code
of the library. `If you’re just using the official builds, you don’t have to do anything
special as the code is already available on GitHub.`


Max plugin size : 200MB:
https://intellij-support.jetbrains.com/hc/en-us/community/posts/206445729--Question-Limitation-of-Upload-Plugin-Size


how to correctly scale/rotate/translate with
AFFINE transformation:
https://math.stackexchange.com/a/820632


----------------------------------------------------------------


# CRASH notes:

```
java.lang.NoClassDefFoundError: org/json/JSONException
    at com.cschar.pmode3.PowerMode3$JSONLoader.loadDefaultJSONTableConfigs(PowerMode3.java:164)
    at com.cschar.pmode3.PowerMode3.loadState(PowerMode3.java:353)
    at com.cschar.pmode3.PowerMode3.loadState(PowerMode3.java:66)
    at com.intellij.configurationStore.ComponentStoreImpl.doInitComponent(ComponentStoreImpl.kt:405)
    at com.intellij.configurationStore.ComponentStoreImpl.initComponent(ComponentStoreImpl.kt:355)
    ....
Caused by: java.lang.ClassNotFoundException: org.json.JSONException PluginClassLoader[com.cschar.powermode3zeranthium, 1.0] com.intellij.ide.plugins.cl.PluginClassLoader@67d484dc
    at com.intellij.ide.plugins.cl.PluginClassLoader.loadClass(PluginClassLoader.java:75)
    at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521
``` 
    
--Fixed by packaging a FAT JAR
https://discuss.gradle.org/t/how-to-include-dependencies-in-jar/19571/5
or using shadowJar w/ gradle

#launch bug:
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.intellij.util.ReflectionUtil to method sun.java2d.SunGraphicsEnvironment.isUIScaleEnabled()
WARNING: Please consider reporting this to the maintainers of com.intellij.util.ReflectionUtil
WARNING: Use --illegal-access=warn to enable warnings of further illeg

this is being tracked here: https://youtrack.jetbrains.com/issue/IDEA-210683



