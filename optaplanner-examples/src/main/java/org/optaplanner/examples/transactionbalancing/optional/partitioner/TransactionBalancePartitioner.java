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

package org.optaplanner.examples.transactionbalancing.optional.partitioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;
import org.optaplanner.examples.common.util.Pair;

public class TransactionBalancePartitioner implements SolutionPartitioner<TransactionBalance> {

    private int partCount = 4;
    private int minimumProcessListSize = 25;

    @SuppressWarnings("unused")
    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    @SuppressWarnings("unused")
    public void setMinimumProcessListSize(int minimumProcessListSize) {
        this.minimumProcessListSize = minimumProcessListSize;
    }

    @Override
    public List<TransactionBalance> splitWorkingSolution(ScoreDirector<TransactionBalance> scoreDirector, Integer runnablePartThreadLimit) {
        TransactionBalance originalSolution = scoreDirector.getWorkingSolution();
        List<TransactionComputer> originalComputerList = originalSolution.getComputerList();
        List<TransactionProcess> originalProcessList = originalSolution.getProcessList();
        int partCount = this.partCount;
        if (originalProcessList.size() / partCount < minimumProcessListSize) {
            partCount = originalProcessList.size() / minimumProcessListSize;
        }
        List<TransactionBalance> partList = new ArrayList<>(partCount);
        for (int i = 0; i < partCount; i++) {
            TransactionBalance partSolution = new TransactionBalance(originalSolution.getId(),
                    new ArrayList<>(originalComputerList.size() / partCount + 1),
                    new ArrayList<>(originalProcessList.size() / partCount + 1));
            partList.add(partSolution);
        }

        int partIndex = 0;
        Map<Long, Pair<Integer, TransactionComputer>> idToPartIndexAndComputerMap = new HashMap<>(originalComputerList.size());
        for (TransactionComputer originalComputer : originalComputerList) {
            TransactionBalance part = partList.get(partIndex);
            TransactionComputer computer = new TransactionComputer(
                    originalComputer.getId(),
                    originalComputer.getCpuPower(), originalComputer.getMemory(),
                    originalComputer.getNetworkBandwidth(), originalComputer.getCost());
            part.getComputerList().add(computer);
            idToPartIndexAndComputerMap.put(computer.getId(), Pair.of(partIndex, computer));
            partIndex = (partIndex + 1) % partList.size();
        }

        partIndex = 0;
        for (TransactionProcess originalProcess : originalProcessList) {
            TransactionBalance part = partList.get(partIndex);
            TransactionProcess process = new TransactionProcess(
                    originalProcess.getId(),
                    originalProcess.getRequiredCpuPower(), originalProcess.getRequiredMemory(),
                    originalProcess.getRequiredNetworkBandwidth());
            part.getProcessList().add(process);
            if (originalProcess.getComputer() != null) {
                Pair<Integer, TransactionComputer> partIndexAndComputer = idToPartIndexAndComputerMap.get(
                        originalProcess.getComputer().getId());
                if (partIndexAndComputer == null) {
                    throw new IllegalStateException("The initialized process (" + originalProcess
                            + ") has a computer (" + originalProcess.getComputer()
                            + ") which doesn't exist in the originalSolution (" + originalSolution + ").");
                }
                if (partIndex != partIndexAndComputer.getKey().intValue()) {
                    throw new IllegalStateException("The initialized process (" + originalProcess
                            + ") with partIndex (" + partIndex
                            + ") has a computer (" + originalProcess.getComputer()
                            + ") which belongs to another partIndex (" + partIndexAndComputer.getKey() + ").");
                }
                process.setComputer(partIndexAndComputer.getValue());
            }
            partIndex = (partIndex + 1) % partList.size();
        }
        return partList;
    }

}
