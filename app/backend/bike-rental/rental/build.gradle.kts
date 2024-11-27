plugins {
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

val axonVersion by extra("4.9.4")

dependencies {
	implementation(platform("org.axonframework:axon-bom:$axonVersion"))
	implementation("io.axoniq.console:console-framework-client-spring-boot-starter:1.7.3")
	runtimeOnly("org.springframework.boot:spring-boot-docker-compose")

	implementation("org.axonframework:axon-spring-boot-starter")
	implementation("io.axoniq.console:console-framework-client-spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
	testImplementation("org.axonframework:axon-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	api(project(":core-api"))
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}
