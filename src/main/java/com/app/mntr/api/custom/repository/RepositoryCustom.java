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

import java.util.stream.Stream;

/**
 * Provides repository methods to create, read, update and delete operations.
 */
public interface RepositoryCustom {

    /**
     * Returns all configuration names.
     *
     * @return a stream of configuration names.
     */
    Stream<DataModel> findAll();


    /**
     * Saves and flushes configuration models.
     *
     * @param stream a stream of configuration models.
     * @return a stream of updated configuration models.
     */
    void saveAndFlush(final Stream<DataModel> dataModels);

    /**
     * Deletes configuration models.
     *
     * @param stream a stream of names.
     * @return a number of deleted models.
     */
    void delete(final Stream<String> stream);
}
