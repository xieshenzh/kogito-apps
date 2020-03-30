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

package org.kie.kogito.index.protobuf.schema;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.kie.kogito.index.protobuf.ProtobufValidationException;
import org.kie.kogito.index.schema.ProcessDescriptor;
import org.kie.kogito.index.schema.SchemaDescriptor;
import org.kie.kogito.index.schema.SchemaRegisteredEvent;
import org.kie.kogito.index.schema.SchemaType;

import static java.lang.String.format;
import static org.kie.kogito.index.Constants.KOGITO_DOMAIN_ATTRIBUTE;

@ApplicationScoped
public class ProtoSchemaProducer {

    static final SchemaType SCHEMA_TYPE = new SchemaType("proto");

    @Inject
    Event<SchemaRegisteredEvent> schemaEvent;

    public void onSchemaDescriptorRegistered(@Observes SchemaDescriptorRegisteredEvent event) {
        SchemaRegisteredEvent schemaRegisteredEvent = event.getDescriptor().map(desc -> {
            Option processIdOption = desc.getOption("kogito_id");
            if (processIdOption == null || processIdOption.getValue() == null) {
                throw new ProtobufValidationException("Missing marker for process id in proto file, please add option kogito_id=\"processid\"");
            }
            String processId = (String) processIdOption.getValue();

            Option model = desc.getOption("kogito_model");
            if (model == null || model.getValue() == null) {
                throw new ProtobufValidationException("Missing marker for main message type in proto file, please add option kogito_model=\"messagename\"");
            }
            String messageName = (String) model.getValue();
            String fullTypeName = desc.getPackage() == null ? messageName : desc.getPackage() + "." + messageName;

            Descriptor descriptor;
            try {
                descriptor = event.messageDescriptorGetter.apply(fullTypeName);
            } catch (IllegalArgumentException ex) {
                throw new ProtobufValidationException(format("Could not find message with name: %s in proto file, e, please review option kogito_model", fullTypeName));
            }

            validateDescriptorField(messageName, descriptor, KOGITO_DOMAIN_ATTRIBUTE);

            return new SchemaRegisteredEvent(new SchemaDescriptor(processId + ".proto", event.getRawSchema(), Optional.of(new ProcessDescriptor(processId, fullTypeName))), SCHEMA_TYPE);
        }).orElseGet(() -> new SchemaRegisteredEvent(new SchemaDescriptor(event.getFileName(), event.getRawSchema(), Optional.empty()), SCHEMA_TYPE));

        schemaEvent.fire(schemaRegisteredEvent);
    }

    private void validateDescriptorField(String messageName, Descriptor descriptor, String processInstancesDomainAttribute) {
        FieldDescriptor processInstances = descriptor.findFieldByName(processInstancesDomainAttribute);
        if (processInstances == null) {
            throw new ProtobufValidationException(format("Could not find %s attribute in proto message: %s", processInstancesDomainAttribute, messageName));
        }
    }
}
