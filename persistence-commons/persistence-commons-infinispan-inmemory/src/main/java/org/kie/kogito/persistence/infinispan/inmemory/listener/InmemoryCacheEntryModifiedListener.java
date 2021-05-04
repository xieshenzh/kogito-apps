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

package org.kie.kogito.persistence.infinispan.inmemory.listener;

import java.util.function.Consumer;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.kie.kogito.persistence.infinispan.listener.CacheListener;

@Listener
public class InmemoryCacheEntryModifiedListener<K, V> implements CacheListener<K, V> {

    private Consumer<V> consumer;

    public InmemoryCacheEntryModifiedListener(Consumer<V> consumer) {
        this.consumer = consumer;
    }

    @CacheEntryModified
    public void handleModifiedEvent(CacheEntryModifiedEvent<K, V> e) {
        consumer.accept(e.getValue());
    }
}
