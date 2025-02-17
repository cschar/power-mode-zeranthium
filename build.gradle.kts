
import org.gradle.internal.impldep.org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutorService.TestTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType


fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin

    //Gradle Test Output
    id("com.adarshr.test-logger") version "4.0.0"

    //Generate docs
    id("org.jetbrains.dokka") version "1.9.20"
    // ./gradlew :dokkaHtml
//    kotlin("jvm") version "1.9.22"

}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(17)
//    jvmToolchain(21)
}

// Configure project's dependencies
repositories {
    mavenCentral()

    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
    }
}

var remoteRobotVersion = "0.11.23"

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(properties("platformType"), properties("platformVersion"))
//        intellijIdeaCommunity("2024.3.3")

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(properties("platformPlugins").map { it.split(',') })



        testFramework(TestFrameworkType.Platform)


        // https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/1638#issuecomment-2151527333
//        jetbrainsRuntime()
    }

    // implementation(libs.exampleLibrary)


    implementation("com.google.code.gson:gson:2.10.1")
    implementation(group = "org.json", name = "json", version = "20231013")
    implementation(group = "javazoom", name = "jlayer", version = "1.0.1")
    implementation(group = "org.imgscalr", name = "imgscalr-lib", version = "4.2")

    implementation(group = "org.eclipse.jgit", name = "org.eclipse.jgit", version = "6.7.0.202309050840-r") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }


    // Testing
    testImplementation("com.intellij.remoterobot:remote-robot:$remoteRobotVersion")
    testImplementation("com.intellij.remoterobot:remote-fixtures:$remoteRobotVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    // Logging Network Calls
    testImplementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    // Video Recording
    testImplementation("com.automation-remarks:video-recorder-junit5:2.0")
    // https://www.baeldung.com/junit-5-gradle#enabling-support-for-old-versions
//    testCompileOnly("junit:junit:4.13.1")
//    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.3.1")

    // If using JUnit Jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")



}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        version = properties("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
    }

    signing {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = environment("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = properties("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        // ...

            ides {

                ide(properties("platformType"),properties("platformVersion"))
//                local(file("/path/to/ide/"))
                recommended()
//                select {
//                    types = listOf(IntelliJPlatformType.PhpStorm)
//                    channels = listOf(ProductRelease.Channel.RELEASE)
//                    sinceBuild = "232"
//                    untilBuild = "241.*"
//                }
            }
    }


}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}




abstract class CreateFileTask : DefaultTask() {
    @TaskAction
    fun action() {
        val file = File("myfile.txt")
        file.createNewFile()
        file.writeText("HELLO FROM MY TASK")
    }
}
tasks.register<CreateFileTask>("createFileTask")


tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
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

//    "test_ui"(Test::class) {
//        useJUnitPlatform {
//            includeTags = setOf("Sound","Thing2")
//        }
//    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
//    testIdeUi {
//        systemProperty("robot-server.port", "8082")
//        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
//        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
//        systemProperty("jb.consents.confirmation.enabled", "false")
//        // Newer IntelliJ versions require this property to avoid trust project popup
//        systemProperty("idea.trust.all.projects", "true")
//        systemProperty("ide.show.tips.on.startup.default.value", "false")
//    }


    publishPlugin {
        dependsOn(patchChangelog)
    }




    // https://docs.gradle.org/current/kotlin-dsl/gradle/org.gradle.api.tasks.javadoc/-javadoc/index.html
    javadoc {
//        setDestinationDir(file("./myjavadocs/docs2"))

//        options.docletpath = "./build/classes/java/main" as List
//        options.docletpath = [rootProject.file("./build/classes/java/main")]

        options.docletpath(file("./build/classes/java/main"))
        options.doclet = "com.cschar.pmode3.tools.JsonConfigMarkdownDoclet"
        options {
            this as StandardJavadocDocletOptions
//            noTimestamp()

        }
    }

}
