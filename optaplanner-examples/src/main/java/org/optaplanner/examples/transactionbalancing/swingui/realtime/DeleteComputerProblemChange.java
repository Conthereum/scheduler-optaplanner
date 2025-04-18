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

package org.optaplanner.examples.transactionbalancing.swingui.realtime;

import java.util.ArrayList;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;

public class DeleteComputerProblemChange implements ProblemChange<TransactionBalance> {

    private final TransactionComputer computer;

    public DeleteComputerProblemChange(TransactionComputer computer) {
        this.computer = computer;
    }

    @Override
    public void doChange(TransactionBalance cloudBalance, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.lookUpWorkingObject(computer)
                .ifPresentOrElse(workingComputer -> {
                    // First remove the problem fact from all planning entities that use it
                    for (TransactionProcess process : cloudBalance.getProcessList()) {
                        if (process.getComputer() == workingComputer) {
                            problemChangeDirector.changeVariable(process, "computer",
                                    workingProcess -> workingProcess.setComputer(null));
                        }
                    }
                    // A SolutionCloner does not clone problem fact lists (such as computerList)
                    // Shallow clone the computerList so only workingSolution is affected, not bestSolution or guiSolution
                    ArrayList<TransactionComputer> computerList = new ArrayList<>(cloudBalance.getComputerList());
                    cloudBalance.setComputerList(computerList);
                    // Remove the problem fact itself
                    problemChangeDirector.removeProblemFact(workingComputer, computerList::remove);
                }, () -> {
                    // The computer has already been deleted (the UI asked to changed the same computer twice).
                });
    }

}
