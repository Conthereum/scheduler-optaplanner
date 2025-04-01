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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;

public class TransactionProcessSwapMove extends AbstractMove<TransactionBalance> {

    private TransactionProcess leftTransactionProcess;
    private TransactionProcess rightTransactionProcess;

    public TransactionProcessSwapMove(TransactionProcess leftTransactionProcess, TransactionProcess rightTransactionProcess) {
        this.leftTransactionProcess = leftTransactionProcess;
        this.rightTransactionProcess = rightTransactionProcess;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<TransactionBalance> scoreDirector) {
        return !Objects.equals(leftTransactionProcess.getComputer(), rightTransactionProcess.getComputer());
    }

    @Override
    public TransactionProcessSwapMove createUndoMove(ScoreDirector<TransactionBalance> scoreDirector) {
        return new TransactionProcessSwapMove(rightTransactionProcess, leftTransactionProcess);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<TransactionBalance> scoreDirector) {
        TransactionComputer oldLeftTransactionComputer = leftTransactionProcess.getComputer();
        TransactionComputer oldRightTransactionComputer = rightTransactionProcess.getComputer();
        scoreDirector.beforeVariableChanged(leftTransactionProcess, "computer");
        leftTransactionProcess.setComputer(oldRightTransactionComputer);
        scoreDirector.afterVariableChanged(leftTransactionProcess, "computer");
        scoreDirector.beforeVariableChanged(rightTransactionProcess, "computer");
        rightTransactionProcess.setComputer(oldLeftTransactionComputer);
        scoreDirector.afterVariableChanged(rightTransactionProcess, "computer");
    }

    @Override
    public TransactionProcessSwapMove rebase(ScoreDirector<TransactionBalance> destinationScoreDirector) {
        return new TransactionProcessSwapMove(destinationScoreDirector.lookUpWorkingObject(leftTransactionProcess),
                destinationScoreDirector.lookUpWorkingObject(rightTransactionProcess));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + TransactionProcess.class.getSimpleName() + ".computer)";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftTransactionProcess, rightTransactionProcess);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftTransactionProcess.getComputer(), rightTransactionProcess.getComputer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionProcessSwapMove other = (TransactionProcessSwapMove) o;
        return Objects.equals(leftTransactionProcess, other.leftTransactionProcess) &&
                Objects.equals(rightTransactionProcess, other.rightTransactionProcess);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftTransactionProcess, rightTransactionProcess);
    }

    @Override
    public String toString() {
        return leftTransactionProcess + " {" + leftTransactionProcess.getComputer() + "} <-> "
                + rightTransactionProcess + " {" + rightTransactionProcess.getComputer() + "}";
    }

}
