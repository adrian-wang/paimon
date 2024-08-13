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

package org.apache.paimon.table;

import org.apache.paimon.annotation.Public;
import org.apache.paimon.stats.Statistics;
import org.apache.paimon.table.sink.BatchWriteBuilder;
import org.apache.paimon.table.sink.StreamWriteBuilder;
import org.apache.paimon.table.source.ReadBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * An external table.
 *
 * @since 0.9.0
 */
@Public
public interface ExternalTable extends Table {

    String location();

    @Override
    ExternalTable copy(Map<String, String> dynamicOptions);

    // ===================== Unsupported ===============================

    @Override
    default Optional<Statistics> statistics() {
        return Optional.empty();
    }

    @Override
    default OptionalLong latestSnapshotId() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void rollbackTo(long snapshotId) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void createTag(String tagName, long fromSnapshotId) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void createTag(String tagName, long fromSnapshotId, Duration timeRetained) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void createTag(String tagName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void createTag(String tagName, Duration timeRetained) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteTag(String tagName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void rollbackTo(String tagName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void createBranch(String branchName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void createBranch(String branchName, String tagName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteBranch(String branchName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void fastForward(String branchName) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ExpireSnapshots newExpireSnapshots() {
        throw new UnsupportedOperationException();
    }

    @Override
    default ExpireSnapshots newExpireChangelog() {
        throw new UnsupportedOperationException();
    }

    @Override
    default ReadBuilder newReadBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    default BatchWriteBuilder newBatchWriteBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    default StreamWriteBuilder newStreamWriteBuilder() {
        throw new UnsupportedOperationException();
    }
}
