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

package org.kie.kogito.index.mongodb.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;

import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_STORAGE;

@MongoEntity(collection = PROCESS_INSTANCES_STORAGE)
public class ProcessInstanceEntity extends PanacheMongoEntityBase {

    @BsonId
    public String id;

    public String processId;

    public Set<String> roles;

    public Document variables;

    public String endpoint;

    public List<NodeInstanceEntity> nodes;

    public Integer state;

    public Long start;

    public Long end;

    public String rootProcessInstanceId;

    public String rootProcessId;

    public String parentProcessInstanceId;

    public String processName;

    public ProcessInstanceErrorEntity error;

    public Set<String> addons;

    public Long lastUpdate;

    public String businessKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessInstanceEntity that = (ProcessInstanceEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(processId, that.processId) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(variables, that.variables) &&
                Objects.equals(endpoint, that.endpoint) &&
                Objects.equals(nodes, that.nodes) &&
                Objects.equals(state, that.state) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) &&
                Objects.equals(rootProcessInstanceId, that.rootProcessInstanceId) &&
                Objects.equals(rootProcessId, that.rootProcessId) &&
                Objects.equals(parentProcessInstanceId, that.parentProcessInstanceId) &&
                Objects.equals(processName, that.processName) &&
                Objects.equals(error, that.error) &&
                Objects.equals(addons, that.addons) &&
                Objects.equals(lastUpdate, that.lastUpdate) &&
                Objects.equals(businessKey, that.businessKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, processId, roles, variables, endpoint, nodes, state, start, end, rootProcessInstanceId, rootProcessId, parentProcessInstanceId, processName, error, addons, lastUpdate, businessKey);
    }

    public static class NodeInstanceEntity {

        public String _id;

        public String id;

        public String name;

        public String nodeId;

        public String type;

        public Long enter;

        public Long exit;

        public String definitionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            NodeInstanceEntity that = (NodeInstanceEntity) o;
            return Objects.equals(_id, that._id) &&
                    Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(nodeId, that.nodeId) &&
                    Objects.equals(type, that.type) &&
                    Objects.equals(enter, that.enter) &&
                    Objects.equals(exit, that.exit) &&
                    Objects.equals(definitionId, that.definitionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_id, id, name, nodeId, type, enter, exit, definitionId);
        }
    }

    public static class ProcessInstanceErrorEntity {

        public String _id;

        public String nodeDefinitionId;

        public String message;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ProcessInstanceErrorEntity that = (ProcessInstanceErrorEntity) o;
            return Objects.equals(_id, that._id) &&
                    Objects.equals(nodeDefinitionId, that.nodeDefinitionId) &&
                    Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_id, nodeDefinitionId, message);
        }
    }
}
