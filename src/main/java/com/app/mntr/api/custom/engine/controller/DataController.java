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
package main.java.com.app.mntr.api.custom.engine.controller;

import com.sun.net.httpserver.HttpExchange;
import main.java.com.app.mntr.api.custom.engine.Constants;
import main.java.com.app.mntr.api.custom.engine.OperationResponse;
import main.java.com.app.mntr.api.custom.engine.exception.MethodNotAllowedException;
import main.java.com.app.mntr.api.custom.model.DataModel;
import main.java.com.app.mntr.api.custom.service.DataServiceCustom;
import main.java.com.app.mntr.extension.WebUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static main.java.com.app.mntr.api.custom.engine.Constants.Method.GET;

/**
 * Provides a handler functionality for the POST accept method.
 */
public final class DataController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(DataController.class.getSimpleName());

    private DataController(final Builder builder) {
        super(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void execute(final HttpExchange httpExchange) throws IOException {
        if (Constants.Method.POST.equals(httpExchange.getRequestMethod())) {
            final OperationResponse<String> response = WebUtils.getPathParams(httpExchange.getRequestURI(), apiPath).
                    findAny().
                    map(param -> {
                        try {
                            configService.create(WebUtils.getValues(param));
                            final String result = String.format(main.java.com.app.mntr.Constants.Messages.DATA_ACCEPTED, param);
                            return new OperationResponse.Builder<String>().result(result).build();
                        } catch (final Exception e) {
                            LOGGER.log(Level.SEVERE, e.toString());
                            return new OperationResponse.Builder<String>().error(main.java.com.app.mntr.Constants.Messages.STRING_TO_JSON_ERROR).build();
                        }
                    }).
                    orElseGet(() -> new OperationResponse.Builder<String>().error(main.java.com.app.mntr.Constants.Messages.PATH_PARAM_NOT_PRESENT).build());
            writeResponse(httpExchange, response);
        }
        if (GET.equals(httpExchange.getRequestMethod())) {
            main.java.com.app.mntr.api.custom.engine.OperationResponse<Collection<DataModel>> response;
            try {
                final Collection<DataModel> configs =
                        configService.findAll().toList();
                response = new main.java.com.app.mntr.api.custom.engine.OperationResponse.Builder<Collection<DataModel>>().result(configs).build();
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, e.toString());
                response = new main.java.com.app.mntr.api.custom.engine.OperationResponse.Builder<Collection<DataModel>>().error(main.java.com.app.mntr.Constants.Messages.STRING_TO_JSON_ERROR).build();
            }
            writeResponse(httpExchange, response);
        }
        if (Constants.Method.DELETE.equals(httpExchange.getRequestMethod())) {
            final OperationResponse<String> response = WebUtils.getPathParams(httpExchange.getRequestURI(), apiPath).
                    findAny().
                    map(param -> {
                        try {
                            configService.delete(WebUtils.getValues(param));
                            final String result = String.format(main.java.com.app.mntr.Constants.Messages.DATA_ACCEPTED, param);
                            return new OperationResponse.Builder<String>().result(result).build();
                        } catch (final Exception e) {
                            LOGGER.log(Level.SEVERE, e.toString());
                            return new OperationResponse.Builder<String>().error(main.java.com.app.mntr.Constants.Messages.STRING_TO_JSON_ERROR).build();
                        }
                    }).
                    orElseGet(() -> new OperationResponse.Builder<String>().error(main.java.com.app.mntr.Constants.Messages.PATH_PARAM_NOT_PRESENT).build());
            writeResponse(httpExchange, response);
        }
        else {
            throw new MethodNotAllowedException(HTTP_BAD_METHOD, main.java.com.app.mntr.Constants.Messages.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Wraps and builds the instance of the accept controller.
     */
    public static final class Builder extends AbstractBuilder {

        /**
         * Constructs a controller with the configuration service param.
         *
         * @param apiPath           an api path.
         * @param dataServiceCustom a configuration service.
         */
        public Builder(final String apiPath, final DataServiceCustom dataServiceCustom) {
            super(apiPath, dataServiceCustom);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DataController build() {
            return new DataController(this);
        }
    }
}
