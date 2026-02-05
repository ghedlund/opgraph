dependencies {
    api(project(":core"))
    api(project(":library"))
    api(project(":xml-io"))
    api("ca.phon:jbreadcrumb:5")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
