/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.protobuf;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.StartupEvent;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.index.protobuf.domain.DomainModelDescriptorRegisteredEvent;
import org.kie.kogito.index.protobuf.schema.SchemaDescriptorRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProtobufService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufService.class);

    @Inject
    FileDescriptorSource kogitoDescriptors;

    @Inject
    Event<DomainModelDescriptorRegisteredEvent> domainModelEvent;

    @Inject
    Event<SchemaDescriptorRegisteredEvent> schemaEvent;

    void onStart(@Observes StartupEvent ev) {
        kogitoDescriptors.getFileDescriptors().forEach((name, bytes) -> {
            LOGGER.info("Registering Kogito ProtoBuffer file: {}", name);
            schemaEvent.fire(new SchemaDescriptorRegisteredEvent(name, new String(bytes), null, t -> null));
        });
    }

    public void registerProtoBufferType(String fileName, String content) {
        LOGGER.debug("Registering new ProtoBuffer file with content: \n{}", content);

        content = content.replaceAll("kogito.Date", "string");
        SerializationContext ctx = new SerializationContextImpl(Configuration.builder().build());
        try {
            ctx.registerProtoFiles(kogitoDescriptors);
            ctx.registerProtoFiles(FileDescriptorSource.fromString("", content));
        } catch (Exception ex) {
            LOGGER.warn("Error trying to parse proto buffer file: {}", ex.getMessage(), ex);
            throw ex;
        }

        FileDescriptor desc = ctx.getFileDescriptors().get("");

        schemaEvent.fire(new SchemaDescriptorRegisteredEvent(fileName, content, desc, ctx::getMessageDescriptor));
        domainModelEvent.fire(new DomainModelDescriptorRegisteredEvent(desc));
    }
}