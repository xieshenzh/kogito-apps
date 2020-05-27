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

package org.kie.kogito.index.protobuf.schema;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import javax.enterprise.event.Event;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.SchemaRegisteredEvent;
import org.kie.kogito.index.schema.ProcessDescriptor;
import org.kie.kogito.index.schema.SchemaDescriptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.kie.kogito.index.protobuf.schema.ProtoSchemaManager.SCHEMA_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtoSchemaManagerTest {

    @Mock
    Event<SchemaRegisteredEvent> schemaEvent;

    @InjectMocks
    ProtoSchemaManager protoSchemaManager;

    @BeforeEach
    void prepare() {
        reset(schemaEvent);
    }

    @Test
    void onSchemaDescriptorRegistered_noProcessDecriptor() {
        String fileName = "testFileName";
        String rawSchema = "testSchema";
        SchemaDescriptorRegisteredEvent event = new SchemaDescriptorRegisteredEvent(fileName, rawSchema, null, null);

        protoSchemaManager.onSchemaDescriptorRegistered(event);

        verify(schemaEvent).fire(eq(new SchemaRegisteredEvent(new SchemaDescriptor(fileName, rawSchema, null), SCHEMA_TYPE)));
    }

    @Test
    void onSchemaDescriptorRegistered_withProcessDescriptor() throws IOException, URISyntaxException {
        String fullTypeName = "org.acme.travels.travels.Travels";
        String fileName = "testFileName";
        String rawSchema = getTestSchema();
        SerializationContext context = getTestContext(rawSchema);
        FileDescriptor descriptor = context.getFileDescriptors().get("test");
        Descriptor messageDescriptor = context.getMessageDescriptor(fullTypeName);
        Function<String, Descriptor> getter = mock(Function.class);
        when(getter.apply(any())).thenReturn(messageDescriptor);
        SchemaDescriptorRegisteredEvent event = new SchemaDescriptorRegisteredEvent(fileName, rawSchema, descriptor, getter);

        protoSchemaManager.onSchemaDescriptorRegistered(event);

        verify(getter).apply(eq(fullTypeName));
        verify(schemaEvent).fire(eq(new SchemaRegisteredEvent(new SchemaDescriptor("travels.proto", rawSchema, new ProcessDescriptor("travels", fullTypeName)), SCHEMA_TYPE)));
    }

    private String getTestSchema() throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("test.proto").toURI());
        return new String(Files.readAllBytes(path));
    }

    private SerializationContext getTestContext(String schema) {
        SerializationContext ctx = new SerializationContextImpl(Configuration.builder().build());
        ctx.registerProtoFiles(FileDescriptorSource.fromString("test", schema));
        return ctx;
    }
}