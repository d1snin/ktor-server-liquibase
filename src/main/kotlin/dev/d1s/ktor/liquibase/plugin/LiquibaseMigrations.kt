/*
 * Copyright 2022-2023 Mikhail Titov
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
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.ResourceAccessor
import org.lighthousegames.logging.logging
import java.sql.Connection

private const val CHANGELOG_PATH_PROPERTY = "liquibase.changelog"

private val logger = logging()

/**
 * Liquibase migrations plugin. As soon as applied it tries to perform a migration using [the supplied configuration][LiquibaseMigrationsConfiguration].
 */
public val LiquibaseMigrations: ApplicationPlugin<LiquibaseMigrationsConfiguration> =
    createApplicationPlugin("liquibase-migrations", ::LiquibaseMigrationsConfiguration) {
        pluginConfig.application = application

        withScope {
            val connection = pluginConfig.requireConnection()
            val database = getDatabaseInstance(connection)
            val liquibase = Liquibase(pluginConfig.changeLogPath!!, pluginConfig.resourceAccessor, database)

            logger.i {
                "Running Liquibase update"
            }

            liquibase.runUpdate(pluginConfig.liquibaseContexts, pluginConfig.liquibaseLabels)

            logger.i {
                "Liquibase update ran successfully"
            }
        }
    }

/**
 * Liquibase plugin configuration. The only required properties are [connection] and [changeLogPath]. The migration will fail if they're not set.
 */
public class LiquibaseMigrationsConfiguration {

    internal lateinit var application: Application

    /**
     * Database connection. Must be set.
     */
    public var connection: Connection? = null

    /**
     * Changelog file path. Could be set either using [LiquibaseMigrationsConfiguration] directly or through `liquibase.changelog` property.
     */
    public var changeLogPath: String? = null
        get() = field ?: application.environment.config.property(CHANGELOG_PATH_PROPERTY).getString()

    /**
     * Resource accessor implementation used to load the changelog file. The default implementation is [ClassLoaderResourceAccessor].
     */
    public var resourceAccessor: ResourceAccessor = ClassLoaderResourceAccessor()

    public val liquibaseConfig: MutableMap<String, String> = mutableMapOf()

    public val liquibaseContexts: MutableList<String> = mutableListOf()

    public val liquibaseLabels: MutableList<String> = mutableListOf()

    internal fun requireConnection() = requireNotNull(connection) {
        "Connection not set"
    }

    
}

private inline fun withScope(crossinline block: () -> Unit) {
    Scope.child(mapOf()) {
        block()
    }
}

private fun getDatabaseInstance(connection: Connection): Database {
    val jdbcConnection = JdbcConnection(connection)
    val databaseFactory = DatabaseFactory.getInstance()

    return databaseFactory.findCorrectDatabaseImplementation(jdbcConnection)
}

private fun Liquibase.runUpdate(liquibaseContexts: List<String>, liquibaseLabels: List<String>) {
    val contexts = Contexts(liquibaseContexts)
    val labelExpression = LabelExpression(liquibaseLabels)

    update(contexts, labelExpression)
}