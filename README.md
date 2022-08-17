[![](https://jitpack.io/v/dev.d1s/ktor-server-liquibase.svg)](https://jitpack.io/#dev.d1s/ktor-server-liquibase)

### Liquibase plugin for Ktor Server

This plugin enables Liquibase migrations for your Ktor application.

### Installation

```kotlin
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    val ktorServerLiquibaseVersion: String by project

    implementation("dev.d1s:ktor-server-liquibase:$ktorServerLiquibaseVersion")
}
```

### Usage

```kotlin
fun Application.configureLiquibase() {
    val dbConnection = openDatabaseConnection()

    install(LiquibaseMigrations) {
        connection = dbConnection
        changeLogPath = "./changelog.json"
    }
}
```
