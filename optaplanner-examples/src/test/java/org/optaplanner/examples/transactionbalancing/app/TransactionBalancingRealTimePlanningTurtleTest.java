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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;
import org.optaplanner.examples.transactionbalancing.persistence.TransactionBalancingGenerator;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.AddComputerProblemChange;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.AddProcessProblemChange;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.DeleteComputerProblemChange;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.DeleteProcessProblemChange;
import org.optaplanner.examples.common.app.RealTimePlanningTurtleTest;

class TransactionBalancingRealTimePlanningTurtleTest extends RealTimePlanningTurtleTest<TransactionBalance> {

    private TransactionBalancingGenerator generator = new TransactionBalancingGenerator(true);

    private List<TransactionComputer> existingComputerList;
    private List<TransactionProcess> existingProcessList;

    @Override
    protected String createSolverConfigResource() {
        return TransactionBalancingApp.SOLVER_CONFIG;
    }

    @Override
    protected TransactionBalance readProblem() {
        TransactionBalance cloudBalance = generator.createTransactionBalance(1200, 4800);
        existingComputerList = new ArrayList<>(cloudBalance.getComputerList());
        existingProcessList = new ArrayList<>(cloudBalance.getProcessList());
        return cloudBalance;
    }

    @Override
    protected ProblemChange<TransactionBalance> nextProblemChange(Random random) {
        boolean capacityTooLow = existingComputerList.size() <= 20
                || existingComputerList.size() < existingProcessList.size() / 4;
        boolean capacityTooHigh = existingComputerList.size() > existingProcessList.size() / 2;
        if (random.nextBoolean()) {
            if (capacityTooLow || (!capacityTooHigh && random.nextBoolean())) {
                return new AddComputerProblemChange(expectedId -> {
                    TransactionComputer computer = generator.generateComputer(expectedId);
                    existingComputerList.add(computer);
                    return computer;
                });
            } else {
                return new DeleteComputerProblemChange(
                        existingComputerList.remove(random.nextInt(existingComputerList.size())));
            }
        } else {
            if (capacityTooHigh || (!capacityTooLow && random.nextBoolean())) {
                return new AddProcessProblemChange(expectedId -> {
                    TransactionProcess process = generator.generateProcess(expectedId);
                    existingProcessList.add(process);
                    return process;
                });
            } else {
                return new DeleteProcessProblemChange(
                        existingProcessList.remove(random.nextInt(existingProcessList.size())));
            }
        }
    }

}
