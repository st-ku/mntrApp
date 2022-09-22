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
package main.java.com.app.mntr.api.custom.service;

import main.java.com.app.mntr.api.custom.model.DataModel;

import java.util.stream.Stream;

/**
 * Provides service methods to create, read, update and delete operations.
 */
public interface DataServiceCustom {
    /**
     * Returns all configuration names.
     *
     * @return a stream of configuration names.
     */
    Stream<DataModel> findAll();

    /**
     * Deletes configuration models.
     *
     * @param stream a stream of names.
     * @return a number of deleted models.
     */
    void delete(final Stream<String> stream);

    void create(Stream<String> models);
}
