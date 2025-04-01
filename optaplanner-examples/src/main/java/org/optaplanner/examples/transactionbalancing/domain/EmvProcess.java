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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.common.util.Util;
import org.optaplanner.examples.transactionbalancing.domain.solver.ComputerStrengthComparator;
import org.optaplanner.examples.transactionbalancing.domain.solver.ProcessDifficultyComparator;

import java.util.Objects;

@Getter
@Setter
@PlanningEntity(difficultyComparatorClass = ProcessDifficultyComparator.class)
public class EmvProcess extends AbstractPersistable implements Labeled {

    // facts; in millisecond
    @NonNull
    private long estimatedExecutionTime;

    @NonNull
    private Integer operationCount;

    @PlanningVariable(valueRangeProviderRefs = "computerList", strengthComparatorClass =
            ComputerStrengthComparator.class, nullable = false)
    private Computer computer;

    //equal or greater than zero, must be of type Long not primitive long, then the huristic algorithm does not think
    // it is already initiated with zero
    @PlanningVariable(valueRangeProviderRefs = "idleTimeRange")
    private Long idleTimeBeforeProcess;

/*//     the time process starts. the base time is starting the block process, in millisecond
    private Long startTime;

//    Next Phase will be involved
    private static final int GAS_TO_OPERATION_CONSTANT = 10;
    private String hash;
    private int estimatedGas;
    private int estimatedOperationCount;*/


    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public int getRequiredMultiplicand() {
//        return requiredCpuPower * requiredMemory * requiredNetworkBandwidth;
        return 1;
    }

    @Override
    public String getLabel() {
        return "Process " + id;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", exeTime=" + estimatedExecutionTime +
                ", operationCount="+operationCount+
                ", idleTime=" + idleTimeBeforeProcess +
                ", computer=" + computer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmvProcess)) return false;
        EmvProcess that = (EmvProcess) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
