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

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.kie.kogito.persistence.infinispan.cache.CacheDelegate;
import org.kie.kogito.persistence.infinispan.cache.CacheManagerAdminDelegate;
import org.kie.kogito.persistence.infinispan.cache.CacheManagerDelegate;

@ApplicationScoped
public class InmemoryCacheManager implements CacheManagerDelegate {

    private EmbeddedCacheManager cacheManager;

    @PostConstruct
    public void init() {
        GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
        this.cacheManager = new DefaultCacheManager(global.build());
    }

    @Override
    public void start() {
        this.cacheManager.start();
    }

    @Override
    public void stop() {
        this.cacheManager.stop();
    }

    @Override
    public void close() {
        try {
            this.cacheManager.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <K, V> CacheDelegate<K, V> getCache(String name) {
        return new InmemoryCache<>(this.cacheManager.getCache(name));
    }

    @Override
    public CacheManagerAdminDelegate administration() {
        return new InmemoryCacheManagerAdmin(this.cacheManager.administration());
    }

    @Override
    public EmbeddedCacheManager getManager() {
        return this.cacheManager;
    }
}
