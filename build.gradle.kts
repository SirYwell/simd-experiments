plugins {
    java
    id("me.champeau.jmh") version "0.6.5"
}

group = "de.sirywell"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}



java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}




tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
    options.compilerArgs.add("--add-modules=jdk.incubator.vector")
}

tasks.getByName<me.champeau.jmh.JmhBytecodeGeneratorTask>("jmhRunBytecodeGenerator") {
    jvmArgs.add("--enable-preview")
    jvmArgs.add("--add-modules=jdk.incubator.vector")
}

jmh {
    jvmArgs.add("--enable-preview --add-modules=jdk.incubator.vector")
    // profilers.add("perfasm")
}
