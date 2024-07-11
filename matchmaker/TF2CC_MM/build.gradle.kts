import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
}

group = "com.github.dosmike"
version = "24w28a"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.jetbrains:annotations:24.0.0")

    implementation("org.java-websocket:Java-WebSocket:1.5.6")

    implementation(platform("org.apache.logging.log4j:log4j-bom:2.23.1"))
    implementation("org.slf4j:slf4j-api:2.0.13")
    //implementation("org.slf4j:slf4j-reload4j:2.0.13")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")

    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")

    implementation("org.apache.commons:commons-compress:1.26.1")

    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
}

tasks.create("fatJar", Jar::class) {
    group = "my tasks" // OR, for example, "build"
    description = "Creates a self-contained fat JAR of the application that can be run."
    manifest.attributes["Main-Class"] = "com.github.dosmike.tf2cc.Main"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    exclude("org/slf4j/impl/*")
    with(tasks.jar.get())
}

tasks {
    "build" {
        dependsOn("fatJar")
    }
}

tasks.test {
    useJUnitPlatform()
}