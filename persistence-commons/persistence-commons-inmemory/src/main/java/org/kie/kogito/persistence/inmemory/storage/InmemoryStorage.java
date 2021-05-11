/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.inmemory.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.inmemory.query.InmemoryQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.kie.kogito.persistence.inmemory.JsonUtils.OBJECT_MAPPER;

public class InmemoryStorage<V> implements Storage<String, V> {

    private Map<String, StorageEntry<V>> storage;

    private String rootType;

    private List<Consumer<V>> entryCreatedListeners;

    private List<Consumer<V>> entryUpdatedListeners;

    private List<Consumer<String>> entryRemovedListeners;

    public InmemoryStorage(String rootType) {
        this.rootType = rootType;
        this.storage = new ConcurrentHashMap<>();
        this.entryCreatedListeners = new ArrayList<>();
        this.entryUpdatedListeners = new ArrayList<>();
        this.entryRemovedListeners = new ArrayList<>();
    }

    @Override
    public void addObjectCreatedListener(Consumer<V> consumer) {
        this.entryCreatedListeners.add(consumer);
    }

    @Override
    public void addObjectUpdatedListener(Consumer<V> consumer) {
        this.entryUpdatedListeners.add(consumer);
    }

    @Override
    public void addObjectRemovedListener(Consumer<String> consumer) {
        this.entryRemovedListeners.add(consumer);
    }

    @Override
    public Query<V> query() {
        return new InmemoryQuery<>(Collections.unmodifiableMap(this.storage));
    }

    @Override
    public V get(String key) {
        return this.storage.get(key).getValue();
    }

    @Override
    public V put(String key, V value) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.set(CACHE_KEY, OBJECT_MAPPER.convertValue(key, JsonNode.class));
        node.set(CACHE_DATA, OBJECT_MAPPER.valueToTree(value));

        StorageEntry<V> entry = this.storage.put(key, new StorageEntry<>(value, node));

        Optional.ofNullable(entry).ifPresentOrElse(
                e -> this.entryUpdatedListeners.forEach(c -> c.accept(value)),
                () -> this.entryCreatedListeners.forEach(c -> c.accept(value)));

        return Optional.ofNullable(entry).map(StorageEntry::getValue).orElse(null);
    }

    @Override
    public V remove(String key) {
        StorageEntry<V> entry = this.storage.remove(key);
        this.entryRemovedListeners.forEach(c -> c.accept(key));
        return Optional.ofNullable(entry).map(StorageEntry::getValue).orElse(null);
    }

    @Override
    public boolean containsKey(String key) {
        return this.storage.containsKey(key);
    }

    @Override
    public Map<String, V> entries() {
        return this.storage.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
    }

    @Override
    public void clear() {
        this.storage.clear();
    }

    @Override
    public String getRootType() {
        return this.rootType;
    }

    public static final String CACHE_KEY = "cache_key";
    public static final String CACHE_DATA = "cache_data";

    public static class StorageEntry<V> {
        private V value;
        private JsonNode json;

        StorageEntry(V value, JsonNode json) {
            this.value = value;
            this.json = json;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public JsonNode getJson() {
            return json;
        }

        public void setJson(JsonNode json) {
            this.json = json;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StorageEntry<?> that = (StorageEntry<?>) o;
            return Objects.equals(value, that.value) && Objects.equals(json, that.json);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, json);
        }
    }
}
