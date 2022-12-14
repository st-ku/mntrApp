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

import main.java.com.app.mntr.api.custom.engine.OperationResponse;

import main.java.com.app.mntr.api.custom.engine.exception.InternalServerErrorException;
import main.java.com.app.mntr.api.custom.engine.exception.InvalidRequestException;
import main.java.com.app.mntr.api.custom.engine.exception.MethodNotAllowedException;
import main.java.com.app.mntr.api.custom.engine.exception.ResourceNotFoundException;
import main.java.com.app.mntr.api.custom.service.DataServiceCustom;
import main.java.com.app.mntr.extension.Validator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static main.java.com.app.mntr.api.custom.engine.Constants.Header.APPLICATION_JSON;

/**
 * Provides a basic functionality for all controllers.
 */
abstract class AbstractController {
    private final static Logger LOGGER = Logger.getLogger(AbstractController.class.getSimpleName());
    final String apiPath;
    final DataServiceCustom configService;

    AbstractController(final AbstractBuilder abstractBuilder) {
        this.apiPath = abstractBuilder.apiPath;
        this.configService = abstractBuilder.configService;
    }

    /**
     * Handles a referred context.
     *
     * @param httpExchange a http exchange.
     * @see com.sun.net.httpserver.HttpExchange for more information.
     */
    public void handle(final HttpExchange httpExchange) {
        try {
            execute(httpExchange);
        } catch (final Exception e) {
            handle(httpExchange, e);
        } finally {
            httpExchange.close();
        }
    }

    /**
     * The main method that handles a context.
     *
     * @param httpExchange a http exchange.
     * @throws java.io.IOException when a controller encounters a problem.
     * @see com.sun.net.httpserver.HttpExchange for more information.
     */
    abstract void execute(final HttpExchange httpExchange) throws IOException;

    /**
     * Writes an operation response.
     *
     * @param httpExchange a http exchange.
     * @param response     an operation response.
     * @param <T>          a type of result.
     * @throws java.io.IOException when a controller encounters a problem.
     * @see com.sun.net.httpserver.HttpExchange for more information.
     */
    <T> void writeResponse(final HttpExchange httpExchange, final OperationResponse<T> response) throws IOException {
        try {
            httpExchange.getResponseHeaders().put("Content-Type", Collections.singletonList(APPLICATION_JSON));
            final byte[] jsonBytes = response.toJson().getBytes();
            httpExchange.sendResponseHeaders(HTTP_OK, jsonBytes.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(jsonBytes);
            outputStream.flush();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InvalidRequestException(HTTP_BAD_REQUEST, e.getMessage());
        }
    }

    private void handle(final HttpExchange httpExchange, final Throwable throwable) {
        try {
            LOGGER.log(Level.WARNING, throwable.getMessage());
            throwable.printStackTrace();
            final OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(getErrorResponse(throwable, httpExchange).toJson().getBytes());
            responseBody.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, throwable.getMessage());
            e.printStackTrace();
        }
    }

    private <T> OperationResponse<T> getErrorResponse(final Throwable throwable, final HttpExchange httpExchange)
            throws IOException {
        final OperationResponse<T> response = new OperationResponse.Builder<T>().error(throwable.getMessage()).build();
        if (throwable instanceof InvalidRequestException) {
            final InvalidRequestException exception = (InvalidRequestException) throwable;
            httpExchange.sendResponseHeaders(exception.getCode(), 0);
        } else if (throwable instanceof ResourceNotFoundException) {
            final ResourceNotFoundException exception = (ResourceNotFoundException) throwable;
            httpExchange.sendResponseHeaders(exception.getCode(), 0);
        } else if (throwable instanceof MethodNotAllowedException) {
            final MethodNotAllowedException exception = (MethodNotAllowedException) throwable;
            httpExchange.sendResponseHeaders(exception.getCode(), 0);
        } else {
            if (throwable instanceof InternalServerErrorException) {
                httpExchange.sendResponseHeaders(((InternalServerErrorException) throwable).getCode(), 0);
            } else {
                httpExchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, 0);
            }
        }
        return response;
    }

    /**
     * Wraps and builds instances of controllers.
     */
    static abstract class AbstractBuilder {
        private final String apiPath;
        private final DataServiceCustom configService;

        /**
         * Constructs a controller with the configuration service param.
         *
         * @param apiPath       an api path.
         * @param configService a configuration service.
         */
        AbstractBuilder(final String apiPath, final DataServiceCustom configService) {
            this.apiPath = Validator.of(apiPath).get();
            this.configService = Validator.of(configService).get();
        }

        /**
         * Builds a controller with the required parameter.
         *
         * @return a builder of the controller.
         */
        abstract AbstractController build();
    }
}
