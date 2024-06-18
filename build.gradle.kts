import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems.jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "ru.perm.v"
// change version on publishing
version = "0.24.0607.1"
description = "Vacancy backend RestAssured tests"

java.sourceCompatibility = JavaVersion.VERSION_11
var springFoxVersion = "3.0.0"
var springBootVersion = "2.5.7"
var springDependencyManagement = "1.0.3.RELEASE"
var mockitoKotlinVersion = "4.0.0"


buildscript {
    var kotlinVersion: String? by extra; kotlinVersion = "1.1.51"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }

}

repositories {
    mavenCentral()
    mavenLocal()
    maven {

        url = uri("http://v.perm.ru:8082/repository/ru.perm.v") //OK
        isAllowInsecureProtocol = true
        credentials {
//            username = "admin"
//            password = "pass"

// export NEXUS_CI_USER=admin
// echo $NEXUS_CI_USER
            username = System.getenv("NEXUS_CRED_USR") ?: extra.properties["nexus-ci-username"] as String?
// export NEXUS_CI_PASS=pass
// echo $NEXUS_CI_PASS
            password = System.getenv("NEXUS_CRED_PASS") ?: extra.properties["nexus-ci-password"] as String?
        }
    }
}

plugins {
    java
    val kotlinVersion = "1.8.21"
    id("org.springframework.boot") version "2.5.6"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("maven-publish")
    id("io.qameta.allure") version "2.8.1"
    id("jacoco")
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("kapt") version "1.7.0"
    idea
    application
}

apply(plugin = "kotlin-kapt")

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    gradlePluginPortal()
}

java.sourceSets["main"].java {
    srcDir("build/classes/java/main")
}

dependencies {

// recomendation from https://habr.com/ru/companies/domclick/articles/505860/
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
// validator

    kapt("jakarta.annotation:jakarta.annotation-api")

    testImplementation ("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.mockito", "mockito-core")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
// https://mvnrepository.com/artifact/io.qameta.allure/allure-junit5
    testImplementation("io.qameta.allure:allure-junit5:2.27.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.4.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
    // Show test log
    testLogging {
//        events("standardOut", "started", "passed", "skipped", "failed")
        events("passed", "skipped", "failed")
    }
//    if (project.hasProperty('excludeTests')) {
//        exclude project.property('excludeTests')
//    }
    filter {
        exclude("*IntegrationTest*")
    }

}

//// remove suffix 'plain' in sonar repository
tasks.jar {
    enabled = true
    // Remove `plain` postfix from jar file name
    archiveClassifier.set("")
}

// Configure Spring Boot plugin task for running the application.
val bootJar by tasks.getting(BootJar::class) {
    enabled = true
}

publishing {
    repositories {
        maven {
            url = uri("http://v.perm.ru:8082/repository/ru.perm.v/")
            isAllowInsecureProtocol = true
            //  publish в nexus "./gradlew publish" из ноута и Jenkins проходит
            // export NEXUS_CRED_USR=admin
            // echo $NEXUS_CRED_USR
            credentials {
                username = System.getenv("NEXUS_CRED_USR")
                password = System.getenv("NEXUS_CRED_PSW")
            }
        }
    }
    publications {
        create<MavenPublication>("maven"){
            artifact(tasks["bootJar"])
        }
    }
}

// use ./gradlew bootRun
springBoot {
    mainClass.set("ru.perm.v.vacancy.VacancyKotlinApplication")
}

// DEMO TASKS
// use ./gradlew myTask1
tasks.register("myTask1") {
    println("echo from myTask1.\nFor run use: ./gradlew myTask1")
}

// use ./gradlew myTask2
tasks.register("myTask2") {
    println("echo from myTask2.\nFor run use: ./gradlew myTask2")
}

// use ./gradlew helloUserCmd
tasks.register("helloUserCmd") {
    println("echo from helloUserCmd.\nFor run use: ./gradlew helloUserCmd")
    val user: String? = System.getenv("USER")
    project.exec {
        commandLine("echo", "Hello,", "$user!")
    }
}

tasks.register("exampleTask") {
    println("exampleTask")
    enabled = false
}

//tasks.named<BootJar>("bootJar") {
//    archiveClassifier.set("")
//}

//tasks.named<Jar>("jar") {
//    archiveClassifier.set("")
//}
