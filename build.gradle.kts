plugins {
    id("java")
}

group = "armeria-hands-on"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.linecorp.armeria:armeria-bom:1.30.0"))
    implementation("com.linecorp.armeria:armeria")
    implementation("com.linecorp.armeria:armeria-logback14")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
}
