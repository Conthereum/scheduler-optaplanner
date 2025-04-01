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

import org.optaplanner.examples.transactionbalancing.domain.Computer;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class ComputerStrengthComparator implements Comparator<Computer> {

    private static final Comparator<Computer> COMPARATOR = comparingInt(Computer::getMultiplicand)
//            .thenComparing(Collections.reverseOrder(comparing(EmvComputer::getCost))) // Descending (but this is debatable)
            .thenComparingLong(Computer::getId);

    @Override
    public int compare(Computer a, Computer b) {
        return COMPARATOR.compare(a, b);
    }
}
