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

import main.java.com.app.mntr.Constants;
import main.java.com.app.mntr.api.custom.model.Config;
import main.java.com.app.mntr.api.custom.service.DataServiceCustom;

/**
 * Provides factory methods to create a web server.
 */
public final class WebServers {

    private WebServers() {
        throw new AssertionError(Constants.Messages.CREATE_FACTORY_CLASS_ERROR);
    }


    /**
     * Returns a default web server.
     *
     * @return a web server.
     * @throws Exception when a web server encounters a problem.
     */
    public static WebServer newServer(final DataServiceCustom dataServiceCustom) throws Exception {
        return new main.java.com.app.mntr.api.custom.engine.Server(dataServiceCustom);
    }

    /**
     * Returns a web server based on the configuration.
     *
     * @param config        config a configuration of a web server.
     * @param configService a configuration service.
     * @return a web server.
     * @throws Exception when a web server encounters a problem.
     */
    public static WebServer newServer(final Config config, final DataServiceCustom configService) throws Exception {
        return new main.java.com.app.mntr.api.custom.engine.Server(config, configService);
    }
}
