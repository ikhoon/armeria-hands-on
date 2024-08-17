plugins {
    id("idea")
    id("java")
}

group = "armeria-hands-on"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.linecorp.armeria:armeria-bom:1.30.0"))
    implementation("com.linecorp.armeria:armeria")
    implementation("com.linecorp.armeria:armeria-brave6")
    implementation("com.linecorp.armeria:armeria-logback14")
    implementation("com.linecorp.armeria:armeria-prometheus1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
}
