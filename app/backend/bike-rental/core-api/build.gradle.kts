plugins {
	id("org.springframework.boot") version "3.4.0" apply false
	id("io.spring.dependency-management") version "1.1.6"
}

version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

dependencyManagement {
	imports {
		mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
	}
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
	compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.axonframework:axon-modelling:4.9.4")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}