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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(scope = Computer.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Computer
        extends AbstractPersistable
        implements Labeled {

    private Integer costPerOperation;
    private Integer costPerIdleTime;

    //    Not involved parameters in this phase
    /*is initiated with zero,  will increase by adding further processes to be calculated by this
     computer, can be idle between, in millisecond*/
    /*private long busyTime;
    private String name;
    private float clockSpeed; //GHz
    private int icp; // Instructions per Cycle (IPC) - can vary based on workload
    private float instructionSpeed; //Giga per second = clockSpeed * ICP*/

    /*private int cpuPower; // in gigahertz
    private int memory; // in gigabyte RAM
    private int networkBandwidth; // in gigabyte per hour
    private int cost; // in euro per month*/

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public int getMultiplicand() {
//        return cpuPower * memory * networkBandwidth;
        return 0;
    }

    @Override
    @JsonIgnore
    public String getLabel() {
        return "Computer " + id;
    }

    @Override
    public String toString() {
        return "c-"+id+ ", cost: "+costPerOperation;
    }
}
