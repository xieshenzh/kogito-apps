/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.mongodb.model;

import io.quarkus.mongodb.panache.runtime.MongoOperations;

/**
 *
 * @param <K>
 * @param <V>
 * @param <E>
 */
public interface MongoEntityMapper<K, V, E> {

    /**
     *
     * @return
     */
    Class<E> getEntityClass();

    /**
     *
     * @param key
     * @param value
     * @return
     */
    E mapToEntity(K key, V value);

    /**
     *
     * @param entity
     * @return
     */
    V mapToModel(E entity);

    /**
     *
     * @param attribute
     * @return
     */
    default String convertAttribute(String attribute) {
        return "id".equalsIgnoreCase(attribute) ? MongoOperations.ID : attribute;
    }
}
