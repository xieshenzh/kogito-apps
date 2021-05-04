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

package org.kie.kogito.persistence.infinispan.cache;

import java.util.Map;

import org.kie.kogito.persistence.infinispan.listener.CacheListener;

public interface CacheDelegate<K, V> {
    V get(K key);

    void clear();

    V remove(K key);

    V put(K key, V value);

    Map<K, V> entries();

    boolean containsKey(K key);

    String getName();

    void addClientListener(CacheListener<K, V> cacheListener);

    CacheDelegate<K, V> withJsonFormat();

    Object getCache();
}
