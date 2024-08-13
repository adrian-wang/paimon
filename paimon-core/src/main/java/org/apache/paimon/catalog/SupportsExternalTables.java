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

package org.apache.paimon.catalog;

import org.apache.paimon.table.ExternalTable;
import org.apache.paimon.table.FormatTable;

/** An interface to provide {@link ExternalTable}s. */
public interface SupportsExternalTables {

    /** Whether enable external tables. */
    boolean enableExternalTables();

    /**
     * Return a {@link FormatTable} identified by the given {@link Identifier}.
     *
     * @param identifier Path of the table
     * @return The requested table
     * @throws Catalog.TableNotExistException if the target does not exist
     */
    ExternalTable getExternalTable(Identifier identifier) throws Catalog.TableNotExistException;
}
