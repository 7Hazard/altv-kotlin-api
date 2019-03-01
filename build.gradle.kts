import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "1.3.21"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    //implementation(kotlin("reflect"))
    
    // https://mvnrepository.com/artifact/com.github.jnr/jnr-ffi
    //compile("com.github.jnr:jnr-ffi:2.1.9")

    compile(project("altv-jvm-module"))
}

tasks.withType<Jar>() {
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }

    manifest {
        attributes["Main-Class"] = "alt.v.kotlin.MainKt"
    }
}
