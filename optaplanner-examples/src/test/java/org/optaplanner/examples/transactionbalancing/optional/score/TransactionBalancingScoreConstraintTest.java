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

package org.optaplanner.examples.transactionbalancing.optional.score;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.transactionbalancing.app.TransactionBalancingApp;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;

class TransactionBalancingScoreConstraintTest {

    private HardSoftScoreVerifier<TransactionBalance> scoreVerifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(TransactionBalancingApp.SOLVER_CONFIG));

    @Test
    void requiredCpuPowerTotal() {
        TransactionComputer c1 = new TransactionComputer(1L, 1000, 1, 1, 1);
        TransactionComputer c2 = new TransactionComputer(2L, 200, 1, 1, 1);
        TransactionComputer c3 = new TransactionComputer(3L, 30, 1, 1, 1);
        TransactionProcess p1 = new TransactionProcess(1L, 700, 5, 5);
        TransactionProcess p2 = new TransactionProcess(2L, 70, 5, 5);
        TransactionProcess p3 = new TransactionProcess(3L, 7, 5, 5);
        TransactionBalance solution = new TransactionBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", 0, solution);
        p1.setComputer(c2);
        p2.setComputer(c2);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", -570, solution);
        p3.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", -570, solution);
        p2.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", -547, solution);
    }

    @Test
    void requiredMemoryTotal() {
        TransactionComputer c1 = new TransactionComputer(1L, 1, 1000, 1, 1);
        TransactionComputer c2 = new TransactionComputer(2L, 1, 200, 1, 1);
        TransactionComputer c3 = new TransactionComputer(3L, 1, 30, 1, 1);
        TransactionProcess p1 = new TransactionProcess(1L, 5, 700, 5);
        TransactionProcess p2 = new TransactionProcess(2L, 5, 70, 5);
        TransactionProcess p3 = new TransactionProcess(3L, 5, 7, 5);
        TransactionBalance solution = new TransactionBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertHardWeight("requiredMemoryTotal", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", 0, solution);
        p1.setComputer(c2);
        p2.setComputer(c2);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", -570, solution);
        p3.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", -570, solution);
        p2.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", -547, solution);
    }

    @Test
    void requiredNetworkBandwidthTotal() {
        TransactionComputer c1 = new TransactionComputer(1L, 1, 1, 1000, 1);
        TransactionComputer c2 = new TransactionComputer(2L, 1, 1, 200, 1);
        TransactionComputer c3 = new TransactionComputer(3L, 1, 1, 30, 1);
        TransactionProcess p1 = new TransactionProcess(1L, 5, 5, 700);
        TransactionProcess p2 = new TransactionProcess(2L, 5, 5, 70);
        TransactionProcess p3 = new TransactionProcess(3L, 5, 5, 7);
        TransactionBalance solution = new TransactionBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", 0, solution);
        p1.setComputer(c2);
        p2.setComputer(c2);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", -570, solution);
        p3.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", -570, solution);
        p2.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", -547, solution);
    }

    @Test
    void computerCost() {
        TransactionComputer c1 = new TransactionComputer(1L, 1, 1, 1, 200);
        TransactionComputer c2 = new TransactionComputer(2L, 1, 1, 1, 30);
        TransactionComputer c3 = new TransactionComputer(3L, 1, 1, 1, 4);
        TransactionProcess p1 = new TransactionProcess(1L, 5, 5, 5);
        TransactionProcess p2 = new TransactionProcess(2L, 5, 5, 5);
        TransactionProcess p3 = new TransactionProcess(3L, 5, 5, 5);
        TransactionBalance solution = new TransactionBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertSoftWeight("computerCost", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertSoftWeight("computerCost", -200, solution);
        p3.setComputer(c3);
        scoreVerifier.assertSoftWeight("computerCost", -204, solution);
    }

}
