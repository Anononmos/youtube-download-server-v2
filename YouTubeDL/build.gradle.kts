plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

repositories {
	mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
	implementation("me.paulschwarz:spring-dotenv:4.0.0")
	implementation("commons-io:commons-io:2.18.0")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.0.6")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
  	implementation("org.springframework.boot:spring-boot-starter-jdbc")
  	implementation("org.springframework.boot:spring-boot-starter-web")
  	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.postgresql:postgresql")
	// runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework:spring-webflux")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
}
