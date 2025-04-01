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

package org.optaplanner.examples.transactionbalancing.app;

import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.persistence.TransactionBalanceSolutionFileIO;
import org.optaplanner.examples.transactionbalancing.swingui.TransactionBalancingPanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * For an easy example, look at {@link TransactionBalancingHelloWorld} instead.
 */
public class TransactionBalancingApp extends CommonApp<TransactionBalance> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/transactionbalancing/transactionBalancingSolverConfig.xml";
    public static final String DATA_DIR_NAME = "transactionbalancing";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TransactionBalancingApp().init();
    }

    public TransactionBalancingApp() {
        super("Transaction balancing",
                "Assign processes to computers.\n\n" +
                        "Each computer must have enough hardware to run all of its processes.\n" +
                        "Each used computer inflicts a maintenance cost.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                TransactionBalancingPanel.LOGO_PATH);
    }

    @Override
    protected TransactionBalancingPanel createSolutionPanel() {
        return new TransactionBalancingPanel();
    }

    @Override
    public SolutionFileIO<TransactionBalance> createSolutionFileIO() {
        return new TransactionBalanceSolutionFileIO();
    }

}
