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

package org.kie.kogito.persistence.inmemory.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.inmemory.storage.InmemoryStorage.StorageEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.kie.kogito.persistence.api.query.SortDirection.DESC;
import static org.kie.kogito.persistence.inmemory.JsonUtils.*;
import static org.kie.kogito.persistence.inmemory.storage.InmemoryStorage.CACHE_DATA;
import static org.kie.kogito.persistence.inmemory.storage.InmemoryStorage.CACHE_KEY;

public class InmemoryQuery<V> implements Query<V> {

    private Integer limit;

    private Integer offset;

    private List<AttributeFilter<?>> filters;

    private List<AttributeSort> sortBy;

    private Map<String, StorageEntry<V>> storage;

    public InmemoryQuery(Map<String, StorageEntry<V>> storage) {
        this.storage = storage;
    }

    @Override
    public Query<V> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query<V> offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query<V> filter(List<AttributeFilter<?>> filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public Query<V> sort(List<AttributeSort> sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    @Override
    public List<V> execute() {
        JsonPath jsonPath = this.createJsonPath();
        ArrayNode arrayNode = storage.values().stream().map(StorageEntry::getJson)
                .reduce(OBJECT_MAPPER.createArrayNode(), ArrayNode::add, ArrayNode::addAll);

        List<String> keys;
        try {
            keys = jsonPath.read(OBJECT_MAPPER.writeValueAsString(arrayNode));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse the cached data.", e);
        }

        return keys.stream().filter(k -> storage.containsKey(k))
                .sorted(new StorageEntryComparator<>(this.storage, this.sortBy))
                .skip(this.offset).limit(this.limit)
                .map(k -> storage.get(k)).map(StorageEntry::getValue)
                .collect(Collectors.toList());
    }

    private JsonPath createJsonPath() {

        return JsonPath.compile("$." + CACHE_KEY, null);
    }

    private static class StorageEntryComparator<V> implements Comparator<String> {
        private Map<String, StorageEntry<V>> storage;
        private List<AttributeSort> sortBy;

        public StorageEntryComparator(Map<String, StorageEntry<V>> storage, List<AttributeSort> sortBy) {
            this.storage = storage;
            this.sortBy = sortBy;
        }

        @Override
        public int compare(String key1, String key2) {
            JsonNode node1 = this.storage.get(key1).getJson();
            JsonNode node2 = this.storage.get(key2).getJson();
            if (Objects.equals(node1, node2)) {
                return 0;
            }

            String s1;
            String s2;
            try {
                s1 = OBJECT_MAPPER.writeValueAsString(node1);
                s2 = OBJECT_MAPPER.writeValueAsString(node2);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not parse the cached data.", e);
            }

            return this.sortBy.stream().mapToInt(s -> {
                String jsonPath = JSON_PATH_ROOT + JSON_PATH_DOT + CACHE_DATA + JSON_PATH_DOT + s.getAttribute();
                String v1;
                try {
                    v1 = JsonPath.read(s1, jsonPath).toString();
                } catch (PathNotFoundException e) {
                    v1 = null;
                }
                String v2;
                try {
                    v2 = JsonPath.read(s2, jsonPath).toString();
                } catch (PathNotFoundException e) {
                    v2 = null;
                }
                if (v1 != null && v2 != null) {
                    return DESC.equals(s.getSort()) ? v2.compareTo(v1) : v1.compareTo(v2);
                } else if (v1 == null && v2 == null) {
                    return 0;
                } else if (DESC.equals(s.getSort()) ^ v1 == null) {
                    return -1;
                } else {
                    return 1;
                }

            }).filter(i -> i != 0).findFirst().orElse(0);
        }
    }
}
