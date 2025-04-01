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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;

public class TransactionBalancingMapBasedEasyScoreCalculator implements EasyScoreCalculator<TransactionBalance, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(TransactionBalance transactionBalance) {
        int computerListSize = transactionBalance.getComputerList().size();
        Map<TransactionComputer, Integer> cpuPowerUsageMap = new HashMap<>(computerListSize);
        Map<TransactionComputer, Integer> memoryUsageMap = new HashMap<>(computerListSize);
        Map<TransactionComputer, Integer> networkBandwidthUsageMap = new HashMap<>(computerListSize);
        for (TransactionComputer computer : transactionBalance.getComputerList()) {
            cpuPowerUsageMap.put(computer, 0);
            memoryUsageMap.put(computer, 0);
            networkBandwidthUsageMap.put(computer, 0);
        }
        Set<TransactionComputer> usedComputerSet = new HashSet<>(computerListSize);

        visitProcessList(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap,
                usedComputerSet, transactionBalance.getProcessList());

        int hardScore = sumHardScore(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap);
        int softScore = sumSoftScore(usedComputerSet);

        return HardSoftScore.of(hardScore, softScore);
    }

    private void visitProcessList(Map<TransactionComputer, Integer> cpuPowerUsageMap,
                                  Map<TransactionComputer, Integer> memoryUsageMap, Map<TransactionComputer, Integer> networkBandwidthUsageMap,
                                  Set<TransactionComputer> usedComputerSet, List<TransactionProcess> processList) {
        // We loop through the processList only once for performance
        for (TransactionProcess process : processList) {
            TransactionComputer computer = process.getComputer();
            if (computer != null) {
                int cpuPowerUsage = cpuPowerUsageMap.get(computer) + process.getRequiredCpuPower();
                cpuPowerUsageMap.put(computer, cpuPowerUsage);
                int memoryUsage = memoryUsageMap.get(computer) + process.getRequiredMemory();
                memoryUsageMap.put(computer, memoryUsage);
                int networkBandwidthUsage = networkBandwidthUsageMap.get(computer) + process.getRequiredNetworkBandwidth();
                networkBandwidthUsageMap.put(computer, networkBandwidthUsage);
                usedComputerSet.add(computer);
            }
        }
    }

    private int sumHardScore(Map<TransactionComputer, Integer> cpuPowerUsageMap, Map<TransactionComputer, Integer> memoryUsageMap,
                             Map<TransactionComputer, Integer> networkBandwidthUsageMap) {
        int hardScore = 0;
        for (Map.Entry<TransactionComputer, Integer> usageEntry : cpuPowerUsageMap.entrySet()) {
            TransactionComputer computer = usageEntry.getKey();
            int cpuPowerAvailable = computer.getCpuPower() - usageEntry.getValue();
            if (cpuPowerAvailable < 0) {
                hardScore += cpuPowerAvailable;
            }
        }
        for (Map.Entry<TransactionComputer, Integer> usageEntry : memoryUsageMap.entrySet()) {
            TransactionComputer computer = usageEntry.getKey();
            int memoryAvailable = computer.getMemory() - usageEntry.getValue();
            if (memoryAvailable < 0) {
                hardScore += memoryAvailable;
            }
        }
        for (Map.Entry<TransactionComputer, Integer> usageEntry : networkBandwidthUsageMap.entrySet()) {
            TransactionComputer computer = usageEntry.getKey();
            int networkBandwidthAvailable = computer.getNetworkBandwidth() - usageEntry.getValue();
            if (networkBandwidthAvailable < 0) {
                hardScore += networkBandwidthAvailable;
            }
        }
        return hardScore;
    }

    private int sumSoftScore(Set<TransactionComputer> usedComputerSet) {
        int softScore = 0;
        for (TransactionComputer usedComputer : usedComputerSet) {
            softScore -= usedComputer.getCost();
        }
        return softScore;
    }

}
