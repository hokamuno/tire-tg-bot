plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.diffplug.spotless") version "6.25.0"

}

group = "ru.azenizzka"
version = "0.0.1-SNAPSHOT"

spotless {
    java {
        googleJavaFormat()
				removeUnusedImports()
    }
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
	implementation("javax.xml.bind:jaxb-api:2.3.1")
	implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")


	implementation("com.google.code.gson:gson:2.8.0")
	implementation("com.squareup.retrofit2:retrofit:2.1.0")
	implementation("com.squareup.retrofit2:converter-gson:2.1.0")
	implementation("com.squareup.retrofit2:converter-jackson:2.11.0")
	implementation("org.jsoup:jsoup:1.18.1")


	implementation("org.telegram:telegrambots:6.9.7.1")

	implementation("joda-time:joda-time:2.13.0")



	compileOnly("org.projectlombok:lombok")

	runtimeOnly("org.postgresql:postgresql")

	annotationProcessor("org.projectlombok:lombok")
}