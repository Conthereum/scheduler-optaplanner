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

package org.optaplanner.examples.transactionbalancing.score;

import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class TransactionBalancingConstraintProviderTest
        extends AbstractConstraintProviderTest<TransactionBalancingConstraintProvider, TransactionBalance> {

    @ConstraintProviderTest
    void requiredCpuPowerTotal(ConstraintVerifier<TransactionBalancingConstraintProvider, TransactionBalance> constraintVerifier) {
        TransactionComputer computer1 = new TransactionComputer(1, 1, 1, 1, 2);
        TransactionComputer computer2 = new TransactionComputer(2, 2, 2, 2, 4);
        TransactionProcess unassignedProcess = new TransactionProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        TransactionProcess process1 = new TransactionProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        TransactionProcess process2 = new TransactionProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        TransactionProcess process3 = new TransactionProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(TransactionBalancingConstraintProvider::requiredCpuPowerTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void requiredMemoryTotal(ConstraintVerifier<TransactionBalancingConstraintProvider, TransactionBalance> constraintVerifier) {
        TransactionComputer computer1 = new TransactionComputer(1, 1, 1, 1, 2);
        TransactionComputer computer2 = new TransactionComputer(2, 2, 2, 2, 4);
        TransactionProcess unassignedProcess = new TransactionProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        TransactionProcess process1 = new TransactionProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        TransactionProcess process2 = new TransactionProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        TransactionProcess process3 = new TransactionProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(TransactionBalancingConstraintProvider::requiredMemoryTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void requiredNetworkBandwidthTotal(ConstraintVerifier<TransactionBalancingConstraintProvider, TransactionBalance> constraintVerifier) {
        TransactionComputer computer1 = new TransactionComputer(1, 1, 1, 1, 2);
        TransactionComputer computer2 = new TransactionComputer(2, 2, 2, 2, 4);
        TransactionProcess unassignedProcess = new TransactionProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        TransactionProcess process1 = new TransactionProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        TransactionProcess process2 = new TransactionProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        TransactionProcess process3 = new TransactionProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(TransactionBalancingConstraintProvider::requiredNetworkBandwidthTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void computerCost(ConstraintVerifier<TransactionBalancingConstraintProvider, TransactionBalance> constraintVerifier) {
        TransactionComputer computer1 = new TransactionComputer(1, 1, 1, 1, 2);
        TransactionComputer computer2 = new TransactionComputer(2, 2, 2, 2, 4);
        TransactionProcess unassignedProcess = new TransactionProcess(0, 1, 1, 1);
        TransactionProcess process = new TransactionProcess(1, 1, 1, 1);
        process.setComputer(computer1);

        constraintVerifier.verifyThat(TransactionBalancingConstraintProvider::computerCost)
                .given(computer1, computer2, unassignedProcess, process)
                .penalizesBy(2);
    }

    @Override
    protected ConstraintVerifier<TransactionBalancingConstraintProvider, TransactionBalance> createConstraintVerifier() {
        return ConstraintVerifier.build(new TransactionBalancingConstraintProvider(), TransactionBalance.class, TransactionProcess.class);

    }
}
