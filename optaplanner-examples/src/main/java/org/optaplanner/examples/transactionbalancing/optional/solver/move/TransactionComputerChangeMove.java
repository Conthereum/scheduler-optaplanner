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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.common.util.Util;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;

public class TransactionComputerChangeMove extends AbstractMove<TransactionBalance> {

    private TransactionProcess transactionProcess;
    private TransactionComputer toTransactionComputer;

    public TransactionComputerChangeMove(TransactionProcess transactionProcess, TransactionComputer toTransactionComputer) {
        this.transactionProcess = transactionProcess;
        this.toTransactionComputer = toTransactionComputer;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<TransactionBalance> scoreDirector) {
        return !Objects.equals(transactionProcess.getComputer(), toTransactionComputer);
    }

    @Override
    public TransactionComputerChangeMove createUndoMove(ScoreDirector<TransactionBalance> scoreDirector) {
        return new TransactionComputerChangeMove(transactionProcess, transactionProcess.getComputer());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<TransactionBalance> scoreDirector) {
        scoreDirector.beforeVariableChanged(transactionProcess, "computer");
        transactionProcess.setComputer(toTransactionComputer);
        scoreDirector.afterVariableChanged(transactionProcess, "computer");
    }

    @Override
    public TransactionComputerChangeMove rebase(ScoreDirector<TransactionBalance> destinationScoreDirector) {
        return new TransactionComputerChangeMove(destinationScoreDirector.lookUpWorkingObject(transactionProcess),
                destinationScoreDirector.lookUpWorkingObject(toTransactionComputer));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + TransactionProcess.class.getSimpleName() + ".computer)";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(transactionProcess);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toTransactionComputer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionComputerChangeMove other = (TransactionComputerChangeMove) o;
        return Objects.equals(transactionProcess, other.transactionProcess) &&
                Objects.equals(toTransactionComputer, other.toTransactionComputer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionProcess, toTransactionComputer);
    }

    @Override
    public String toString() {
        return transactionProcess + " {" + transactionProcess.getComputer() + " -> " + toTransactionComputer + "}";
    }

}
