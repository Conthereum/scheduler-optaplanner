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

package org.optaplanner.examples.transactionbalancing.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.optaplanner.examples.transactionbalancing.app.TransactionBalancingApp;
import org.optaplanner.examples.transactionbalancing.domain.TransactionBalance;
import org.optaplanner.examples.transactionbalancing.domain.TransactionComputer;
import org.optaplanner.examples.transactionbalancing.domain.TransactionProcess;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TransactionBalancingGenerator extends LoggingMain {

    private static class Price {

        private int hardwareValue;
        private String description;
        private int cost;

        private Price(int hardwareValue, String description, int cost) {
            this.hardwareValue = hardwareValue;
            this.description = description;
            this.cost = cost;
        }

        public int getHardwareValue() {
            return hardwareValue;
        }

        public String getDescription() {
            return description;
        }

        public int getCost() {
            return cost;
        }
    }

    private static final Price[] CPU_POWER_PRICES = { // in gigahertz
            new Price(3, "single core 3ghz", 110),
            new Price(4, "dual core 2ghz", 140),
            new Price(6, "dual core 3ghz", 180),
            new Price(8, "quad core 2ghz", 270),
            new Price(12, "quad core 3ghz", 400),
            new Price(16, "quad core 4ghz", 1000),
            new Price(24, "eight core 3ghz", 3000),
    };
    private static final Price[] MEMORY_PRICES = { // in gigabyte RAM
            new Price(2, "2 gigabyte", 140),
            new Price(4, "4 gigabyte", 180),
            new Price(8, "8 gigabyte", 220),
            new Price(16, "16 gigabyte", 300),
            new Price(32, "32 gigabyte", 400),
            new Price(64, "64 gigabyte", 600),
            new Price(96, "96 gigabyte", 1000),
    };
    private static final Price[] NETWORK_BANDWIDTH_PRICES = { // in gigabyte per hour
            new Price(2, "2 gigabyte", 100),
            new Price(4, "4 gigabyte", 200),
            new Price(6, "6 gigabyte", 300),
            new Price(8, "8 gigabyte", 400),
            new Price(12, "12 gigabyte", 600),
            new Price(16, "16 gigabyte", 800),
            new Price(20, "20 gigabyte", 1000),
    };

    private static final int MAXIMUM_REQUIRED_CPU_POWER = 12; // in gigahertz
    private static final int MAXIMUM_REQUIRED_MEMORY = 32; // in gigabyte RAM
    private static final int MAXIMUM_REQUIRED_NETWORK_BANDWIDTH = 12; // in gigabyte per hour

    public static void main(String[] args) {
        TransactionBalancingGenerator generator = new TransactionBalancingGenerator();
        generator.writeTransactionBalance(2, 6);
        generator.writeTransactionBalance(3, 9);
        generator.writeTransactionBalance(4, 12);
        //        generator.writeTransactionBalance(5, 15);
        //        generator.writeTransactionBalance(6, 18);
        //        generator.writeTransactionBalance(7, 21);
        //        generator.writeTransactionBalance(8, 24);
        //        generator.writeTransactionBalance(9, 27);
        //        generator.writeTransactionBalance(10, 30);
        //        generator.writeTransactionBalance(11, 33);
        //        generator.writeTransactionBalance(12, 36);
        //        generator.writeTransactionBalance(13, 39);
        //        generator.writeTransactionBalance(14, 42);
        //        generator.writeTransactionBalance(15, 45);
        //        generator.writeTransactionBalance(16, 48);
        //        generator.writeTransactionBalance(17, 51);
        //        generator.writeTransactionBalance(18, 54);
        //        generator.writeTransactionBalance(19, 57);
        //        generator.writeTransactionBalance(20, 60);
        generator.writeTransactionBalance(100, 300);
        generator.writeTransactionBalance(200, 600);
        generator.writeTransactionBalance(400, 1200);
        generator.writeTransactionBalance(800, 2400);
        generator.writeTransactionBalance(1600, 4800);
    }

    protected final SolutionFileIO<TransactionBalance> solutionFileIO;
    protected final File outputDir;

    protected Random random;

    public TransactionBalancingGenerator() {
        solutionFileIO = new TransactionBalanceSolutionFileIO();
        outputDir = new File(CommonApp.determineDataDir(TransactionBalancingApp.DATA_DIR_NAME), "unsolved");
        checkConfiguration();
    }

    public TransactionBalancingGenerator(boolean withoutDao) {
        if (!withoutDao) {
            throw new IllegalArgumentException("The parameter withoutDao (" + withoutDao + ") must be true.");
        }
        solutionFileIO = null;
        outputDir = null;
        checkConfiguration();
    }

    private void checkConfiguration() {
        if (CPU_POWER_PRICES.length != MEMORY_PRICES.length || CPU_POWER_PRICES.length != NETWORK_BANDWIDTH_PRICES.length) {
            throw new IllegalStateException("All price arrays must be equal in length.");
        }
    }

    private void writeTransactionBalance(int computerListSize, int processListSize) {
        String fileName = determineFileName(computerListSize, processListSize);
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        TransactionBalance cloudBalance = createTransactionBalance(fileName, computerListSize, processListSize);
        solutionFileIO.write(cloudBalance, outputFile);
        logger.info("Saved: {}", outputFile);
    }

    public TransactionBalance createTransactionBalance(int computerListSize, int processListSize) {
        return createTransactionBalance(determineFileName(computerListSize, processListSize),
                computerListSize, processListSize);
    }

    private String determineFileName(int computerListSize, int processListSize) {
        return computerListSize + "computers-" + processListSize + "processes";
    }

    public TransactionBalance createTransactionBalance(String inputId, int computerListSize, int processListSize) {
        random = new Random(47);
        List<TransactionComputer> computerList = createComputerList(computerListSize);
        List<TransactionProcess> processList = createProcessList(processListSize);
        TransactionBalance cloudBalance = new TransactionBalance(0, computerList, processList);
        assureComputerCapacityTotalAtLeastProcessRequiredTotal(cloudBalance);
        BigInteger possibleSolutionSize = BigInteger.valueOf(cloudBalance.getComputerList().size()).pow(
                cloudBalance.getProcessList().size());
        logger.info("TransactionBalance {} has {} computers and {} processes with a search space of {}.",
                inputId, computerListSize, processListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return cloudBalance;
    }

    private List<TransactionComputer> createComputerList(int computerListSize) {
        List<TransactionComputer> computerList = new ArrayList<>(computerListSize);
        for (int i = 0; i < computerListSize; i++) {
            TransactionComputer computer = generateComputer(i);
            computerList.add(computer);
        }
        return computerList;
    }

    public TransactionComputer generateComputer(long id) {
        int cpuPowerPricesIndex = random.nextInt(CPU_POWER_PRICES.length);
        int memoryPricesIndex = distortIndex(cpuPowerPricesIndex, MEMORY_PRICES.length);
        int networkBandwidthPricesIndex = distortIndex(cpuPowerPricesIndex, NETWORK_BANDWIDTH_PRICES.length);
        int cost = CPU_POWER_PRICES[cpuPowerPricesIndex].getCost()
                + MEMORY_PRICES[memoryPricesIndex].getCost()
                + NETWORK_BANDWIDTH_PRICES[networkBandwidthPricesIndex].getCost();
        TransactionComputer computer = new TransactionComputer(id, CPU_POWER_PRICES[cpuPowerPricesIndex].getHardwareValue(),
                MEMORY_PRICES[memoryPricesIndex].getHardwareValue(),
                NETWORK_BANDWIDTH_PRICES[networkBandwidthPricesIndex].getHardwareValue(),
                cost);
        logger.trace("Created computer with cpuPowerPricesIndex ({}), memoryPricesIndex ({}),"
                + " networkBandwidthPricesIndex ({}).",
                cpuPowerPricesIndex, memoryPricesIndex, networkBandwidthPricesIndex);
        return computer;
    }

    private int distortIndex(int referenceIndex, int length) {
        int index = referenceIndex;
        double randomDouble = random.nextDouble();
        double loweringThreshold = 0.25;
        while (randomDouble < loweringThreshold && index >= 1) {
            index--;
            loweringThreshold *= 0.10;
        }
        double heighteningThreshold = 0.75;
        while (randomDouble >= heighteningThreshold && index <= (length - 2)) {
            index++;
            heighteningThreshold = (1.0 - ((1.0 - heighteningThreshold) * 0.10));
        }
        return index;
    }

    private List<TransactionProcess> createProcessList(int processListSize) {
        List<TransactionProcess> processList = new ArrayList<>(processListSize);
        for (int i = 0; i < processListSize; i++) {
            TransactionProcess process = generateProcess(i);
            processList.add(process);
        }
        return processList;
    }

    public TransactionProcess generateProcess(long id) {
        int requiredCpuPower = generateRandom(MAXIMUM_REQUIRED_CPU_POWER);
        int requiredMemory = generateRandom(MAXIMUM_REQUIRED_MEMORY);
        int requiredNetworkBandwidth = generateRandom(MAXIMUM_REQUIRED_NETWORK_BANDWIDTH);
        TransactionProcess process = new TransactionProcess(id, requiredCpuPower, requiredMemory, requiredNetworkBandwidth);
        logger.trace("Created TransactionProcess with requiredCpuPower ({}), requiredMemory ({}),"
                + " requiredNetworkBandwidth ({}).",
                requiredCpuPower, requiredMemory, requiredNetworkBandwidth);
        // Notice that we leave the PlanningVariable properties on null
        return process;
    }

    private int generateRandom(int maximumValue) {
        double randomDouble = random.nextDouble();
        double parabolaBase = 2000.0;
        double parabolaRandomDouble = (Math.pow(parabolaBase, randomDouble) - 1.0) / (parabolaBase - 1.0);
        if (parabolaRandomDouble < 0.0 || parabolaRandomDouble >= 1.0) {
            throw new IllegalArgumentException("Invalid generated parabolaRandomDouble (" + parabolaRandomDouble + ")");
        }
        int value = ((int) Math.floor(parabolaRandomDouble * maximumValue)) + 1;
        if (value < 1 || value > maximumValue) {
            throw new IllegalArgumentException("Invalid generated value (" + value + ")");
        }
        return value;
    }

    private void assureComputerCapacityTotalAtLeastProcessRequiredTotal(TransactionBalance cloudBalance) {
        List<TransactionComputer> computerList = cloudBalance.getComputerList();
        int cpuPowerTotal = 0;
        int memoryTotal = 0;
        int networkBandwidthTotal = 0;
        for (TransactionComputer computer : computerList) {
            cpuPowerTotal += computer.getCpuPower();
            memoryTotal += computer.getMemory();
            networkBandwidthTotal += computer.getNetworkBandwidth();
        }
        int requiredCpuPowerTotal = 0;
        int requiredMemoryTotal = 0;
        int requiredNetworkBandwidthTotal = 0;
        for (TransactionProcess process : cloudBalance.getProcessList()) {
            requiredCpuPowerTotal += process.getRequiredCpuPower();
            requiredMemoryTotal += process.getRequiredMemory();
            requiredNetworkBandwidthTotal += process.getRequiredNetworkBandwidth();
        }
        int cpuPowerLacking = requiredCpuPowerTotal - cpuPowerTotal;
        while (cpuPowerLacking > 0) {
            TransactionComputer computer = computerList.get(random.nextInt(computerList.size()));
            int upgrade = determineUpgrade(cpuPowerLacking);
            computer.setCpuPower(computer.getCpuPower() + upgrade);
            cpuPowerLacking -= upgrade;
        }
        int memoryLacking = requiredMemoryTotal - memoryTotal;
        while (memoryLacking > 0) {
            TransactionComputer computer = computerList.get(random.nextInt(computerList.size()));
            int upgrade = determineUpgrade(memoryLacking);
            computer.setMemory(computer.getMemory() + upgrade);
            memoryLacking -= upgrade;
        }
        int networkBandwidthLacking = requiredNetworkBandwidthTotal - networkBandwidthTotal;
        while (networkBandwidthLacking > 0) {
            TransactionComputer computer = computerList.get(random.nextInt(computerList.size()));
            int upgrade = determineUpgrade(networkBandwidthLacking);
            computer.setNetworkBandwidth(computer.getNetworkBandwidth() + upgrade);
            networkBandwidthLacking -= upgrade;
        }
    }

    private int determineUpgrade(int lacking) {
        for (int upgrade : new int[] { 8, 4, 2, 1 }) {
            if (lacking >= upgrade) {
                return upgrade;
            }
        }
        throw new IllegalStateException("Lacking (" + lacking + ") should be at least 1.");
    }

}
