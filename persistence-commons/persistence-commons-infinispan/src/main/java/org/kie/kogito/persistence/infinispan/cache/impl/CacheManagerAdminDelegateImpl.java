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

package org.kie.kogito.persistence.infinispan.cache.impl;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCacheManagerAdmin;
import org.infinispan.commons.configuration.BasicConfiguration;
import org.kie.kogito.persistence.infinispan.cache.CacheDelegate;
import org.kie.kogito.persistence.infinispan.cache.CacheManagerAdminDelegate;

public class CacheManagerAdminDelegateImpl implements CacheManagerAdminDelegate {

    private RemoteCacheManagerAdmin admin;

    private DataFormat jsonDataFormat;

    public CacheManagerAdminDelegateImpl(RemoteCacheManagerAdmin admin, DataFormat jsonDataFormat) {
        this.admin = admin;
        this.jsonDataFormat = jsonDataFormat;
    }

    @Override
    public <V, K> CacheDelegate<K, V> createCache(String name, String s) {
        return new CacheDelegateImpl<>(this.admin.createCache(name, s), jsonDataFormat);
    }

    @Override
    public <V, K> CacheDelegate<K, V> createCache(String name, BasicConfiguration configuration) {
        return new CacheDelegateImpl<>(this.admin.createCache(name, configuration), jsonDataFormat);
    }

    @Override
    public <V, K> CacheDelegate<K, V> getOrCreateCache(String name, BasicConfiguration configuration) {
        return new CacheDelegateImpl<>(this.admin.getOrCreateCache(name, configuration), jsonDataFormat);
    }
}
