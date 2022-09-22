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
package main.java.com.app.mntr.api.custom.model;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

import java.io.IOException;
import java.io.Writer;

/**
 * The configuration model that contains parameters, attributes and properties.
 */
public final class DataModel implements Jsonable {

    private final long id;
    private final String value;

    public DataModel(String value, long id) {
        this.value = value;
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public String toJson() {
        final JsonObject json = new JsonObject();
        json.put("value", value);
        json.put("id", id);
        return json.toJson();
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        final JsonObject json = new JsonObject();
        json.put("value", value);
        json.put("id", id);
        json.toJson(writable);
    }

    public String toCreateSql() {
        return "INSERT INTO data VALUES (" + id + ", '" + value + "')";
    }

    public String toDeleteSql() {
        return "DELETE FROM data WHERE id = " + id;
    }



}

