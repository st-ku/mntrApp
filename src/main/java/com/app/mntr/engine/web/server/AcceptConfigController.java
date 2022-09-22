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
package main.java.com.app.mntr.engine.web.server;

import main.java.com.app.mntr.engine.web.Constants;
import main.java.com.app.mntr.api.ConfigService;
import com.sun.net.httpserver.HttpExchange;
import main.java.com.app.mntr.extension.WebUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * Provides a handler functionality for the POST accept method.
 */
final class AcceptConfigController extends AbstractController {
    private final static Logger LOGGER = Logger.getLogger(AcceptConfigController.class.getSimpleName());

    private AcceptConfigController(final Builder builder) {
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
                            configService.accept(WebUtils.getValues(param));
                            final String result = String.format(main.java.com.app.mntr.Constants.Messages.CONFIG_ACCEPTED, param);
                            return new OperationResponse.Builder<String>().result(result).build();
                        } catch (final Exception e) {
                            LOGGER.log(Level.SEVERE, e.toString());
                            return new OperationResponse.Builder<String>().error(main.java.com.app.mntr.Constants.Messages.STRING_TO_JSON_ERROR).build();
                        }
                    }).
                    orElseGet(() -> new OperationResponse.Builder<String>().error(main.java.com.app.mntr.Constants.Messages.PATH_PARAM_NOT_PRESENT).build());
            writeResponse(httpExchange, response);
        } else {
            throw new MethodNotAllowedException(HTTP_BAD_METHOD, main.java.com.app.mntr.Constants.Messages.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Wraps and builds the instance of the accept controller.
     */
    final static class Builder extends AbstractBuilder {

        /**
         * Constructs a controller with the configuration service param.
         *
         * @param apiPath       an api path.
         * @param configService a configuration service.
         */
        Builder(final String apiPath, final ConfigService configService) {
            super(apiPath, configService);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        AcceptConfigController build() {
            return new AcceptConfigController(this);
        }
    }
}
