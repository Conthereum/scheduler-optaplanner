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

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.examples.transactionbalancing.domain.Computer;
import org.optaplanner.examples.transactionbalancing.domain.EmvBalance;
import org.optaplanner.examples.transactionbalancing.domain.EmvProcess;
import org.optaplanner.examples.transactionbalancing.domain.solver.ComputingPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EmvTransactionBalancingIncrementalScoreCalculator
        implements IncrementalScoreCalculator<EmvBalance, HardMediumSoftScore> {
    private EmvBalance solutionRef;
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final Lock lock = new ReentrantLock();
    private boolean locked = false;
    private boolean resetLocked = false;

    private boolean isFMethodRunning = false;

    @Override
    /**
     * Note: a new solution is presented so adjust everything based on that
     */
    public synchronized void resetWorkingSolution(EmvBalance solution) {
        lock.lock();
        resetLocked = true;
        if(locked){
            logger.error("interference happened!");
        }
        logger.trace("resetWorkingSolution - input solution: " + solution);
        if (solutionRef == null) {
            solutionRef = solution;
        }
        if (solution.getComputingPlan() == null) {
            ComputingPlan computingPlan = new ComputingPlan(solution);
            solution.setComputingPlan(computingPlan);
        } else {
            solution.resetWorkingSolution();
        }

        for (EmvProcess process : solution.getProcessList()) {
            insert(process);
        }
        solution.setScore(solution.getComputingPlan().getScore());
        logger.trace("resetWorkingSolution - resulted solution: " + solution);
        resetLocked = false;
        lock.unlock();
    }

    @Override
    public synchronized void beforeEntityAdded(Object entity) {
        lock.lock();
        locked = true;
        logger.trace("beforeEntityAdded - entity: " + entity);
        retract((EmvProcess) entity);

    }

    @Override
    public synchronized void afterEntityAdded(Object entity) {
        lock.unlock();
        logger.trace("afterEntityAdded - entity: " + entity);
        insert((EmvProcess) entity);
        locked = false;
    }


    @Override
    public synchronized void beforeVariableChanged(Object entity, String variableName) {
        lock.lock();
        locked = true;
        if(resetLocked){
            logger.error("middle of reset");
        }
        logger.trace("beforeVariableChanged - entity: " + entity + ", variableName: " + variableName);
        retract((EmvProcess) entity);
//        debugRetractWithTimeout(entity);
        logger.trace("beforeVariableChanged - done");
    }

    @Override
    public synchronized void afterVariableChanged(Object entity, String variableName) {
        logger.trace("afterVariableChanged - entity: " + entity + ", variableName: " + variableName);
        insert((EmvProcess) entity);
        if(resetLocked){
            logger.error("middle of reset");
        }
//        debugInsertWithTimeout(entity);
        logger.trace("afterVariableChanged - done");
        locked = false;
        lock.unlock();
    }

    @Override
    public synchronized void beforeEntityRemoved(Object entity) {
        lock.lock();
        locked = true;
        logger.trace("beforeEntityRemoved - entity: " + entity);
        retract((EmvProcess) entity);
        logger.trace("beforeEntityRemoved - done");
    }

    @Override
    public synchronized void afterEntityRemoved(Object entity) {
        // Do nothing, everything has done in beforeEntityRemoved
        logger.trace("afterEntityRemoved - do nothing");
        locked = false;
        lock.unlock();
    }

    public void debugRetractWithTimeout(Object entity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            retract((EmvProcess) entity);  // This is the method that might run infinitely
        });

        try {
            // Wait for 10 seconds to see if the method finishes
            future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // This is where the method takes too long, so we can put a breakpoint here
            System.out.println("Timeout reached, method didn't finish in 10 seconds");
            // Put a breakpoint here to inspect the internal state at the time of timeout
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            future.cancel(true);  // Cancel the task if it's still running
            executor.shutdown();
        }
    }


    public void debugInsertWithTimeout(Object entity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            insert((EmvProcess) entity);  // This is the method that might run infinitely
        });

        try {
            // Wait for 10 seconds to see if the method finishes
            future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // This is where the method takes too long, so we can put a breakpoint here
            System.out.println("Timeout reached, method didn't finish in 10 seconds");
            // Put a breakpoint here to inspect the internal state at the time of timeout
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            future.cancel(true);  // Cancel the task if it's still running
            executor.shutdown();
        }
    }

    private void retract(EmvProcess process) {
        Computer computer = process.getComputer();
        if (computer != null) {
            //todo computer in the process is not synch with the solution, why?
            solutionRef.getComputingPlan().removeSlot(computer, process);
//            solutionRef.getComputingPlan().removeSlot(process);//temp method
        }
    }

    private void insert(EmvProcess process) {
        Computer computer = process.getComputer();
        if (computer != null) {
            solutionRef.getComputingPlan().addSlot(computer, process);
        }
    }

    @Override
    public synchronized HardMediumSoftScore calculateScore() {
        HardMediumSoftScore hardSoftScore = solutionRef.getComputingPlan().getScore();
        logger.trace("calculateScore - return: " + hardSoftScore);
        logger.trace("calculateScore - solution: " + solutionRef);
        return hardSoftScore;
    }
}
