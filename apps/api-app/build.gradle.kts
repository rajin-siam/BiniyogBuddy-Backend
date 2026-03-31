description = "API App - REST API endpoints"

plugins {
    java
    id("org.springframework.boot")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.biniyogbuddy.api.BiniyogBuddyApplication")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":libs:users"))
    implementation(project(":libs:auth"))

    // Spring Web & API
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

