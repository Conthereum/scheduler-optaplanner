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

package org.optaplanner.examples.transactionbalancing.domain;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.*;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.transactionbalancing.domain.solver.ComputingPlan;

import java.util.List;
import java.util.Set;

@PlanningSolution
@Getter
@Setter
public class EmvBalance extends AbstractPersistable {
    @ValueRangeProvider(id="computerList")
    @ProblemFactCollectionProperty
    private List<Computer> computerList;

    @PlanningEntityCollectionProperty
    private Set<EmvProcess> processList;

    @ProblemFactProperty
    private Integer timeWeigh;

    @ProblemFactProperty
    private Integer costWeigh;

    @PlanningScore
    private HardMediumSoftScore score;

    @ValueRangeProvider(id = "idleTimeRange")
    public CountableValueRange<Long> getIdleTimeRange() {
        return ValueRangeFactory.createLongValueRange(0L, 1000L); // From 0 to 1000, adjust as needed
    }

    //updated manually
    private ComputingPlan computingPlan;

    // conflicting processes
    private List<UnorderedPair<Long>> conflictList;

    //commutative processes
    private List<OrderedPair<Long>> nonCommutativeList;

    @Override
    public String toString() {
        return "EmvBalance{" +
                "computerList=" + computerList +
                ", processList=" + processList +
                ", timeWeigh=" + timeWeigh +
                ", costWeigh=" + costWeigh +
                ", score=" + score +
                ", computingPlan=" + computingPlan +
                ", conflictList=" + conflictList +
                ", nonCommutativeList=" + nonCommutativeList +
                '}';
    }

    public void resetWorkingSolution() {
        computingPlan.resetWorkingSolution();
//        score = computingPlan.getScore(); // it is calculated after inserting the processes and making their
//        affects on slots in the score calculator immediately after calling this method
    }
}
