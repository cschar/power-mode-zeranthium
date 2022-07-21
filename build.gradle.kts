//import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.TestResult.ResultType
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    // Java support - https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin
    id("java")
    // Kotlin support
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
//    id("org.jetbrains.kotlin.jvm") version "1.4.10"

    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.7.0"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
//    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    //  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"

    id("com.github.johnrengelman.shadow") version "7.0.0"
}
group = project.findProperty("pluginGroup").toString()
// group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    // for remoterobot
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring_multiple_repositories
    maven {
        // https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/com/intellij/remoterobot/robot-server-plugin/
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

var remoteRobotVersion = "0.11.14"

dependencies {
//    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")

    implementation(group = "org.json", name = "json", version = "20090211")
    implementation(group = "javazoom", name = "jlayer", version = "1.0.1")
    implementation(group = "org.imgscalr", name = "imgscalr-lib", version = "4.2")

    implementation(group = "org.eclipse.jgit", name = "org.eclipse.jgit", version = "5.12.0.202106070339-r") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // robot-ui testing stuff
//    testImplementation 'com.intellij.remoterobot:remote-robot:' + remoteRobotVersion
//    testImplementation 'com.intellij.remoterobot:remote-fixtures:' + remoteRobotVersion
    // for some reason this version is on maven
    // https://mvnrepository.com/artifact/com.intellij.remoterobot/remote-fixtures/1.1.18
    // https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/com/intellij/remoterobot/

    // https://github.com/JetBrains/intellij-ui-test-robot
    testImplementation("com.intellij.remoterobot:remote-robot:" + remoteRobotVersion)
    testImplementation("com.intellij.remoterobot:remote-fixtures:" + remoteRobotVersion)
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")

    // Logging Network Calls
    testImplementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Video Recording
    testImplementation("com.automation-remarks:video-recorder-junit5:2.0")

    // https://www.baeldung.com/junit-5-gradle#enabling-support-for-old-versions
    testCompileOnly("junit:junit:4.12")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.3.1")
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
//detekt {
//    config = files("./detekt-config.yml")
//    buildUponDefaultConfig = true
//
//    reports {
//        html.enabled = false
//        xml.enabled = false
//        txt.enabled = false
//    }
//}

val commandLineProjectProp: String by project

tasks.register("hello") {
    doLast {
        println(commandLineProjectProp)
    }
}


// For Some reason :jar task adds frame classes to jar? here we manually exclude it
sourceSets.forEach {
    val dirPaths = it.java.srcDirs.map { d -> d.path }
    println("${it.name} : ${dirPaths.joinToString()}")
}
sourceSets {
//    test {
//        java {
//            exclude("**/uitest**")
//        }
//    }
}


subprojects {
    apply {
        plugin(JavaPlugin::class.java)
        plugin("org.jetbrains.kotlin.jvm")
    }

//    tasks {
//        runIde {
//            onlyIf {
//                println("skipping runIde")
//                false
//            }
//        }
//    }
}

// https://docs.gradle.org/current/userguide/tutorial_using_tasks.html
tasks {
    jar {
        // https://docs.gradle.org/current/userguide/more_about_tasks.html
        // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html#org.gradle.api.tasks.bundling.Jar:archiveBaseName

        // https://stackoverflow.com/questions/56518451/
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    //  https://youtrack.jetbrains.com/issue/IDEA-210683/Illegal-reflective-access-in-IJ-codebase#focus=Comments-27-4620700.0-0
    getByName<JavaExec>("buildSearchableOptions") {
        jvmArgs(
            "--illegal-access=deny",
            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
            "--add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.font=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.swing=ALL-UNNAMED",
            "--add-opens=java.desktop/com.apple.eawt.event=ALL-UNNAMED"
        )
    }

    // tag example: https://www.baeldung.com/junit-5-gradle#configuring-junit-5-tests-with-gradle
    // from CLI: // gradle clean test -DincludeTags='regression' -DexcludeTags='accessibility'
    // custom gradle test config: https://stackoverflow.com/a/59022129/5198805
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html
    test {

        // we can specify to use Junit5... above
        // but still have normal Junit4 tests working
        // https://www.baeldung.com/junit-5-gradle#enabling-support-for-old-versions.
        useJUnitPlatform()

        // even trying to mute them all, new warnings still popup...
        // https://youtrack.jetbrains.com/issue/IDEA-258551
//        jvmArgs = listOf(
//            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
//            "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
//            "--add-opens=java.base/java.lang=ALL-UNNAMED",
//            "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
//            "--add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED",
//            "--add-opens=java.desktop/sun.font=ALL-UNNAMED",
//            "--add-opens=java.desktop/sun.swing=ALL-UNNAMED",
//            "--add-opens=java.desktop/com.apple.eawt.event=ALL-UNNAMED",
//            "--add-opens=java.base/java.io=ALL-UNNAMED"
//        )

        outputs.upToDateWhen { false }
        testLogging.showStandardStreams = true

        // https://stackoverflow.com/a/69840376/5198805
        testLogging {
            showCauses = false
            showExceptions = false
            showStackTraces = false
            showStandardStreams = false

            val ansiReset = "\u001B[0m"
            val ansiGreen = "\u001B[32m"
            val ansiRed = "\u001B[31m"
            val ansiYellow = "\u001B[33m"

            fun getColoredResultType(resultType: ResultType): String {
                return when (resultType) {
                    ResultType.SUCCESS -> "$ansiGreen $resultType $ansiReset"
                    ResultType.FAILURE -> "$ansiRed $resultType $ansiReset"
                    ResultType.SKIPPED -> "$ansiYellow $resultType $ansiReset"
                }
            }

            afterTest(
                KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                    println("${desc.className} | ${desc.displayName} = ${getColoredResultType(result.resultType)}")
                })
            )

            afterSuite(
                KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                    if (desc.parent == null) {
                        println("Result: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)")
                    }
                })
            )
        }
    }

    buildSearchableOptions {
        jvmArgs = listOf(
            "-Xmx6G",
            "-Djdk.attach.allowAttachSelf=true", // for IDE perf plugin to be allowed to attach a trace agent
            // "--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED" //j.internal.DebugAttachDetector
            "--add-exports=java.base/jdk.internal.vm=ALL-UNNAMED", // j.internal.DebugAttachDetector

        )
    }

    runIde {

        //  ./gradlew runIde --args="C:\\path\\to\\project\\file.java"
//          args=listOf()

        jvmArgs = listOf(
            "-Xmx4G",
            "-Djdk.attach.allowAttachSelf=true", // for IDE perf plugin to be allowed to attach a trace agent
            // "--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED" //j.internal.DebugAttachDetector
            "--add-exports=java.base/jdk.internal.vm=ALL-UNNAMED", // j.internal.DebugAttachDetector

        )
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
        systemProperty("idea.trust.all.projects", "true")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    var javaVersion = "11"

    // Set the compatibility versions to 11
    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }

//    withType<Detekt> {
//        jvmTarget = javaVersion
//    }

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

    // added to remove warning on build
    inspectClassesForKotlinIC {
        dependsOn(instrumentTestCode)
    }
}
