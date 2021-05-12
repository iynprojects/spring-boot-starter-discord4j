/*
 * This file is part of spring-boot-starter-discord4j.
 *
 * spring-boot-starter-discord4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spring-boot-starter-discord4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with spring-boot-starter-discord4j.  If not, see <https://www.gnu.org/licenses/>.
 */
import org.gradle.api.JavaVersion.*
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.*

plugins {
    `java-library`

    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.discord4j"
version = "0.1.0"

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = sourceCompatibility
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.4.5")
    }
}

dependencies {
    api("com.discord4j:discord4j-core:3.1.5")

    api("org.springframework.boot:spring-boot-starter")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    test {
        useJUnitPlatform()
    }

    dependencyUpdates {
        rejectVersionIf {
            val rejectedRegex = ".*(?:M\\d+|RC.*)".toRegex()
            rejectedRegex.matches(candidate.version)
        }
    }

    wrapper {
        distributionType = ALL
        gradleVersion = "7.0.1"
    }
}
