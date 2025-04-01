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

package org.optaplanner.examples.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.optaplanner.examples.transactionbalancing.app.EmvBalancingApp;
import org.optaplanner.examples.transactionbalancing.app.TransactionBalancingApp;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.swingui.OpenBrowserAction;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;

public class EmvOptimizerApp extends JFrame {

    /**
     * Supported system properties: {@link CommonApp#DATA_DIR_SYSTEM_PROPERTY}.
     *
     * @param args never null
     */
    public static void main(String[] args) {
        CommonApp.prepareSwingEnvironment();
        EmvOptimizerApp emvOptimizerApp = new EmvOptimizerApp();
        emvOptimizerApp.pack();
        emvOptimizerApp.setLocationRelativeTo(null);
        emvOptimizerApp.setVisible(true);
    }

    private static String determineOptaPlannerExamplesVersion() {
        String optaPlannerExamplesVersion = EmvOptimizerApp.class.getPackage().getImplementationVersion();
        if (optaPlannerExamplesVersion == null) {
            optaPlannerExamplesVersion = "";
        }
        return optaPlannerExamplesVersion;
    }

    private JTextArea descriptionTextArea;

    public EmvOptimizerApp() {
        super("OptaPlanner examples " + determineOptaPlannerExamplesVersion());
        setIconImage(SolverAndPersistenceFrame.OPTAPLANNER_ICON.getImage());
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Which module do you want to see?", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
        contentPane.add(titleLabel, BorderLayout.NORTH);
        JScrollPane examplesScrollPane = new JScrollPane(createExamplesPanel());
        examplesScrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        examplesScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        examplesScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPane.add(examplesScrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(createDescriptionPanel(), BorderLayout.CENTER);
        bottomPanel.add(createExtraPanel(), BorderLayout.EAST);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        return contentPane;
    }

    private JPanel createExamplesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Stream.of(new TransactionBalancingApp(), new EmvBalancingApp())
                .map(this::createExampleButton)
                .forEach(panel::add);

        return panel;
    }

    private JButton createExampleButton(final CommonApp commonApp) {
        String iconResource = commonApp.getIconResource();
        Icon icon = iconResource == null ? new EmptyIcon() : new ImageIcon(getClass().getResource(iconResource));
        JButton button = new JButton(new AbstractAction(commonApp.getName(), icon) {
            @Override
            public void actionPerformed(ActionEvent e) {
                commonApp.init(EmvOptimizerApp.this, false);
            }
        });
        button.setHorizontalAlignment(JButton.LEFT);
        button.setHorizontalTextPosition(JButton.RIGHT);
        button.setVerticalTextPosition(JButton.CENTER);
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                descriptionTextArea.setText(commonApp.getDescription());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                descriptionTextArea.setText("");
            }

        });
        return button;
    }

    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel(new BorderLayout(2, 2));
        descriptionPanel.add(new JLabel("Description"), BorderLayout.NORTH);
        descriptionTextArea = new JTextArea(8, 65);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionPanel.add(new JScrollPane(descriptionTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        return descriptionPanel;
    }

    private JPanel createExtraPanel() {
        JPanel extraPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        extraPanel.add(new JPanel());
        Action homepageAction = new OpenBrowserAction("Source Code Repository", "https://github" +
                ".com/xxxx/concurrent-evm");//removed for blind review purpose
        extraPanel.add(new JButton(homepageAction));
        Action documentationAction = new OpenBrowserAction("Documentation",
                "https://github.com/xxxx/concurrent-evm");//removed for blind review purpose
        extraPanel.add(new JButton(documentationAction));
        return extraPanel;
    }

    private static class EmptyIcon implements Icon {

        @Override
        public int getIconWidth() {
            return 64;
        }

        @Override
        public int getIconHeight() {
            return 64;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            // Do nothing
        }

    }

}
