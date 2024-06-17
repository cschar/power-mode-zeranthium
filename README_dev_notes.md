

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
 
 6. publish  
         # ensure you have PUBLISH_TOKEN set as env var
        ./gradlew :publishPlugin --no-configuration-cache --info

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
  `./gradlew :clean :testIdeUi`
  `./gradlew :clean :testIdeUi --args="C:\\Users\\codywin\\IdeaProjects\\untitled13\\src\\App22336.kt"`

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

# then the log entries will be visible in
    `build/idea-sandbox/<IDE-VERSION>/log/idea.log`
ex: `build/idea-sandbox/IC-2024.2/log/idea.log`


### generating custom javadocs

```
./gradlew :javadoc --rerun --quiet
./gradlew :javadoc --rerun --quiet > ./docs/_includes/options.md
```

### Testing doc page
```
cd docs
bundle exec jekyll serve
```

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

###### any new sounds, can compress like so
https://trac.ffmpeg.org/wiki/Encode/MP3
ffmpeg -i input.mp3 -codec:a libmp3lame -qscale:a 8 output.mp3
###### and volume adjusted
ffmpeg -i input.mp3 -filter:a "volume=0.5" output.mp3

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
