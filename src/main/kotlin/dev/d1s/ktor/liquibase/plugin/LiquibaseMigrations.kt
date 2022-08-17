/*
 * Copyright 2022 Mikhail Titov and other contributors
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

package dev.d1s.ktor.liquibase.plugin

import io.ktor.server.application.*
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.Scope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.ResourceAccessor
import org.lighthousegames.logging.logging
import java.sql.Connection

private const val CHANGELOG_PATH_PROPERTY = "liquibase.changelog"

private val logger = logging()

public class LiquibaseMigrationsConfiguration {

    internal lateinit var application: Application

    public var connection: Connection? = null

    public var changeLogPath: String? = null
        get() = field ?: application.environment.config.property(CHANGELOG_PATH_PROPERTY).getString()

    public var resourceAccessor: ResourceAccessor = ClassLoaderResourceAccessor()

    public val liquibaseConfig: MutableMap<String, String> = mutableMapOf()

    public val liquibaseContexts: MutableList<String> = mutableListOf()

    public val liquibaseLabels: MutableList<String> = mutableListOf()
}

public val LiquibaseMigrations: ApplicationPlugin<LiquibaseMigrationsConfiguration> =
    createApplicationPlugin("liquibase-migrations", ::LiquibaseMigrationsConfiguration) {
        pluginConfig.application = application

        Scope.child(mapOf()) {
            val connection = requireNotNull(pluginConfig.connection) {
                "Connection not set"
            }

            val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))

            val liquibase = Liquibase(
                requireNotNull(pluginConfig.changeLogPath) {
                    "Changelog path not set"
                }, pluginConfig.resourceAccessor, database
            )

            logger.i {
                "Running Liquibase update"
            }

            liquibase.update(
                Contexts(pluginConfig.liquibaseContexts), LabelExpression(pluginConfig.liquibaseLabels)
            )

            logger.i {
                "Liquibase update ran successfully"
            }
        }
    }