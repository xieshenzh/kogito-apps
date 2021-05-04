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

package org.kie.kogito.persistence.infinispan.query.impl;

import javax.enterprise.context.ApplicationScoped;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.kie.kogito.persistence.infinispan.cache.CacheDelegate;
import org.kie.kogito.persistence.infinispan.query.QueryFactoryGenerator;

import io.quarkus.arc.DefaultBean;

@DefaultBean
@ApplicationScoped
public class QueryFactoryGeneratorImpl implements QueryFactoryGenerator {

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> QueryFactory generateQueryFactory(CacheDelegate<K, V> delegate) {
        return Search.getQueryFactory((RemoteCache<K, V>) delegate.getCache());
    }
}
