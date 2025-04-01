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

package org.optaplanner.examples.transactionbalancing.domain.solver;

import lombok.Setter;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.transactionbalancing.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * in construction and reset, it is always maintained to initiate the planMap and having the ready to use
 * ComputerPlan for all the computers
 */
public class ComputingPlan {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Computer, ComputerPlan> computerPlanMap;
    private int hardScore;
    private int softScore;
    private int mediumScore;

    private Map<UnorderedPair<Long>, Boolean> conflictsMap;
    @Setter
    private List<OrderedPair<Long>> nonCommutativeList; // (first, second) orders must be preserved

    @Setter
    private Integer timeWeigh;
    @Setter
    private Integer costWeigh;

    private void declareConflicts(List<UnorderedPair<Long>> conflictList) {
        if (this.conflictsMap == null) {
            this.conflictsMap = new HashMap<>();
        }
        for (UnorderedPair<Long> unorderedPair : conflictList) {
            conflictsMap.put(unorderedPair, true);
        }
    }

    public ComputingPlan(EmvBalance solution) {
        computerPlanMap = new HashMap<>(solution.getComputerList().size());
        for (Computer computer : solution.getComputerList()) {
            ComputerPlan plan = new ComputerPlan(computer);
            computerPlanMap.put(computer, plan);
        }
        declareConflicts(solution.getConflictList());
        setNonCommutativeList(solution.getNonCommutativeList());
        setCostWeigh(solution.getCostWeigh());
        setTimeWeigh(solution.getTimeWeigh());
        hardScore = 0;
        softScore = 0;
        mediumScore = 0;
    }

    public void addSlot(Computer computer, EmvProcess process) {
        TimeSlot timeSlot = new TimeSlot(process);
        if (existProcessInPlans(timeSlot)) {
            logger.error("duplicate process is requested to add:" + process);
        }
        computerPlanMap.get(computer).addTimeSlot(timeSlot);
    }

