import jdk.javadoc.doclet.Doclet
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.gradle.api.tasks.testing.TestResult.ResultType

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    //Gradle Test Output
    id("com.adarshr.test-logger") version "3.2.0"

    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.13.2"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.0.0"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"
    // Gradle Kover Plugin
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

var remoteRobotVersion = "0.11.18"

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(group = "org.json", name = "json", version = "20220320")
    implementation(group = "javazoom", name = "jlayer", version = "1.0.1")
    implementation(group = "org.imgscalr", name = "imgscalr-lib", version = "4.2")

    implementation(group = "org.eclipse.jgit", name = "org.eclipse.jgit", version = "5.12.0.202106070339-r") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // Testing
    testImplementation("com.intellij.remoterobot:remote-robot:$remoteRobotVersion")
    testImplementation("com.intellij.remoterobot:remote-fixtures:$remoteRobotVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    // Logging Network Calls
    testImplementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    // Video Recording
    testImplementation("com.automation-remarks:video-recorder-junit5:2.0")
    // https://www.baeldung.com/junit-5-gradle#enabling-support-for-old-versions
//    testCompileOnly("junit:junit:4.13.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.3.1")


//    javadocToJson()

}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()

    // for remoterobot
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring_multiple_repositories
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(17)
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    instrumentCode.set(false)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) })
}


// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl.set(properties("pluginRepositoryUrl"))
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
    cachePath.set(provider { file(".qodana").canonicalPath })
    reportPath.set(provider { file("build/reports/inspections").canonicalPath })
    saveReport.set(true)
    showReport.set(environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false))
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover.xmlReport {
    onCheck.set(true)
}




tasks {

//    https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html#tasks-buildsearchableoptions
    buildSearchableOptions {
        enabled = false
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    runIde {
        maxHeapSize = "4g"
        systemProperties["idea.log.debug.categories"] = "#com.cschar.pmode3"
        systemProperties["idea.log.trace.categories"] = "#com.cschar.pmode3"
    }

    jar {
        // https://docs.gradle.org/current/userguide/more_about_tasks.html
        // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html#org.gradle.api.tasks.bundling.Jar:archiveBaseName
        // https://stackoverflow.com/questions/56518451/
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        })

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes.set(properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        })
    }

    // tag example: https://www.baeldung.com/junit-5-gradle#configuring-junit-5-tests-with-gradle
    // from CLI: // gradle clean test -DincludeTags='regression' -DexcludeTags='accessibility'
    // custom gradle test config: https://stackoverflow.com/a/59022129/5198805
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html
    test {
        useJUnitPlatform()
        outputs.upToDateWhen { false }
        testLogging.showStandardStreams = true
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }





    runPluginVerifier {
        ideVersions.set(listOf(properties("pluginVerifierIdeVersions").get()))

    }

    signPlugin {
        certificateChain.set(environment("CERTIFICATE_CHAIN"))
        privateKey.set(environment("PRIVATE_KEY"))
        password.set(environment("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(environment("PUBLISH_TOKEN"))
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) })
    }
}
