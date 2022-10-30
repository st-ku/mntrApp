/* Copyright 2019-2022 Andrey Karazhev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package main.java.com.app.mntr.api.custom.repository;

import main.java.com.app.mntr.api.custom.model.DataModel;
import main.java.com.app.mntr.extension.Validator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static main.java.com.app.mntr.Constants.Mapping.CONFIGS_TABLE;
import static main.java.com.app.mntr.Constants.Mapping.CONFIG_ATTRIBUTES_TABLE;
import static main.java.com.app.mntr.Constants.Mapping.DATA_TABLE;
import static main.java.com.app.mntr.Constants.Mapping.PROPERTIES_TABLE;
import static main.java.com.app.mntr.Constants.Mapping.PROPERTY_ATTRIBUTES_TABLE;
import static main.java.com.app.mntr.Constants.Messages.CREATE_DATA_TABLE_ERROR;
import static main.java.com.app.mntr.Constants.Messages.DB_CONNECTION_ERROR;
import static main.java.com.app.mntr.Constants.Messages.DB_ERROR;
import static main.java.com.app.mntr.Constants.Messages.DB_ROLLBACK_ERROR;
import static main.java.com.app.mntr.Constants.Settings.DB_DIALECT;
import static main.java.com.app.mntr.Constants.Settings.DEFAULT;
import static main.java.com.app.mntr.Constants.Settings.FETCH_SIZE;
import static main.java.com.app.mntr.Constants.Settings.POSTGRE;

/**
 * {@inheritDoc}
 */
public final class DbRepositoryCustom implements RepositoryCustom {
    private final DataSource dataSource;

    public DbRepositoryCustom(DataSource dataSource) {
        this.dataSource = dataSource;
        DbRepositoryCustom.JDBCUtils.createDataBase(this.dataSource);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<DataModel> findAll() {
        List<DataModel> dataModels = new LinkedList<>();
        try {
            final String sql = "SELECT * FROM " + DATA_TABLE;
            try (final Connection connection = dataSource.getConnection();
                 final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    dataModels.add(new DataModel(resultSet.getString("value"), Long.parseLong(resultSet.getString("id"))));
                }
                return dataModels.stream();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAndFlush(final Stream<DataModel> stream) {
        Connection connection = null;
        try {
            connection = JDBCUtils.open(dataSource);
            saveAndFlush(connection, stream.toArray(DataModel[]::new));
        } catch (final SQLException e) {
            JDBCUtils.rollback(connection, e);
        } finally {
            JDBCUtils.close(connection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Stream<String> ids) {
        Connection connection = null;
        try {
            connection = JDBCUtils.open(dataSource);
            delete(connection, ids);
        } catch (final SQLException e) {
            JDBCUtils.rollback(connection, e);
        } finally {
            JDBCUtils.close(connection);
        }
    }

    private DataModel[] saveAndFlush(final Connection connection, final DataModel[] data) throws SQLException {
        Statement query = connection.createStatement();
        Arrays.stream(data).forEach(d -> {
            try {
                query.execute(d.toCreateSql());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return data;
    }

    private void delete(final Connection connection, final Stream<String> ids) throws SQLException {
        Statement query = connection.createStatement();
        ids.forEach(d -> {
            try {
                query.execute("DELETE FROM " + DATA_TABLE + " WHERE id = " + d);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static final class JDBCUtils {

        private static void createDataBase(final DataSource dataSource) {
            Connection connection = null;
            try {
                connection = JDBCUtils.open(dataSource);
                createTables(connection);
            } catch (final SQLException e) {
                JDBCUtils.rollback(connection, e);
            } finally {
                JDBCUtils.close(connection);
            }
        }

        private static void createTables(final Connection connection)
                throws SQLException {
            try {
                try (final Statement statement = connection.createStatement()) {
                    statement.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS `Data` (
                                `id`         INTEGER  PRIMARY KEY AUTO_INCREMENT,
                                 `value` VARCHAR(50) NOT NULL);
                                 """);
                    connection.commit();
                }
            } catch (final SQLException e) {
                throw new SQLException(CREATE_DATA_TABLE_ERROR, e);
            }
        }

        private static Connection open(final DataSource dataSource) throws SQLException {
            final Connection connection = Validator.of(dataSource).get().getConnection();
            connection.setAutoCommit(false);
            return connection;
        }

        private static void rollback(final Connection connection, final SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (final SQLException ex) {
                    throw new RuntimeException(DB_ROLLBACK_ERROR, e);
                }
                throw new RuntimeException(DB_ERROR, e);
            }
        }

        private static void close(final Connection connection) {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (final SQLException e) {
                    throw new RuntimeException(DB_CONNECTION_ERROR);
                }
            }
        }
    }
    public final static class Builder {
        private final DataSource dataSource;
        private Map<String, String> mapping;
        private Map<String, Object> settings;

        /**
         * Constructs a DB config repository with a required parameter.
         *
         * @param dataSource a datasource.
         */
        public Builder(final DataSource dataSource) {
            this.dataSource = Validator.of(dataSource).get();
        }

        /**
         * Constructs a DB config repository with a mapping.
         *
         * @param mapping a table mapping.
         * @return a builder of the DB config repository.
         */
        public DbRepositoryCustom.Builder mapping(final Map<String, String> mapping) {
            this.mapping = Validator.of(mapping).
                    validate(m -> validate(m, CONFIGS_TABLE), CONFIGS_TABLE + " mapping is wrong.").
                    validate(m -> validate(m, CONFIG_ATTRIBUTES_TABLE), CONFIG_ATTRIBUTES_TABLE +
                            " mapping is wrong.").
                    validate(m -> validate(m, PROPERTIES_TABLE), PROPERTIES_TABLE + " mapping is wrong.").
                    validate(m -> validate(m, PROPERTY_ATTRIBUTES_TABLE), PROPERTY_ATTRIBUTES_TABLE +
                            " mapping is wrong.").get();
            return this;
        }

        /**
         * Constructs a DB config repository with settings.
         *
         * @param settings DB settings.
         * @return a builder of the DB config repository.
         */
        public DbRepositoryCustom.Builder settings(final Map<String, Object> settings) {
            this.settings = Validator.of(settings).
                    validate(m -> {
                        if (settings.containsKey(FETCH_SIZE)) {
                            final Object value = settings.get(FETCH_SIZE);
                            return value instanceof Integer;
                        }

                        if (settings.containsKey(DB_DIALECT)) {
                            final Object value = settings.get(DB_DIALECT);
                            return POSTGRE.equals(value) || DEFAULT.equals(value);
                        }

                        return true;
                    }, FETCH_SIZE + " setting is wrong.").get();
            return this;
        }

        /**
         * Builds a DB config repository with a required parameter.
         *
         * @return a builder of the DB config repository.
         */
        public RepositoryCustom build() {
            return new DbRepositoryCustom(dataSource);
        }

        private boolean validate(final Map<String, String> mapping, final String key) {
            if (mapping.containsKey(key)) {
                final String configs = mapping.get(key);
                return configs != null && configs.length() > 0;
            }

            return true;
        }
    }

}