    private boolean existProcessInPlans(TimeSlot timeSlot) {
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            if (computerPlan.getTimeSlots().indexOf(timeSlot) >= 0) {
                return true;
            }
        }
        return false;
    }

    public void removeSlot(Computer computer, EmvProcess process) {
        TimeSlot timeSlot = new TimeSlot(process);
        computerPlanMap.get(computer).removeTimeSlot(timeSlot);
    }

    public void removeSlot(EmvProcess process) {//temporary method
        TimeSlot timeSlot = new TimeSlot(process);
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            if (computerPlan.getTimeSlots().indexOf(timeSlot) != -1) {
                removeSlot(computerPlan.getComputer(), process);
            }
        }
    }


    /**
     * @param processId
     * @return the timeSlot of givven processId by searching all the computers and return null if not find it.
     * Note that the computer id is accessible through TimeSlot.Process.Computer.id
     */
    public TimeSlot getTimeSlot(Long processId) {
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            for (TimeSlot slot : computerPlan.getTimeSlots()) {
                if (slot.getProcess().getId() == processId)
                    return slot;
            }
        }
        return null;
    }

    /**
     * get the maximum of all busy time for all computers
     *
     * @return
     */
    public long getBusyTime() {
        if (computerPlanMap == null || computerPlanMap.keySet().isEmpty()) {
            return 0;
        }
        long maxBusyTime = 0; // between all computer plans
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            if (computerPlan.getBusyTime() > maxBusyTime) {
                maxBusyTime = computerPlan.getBusyTime();
            }
        }
        return maxBusyTime;
    }

    /**
     * @return sum of all idle time of all computers
     */
    private long getIdleTime() {
        if (computerPlanMap == null || computerPlanMap.keySet().isEmpty()) {
            return 0;
        }
        long idleTimeSum = 0; // between all computer plans
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            idleTimeSum += computerPlan.getIdleTime();
        }
        return idleTimeSum;
    }

    private int getUsedComputersCount() {
        int counter = 0;
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            if (computerPlan.getBusyTime() > 0) {
                counter++;
            }
        }
        return counter;
    }

    public HardMediumSoftScore getScore() {
        //recalculating the scores
        hardScore = 0;
        mediumScore = 0;
        softScore = 0;


        //if a plan is possible with minimal computers, it is more favorable
        int usedComputers = getUsedComputersCount();
        softScore += -1 * usedComputers;

        //give hard penalty for unassigned processes to any computer
        // I specify nullable = false for computer in process class and so it is not needed now

        //give soft penalty for busy time
        int busyTime = (int) getBusyTime();

        //give soft penalty for any idle time before any processes in any computer
        int idleTime = (int) getIdleTime();

        //give penalty for computer cost model
        int computerCosts = (int) getCosts();

        mediumScore += -1 * ((idleTime + busyTime) * timeWeigh) + (computerCosts * costWeigh);

        //give a VERY hard penalty for any conflicting transaction
        long conflictingTime = getConflictingTime();
        hardScore += (int) conflictingTime * Integer.MAX_VALUE;

        //give a VERY hard penalty for any commutativity deviation count
        long commutativityDeviationCount = getCommutativityDeviationCount();
        hardScore += (int) commutativityDeviationCount * Integer.MAX_VALUE;


        logger.trace("Soft: usedComputers: " + usedComputers + ", busyTime: " + busyTime + ", idleTime: " +
                +idleTime + ", Hard: conflictingTime: " + conflictingTime + ", commutativityDeviationCount: " + commutativityDeviationCount);
        return HardMediumSoftScore.of(hardScore, mediumScore, softScore);
    }

    private long getCosts() {
        if (computerPlanMap == null || computerPlanMap.keySet().isEmpty()) {
            return 0;
        }
        long costs = 0; // in all computer plans
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            Integer costPerOperation = computerPlan.getComputer().getCostPerOperation();
            Integer costPerIdleTime = computerPlan.getComputer().getCostPerIdleTime();

            for (TimeSlot slot: computerPlan.getTimeSlots()) {
                costs += (slot.getProcess().getOperationCount()*costPerOperation);
                costs += (slot.getProcess().getIdleTimeBeforeProcess()*costPerIdleTime);
            }
        }
        return costs;
    }


    /**
     * sum up all the time that any two transactions have conflicts, if at any time more than tow transactions may
     * conflict, it is counted as twice.
     *
     * @return
     */
    private Long getConflictingTime() {
        int emvBusyTime = (int) getBusyTime();
        long conflictingCount = 0;
        List<Long> concurrentProcessList = new ArrayList<>();
        for (long time = 0; time <= emvBusyTime; time++) {//in each time
            concurrentProcessList.clear();
            for (ComputerPlan computerPlan : computerPlanMap.values()) {//iterate the computers
                long processId = computerPlan.getProcessAt(time);//find the active process
                if (processId != -1) {
                    concurrentProcessList.add(processId);
                }
            }
            conflictingCount += (long) getConflictingCount(concurrentProcessList);
        }
        return conflictingCount;
    }

    private Integer getConflictingCount(List<Long> concurrentProcessList) {
        Integer count = 0;
        UnorderedPair<Long> unorderedPair = new UnorderedPair();//instantiate once for better performance
        for (int i = 0; i < concurrentProcessList.size() - 1; i++) {
            for (int j = i + 1; j < concurrentProcessList.size(); j++) {
                unorderedPair.setValues(concurrentProcessList.get(i), concurrentProcessList.get(j));
                if (conflictsMap.get(unorderedPair) != null) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * counts how many times the commutativity constraint is deviated in all the computer plans and return the sum of
     * the counts.
     * <p>
     * Every non-commutative methods are conflicting, because conflict times are counted in another method so here we do
     * not consider the conflicting anymore, and so we just compare the actual execution start times (not slot start
     * time). so if two conflicting and non-commutative transactions starts with correct order but have overlap
     * execution time, this method does not count them because they are correct in terms of commutativity and in
     * terms of conflict will be counted in a separate method.
     *
     * @return
     */
    private Integer getCommutativityDeviationCount() {
        Integer commutativityDeviationCount = 0;
        for (OrderedPair<Long> pair : nonCommutativeList) {
            Long firstProcessId = pair.getFirst();
            Long secondProcessId = pair.getSecond();
            TimeSlot firstSlot = getTimeSlot(firstProcessId);
            TimeSlot secondSlot = getTimeSlot(secondProcessId);
            if(firstSlot != null && secondSlot != null) {
                if (!(firstSlot.getExeStart() < secondSlot.getExeStart())) {// if the second one start before the first
                    // one finish, then
                    commutativityDeviationCount++;
                }
            }
        }
        return commutativityDeviationCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ComputingPlan " +
                        "(hardScore=").append(hardScore).append(", ").
                append("softScore=").append(softScore).append("):\n");
        for (ComputerPlan computerPlan : computerPlanMap.values()) {
            sb.append("\t").append(computerPlan.toString() + "\n");
        }
        return sb.toString();
    }

    public void resetWorkingSolution() {
        if (computerPlanMap != null) {
            for (Computer computer : computerPlanMap.keySet()) {
                computerPlanMap.get(computer).reset();
            }
        }
        hardScore = 0;
        softScore = 0;
    }
}
