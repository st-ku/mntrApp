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
package main.java.com.app.mntr.api.custom.engine;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import main.java.com.app.mntr.Constants;
import main.java.com.app.mntr.api.custom.model.Config;
import main.java.com.app.mntr.api.custom.model.Property;
import main.java.com.app.mntr.api.custom.engine.controller.DataController;
import main.java.com.app.mntr.api.custom.service.DataServiceCustom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The internal implementation of the web server.
 */
public final class Server implements WebServer {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getSimpleName());
    private final HttpsServer httpsServer;

    /**
     * Settings constants for the web server.
     */
    public static final class Settings {

        private Settings() {
            throw new AssertionError(Constants.CREATE_CONSTANT_CLASS_ERROR);
        }

        // The configuration name
        public static final String CONFIG_NAME = "data";
        // The hostname key
        public static final String HOSTNAME = "hostname";
        // The hostname value
        static final String HOSTNAME_VALUE = "localhost";
        // The api path key
        static final String API_PATH = "api-path";
        // The api path value
        static final String API_PATH_VALUE = "/api/";
        // The port key
        public static final String PORT = "port";
        // The port value
        static final int PORT_VALUE = 8000;
        // The backlog key
        public static final String BACKLOG = "backlog";
        // The backlog value
        static final int BACKLOG_VALUE = 0;
        // The key-store-file key
        public static final String KEY_STORE_FILE = "key-store-file";
        // The key-store-file value
        static final String KEY_STORE_FILE_VALUE = "./data/metacfg4j.keystore";
        // The alias key
        public static final String ALIAS = "alias";
        // The alias value
        static final String ALIAS_VALUE = "alias";
        // The store password key
        public static final String STORE_PASSWORD = "store-password";
        // The store password value
        static final String STORE_PASSWORD_VALUE = "password";
        // The key password key
        public static final String KEY_PASSWORD = "key-password";
        // The key password value
        static final String KEY_PASSWORD_VALUE = "password";
    }

    /**
     * Constructs a default web server.
     *
     * @param dataServiceCustom a configuration service.
     * @throws Exception when a web server encounters a problem.
     */
    public Server(final DataServiceCustom dataServiceCustom) throws Exception {
        // Set the default config
        this(new Config.Builder(Settings.CONFIG_NAME, Arrays.asList(
                new Property.Builder(Settings.HOSTNAME, Settings.HOSTNAME_VALUE).build(),
                new Property.Builder(Settings.PORT, Settings.PORT_VALUE).build(),
                new Property.Builder(Settings.BACKLOG, Settings.BACKLOG_VALUE).build(),
                new Property.Builder(Settings.KEY_STORE_FILE, Settings.KEY_STORE_FILE_VALUE).build(),
                new Property.Builder(Settings.ALIAS, Settings.ALIAS_VALUE).build(),
                new Property.Builder(Settings.STORE_PASSWORD, Settings.STORE_PASSWORD_VALUE).build(),
                new Property.Builder(Settings.KEY_PASSWORD, Settings.KEY_PASSWORD_VALUE).build())).build(), dataServiceCustom);
    }

    /**
     * Constructs a web server based on the configuration.
     *
     * @param config        config a configuration of a web server.
     * @param dataServiceCustom a configuration service.
     * @throws Exception when a web server encounters a problem.
     */
    public Server(final Config config, final DataServiceCustom dataServiceCustom) throws Exception {
        // Validate the config
        // Get the hostname
        final String hostname = config.getProperty(Settings.HOSTNAME).
                map(Property::getValue).
                orElse(Settings.HOSTNAME_VALUE);
        // Get the api path
        final String apiPath = config.getProperty(Settings.API_PATH).
                map(Property::getValue).
                orElse(Settings.API_PATH_VALUE);
        // Get the port
        final int port = config.getProperty(Settings.PORT).
                map(property -> (int) property.asLong()).
                orElse(Settings.PORT_VALUE);
        // Get the backlog
        final int backlog = config.getProperty(Settings.BACKLOG).
                map(property -> (int) property.asLong()).
                orElse(Settings.BACKLOG_VALUE);
        // Init the server
        httpsServer = HttpsServer.create(new InetSocketAddress(hostname, port), backlog);
        // Get the data config endpoint
        final String acceptConfigEndpoint = config.getProperty(Constants.Endpoints.DATA).
                map(Property::getValue).
                orElse(Constants.Endpoints.DATA_VALUE);
        final String acceptApi = apiPath + acceptConfigEndpoint;
        httpsServer.createContext(acceptApi,
                new DataController.Builder(acceptApi, dataServiceCustom).build()::handle);
//        // Get the config names endpoint
//        final String configNamesEndpoint = serverConfig.getProperty(Constants.Endpoints.CONFIG_NAMES).
//                map(Property::getValue).
//                orElse(Constants.Endpoints.CONFIG_NAMES_VALUE);
//        httpsServer.createContext(apiPath + configNamesEndpoint,
//                new ConfigNamesController.Builder(configService).build()::handle);
//        // Get the config endpoint
//        final String configEndpoint = serverConfig.getProperty(Constants.Endpoints.CONFIG).
//                map(Property::getValue).
//                orElse(Constants.Endpoints.CONFIG_VALUE);
//        httpsServer.createContext(apiPath + configEndpoint,
//                new ConfigController.Builder(configService).build()::handle);
        httpsServer.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(getSSLContext(config)) {

            /**
             * {@inheritDoc}
             */
            @Override
            public void configure(final HttpsParameters params) {
                try {
                    final SSLContext sslContext = SSLContext.getDefault();
                    final SSLEngine sslEngine = sslContext.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(sslEngine.getEnabledCipherSuites());
                    params.setProtocols(sslEngine.getEnabledProtocols());
                    final SSLParameters defaultSSLParameters = sslContext.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, Constants.Messages.SERVER_CREATE_ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebServer start() {
        httpsServer.start();
        LOGGER.log(Level.INFO, Constants.Messages.SERVER_STARTED);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        httpsServer.stop(0);
        LOGGER.log(Level.INFO, Constants.Messages.SERVER_STOPPED);
    }

    private SSLContext getSSLContext(final Config serverConfig) throws Exception {
        final Optional<Property> keyStoreFile = serverConfig.getProperty(Settings.KEY_STORE_FILE);
        if (!keyStoreFile.isPresent()) {
            throw new Exception(Constants.Messages.CERTIFICATE_LOAD_ERROR);
        }

        final Collection<Throwable> exceptions = new LinkedList<>();
        final KeyStore keyStore = KeyStore.getInstance("JKS");

        try (final FileInputStream fileInputStream = new FileInputStream(keyStoreFile.get().getValue())) {
            serverConfig.getProperty(Settings.STORE_PASSWORD).ifPresent(property -> {
                try {
                    keyStore.load(fileInputStream, property.getValue().toCharArray());
                } catch (final IOException | NoSuchAlgorithmException | CertificateException e) {
                    exceptions.add(e);
                }
            });
        }

        serverConfig.getProperty(Settings.ALIAS).ifPresent(property -> {
            try {
                LOGGER.log(Level.INFO, keyStore.getCertificate(property.getValue()).toString());
            } catch (final KeyStoreException e) {
                exceptions.add(e);
            }
        });

        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        serverConfig.getProperty(Settings.STORE_PASSWORD).ifPresent(property -> {
            try {
                keyManagerFactory.init(keyStore, property.getValue().toCharArray());
            } catch (final KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                exceptions.add(e);
            }
        });

        if (exceptions.size() > 0) {
            throw new Exception(Constants.Messages.CERTIFICATE_LOAD_ERROR);
        }

        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
}
