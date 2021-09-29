val springVersion = "2.5.5"
val lombokVersion = "1.18.20"
val reactorVersion = "3.4.10"
val jacksonVersion = "2.12.5"

plugins {
    java
    id("org.springframework.boot") version "2.5.5"
}

group = "com.project."
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.springframework.boot:spring-boot-starter:$springVersion")
    implementation("io.projectreactor:reactor-core:$reactorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}