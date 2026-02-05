plugins {
    antlr
}

dependencies {
    api(project(":core"))
    api(project(":app"))
    implementation(project(":xml-io"))

    antlr("org.antlr:antlr4:4.13.2")
    implementation("org.antlr:antlr4-runtime:4.13.2")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor")
}

// Ensure generated ANTLR sources are compiled with main sources
sourceSets {
    main {
        java {
            srcDir(tasks.generateGrammarSource)
        }
    }
}

tasks.named<Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
