plugins {
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

dependencies {
	runtimeOnly("com.h2database:h2")
	implementation("io.projectreactor:reactor-core")
	testImplementation("io.projectreactor:reactor-test")
	implementation("org.axonframework:axon-spring-boot-starter:4.8.1")
	testImplementation("org.axonframework:axon-test:4.8.1")
	implementation("org.axonframework:axon-micrometer:4.8.1")
	implementation("org.axonframework.firestarter:firestarter-spring-starter:0.0.1")
	implementation("io.axoniq.console:console-framework-client-spring-boot-starter:0.2.1")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	api(project(":core-api"))
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}