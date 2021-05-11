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

import io.quarkus.arc.properties.IfBuildProperty;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;

import javax.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.inmemory.storage.InmemoryStorageService.INMEMORY_STORAGE;

@ApplicationScoped
@IfBuildProperty(name = PERSISTENCE_TYPE_PROPERTY, stringValue = INMEMORY_STORAGE)
public class InmemoryStorageService implements StorageService {

    public static final String INMEMORY_STORAGE = "inmemory";

    private Map<String, InmemoryStorage<?>> cache;

    public InmemoryStorageService() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public Storage<String, String> getCache(String name) {
        return this.getOrCreateCache(name, String.class.getName());
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type) {
        return this.getOrCreateCache(name, type.getName());
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type, String rootType) {
        return this.getOrCreateCache(name, rootType);
    }

    @SuppressWarnings("unchecked")
    private <T> InmemoryStorage<T> getOrCreateCache(String name, String rootType) {
        InmemoryStorage<T> result = (InmemoryStorage<T>) cache.get(name);
        if (result == null) {
            InmemoryStorage<T> storage = new InmemoryStorage<>(rootType);
            result = (InmemoryStorage<T>) cache.putIfAbsent(name, storage);
            if (result == null)
                result = storage;
        }
        return result;
    }
}
