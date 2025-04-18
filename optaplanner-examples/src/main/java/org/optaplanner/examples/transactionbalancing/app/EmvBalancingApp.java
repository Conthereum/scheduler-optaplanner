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

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.transactionbalancing.domain.EmvBalance;
import org.optaplanner.examples.transactionbalancing.persistence.EmvBalanceSolutionFileIO;
import org.optaplanner.examples.transactionbalancing.swingui.EmvTransactionBalancingPanel;
import org.optaplanner.examples.transactionbalancing.swingui.TransactionBalancingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * For an easy example, look at {@link TransactionBalancingHelloWorld} instead.
 */
public class EmvBalancingApp extends CommonApp<EmvBalance> {

     public static final String SOLVER_CONFIG = "org/optaplanner/examples/emvtransactionbalancing" +
            "/emvTransactionBalancingSolverConfig.xml";
    public static final String DATA_DIR_NAME = "emvtransactionbalancing";


    public static void main(String[] args) {
        prepareSwingEnvironment();
        new EmvBalancingApp().init();
    }

    public EmvBalancingApp() {
        super("EMV Transaction Balancing",
                "Assign transaction processes to computers.\n\n" +
                        "Processes are needed to be executed in an optimal time using available computers.\n" +
                        "",
                SOLVER_CONFIG, DATA_DIR_NAME,
                TransactionBalancingPanel.LOGO_PATH);
    }

    @Override
    protected EmvTransactionBalancingPanel createSolutionPanel() {
        return new EmvTransactionBalancingPanel();
    }

    @Override
    public SolutionFileIO<EmvBalance> createSolutionFileIO() {
        return new EmvBalanceSolutionFileIO();
    }

}
