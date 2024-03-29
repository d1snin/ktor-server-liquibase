/*
 * Copyright 2022-2024 Mikhail Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

rootProject.name = "ktor-server-liquibase"

pluginManagement {
    plugins {
        val kotlinVersion: String by settings

        val dokkaVersion: String by settings

        val versionsPluginVersion: String by settings

        kotlin("jvm") version kotlinVersion

        id("org.jetbrains.dokka") version dokkaVersion

        id("com.github.ben-manes.versions") version versionsPluginVersion
    }
}