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
package main.java.com.app.mntr;

/**
 * Library constants.
 */
public final class Constants {

    public static final String CREATE_CONSTANT_CLASS_ERROR = "Constant class can not be created.";

    private Constants() {
        throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
    }

    /**
     * Mapping constants for the library.
     */
    public final static class Mapping {

        private Mapping() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String CONFIGS_TABLE = "configs";
        public static final String DATA_TABLE = "data";
        public static final String CONFIG_ATTRIBUTES_TABLE = "config-attributes";
        public static final String PROPERTIES_TABLE = "properties";
        public static final String PROPERTY_ATTRIBUTES_TABLE = "property-attributes";
    }

    /**
     * Settings constants for the library.
     */
    public final static class Settings {

        public static final String FETCH_SIZE = "fetch-size";
        public static final String DB_DIALECT = "db-dialect";
        public static final String POSTGRE = "postgre";
        public static final String DEFAULT = "default";

        private Settings() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }
    }

    /**
     * Endpoints constants for the library.
     */
    public final static class Endpoints {

        private Endpoints() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String DATA = "data-endpoint";
        public static final String DATA_VALUE = "data";
    }

    /**
     * Messages constants for the library.
     */
    public final static class Messages {

        private Messages() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String CREATE_FACTORY_CLASS_ERROR = "Factory class can not be created.";
        public static final String CREATE_UTILS_CLASS_ERROR = "Utils class can not be created.";
        public static final String CREATE_HELPER_CLASS_ERROR = "Helper class can not be created.";
        public static final String META_CONFIG_ERROR = "MetaConfig can not be instantiated.";
        public static final String STRING_TO_JSON_ERROR = "String can not be parsed to JSON.";
        public static final String WRONG_ID_VALUE = "Id value must be greater than zero.";
        public static final String WRONG_VERSION_VALUE = "Version value must be greater than zero.";
        public static final String WRONG_UPDATED_VALUE = "Updated value must be greater than zero.";
        public static final String SERVER_STARTED = "Server started.";
        public static final String SERVER_STOPPED = "Server stopped.";
        public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
        public static final String PATH_PARAM_NOT_PRESENT = "Path param is not presented.";
        public static final String DATA_ACCEPTED = "Accepted '%s' data.";
        public static final String CREATE_DATA_TABLE_ERROR = "'Data' table can not be created.";
        public static final String DB_ERROR = "Database error.";
        public static final String DB_ROLLBACK_ERROR = "Database rollback error.";
        public static final String DB_CONNECTION_ERROR = "Database connection error.";
        public static final String SERVER_CREATE_ERROR = "Failed to create HTTPS server.";
        public static final String CERTIFICATE_LOAD_ERROR = "Failed to load the certificate.";
        public static final String PARAM_ENCODING_ERROR = "Param can not be encoded.";
        public static final String PARAM_DECODING_ERROR = "Param can not be decoded.";
    }
}
