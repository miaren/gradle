/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.jvm.toolchain

import org.gradle.integtests.fixtures.AbstractIntegrationSpec

class JavaToolchainDownloadSoakTest extends AbstractIntegrationSpec {

    public static final int VERSION = 17
    private static final String ECLIPSE_DISTRO_NAME = "eclipse_adoptium"
    public static final String FOOJAY_PLUGIN_SECTION = """
            plugins {
                id 'org.gradle.toolchains.foojay-resolver-convention' version '0.4.0'
            }
        """

    public static final String TOOLCHAIN_WITH_VERSION = """
            java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of($VERSION)
                }
            }
        """

    def setup() {
        settingsFile << FOOJAY_PLUGIN_SECTION

        buildFile << """
            plugins {
                id "java"
            }

            $TOOLCHAIN_WITH_VERSION
        """

        file("src/main/java/Foo.java") << "public class Foo {}"

        executer.requireOwnGradleUserHomeDir()
            .withToolchainDownloadEnabled()
    }

    def "can download missing jdk automatically"() {
        when:
        result = executer
                .withTasks("compileJava")
                .expectDocumentedDeprecationWarning("Java toolchain auto-provisioning enabled, but no java toolchain repositories declared by the build. Will rely on the built-in repository. " +
                        "This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
                        "In order to declare a repository for java toolchains, you must edit your settings script and add one via the toolchainManagement block. " +
                        "See https://docs.gradle.org/current/userguide/toolchains.html#sec:provisioning for more details.")
                .run()

        then:
        javaClassFile("Foo.class").assertExists()
        assertJdkWasDownloaded(ECLIPSE_DISTRO_NAME)
    }

    def "can download missing j9 jdk automatically"() {
        buildFile << """
            java {
                toolchain {
                    implementation = JvmImplementation.J9
                }
            }
        """

        when:
        result = executer
                .withTasks("compileJava")
                .expectDocumentedDeprecationWarning("Java toolchain auto-provisioning enabled, but no java toolchain repositories declared by the build. Will rely on the built-in repository. " +
                        "This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
                        "In order to declare a repository for java toolchains, you must edit your settings script and add one via the toolchainManagement block. " +
                        "See https://docs.gradle.org/current/userguide/toolchains.html#sec:provisioning for more details.")
                .run()

        then:
        javaClassFile("Foo.class").assertExists()
        assertJdkWasDownloaded("openj9")
    }

    def "will get deprecation message for no configured repositories even when not downloading"() {
        when: "build runs and doesn't have a local JDK to use for compilation"
        result = executer
                .expectDocumentedDeprecationWarning("Java toolchain auto-provisioning enabled, but no java toolchain repositories declared by the build. " +
                        "Will rely on the built-in repository. This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
                        "In order to declare a repository for java toolchains, you must edit your settings script and add one via the toolchainManagement block. " +
                        "See https://docs.gradle.org/current/userguide/toolchains.html#sec:provisioning for more details.")
                .withTasks("compileJava", "-Porg.gradle.java.installations.auto-detect=false")
                .run()

        then: "suitable JDK gets auto-provisioned"
        javaClassFile("Foo.class").assertExists()
        assertJdkWasDownloaded(ECLIPSE_DISTRO_NAME)

        when: "the marker file of the auto-provisioned JDK is deleted, making the JDK not detectable"
        //delete marker file to make the previously downloaded installation undetectable
        def markerFile = findMarkerFile(executer.gradleUserHomeDir.file("jdks"))
        markerFile.delete()
        assert !markerFile.exists()

        and: "build runs again"
        executer
                .withTasks("compileJava", "-Porg.gradle.java.installations.auto-detect=false", "-Porg.gradle.java.installations.auto-download=true")
                .run()

        then: "the JDK is auto-provisioned again and its files, even though they are already there don't trigger an error, they just get overwritten"
        markerFile.exists()
    }

    def "issue warning on using auto-provisioned toolchain with no configured repositories"() {
        when: "build runs and doesn't have a local JDK to use for compilation"
        result = executer
                .withTasks("compileJava", "-Porg.gradle.java.installations.auto-detect=false")
                .run()

        then: "suitable JDK gets auto-provisioned"
        javaClassFile("Foo.class").assertExists()
        assertJdkWasDownloaded(ECLIPSE_DISTRO_NAME)

        when: "build has no toolchain repositories configured"
        settingsFile.text = ''

        then: "build runs again, uses previously auto-provisioned toolchain and warns about toolchain repositories not being configured"
        executer
                .expectDocumentedDeprecationWarning("Java toolchain auto-provisioning enabled, but no java toolchain repositories declared by the build. " +
                        "Will rely on the built-in repository. This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
                        "In order to declare a repository for java toolchains, you must edit your settings script and add one via the toolchainManagement block. " +
                        "See https://docs.gradle.org/current/userguide/toolchains.html#sec:provisioning for more details.")
                .withTasks("compileJava", "-Porg.gradle.java.installations.auto-detect=true", "-Porg.gradle.java.installations.auto-download=true")
                .run()
    }


    private void assertJdkWasDownloaded(String implementation) {
        assert executer.gradleUserHomeDir.file("jdks").listFiles({ file ->
            file.name.contains("-$VERSION-") && file.name.contains(implementation)
        } as FileFilter)
    }

    def cleanup() {
        executer.gradleUserHomeDir.file("jdks").deleteDir()
    }
}
