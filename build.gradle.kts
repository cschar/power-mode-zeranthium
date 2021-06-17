import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    // Java support
    // https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.0"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    //for remoterobot
    //https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring_multiple_repositories
    maven {
        //https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/com/intellij/remoterobot/robot-server-plugin/
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

//https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html#custom_configurations_and_dependencies
//configurations {
//    // configuration that holds jars to include in the jar
//    extraLibs
//}
//val extraLibs by configurations.creating
val extraLibs by configurations.creating {
    //extendsFrom(configurations["compileOnly"])
    extendsFrom(configurations["implementation"])
}

var remoteRobotVersion = "0.11.4"

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")

    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    //testCompile group: 'junit', name: 'junit', version: '4.12'

    // https://mvnrepository.com/artifact/org.imgscalr/imgscalr-lib
    //  implementation files('/Applications/IntelliJ IDEA CE.app/Contents/plugins/java/lib/java-api.jar')
    //implementation(group: 'org.imgscalr', name: 'imgscalr-lib', version: '4.2')
    //implementation("org.imgscalr.imgscalr-lib:4.2")

    //
    //https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph

    // https://mvnrepository.com/artifact/javazoom/jlayer
    //compile group: 'javazoom', name: 'jlayer', version: '1.0.1'
    //compileOnly("javazoom.jlayer:1.0.1")
    // https://mvnrepository.com/artifact/org.json/json
    //compile group: 'org.json', name: 'json', version: '20090211'
    //compileOnly("org.json:20090211")

    // This adds the following libraries into the .jar file produced by the build
//    extraLibs group: 'org.json', name: 'json', version: '20090211'
//    extraLibs group: 'javazoom', name: 'jlayer', version: '1.0.1'
//    extraLibs group: 'org.imgscalr', name: 'imgscalr-lib', version: '4.2'
//    extraLibs("org.json.json:20090211")
//    extraLibs("javazoom.jlayer:1.0.1")
//    extraLibs("org.imgscalr.imgscalr-lib:4.2")

    //https://docs.gradle.org/current/userguide/declaring_dependencies.html#sub:module_dependencies
    //compileOnly("org.json.json:20090211")
    //compileOnly(group = "org.json", name = "json", version = "20090211")

    //compileOnly("javazoom.jlayer:1.0.1")
    //compileOnly(group="javazoom", name="jlayer", version="1.0.1")

    //compileOnly("org.imgscalr.imgscalr-lib:4.2")
    //compileOnly(group="org.imgscalr", name="imgscalr-lib", version="4.2")

    implementation(group = "org.json", name = "json", version = "20090211")
    implementation(group="javazoom", name="jlayer", version="1.0.1")
    implementation(group="org.imgscalr", name="imgscalr-lib", version="4.2")

    //configurations.compile.extendsFrom(configurations.extraLibs)

    //robot-ui testing stuff
//    testImplementation 'com.intellij.remoterobot:remote-robot:' + remoteRobotVersion
//    testImplementation 'com.intellij.remoterobot:remote-fixtures:' + remoteRobotVersion
    //for some reason this version is on maven
    // https://mvnrepository.com/artifact/com.intellij.remoterobot/remote-fixtures/1.1.18
    //https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/com/intellij/remoterobot/
    testImplementation("com.intellij.remoterobot:remote-robot:" + "0.11.4")
    testImplementation("com.intellij.remoterobot:remote-fixtures:" + "0.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")

    // Logging Network Calls
    testImplementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
}



// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {

    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version = properties("pluginVersion")
    groups = emptyList()
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks.register("hello") {
    doLast {
        println("Hello world!")
    }
}

// https://docs.gradle.org/current/userguide/tutorial_using_tasks.html
tasks {
    //https://github.com/johnrengelman/shadow
//    jar {
//        from {
//            configurations.extraLibs.collect { it.isDirectory() ? it : zipTree(it) }
//        }
//    }
    test {
        useJUnitPlatform()
    }

    runIdeForUiTests {
        jvmArgs = listOf("-Xmx4G")

       // maxHeapSize = "4g"

//    In case your Idea is launched on remote machine you can enable public port and enable encryption of JS calls
//    systemProperty "robot-server.host.public", "true"
//    systemProperty "robot.encryption.enabled", "true"
//    systemProperty "robot.encryption.password", "my super secret"

        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }
    runIde {
        //maxHeapSize = "4g"
        jvmArgs = listOf("-Xmx4G")
    }
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Detekt> {
        jvmTarget = "1.8"
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            File(projectDir, "README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }

    downloadRobotServerPlugin {
        version.set(remoteRobotVersion)
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}
