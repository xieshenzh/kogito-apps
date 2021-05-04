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

package org.kie.kogito.persistence.infinispan.inmemory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.kie.kogito.persistence.infinispan.inmemory.cache.InmemoryCacheManager;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class InmemoryDataIndex {

    @Inject
    private InmemoryCacheManager manager;

    void onStart(@Observes StartupEvent ev) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.clustering().cacheMode(CacheMode.LOCAL);
        manager.getManager().administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache("___protobuf_metadata", builder.build());
        manager.getManager().administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache("processinstances", builder.build());
        manager.getManager().administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache("usertaskinstances", builder.build());
        manager.getManager().administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache("processidmodel", builder.build());
        manager.getManager().administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache("jobs", builder.build());
    }
}
