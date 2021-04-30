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

import org.infinispan.Cache;
import org.kie.kogito.persistence.infinispan.cache.CacheDelegate;
import org.kie.kogito.persistence.infinispan.listener.CacheListener;

import java.util.Map;

public class InmemoryCache<K, V> implements CacheDelegate<K, V> {

    private Cache<K, V> cache;

    public InmemoryCache(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public Map<K, V> entries() {
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void addClientListener(CacheListener<K, V> cacheListener) {

    }

    @Override
    public CacheDelegate<K, V> withJsonFormat() {
        return null;
    }

    @Override
    public Object getCache() {
        return null;
    }
}
