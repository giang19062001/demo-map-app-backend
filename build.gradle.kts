import java.util.Properties

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "9.0" // JOOQ
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.vietq"
version = "0.0.1-SNAPSHOT"
description = "Demo map app backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // socket
    implementation("com.corundumstudio.socketio:netty-socketio:2.0.6")

    // jOOQ
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.19.29")
    implementation("org.jooq:jooq-meta:3.19.29")
    implementation("org.jooq:jooq-codegen:3.19.29")

    // MySQL
    implementation("com.mysql:mysql-connector-j:8.3.0")
    jooqGenerator("com.mysql:mysql-connector-j:8.3.0")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

// Detect environment (Local or Prod)
val activeProfile = System.getProperty("spring.profiles.active")
    ?: System.getenv("SPRING_PROFILES_ACTIVE")
    ?: project.findProperty("spring.profiles.active")?.toString()
    ?: "local"

// get local.properties
val isProduction = activeProfile.contains("prod")
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

fun localProp(key: String): String = localProperties.getProperty(key) ?: throw GradleException("Missing '$key' in local.properties")

// JOOQ GENERATE MODEL
jooq {
    version.set("3.19.29")

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = localProp("db.url")
                    user = localProp("db.user")
                    password = localProp("db.password")
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"

                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "demo-map-app"
                        // excludes = "" // skip generate tables
                    }

                    generate.apply {
                        isDeprecated = false
                        isRecords = false
                        isPojos = false
                        isFluentSetters = false
                        isDaos = false
                        isSpringAnnotations = false

                        // conflict
                        isGlobalKeyReferences = false
                        isGlobalTableReferences = false
                        isGlobalSequenceReferences = false
                        isGlobalRoutineReferences = false
                    }

                    target.apply {
                        packageName = "com.study.jooq"
                        directory = "build/generated-src/jooq"  //  generated RECORD class
                    }

                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

// Disable JOOQ generation in Prod
tasks.named("generateJooq") {
    enabled = !isProduction
    doFirst {
        logger.lifecycle("Active Profile: $activeProfile")
        logger.lifecycle("JOOQ Generation: ${if (enabled) "ENABLED" else "DISABLED"}")
    }
}
// Disable JOOQ generation in Prod


// config path of generated source for generate Record Class
sourceSets {
    main {
        java {
            srcDir("src/main/generated/jooq")
        }
    }
}
// config  path of generated source for generate Record Class


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }

    sourceSets {
        main {
            kotlin.srcDir("src/main/generated/jooq")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}