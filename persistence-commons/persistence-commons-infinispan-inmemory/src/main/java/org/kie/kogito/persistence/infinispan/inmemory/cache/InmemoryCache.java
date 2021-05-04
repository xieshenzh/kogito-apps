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

package org.kie.kogito.persistence.infinispan.inmemory.cache;

import java.util.HashMap;
import java.util.Map;

import org.infinispan.Cache;
import org.kie.kogito.persistence.infinispan.cache.CacheDelegate;
import org.kie.kogito.persistence.infinispan.listener.CacheListener;

public class InmemoryCache<K, V> implements CacheDelegate<K, V> {

    private Cache<K, V> cache;

    public InmemoryCache(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) {
        return this.cache.get(key);
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public V remove(K key) {
        return this.cache.remove(key);
    }

    @Override
    public V put(K key, V value) {
        return this.cache.put(key, value);
    }

    @Override
    public Map<K, V> entries() {
        return new HashMap<>(this.cache);
    }

    @Override
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    @Override
    public String getName() {
        return this.cache.getName();
    }

    @Override
    public void addClientListener(CacheListener<K, V> cacheListener) {
        this.cache.addListener(cacheListener);
    }

    @Override
    public CacheDelegate<K, V> withJsonFormat() {
        return this;
    }

    @Override
    public Cache<K, V> getCache() {
        return this.cache;
    }
}
