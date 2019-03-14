
kotlin.sourceSets {
    val commonMain by getting {
        dependencies {
            api(project(":ktor-client:ktor-client-features:ktor-client-json"))
        }
    }
}
