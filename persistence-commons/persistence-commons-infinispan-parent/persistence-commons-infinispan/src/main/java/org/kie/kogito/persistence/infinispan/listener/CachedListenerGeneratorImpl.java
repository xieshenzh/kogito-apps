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

package org.kie.kogito.persistence.infinispan.listener;

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;

import org.infinispan.client.hotrod.RemoteCache;
import org.kie.kogito.persistence.infinispan.cache.CacheDelegate;

@ApplicationScoped
public class CachedListenerGeneratorImpl implements CachedListenerGenerator {

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> CacheListener<K, V> generateCacheObjectCreatedListener(CacheDelegate<K, V> cache, Consumer<V> consumer) {
        return new CacheObjectCreatedListener<>((RemoteCache<K, V>) cache.getCache(), consumer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> CacheListener<K, V> generateCacheObjectUpdatedListener(CacheDelegate<K, V> cache, Consumer<V> consumer) {
        return new CacheObjectUpdatedListener<>((RemoteCache<K, V>) cache.getCache(), consumer);
    }

    @Override
    public <K, V> CacheListener<K, V> generateCacheObjectRemovedListener(CacheDelegate<K, V> cache, Consumer<K> consumer) {
        return new CacheObjectRemovedListener<>(consumer);
    }
}
