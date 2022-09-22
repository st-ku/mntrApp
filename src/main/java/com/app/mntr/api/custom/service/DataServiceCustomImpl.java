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

import main.java.com.app.mntr.api.Config;
import main.java.com.app.mntr.api.custom.model.DataModel;
import main.java.com.app.mntr.api.custom.repository.RepositoryCustom;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
public final class DataServiceCustomImpl implements DataServiceCustom {
    private final RepositoryCustom repositoryCustom;

    private DataServiceCustomImpl(final Builder builder) {
        this.repositoryCustom = builder.repositoryCustom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<DataModel> findAll() {
        return repositoryCustom.findAll();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Stream<String> stream) {
        repositoryCustom.delete(stream);
    }

    @Override
    public void create(Stream<String> models) {
        repositoryCustom.saveAndFlush(models.map(value -> new DataModel(value, new Random().nextInt(100000))));
    }

    /**
     * Wraps and builds the instance of the config service.
     */
    public static final class Builder {
        private final RepositoryCustom repositoryCustom;

        /**
         * Constructs a config service with a required parameter.
         *
         * @param repositoryCustom a config repository.
         */
        public Builder(final RepositoryCustom repositoryCustom) {
            this.repositoryCustom = repositoryCustom;
        }

        /**
         * Builds a config service with a required parameter.
         *
         * @return a builder of the config service.
         */
        public DataServiceCustom build() {
            return new DataServiceCustomImpl(this);
        }
    }
}
