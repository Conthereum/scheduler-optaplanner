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

package org.optaplanner.examples.transactionbalancing.swingui;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.transactionbalancing.domain.*;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.AddComputerProblemChange;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.AddProcessProblemChange;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.DeleteComputerProblemChange;
import org.optaplanner.examples.transactionbalancing.swingui.realtime.DeleteProcessProblemChange;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;
import java.util.function.LongFunction;

public class EmvTransactionBalancingPanel extends SolutionPanel<EmvBalance> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/transactionbalancing/swingui/cloudBalancingLogo.png";

    private final TangoColorFactory processColorFactory;
    private final ImageIcon cloudComputerIcon;
    private final ImageIcon addCloudComputerIcon;
    private final ImageIcon deleteCloudComputerIcon;
    private final ImageIcon cloudProcessIcon;
    private final ImageIcon addCloudProcessIcon;
    private final ImageIcon deleteCloudProcessIcon;

    private JPanel computersPanel;

    private EmvTransactionComputerPanel unassignedPanel;
    private Map<Computer, EmvTransactionComputerPanel> computerToPanelMap;

    private int maximumComputerDurationTime;
    private int maximumComputerMemory;
    private int maximumComputerNetworkBandwidth;

    public EmvTransactionBalancingPanel() {
        processColorFactory = new TangoColorFactory();
        cloudComputerIcon = new ImageIcon(getClass().getResource("cloudComputer.png"));
        addCloudComputerIcon = new ImageIcon(getClass().getResource("addCloudComputer.png"));
        deleteCloudComputerIcon = new ImageIcon(getClass().getResource("deleteCloudComputer.png"));
        cloudProcessIcon = new ImageIcon(getClass().getResource("cloudProcess.png"));
        addCloudProcessIcon = new ImageIcon(getClass().getResource("addCloudProcess.png"));
        deleteCloudProcessIcon = new ImageIcon(getClass().getResource("deleteCloudProcess.png"));
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        JPanel headerPanel = createHeaderPanel();
        JPanel computersPanel = createComputersPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(computersPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(computersPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    public Color getCloudProcessColor(EmvProcess process) {
        return processColorFactory.pickColor(process.getLabel());
    }

    public ImageIcon getCloudComputerIcon() {
        return cloudComputerIcon;
    }

    public ImageIcon getAddCloudComputerIcon() {
        return addCloudComputerIcon;
    }

    public ImageIcon getDeleteCloudComputerIcon() {
        return deleteCloudComputerIcon;
    }

    public ImageIcon getCloudProcessIcon() {
        return cloudProcessIcon;
    }

    public ImageIcon getAddCloudProcessIcon() {
        return addCloudProcessIcon;
    }

    public ImageIcon getDeleteCloudProcessIcon() {
        return deleteCloudProcessIcon;
    }

    public int getMaximumComputerDurationTime() {
        return maximumComputerDurationTime;
    }

    //todo dummy values
    public int getMaximumComputerCpuPower() {
//        return maximumComputerMemory;
        return 1;
    }

    public int getMaximumComputerMemory() {
//        return maximumComputerMemory;
        return 1;
    }

    public int getMaximumComputerNetworkBandwidth() {
//        return maximumComputerNetworkBandwidth;
        return 1;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(0, 5));
        JPanel addPanel = new JPanel(new GridLayout());
        JButton addComputerButton = SwingUtils.makeSmallButton(new JButton(addCloudComputerIcon));
        addComputerButton.setToolTipText("Add computer");
        addComputerButton
                .addActionListener(e -> addComputer(expectedId -> new Computer()));
                //(expectedId, 12, 32, 12,400 + 400 +600)));
        addPanel.add(addComputerButton);
        JButton addProcessButton = SwingUtils.makeSmallButton(new JButton(addCloudProcessIcon));
        addProcessButton.setToolTipText("Add process");
        addProcessButton.addActionListener(e -> addProcess(expectedId -> new EmvProcess()));//(expectedId, 3, 8, 3)));
        addPanel.add(addProcessButton);
        JPanel cornerPanel = new JPanel(new BorderLayout());
        cornerPanel.add(addPanel, BorderLayout.EAST);
        headerPanel.add(cornerPanel);
        JLabel durationTimeLabel = new JLabel("duration time");
        headerPanel.add(durationTimeLabel);
        JLabel memoryLabel = new JLabel("Memory");
        headerPanel.add(memoryLabel);
        JLabel networkBandwidthLabel = new JLabel("Network bandwidth");
        headerPanel.add(networkBandwidthLabel);
        JLabel costLabel = new JLabel("Cost");
        headerPanel.add(costLabel);
        return headerPanel;
    }

    private JPanel createComputersPanel() {
        computersPanel = new JPanel(new GridLayout(0, 1));
        computerToPanelMap = new LinkedHashMap<>();
        return computersPanel;
    }

    @Override
    public void resetPanel(EmvBalance emvBalance) {
        maximumComputerDurationTime = 0;
        /*for (Computer computer : emvBalance.getComputerList()) {
            if (computer.getCpuPower() > maximumComputerDurationTime) {
                maximumComputerDurationTime = computer.getCpuPower();
            }
        }*/
        for (EmvTransactionComputerPanel computerPanel : computerToPanelMap.values()) {
            if (computerPanel.getComputer() != null) {
                computersPanel.remove(computerPanel);
            }
        }
        computerToPanelMap.clear();
        computersPanel.removeAll();
        unassignedPanel = new EmvTransactionComputerPanel(this, null);
        computersPanel.add(unassignedPanel);
        computerToPanelMap.put(null, unassignedPanel);
        updatePanel(emvBalance);
    }

    @Override
    public void updatePanel(EmvBalance cloudBalance) {
        Set<Computer> deadCloudComputerSet = new LinkedHashSet<>(computerToPanelMap.keySet());
        deadCloudComputerSet.remove(null);
        for (Computer computer : cloudBalance.getComputerList()) {
            deadCloudComputerSet.remove(computer);
            EmvTransactionComputerPanel computerPanel = computerToPanelMap.get(computer);
            if (computerPanel == null) {
                computerPanel = new EmvTransactionComputerPanel(this, computer);
                computersPanel.add(computerPanel);
                computerToPanelMap.put(computer, computerPanel);
            }
            computerPanel.clearProcesses();
        }
        unassignedPanel.clearProcesses();
        for (EmvProcess process : cloudBalance.getProcessList()) {
            Computer computer = process.getComputer();
            EmvTransactionComputerPanel computerPanel = computerToPanelMap.get(computer);
            computerPanel.addProcess(process);
        }
        for (Computer deadComputer : deadCloudComputerSet) {
            EmvTransactionComputerPanel deadComputerPanel = computerToPanelMap.remove(deadComputer);
            computersPanel.remove(deadComputerPanel);
        }
        for (EmvTransactionComputerPanel computerPanel : computerToPanelMap.values()) {
            computerPanel.update();
        }
        // If computersPanel.add() or computersPanel.remove() was called, the component needs to be validated.
        computersPanel.validate();
    }

    public void addComputer(final LongFunction<Computer> computerGenerator) {
        logger.info("Scheduling addition of computer.");
//        doProblemChange(new AddComputerProblemChange(computerGenerator));//todo
    }

    public void deleteComputer(final Computer computer) {
        logger.info("Scheduling delete of computer ({}).", computer);
//        doProblemChange(new DeleteComputerProblemChange(computer));//todo
    }

    public void addProcess(final LongFunction<EmvProcess> processGenerator) {
        logger.info("Scheduling addition of process.");
//        doProblemChange(new AddProcessProblemChange(processGenerator));//todo
    }

    public void deleteProcess(final EmvProcess process) {
        logger.info("Scheduling delete of process ({}).", process);
//        doProblemChange(new DeleteProcessProblemChange(process));//todo
    }

    public JButton createButton(EmvProcess process, Runnable removeAction) {
        return SwingUtils.makeSmallButton(new JButton(new CloudProcessAction(process, removeAction)));
    }

    private class CloudProcessAction extends AbstractAction {

        private final EmvProcess process;
        private final Runnable removeAction;

        public CloudProcessAction(EmvProcess process, Runnable removeAction) {
            super(process.getLabel());
            this.process = process;
            this.removeAction = removeAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(1, 2));
            listFieldsPanel.add(new JLabel("Computer:"));
            List<Computer> computerList = getSolution().getComputerList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox computerListField = new JComboBox(computerList.toArray(new Object[computerList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(computerListField);
            computerListField.setSelectedItem(process.getComputer());
            listFieldsPanel.add(computerListField);
            int result = JOptionPane.showConfirmDialog(EmvTransactionBalancingPanel.this.getRootPane(), listFieldsPanel,
                    "Select computer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Computer toComputer = (Computer) computerListField.getSelectedItem();
                if (process.getComputer() != toComputer) {
                    doProblemChange((workingSolution, problemChangeDirector) -> problemChangeDirector.changeVariable(process,
                            "computer",
                            proc -> process.setComputer(toComputer)), true);
                    removeAction.run();
                }
                solverAndPersistenceFrame.resetScreen();
            }
        }
    }
}
