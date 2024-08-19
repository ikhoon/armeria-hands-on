plugins {
    id("idea")
    id("java")
    id("com.google.protobuf") version "0.9.4"
}

group = "armeria-hands-on"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.linecorp.armeria:armeria-bom:1.30.0"))
    implementation("com.linecorp.armeria:armeria")
    implementation("com.linecorp.armeria:armeria-brave6")
    implementation("com.linecorp.armeria:armeria-grpc")
    implementation("com.linecorp.armeria:armeria-logback14")
    implementation("com.linecorp.armeria:armeria-prometheus1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

protobuf {
    // Configure the protoc executable.
    protoc {
        // Download from the repository.
        artifact = "com.google.protobuf:protoc:3.25.1"
    }

    // Locate the codegen plugins.
    plugins {
        // Locate a plugin with name 'grpc'.
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.65.0"
        }
    }
    generateProtoTasks {
        all().configureEach {
            plugins {
                create("grpc")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
