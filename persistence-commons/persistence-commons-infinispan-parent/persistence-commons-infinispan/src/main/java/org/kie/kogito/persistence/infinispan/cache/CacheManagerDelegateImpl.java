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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.dataconversion.MediaType;

@ApplicationScoped
public class CacheManagerDelegateImpl implements CacheManagerDelegate {

    @Inject
    RemoteCacheManager manager;

    @Inject
    JsonDataFormatMarshaller marshaller;

    DataFormat jsonDataFormat;

    @PostConstruct
    public void init() {
        jsonDataFormat = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).valueMarshaller(marshaller).build();
    }

    @Override
    public void start() {
        manager.start();
    }

    @Override
    public void stop() {
        manager.stop();
    }

    @Override
    public void close() {
        manager.close();
    }

    @Override
    public <K, V> CacheDelegate<K, V> getCache(String name) {
        return new CacheDelegateImpl<>(manager.getCache(name), jsonDataFormat);
    }

    @Override
    public CacheManagerAdminDelegate administration() {
        return new CacheManagerAdminDelegateImpl(manager.administration(), jsonDataFormat);
    }
}
