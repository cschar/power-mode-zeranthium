# TODO:
 - move over to kotlin based templates
 https://github.com/JetBrains/intellij-platform-plugin-template
 - upgrade to gradle intellij-plugin 1.0 
 https://github.com/JetBrains/gradle-intellij-plugin/

# Deploying new version

 1. Change version in build.gradle 
 2. Update change notes in build.gradle
 3. Build the jar ex: ```./gradlew :jar```
 4. upload new .jar file to site

# logo design

-- SVG editor for logo here:
https://editor.method.ac/


# LICENSING ... 

audio code is LGPL:
https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java

can i combine LGPL with this project + APACHE 2.0 code?
Can i license it under MIT? 
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

To run project:

./gradlew runIde


# Snags I ran into:

- If gradle forces you to download new IDE for sandbox
 -- download separately and place e.g.
 '/Users/USERNAME/.gradle/caches/modules-2/files-2.1/com.jetbrains/jbre/jbr-11_0_3-osx-x64-b304.10.tar.gz'
.e.g.
```
cd ~/.gradle/caches/modules-2/files-2.1/com.jetbrains/jbre
rm -rf *.*
cp ~/Downloads/jbr-11_0_6-osx-x64-b765.25.tar.gz ~/.gradle/caches/modules-2/files-2.1/com.jetbrains/jbre
## or whatever version is current e.g.
cp ~/Downloads/jbr-XX_X_X-osx-x64-bXXX.XX.tar.gz ~/.gradle/caches/modules-2/files-2.1/com.jetbrains/jbre
```

 
- If serialized options are messed up
--- delete  build/idea-sandbox/config/options/power.mode3.xml
  
  


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


#launch bug:
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.intellij.util.ReflectionUtil to method sun.java2d.SunGraphicsEnvironment.isUIScaleEnabled()
WARNING: Please consider reporting this to the maintainers of com.intellij.util.ReflectionUtil
WARNING: Use --illegal-access=warn to enable warnings of further illeg

this is being tracked here: https://youtrack.jetbrains.com/issue/IDEA-210683

##runIdeForUiTest gotcha
make sure no file is open inside the ./build folder, :clean and :runIdeForUiTest will break


#Gradle dev env
in intellij run configurations make an empty gradle config with these args to intended project file
runIde --args="C:\\path\\to\\my\\project\\file\\App22336.kt"

When upgrading gradle... (to make ./gradlew use a diff version on commadnline...)
go into gradle/wrapper/gradle-wrapper.properties, and change the URL from which it downloads the version


## when setting up on a new computer...

step 1. in File -> project structure -> SDKs ... '+' icon ---> add the IntelliJP Platform Plugin SDK
step 2. set that SDK as the project SDK to have the external library defined to access all intellij.openapi stuff



# testing

./gradlew :clean :runIdeForUiTests


## for example reference of test code
https://github.com/JetBrains/gradle-intellij-plugin/tree/master/examples/simple-plugin/src/test/java/org/intellij/examples/simple/plugin
and maybe..
https://github.com/JetBrains/intellij-sdk-code-samples/