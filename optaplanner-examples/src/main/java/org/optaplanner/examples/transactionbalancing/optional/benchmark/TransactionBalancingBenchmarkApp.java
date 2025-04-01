/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.transactionbalancing.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class TransactionBalancingBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new TransactionBalancingBenchmarkApp().buildAndBenchmark(args);
    }

    public TransactionBalancingBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingStepLimitBenchmarkConfig.xml"),
                new ArgOption("scoreDirector",
                        "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingScoreDirectorBenchmarkConfig.xml"),
                new ArgOption("template",
                        "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingBenchmarkConfigTemplate.xml.ftl",
                        true),
                new ArgOption("partitioned",
                        "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingPartitionedSearchBenchmarkConfig.xml"));
    }
}
