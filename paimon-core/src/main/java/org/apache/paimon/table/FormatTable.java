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
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.types.RowType;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An external table.
 *
 * @since 0.9.0
 */
@Public
public interface FormatTable extends ExternalTable {

    Format format();

    @Override
    FormatTable copy(Map<String, String> dynamicOptions);

    /** Format of the table. */
    enum Format {
        ORC,
        PARQUET,
        CSV
    }

    static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link FormatTable}. */
    class Builder {

        private Identifier identifier;
        private RowType rowType;
        private List<String> partitionKeys;
        private String location;
        private Format format;
        private Map<String, String> options;
        @Nullable private String comment;

        public Builder identifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder rowType(RowType rowType) {
            this.rowType = rowType;
            return this;
        }

        public Builder partitionKeys(List<String> partitionKeys) {
            this.partitionKeys = partitionKeys;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder format(Format format) {
            this.format = format;
            return this;
        }

        public Builder options(Map<String, String> options) {
            this.options = options;
            return this;
        }

        public Builder comment(@Nullable String comment) {
            this.comment = comment;
            return this;
        }

        public FormatTable build() {
            return new FormatTableImpl(
                    identifier, rowType, partitionKeys, location, format, options, comment);
        }
    }

    /** An implementation for {@link FormatTable}. */
    class FormatTableImpl implements FormatTable {

        private final Identifier identifier;
        private final RowType rowType;
        private final List<String> partitionKeys;
        private final String location;
        private final Format format;
        private final Map<String, String> options;
        @Nullable private final String comment;

        public FormatTableImpl(
                Identifier identifier,
                RowType rowType,
                List<String> partitionKeys,
                String location,
                Format format,
                Map<String, String> options,
                @Nullable String comment) {
            this.identifier = identifier;
            this.rowType = rowType;
            this.partitionKeys = partitionKeys;
            this.location = location;
            this.format = format;
            this.options = options;
            this.comment = comment;
        }

        @Override
        public String name() {
            return identifier.getTableName();
        }

        @Override
        public String fullName() {
            return identifier.getFullName();
        }

        @Override
        public RowType rowType() {
            return rowType;
        }

        @Override
        public List<String> partitionKeys() {
            return partitionKeys;
        }

        @Override
        public List<String> primaryKeys() {
            return Collections.emptyList();
        }

        @Override
        public String location() {
            return location;
        }

        @Override
        public Format format() {
            return format;
        }

        @Override
        public Map<String, String> options() {
            return options;
        }

        @Override
        public Optional<String> comment() {
            return Optional.ofNullable(comment);
        }

        @Override
        public FormatTable copy(Map<String, String> dynamicOptions) {
            Map<String, String> newOptions = new HashMap<>(options);
            newOptions.putAll(dynamicOptions);
            return new FormatTableImpl(
                    identifier, rowType, partitionKeys, location, format, newOptions, comment);
        }
    }
}
