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

package org.optaplanner.examples.transactionbalancing.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.transactionbalancing.domain.*;
import org.optaplanner.examples.transactionbalancing.optional.benchmark.TransactionBalancingBenchmarkHelloWorld;
import org.optaplanner.examples.transactionbalancing.optional.score.EmvTransactionBalancingIncrementalScoreCalculator;
import org.optaplanner.examples.transactionbalancing.persistence.TransactionBalancingGenerator;
import org.optaplanner.examples.transactionbalancing.score.TransactionBalancingConstraintProvider;

import java.time.Duration;

/**
 * To benchmark this solver config, run {@link TransactionBalancingBenchmarkHelloWorld} instead.
 */
public class EmvBalancingHelloWorld {
    public static final String SOLVER_CONFIG = "org/optaplanner/examples/emvtransactionbalancing" +
            "/emvTransactionBalancingSolverConfig.xml";
    public static final String DATA_DIR_NAME = "emvtransactionbalancing";

    public static void main(String[] args) {
        // Build the Solver
        SolverFactory<EmvBalance> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);

//
//                create(new SolverConfig()
//                .withSolutionClass(EmvBalance.class)
//                .withEntityClasses(EmvProcess.class)
////                        .with
//                        .withEasyScoreCalculatorClass(EmvTransactionBalancingIncrementalScoreCalculator.class)
////                .withConstraintProviderClass(TransactionBalancingConstraintProvider.class)
//                .withTerminationSpentLimit(Duration.ofMinutes(2)));
        Solver<EmvBalance> solver = solverFactory.buildSolver();

        // Load a problem with 400 computers and 1200 processes
//        new TransactionBalancingGenerator().createTransactionBalance(400, 1200);
        /*EmvBalance unsolvedBalance =

        // Solve the problem
        EmvBalance solvedBalance = solver.solve(unsolvedBalance);

        // Display the result
        System.out.println("\nSolved solution:\n"
//                + toDisplayString(solvedTransactionBalance));
                +solvedBalance);*/
    }

    /*public static String toDisplayString(EmvBalance cloudBalance) {
        StringBuilder displayString = new StringBuilder();
        for (TransactionProcess process : cloudBalance.getProcessList()) {
            TransactionComputer computer = process.getComputer();
            displayString.append("  ").append(process.getLabel()).append(" -> ")
                    .append(computer == null ? null : computer.getLabel()).append("\n");
        }
        return displayString.toString();
    }*/

}
