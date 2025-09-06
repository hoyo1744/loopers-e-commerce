
dependencies {
    // add-ons
    implementation(project(":modules:common-kafka-event"))
    implementation(project(":modules:jpa"))
    implementation(project(":modules:redis"))
    implementation(project(":modules:kafka"))
    implementation(project(":supports:jackson"))
    implementation(project(":supports:logging"))
    implementation(project(":supports:monitoring"))

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api:3.1.0")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // querydsl
    // kapt("com.querydsl:querydsl-apt::jakarta")

    // test-fixtures
    testImplementation(project(":modules:common-kafka-event"))
    testImplementation(testFixtures(project(":modules:jpa")))
    testImplementation(testFixtures(project(":modules:redis")))

}
