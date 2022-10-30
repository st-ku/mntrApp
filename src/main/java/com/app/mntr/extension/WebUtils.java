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
package main.java.com.app.mntr.extension;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.java.com.app.mntr.Constants;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Contains functions for URL/URI processing.
 */
public final class WebUtils {

    private WebUtils() {
        throw new AssertionError(Constants.Messages.CREATE_UTILS_CLASS_ERROR);
    }

    /**
     * Encodes an URL param.
     *
     * @param param   a param to encode.
     * @param charset a charset.
     * @return an encoded param.
     */
    public static String encode(final String param, final Charset charset) {
        try {
            return URLEncoder.encode(param, charset.toString());
        } catch (final Exception e) {
            throw new RuntimeException(Constants.Messages.PARAM_ENCODING_ERROR, e);
        }
    }

    /**
     * Decodes an URL param.
     *
     * @param param   a param to decode.
     * @param charset a charset.
     * @return a decoded param.
     */
    public static String decode(final String param, final Charset charset) {
        try {
            return URLDecoder.decode(param, charset.toString());
        } catch (final Exception e) {
            throw new RuntimeException(Constants.Messages.PARAM_DECODING_ERROR, e);
        }
    }

    /**
     * Returns path params.
     *
     * @param uri an URI with the path.
     * @param api a based API.
     * @return a stream of path params.
     */
    public static Stream<String> getPathParams(final URI uri, final String api) {
        final String path = uri.getPath();
        return path.contains(api) ?
                Arrays.stream(path.substring(api.length() + 1).split("/")).map(param ->
                        decode(param, StandardCharsets.UTF_8)) :
                Stream.empty();
    }

    /**
     * Returns values belong to the param.
     *
     * @param param a param to get a value of.
     * @return a stream of values.
     * @throws JsonException when a parser encounters a problem.
     */
    public static Stream<String> getValues(final String param) throws JsonException {
        final String json = new String(Base64.getDecoder().decode(param), StandardCharsets.UTF_8);
        return ((JsonArray) Jsoner.deserialize(json)).stream().map(Objects::toString);
    }

}
