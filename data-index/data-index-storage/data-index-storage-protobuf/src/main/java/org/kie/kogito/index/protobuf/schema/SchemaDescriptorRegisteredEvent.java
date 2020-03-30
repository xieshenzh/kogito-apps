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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;

public class SchemaDescriptorRegisteredEvent {

    String fileName;

    String rawSchema;

    Optional<FileDescriptor> descriptor;

    Function<String, Descriptor> messageDescriptorGetter;

    public SchemaDescriptorRegisteredEvent(String fileName, String rawSchema, Optional<FileDescriptor> descriptor, Function<String, Descriptor> messageDescriptorGetter) {
        this.fileName = fileName;
        this.rawSchema = rawSchema;
        this.descriptor = descriptor;
        this.messageDescriptorGetter = messageDescriptorGetter;
    }

    String getFileName() {
        return fileName;
    }

    String getRawSchema() {
        return rawSchema;
    }

    Optional<FileDescriptor> getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaDescriptorRegisteredEvent that = (SchemaDescriptorRegisteredEvent) o;
        return Objects.equals(fileName, that.fileName) &&
                Objects.equals(rawSchema, that.rawSchema) &&
                Objects.equals(descriptor, that.descriptor) &&
                Objects.equals(messageDescriptorGetter, that.messageDescriptorGetter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, rawSchema, descriptor, messageDescriptorGetter);
    }
}
