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

package org.kie.kogito.index.infinispan.protostream;

import java.util.Map;

import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.cache.Cache;
import org.kie.kogito.index.event.SchemaRegisteredEvent;
import org.kie.kogito.index.infinispan.cache.InfinispanCacheManager;
import org.kie.kogito.index.schema.ProcessDescriptor;
import org.kie.kogito.index.schema.SchemaDescriptor;
import org.kie.kogito.index.schema.SchemaRegistrationException;
import org.kie.kogito.index.schema.SchemaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.index.infinispan.protostream.ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class ProtoSchemaManagerTest {

    @Mock
    ProtoSchemaAcceptor protoSchemaAcceptor;

    @Mock
    InfinispanCacheManager infinispanCacheManager;

    @InjectMocks
    ProtoSchemaManager protoSchemaManager;

    @Mock
    Map<String, String> protobufCache;

    @Mock
    Cache<String, String> processIdModelCache;

    @BeforeEach
    void prepare() {
        initMocks(this);
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
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(name, content, processDescriptor);
        SchemaType schemaType = new SchemaType(PROTO_SCHEMA_TYPE);
        SchemaRegisteredEvent event = new SchemaRegisteredEvent(schemaDescriptor, schemaType);

        protoSchemaManager.onSchemaRegisteredEvent(event);

        verify(protoSchemaAcceptor).accept(eq(schemaType));
        verify(protobufCache).put(eq(name), eq(content));
        verify(processIdModelCache).put(eq(processId), eq(processType));
    }

    @Test
    void onSchemaRegisteredEventWithError() {
        String processId = "testProcessId";
        String processType = "testProcessType";
        ProcessDescriptor processDescriptor = new ProcessDescriptor(processId, processType);
        String name = "testName";
        String content = "testContent";
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(name, content, processDescriptor);
        SchemaType schemaType = new SchemaType(PROTO_SCHEMA_TYPE);
        SchemaRegisteredEvent event = new SchemaRegisteredEvent(schemaDescriptor, schemaType);

        when(protobufCache.containsKey(eq(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX))).thenReturn(true);
        when(protobufCache.get(eq(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX))).thenReturn("testError");

        assertThrows(SchemaRegistrationException.class, () -> protoSchemaManager.onSchemaRegisteredEvent(event));

        verify(protoSchemaAcceptor).accept(eq(schemaType));
        verify(protobufCache).put(eq(name), eq(content));
        verify(processIdModelCache).put(eq(processId), eq(processType));
    }
}