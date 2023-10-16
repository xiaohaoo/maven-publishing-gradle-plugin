/*
 * Copyright (c) 2023 xiaohao
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    `java-gradle-plugin`
    `maven-publish`
    signing
}

group = "com.xiaohaoo"
version = "1.0.3"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    withSourcesJar()
    withJavadocJar()
}


tasks.withType<Javadoc> {
    if (JavaVersion.current().isJava8Compatible()) {
        options {
            this as StandardJavadocDocletOptions
            addBooleanOption("Xdoclint:none", true)
        }
    }
}


val gitUrl = "https://github.com/xiaohaoo/maven-publishing-gradle-plugin"

publishing {
    repositories {
        maven {
            name = "mavenCenter"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                val ossrhUsername: String? by project
                val ossrhPassword: String? by project
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
    publications {
        withType<MavenPublication>() {
            pom {
                url.set(gitUrl)
                name.set(project.name)
                description.set("可以用来发布到Maven中央仓库的Gradle插件")
                licenses {
                    license {
                        name.set("GNU AFFERO GENERAL PUBLIC LICENSE, Version 3")
                        url.set("http://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("xiaohao")
                        name.set("xiaohao")
                        email.set("sdwenhappy@163.com")
                    }
                }
                scm {
                    connection.set("${gitUrl.replace(Regex("https?"), "scm:git:git")}.git")
                    developerConnection.set("${gitUrl.replace(Regex("https?"), "scm:git:ssh")}.git")
                    url.set(gitUrl)
                }
            }
        }
        create<MavenPublication>("mavenPublication") {
            pom {
                from(components["java"])
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

gradlePlugin {
    plugins {
        create("mavenPublishingPlugin") {
            id = "com.xiaohaoo.maven-publishing"
            implementationClass = "com.xiaohaoo.gradle.plugin.MavenPublishingPlugin"
            displayName = "Maven Publishing"
            description = "Gradle plugin to Maven Publishing"
        }
    }
}