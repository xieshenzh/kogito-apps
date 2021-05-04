/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.repository.postgresql;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.flywaydb.core.Flyway;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

@ApplicationScoped
public class EmbeddedPostgresManager {

    EmbeddedPostgres pg;

    void onStart(@Observes StartupEvent ev) throws IOException {
        pg = EmbeddedPostgres.start();

        Flyway flyway = Flyway.configure().dataSource(pg.getPostgresDatabase()).load();
        flyway.migrate();
    }

    void onStop(@Observes ShutdownEvent ev) throws IOException {
        pg.close();
    }

    public EmbeddedPostgres getPostgres() {
        return pg;
    }
}
