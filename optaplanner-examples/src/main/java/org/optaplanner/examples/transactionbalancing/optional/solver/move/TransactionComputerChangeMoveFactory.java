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

package org.optaplanner.examples.transactionbalancing.optional.solver.move;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;

public class TransactionComputerChangeMoveFactory implements MoveListFactory<TransactionBalance> {

    @Override
    public List<TransactionComputerChangeMove> createMoveList(TransactionBalance cloudBalance) {
        List<TransactionComputerChangeMove> moveList = new ArrayList<>();
        List<TransactionComputer> cloudComputerList = cloudBalance.getComputerList();
        for (TransactionProcess cloudProcess : cloudBalance.getProcessList()) {
            for (TransactionComputer transactionComputer : cloudComputerList) {
                moveList.add(new TransactionComputerChangeMove(cloudProcess, transactionComputer));
            }
        }
        return moveList;
    }

}
