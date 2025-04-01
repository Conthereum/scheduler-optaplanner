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

import java.util.Arrays;
import java.util.List;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;
import org.optaplanner.examples.transactionbalancing.app.TransactionBalancingHelloWorld;
import org.optaplanner.examples.transactionbalancing.persistence.TransactionBalancingGenerator;

/**
 * @see TransactionBalancingHelloWorld
 */
public class TransactionBalancingBenchmarkHelloWorld {

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        boolean advanced = argList.contains("--advanced");
        if (!advanced) {
            runBasicBenchmark();
        } else {
            boolean aggregator = argList.contains("--aggregator");
            runAdvancedBenchmark(aggregator);
        }
    }

    /**
     * Basic (no benchmark XML): just benchmark the solver config
     */
    public static void runBasicBenchmark() {
        // Build the PlannerBenchmark
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "org/optaplanner/examples/transactionbalancing/transactionBalancingSolverConfig.xml");

        TransactionBalancingGenerator generator = new TransactionBalancingGenerator();
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(
                generator.createTransactionBalance(200, 600),
                generator.createTransactionBalance(400, 1200));

        // Benchmark the problem and show it
        benchmark.benchmarkAndShowReportInBrowser();
    }

    /**
     * Advanced (benchmark XML): benchmark multiple solver configurations
     */
    public static void runAdvancedBenchmark(boolean aggregator) {
        // Build the PlannerBenchmark
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingBenchmarkConfig.xml");

        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        // Benchmark the problem and show it
        benchmark.benchmarkAndShowReportInBrowser();

        // Show aggregator to aggregate multiple reports
        if (aggregator) {
            BenchmarkAggregatorFrame.createAndDisplayFromXmlResource(
                    "org/optaplanner/examples/transactionbalancing/optional/benchmark/transactionBalancingBenchmarkConfig.xml");
        }
    }

}
