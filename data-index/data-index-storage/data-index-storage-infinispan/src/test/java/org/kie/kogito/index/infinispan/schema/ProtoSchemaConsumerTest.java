/*
 *
 *  * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.kie.kogito.index.infinispan.schema;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.cache.Cache;
import org.kie.kogito.index.infinispan.cache.InfinispanCacheManager;
import org.kie.kogito.index.schema.ProcessDescriptor;
import org.kie.kogito.index.schema.SchemaDescriptor;
import org.kie.kogito.index.schema.SchemaRegisteredEvent;
import org.kie.kogito.index.schema.SchemaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.kie.kogito.index.infinispan.schema.ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtoSchemaConsumerTest {

    @Mock
    ProtoSchemaAcceptor protoSchemaAcceptor;

    @Mock
    InfinispanCacheManager infinispanCacheManager;

    @InjectMocks
    ProtoSchemaConsumer protoSchemaConsumer;

    @Mock
    Map<String, String> protobufCache;

    @Mock
    Cache<String, String> processIdModelCache;

    @BeforeEach
    void prepare() {
        when(protoSchemaAcceptor.accept(any())).thenReturn(true);
        when(infinispanCacheManager.getProtobufCache()).thenReturn(protobufCache);
        when(infinispanCacheManager.getProcessIdModelCache()).thenReturn(processIdModelCache);
    }

    @Test
    void onSchemaRegisteredEvent() {
        String processId = "testProcessId";
        String processType = "testProcessType";
        ProcessDescriptor processDescriptor = new ProcessDescriptor(processId, processType);
        String name = "testName";
        String content = "testContent";
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(name, content, Optional.of(processDescriptor));
        SchemaType schemaType = new SchemaType(PROTO_SCHEMA_TYPE);
        SchemaRegisteredEvent event = new SchemaRegisteredEvent(schemaDescriptor, schemaType);

        protoSchemaConsumer.onSchemaRegisteredEvent(event);

        verify(protoSchemaAcceptor).accept(eq(schemaType));
        verify(protobufCache).put(eq(name), eq(content));
        verify(processIdModelCache).put(eq(processId), eq(processType));
    }
}