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
package main.java.com.app.mntr.api.custom;

import main.java.com.app.mntr.api.Config;
import main.java.com.app.mntr.api.custom.model.DataModel;
import main.java.com.app.mntr.api.custom.repository.DbRepositoryCustom;
import main.java.com.app.mntr.api.custom.repository.RepositoryCustom;
import main.java.com.app.mntr.api.custom.service.DataServiceCustom;
import main.java.com.app.mntr.api.custom.service.DataServiceCustomImpl;
import main.java.com.app.mntr.engine.web.WebServer;
import main.java.com.app.mntr.extension.Validator;

import javax.sql.DataSource;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static main.java.com.app.mntr.Constants.Messages.META_CONFIG_ERROR;

/**
 * The core configuration class that provides the functionality.
 */
public final class MetaConfigCustom implements Closeable {
    private final WebServer webServer;
    private final DataServiceCustom dataServiceCustom;

    private MetaConfigCustom(final WebServer webServer, final DataServiceCustom dataServiceCustom) {
        this.webServer = webServer;
        this.dataServiceCustom = dataServiceCustom;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Stop the web server
        if (webServer != null) {
            webServer.stop();
        }
    }

    /**
     * Wraps and builds the instance of the core configuration class.
     */
    public final static class Builder {
        private Config webClient;
        private Config webConfig;
        private Map<String, String> dataMapping;
        private Map<String, Object> dbSettings;
        private DataSource dataSource;
        private boolean isDefaultConfig;

        /**
         * The default constructor.
         */
        public Builder() {
            this.isDefaultConfig = false;
        }

        /**
         * Constructs the core configuration class with the configuration of a web client.
         *
         * @param config a configuration a web client.
         * @return a builder of the core configuration class.
         */
        public Builder webClient(final Config config) {
            this.webClient = Validator.of(config).get();
            return this;
        }

        /**
         * Constructs the core configuration class with the configuration of a web server.
         *
         * @param config a configuration a web server.
         * @return a builder of the core configuration class.
         */
        public Builder webServer(final Config config) {
            this.webConfig = Validator.of(config).get();
            return this;
        }

        /**
         * Constructs the core configuration class with the custom mapping.
         *
         * @param mapping a table mapping.
         * @return a builder of the core configuration class.
         */
        public Builder dataMapping(final Map<String, String> mapping) {
            this.dataMapping = Validator.of(mapping).get();
            return this;
        }

        /**
         * Constructs the core configuration class with the custom DB settings.
         *
         * @param settings DB settings.
         * @return a builder of the core configuration class.
         */
        public Builder dbSettings(final Map<String, Object> settings) {
            this.dbSettings = Validator.of(settings).get();
            return this;
        }

        /**
         * Constructs the core configuration class with an existed data source.
         *
         * @param dataSource a data source.
         * @return a builder of the core configuration class.
         */
        public Builder dataSource(final DataSource dataSource) {
            this.dataSource = Validator.of(dataSource).get();
            return this;
        }

        /**
         * Constructs the core configuration with the default configuration.
         *
         * @return a builder of the core configuration class.
         */
        public Builder defaultConfig() {
            this.isDefaultConfig = true;
            return this;
        }

        /**
         * Builds the core configuration class with parameters.
         *
         * @return a builder of the core configuration class.
         */
        public MetaConfigCustom build() {
            try {
                // init a mapping
                final Map<String, String> mapping = dataMapping != null ? dataMapping : new HashMap<>();
                // init settings
                final Map<String, Object> settings = dbSettings != null ? dbSettings : new HashMap<>();
                // Init the repository
                final RepositoryCustom repositoryCustom =
                        new DbRepositoryCustom.Builder(dataSource).mapping(mapping).settings(settings).build();
                // Init the config service
                final  DataServiceCustom dataServiceCustom = new DataServiceCustomImpl.Builder(repositoryCustom).build();
                // Init the web server
                WebServer webServer = null;
                if (isDefaultConfig) {
                    webServer = main.java.com.app.mntr.api.custom.engine.WebServers.newServer(dataServiceCustom).start();
                } else if (webConfig != null) {
                    webServer = main.java.com.app.mntr.api.custom.engine.WebServers.newServer(webConfig, dataServiceCustom).start();
                }
                // Create the main instance
                return new MetaConfigCustom(webServer, dataServiceCustom);
            } catch (final Exception e) {
                throw new RuntimeException(META_CONFIG_ERROR, e);
            }
        }
    }
}
