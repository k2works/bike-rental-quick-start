plugins {
    java
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "buildlogic.java-application-conventions")
    apply(plugin = "buildlogic.java-common-conventions")
    apply(plugin = "buildlogic.java-library-conventions")
}

subprojects {
    group = "io.axoniq.demo.bikerental"

    repositories {
        mavenCentral()
    }

    dependencies {
        runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}