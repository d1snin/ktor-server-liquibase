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

### License

```
   Copyright 2022-2023 Mikhail Titov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```