// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.0-alpha05" apply false
    id("com.android.library") version "8.8.0-alpha05" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.0" apply false
}

// Set JDK compatibility to align with requirements in README
allprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

// Clean task from Kioskable build.gradle
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}