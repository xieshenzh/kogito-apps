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

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.index.mongodb.model.ModelUtils.zonedDateTimeToInstant;

class JobEntityMapperTest {

    JobEntityMapper jobEntityMapper = new JobEntityMapper();

    Job job;

    JobEntity jobEntity;

    {
        String testId = "testId";
        ZonedDateTime time = ZonedDateTime.now();
        String status = "ACTIVE";
        String processId = "testProcessId";
        String processInstanceId = "testProcessInstanceId";
        String rootProcessId = "testRootProcessId";
        String rootProcessInstanceId = "testRootProcessInstanceId";
        Integer priority = 79;
        String callbackEndpoint = "testCallbackEndpoint";
        Long repeatInterval = 70L;
        Integer repeatLimit = 89;
        String scheduledId = "testScheduleId";
        Integer retries = 25;
        Integer executionCounter = 17;

        job = new Job();
        job.setId(testId);
        job.setStatus(status);
        job.setLastUpdate(time);
        job.setProcessId(processId);
        job.setProcessInstanceId(processInstanceId);
        job.setRootProcessId(rootProcessId);
        job.setRootProcessInstanceId(rootProcessInstanceId);
        job.setExpirationTime(time);
        job.setPriority(priority);
        job.setCallbackEndpoint(callbackEndpoint);
        job.setRepeatInterval(repeatInterval);
        job.setRepeatLimit(repeatLimit);
        job.setScheduledId(scheduledId);
        job.setRetries(retries);
        job.setExecutionCounter(executionCounter);

        jobEntity = new JobEntity();
        jobEntity.id = testId;
        jobEntity.status = status;
        jobEntity.lastUpdate = zonedDateTimeToInstant(time);
        jobEntity.processId = processId;
        jobEntity.processInstanceId = processInstanceId;
        jobEntity.rootProcessId = rootProcessId;
        jobEntity.rootProcessInstanceId = rootProcessInstanceId;
        jobEntity.expirationTime = zonedDateTimeToInstant(time);
        jobEntity.priority = priority;
        jobEntity.callbackEndpoint = callbackEndpoint;
        jobEntity.repeatInterval = repeatInterval;
        jobEntity.repeatLimit = repeatLimit;
        jobEntity.scheduledId = scheduledId;
        jobEntity.retries = retries;
        jobEntity.executionCounter = executionCounter;
    }

    @Test
    void testGetEntityClass() {
        assertEquals(JobEntity.class, jobEntityMapper.getEntityClass());
    }

    @Test
    void testMapToEntity() {
        JobEntity result = jobEntityMapper.mapToEntity(job.getId(), job);
        assertEquals(jobEntity, result);
    }

    @Test
    void testMapToModel() {
        Job result = jobEntityMapper.mapToModel(jobEntity);
        assertEquals(job, result);
    }
}