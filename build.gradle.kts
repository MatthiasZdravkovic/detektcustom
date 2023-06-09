plugins {
    kotlin("jvm") version "1.8.22"
    `maven-publish`
    id("maven-publish")
}

group = "com.github.MatthiasZdravkovic"
version = "1.0"

dependencies {
    compileOnly("io.gitlab.arturbosch.detekt:detekt-api:1.23.0")

    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.23.0")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

kotlin {
    jvmToolchain(8)
}


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    systemProperty("compile-snippet-tests", project.hasProperty("compile-test-snippets"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
