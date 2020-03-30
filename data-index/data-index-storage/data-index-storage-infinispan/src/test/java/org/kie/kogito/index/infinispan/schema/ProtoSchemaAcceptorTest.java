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

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.schema.SchemaType;
import org.wildfly.common.Assert;

import static org.kie.kogito.index.infinispan.Constants.INFINISPAN_STORAGE;
import static org.kie.kogito.index.infinispan.schema.ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE;

class ProtoSchemaAcceptorTest {

    ProtoSchemaAcceptor protoSchemaAcceptor = new ProtoSchemaAcceptor();

    @Test
    void accept_true() {
        protoSchemaAcceptor.storageType = INFINISPAN_STORAGE;
        Assert.assertTrue(protoSchemaAcceptor.accept(new SchemaType(PROTO_SCHEMA_TYPE)));
    }

    @Test
    void accept_false_schemaType() {
        protoSchemaAcceptor.storageType = INFINISPAN_STORAGE;
        Assert.assertFalse(protoSchemaAcceptor.accept(new SchemaType("test")));
    }

    @Test
    void accept_false_storageType() {
        protoSchemaAcceptor.storageType = "test";
        Assert.assertFalse(protoSchemaAcceptor.accept(new SchemaType(PROTO_SCHEMA_TYPE)));
    }
}