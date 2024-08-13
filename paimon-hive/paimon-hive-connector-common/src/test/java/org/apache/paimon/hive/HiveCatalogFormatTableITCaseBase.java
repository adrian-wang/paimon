/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.hive;

import org.apache.paimon.hive.runner.PaimonEmbeddedHiveRunner;

import com.klarna.hiverunner.HiveShell;
import com.klarna.hiverunner.annotations.HiveSQL;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.paimon.hive.HiveCatalogOptions.FORMAT_TABLE_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;

/** IT cases for using Paimon {@link HiveCatalog}. */
@RunWith(PaimonEmbeddedHiveRunner.class)
public abstract class HiveCatalogFormatTableITCaseBase {

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    protected String path;
    protected TableEnvironment tEnv;

    @HiveSQL(files = {})
    protected static HiveShell hiveShell;

    private void before(boolean locationInProperties) throws Exception {
        this.path = folder.newFolder().toURI().toString();
        Map<String, String> options = new HashMap<>();
        options.put("type", "paimon");
        options.put("metastore", "hive");
        options.put("uri", "");
        options.put("lock.enabled", "true");
        options.put("location-in-properties", String.valueOf(locationInProperties));
        options.put("warehouse", path);
        options.put(FORMAT_TABLE_ENABLED.key(), "true");
        tEnv = TableEnvironmentImpl.create(EnvironmentSettings.newInstance().inBatchMode().build());
        tEnv.executeSql(
                        String.join(
                                "\n",
                                "CREATE CATALOG my_hive WITH (",
                                options.entrySet().stream()
                                        .map(
                                                e ->
                                                        String.format(
                                                                "'%s' = '%s'",
                                                                e.getKey(), e.getValue()))
                                        .collect(Collectors.joining(",\n")),
                                ")"))
                .await();

        tEnv.executeSql("USE CATALOG my_hive").await();
        tEnv.executeSql("DROP DATABASE IF EXISTS test_db CASCADE");
        tEnv.executeSql("CREATE DATABASE test_db").await();
        tEnv.executeSql("USE test_db").await();

        hiveShell.execute("USE test_db");
    }

    private void after() {
        hiveShell.execute("DROP DATABASE IF EXISTS test_db CASCADE");
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface LocationInProperties {}

    @Rule
    public TestRule environmentRule =
            (base, description) ->
                    new Statement() {
                        @Override
                        public void evaluate() throws Throwable {
                            try {
                                before(
                                        description.getAnnotation(LocationInProperties.class)
                                                != null);
                                base.evaluate();
                            } finally {
                                after();
                            }
                        }
                    };

    @Test
    public void testFormatTable() throws Exception {
        hiveShell.execute("CREATE TABLE hive_table ( a INT, b STRING )");
        hiveShell.execute("INSERT INTO hive_table VALUES (100, 'Hive'), (200, 'Table')");
        assertThat(collect("SELECT * FROM hive_table"))
                .containsExactlyInAnyOrder(Row.of(100, "Hive"), Row.of(200, "Table"));
        tEnv.executeSql("INSERT INTO hive_table VALUES (300, 'Paimon')").await();
        assertThat(collect("SELECT * FROM hive_table"))
                .containsExactlyInAnyOrder(
                        Row.of(100, "Hive"), Row.of(200, "Table"), Row.of(300, "Paimon"));
    }

    protected List<Row> collect(String sql) throws Exception {
        List<Row> result = new ArrayList<>();
        try (CloseableIterator<Row> it = tEnv.executeSql(sql).collect()) {
            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        return result;
    }
}
