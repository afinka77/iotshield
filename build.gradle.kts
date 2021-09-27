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
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    implementation("org.springframework.boot:spring-boot-starter:2.5.4")

    testCompileOnly("org.projectlombok:lombok:1.18.20")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.20")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.4")
    //testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}