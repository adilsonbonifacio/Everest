package gui;

import algorithm.GenerateIOLTS;
import util.Settings;
import view.ViewConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateTab {
    private JPanel panel;
    private JPanel modelprop;
    private JPanel genprop;
    private JPanel execprop;
    private JPanel modeltype;
    private JPanel fileprop;
    private JPanel amountsprop;
    private JPanel conformprop;
    private JTextField nameField;
    private JTextField pathField;
    private JTextField inputsField;
    private JTextField outputsField;
    private JTextField baseField;
    private JTabbedPane parent;

    // System colors
    private SystemColor backgroundColor = SystemColor.menu;
    private SystemColor labelColor = SystemColor.windowBorder;
    private SystemColor tipColor = SystemColor.windowBorder;
    private SystemColor borderColor = SystemColor.windowBorder;
    private SystemColor textColor = SystemColor.controlShadow;
    private SystemColor buttonColor = SystemColor.activeCaptionBorder;

    // Settings
    JFileChooser dirChooser = new JFileChooser();
    JFileChooser fileChooser = new JFileChooser();
    private Settings settings;

    public GenerateTab(JTabbedPane parent)
    {
        this.settings = new Settings();
        this.settings.setupDir();
        this.parent = parent;

        setUI();
    }

    public void setUI()
    {
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));

        // Creating components
        JPanel execProp = executionPropertyPanel();
        JPanel modelProp = modelPropertyPanel();

        // Adding components
        this.panel.add(modelProp);
        this.panel.add(execProp);

        this.parent.addTab("Model Generation", null, this.panel, "Setup and run random model generation.");
//        setCloseButton();
//        this.parent.setSelectedIndex(this.parent.getTabCount()-1);
    }

    /**
     * Creates panel with settings related to
     * properties of reactive models, i.e
     * acyclic, input-enabled, output-deterministic.
     * @return panel
     */
    public JPanel modelPropertyPanel()
    {
        this.modelprop = new JPanel();
        this.modelprop.setBorder(BorderFactory.createTitledBorder("Model Properties"));

        // GB Layout for Model Properties

        GridBagLayout modelgrid = new GridBagLayout();
        modelprop.setLayout(modelgrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;

        JLabel labelModelProp = new JLabel("Reactive model properties:");
        JCheckBox boxInputEnabled = new JCheckBox("Input-enabled");
        JCheckBox boxOutputEnabled = new JCheckBox("Output-enabled");
        JCheckBox boxInputDetermined = new JCheckBox("Input-deterministic");
        JCheckBox boxOutputDetermined = new JCheckBox("Output-deterministic");
        JCheckBox boxAcyclic = new JCheckBox("Acyclic");

        // Options wrapper
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setBorder(BorderFactory.createTitledBorder("Reactive model properties"));

        // Adding components

        // Checkbox options pane
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(10, 10, 10, 10);
        modelprop.add(options);
        modelgrid.setConstraints(options, constraints);

        // Options
        options.add(boxAcyclic);
        boxAcyclic.addActionListener(actionEvent -> {
            this.settings.setAcyclic(!this.settings.isAcyclic());
        });

        options.add(boxInputEnabled);
        boxInputEnabled.addActionListener(actionEvent -> {
            this.settings.setInputComplete(!this.settings.isInputComplete());
        });

        options.add(boxOutputEnabled);
        boxOutputEnabled.addActionListener(actionEvent -> {
            this.settings.setOutputComplete(!this.settings.isOutputComplete());
        });

        options.add(boxInputDetermined);
        boxInputDetermined.addActionListener(actionEvent -> {
            this.settings.setInputDeterminism(!this.settings.isInputDeterminism());
        });

        options.add(boxOutputDetermined);
        boxOutputDetermined.addActionListener(actionEvent -> {
            this.settings.setOutputDeterminism(!this.settings.isOutputDeterminism());
        });

        // Model Behavior Panel
        JPanel modelTypePanel = modelTypePanel();

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weighty = 1;
        constraints.weightx = 1;
        modelprop.add(modelTypePanel);
        modelgrid.setConstraints(modelTypePanel, constraints);

        // Conformance Panel
        JPanel confPanel = conformancePanel();

        constraints.gridy++;
        modelprop.add(confPanel);
        modelgrid.setConstraints(confPanel, constraints);

        // File Panel
        JPanel filePanel = filePanel();

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weighty = 1;
        constraints.weightx = 1;
        modelprop.add(filePanel);
        modelgrid.setConstraints(filePanel, constraints);

        return this.modelprop;
    }

    /* UNUSED - Strategies */
    public JPanel generationPropertyPanel()
    {
        this.genprop = new JPanel();
        this.genprop.setBorder(BorderFactory.createTitledBorder("Generation Strategies"));

        JLabel genPropLabel = new JLabel("Generation Method:");
        JRadioButton gen1 = new JRadioButton("Strategy 1 - Random");
        gen1.setSelected(true);
//        settings.setStrategy(1);
        JRadioButton gen2 = new JRadioButton("Strategy 2");
        JRadioButton gen3 = new JRadioButton("Strategy 3");

        ButtonGroup genPropGroup = new ButtonGroup();
        genPropGroup.add(gen1);
        genPropGroup.add(gen2);
        genPropGroup.add(gen3);

        gen1.addActionListener(actionEvent -> {
//            configurations.setStrategy(1);
        });

        gen2.addActionListener(actionEvent -> {
//            configurations.setStrategy(2);
        });

        gen3.addActionListener(actionEvent -> {
//            configurations.setStrategy(3);
        });

        genprop.add(genPropLabel);
        genprop.add(gen1);
        genprop.add(gen2);
        genprop.add(gen3);

        genprop.setLayout(new BoxLayout(genprop, BoxLayout.Y_AXIS));

        return this.genprop;
    }

    /**
     * Creates panel containing panels with execution properties,
     * as well as the run button.
     * @return panel
     */
    public JPanel executionPropertyPanel()
    {
        execprop = new JPanel();
        execprop.setBorder(BorderFactory.createTitledBorder("Generate Models"));

        // GB Layout for Execution Parameters

        GridBagLayout execgrid = new GridBagLayout();
        execprop.setLayout(execgrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;

        // Create components

        JPanel amountsprop = amountsPropertyPanel();
        JPanel labelsprop = labelsPropertyPanel();
        JLabel doneLabel = new JLabel("Models successfully generated");
        doneLabel.setFont(new Font(doneLabel.getFont().getName(), Font.BOLD, 12));
        doneLabel.setForeground(new Color(66, 171, 31));
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font(doneLabel.getFont().getName(), Font.BOLD, 12));
        errorLabel.setForeground(new Color(186, 43, 43));

        JButton genBtn = new JButton("Run");
        genBtn.addActionListener(actionEvent -> {

            // If no name was inserted
            if(nameField.getText() == null || nameField.getText().equals("") || nameField.getText().equals(" "))
            {
                // Remove doneLabel if displayed
                // execprop.remove(doneLabel);



                 settings.setName("model");
                 nameField.setText(settings.getName());
            }

            // If no directory was selected
            if(settings.getPath() == null || settings.getPath() == "")
            {
                settings.setPath(System.getProperty("user.dir"));
                pathField.setText(settings.getPath());
            }

            // If required conformance but passed no path/path doesn't exist
            File file = new File(settings.getPathBase());
            if(!settings.getConformanceType().equals("None") && (settings.getPathBase() == null || settings.getPathBase().isEmpty() || !file.exists() || file.isDirectory()))
            {
                 errorLabel.setText("Error: Base model is an invalid file.");
                 constraints.gridy++;
                 constraints.weighty = 1;
                 execprop.add(errorLabel);
                 execgrid.setConstraints(errorLabel, constraints);
            }
            else {
                executeAction();
            }

            // Remove any error displayed
            // execprop.remove(errorLabel);

            // Displays doneLabel
            constraints.gridy++;
            constraints.weighty = 1;
            execprop.add(doneLabel);
            execgrid.setConstraints(doneLabel, constraints);

        });

        // Adding components
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.weighty = 5;
        execprop.add(amountsprop);
        execgrid.setConstraints(amountsprop, constraints);

        constraints.gridy++;
        constraints.insets = new Insets(0, 10, 10, 10);
        constraints.weighty = 5;
        execprop.add(labelsprop);
        execgrid.setConstraints(labelsprop, constraints);

        constraints.gridy++;
        constraints.weighty = 1;
        execprop.add(genBtn);
        execgrid.setConstraints(genBtn, constraints);

        return this.execprop;
    }

    /**
     * Creates panel with settings for generated models
     * i.e number of states, inputs, models.
     * @return panel
     */
    public JPanel amountsPropertyPanel()
    {
        amountsprop = new JPanel();
        amountsprop.setBorder(BorderFactory.createTitledBorder("Quantity Settings"));

        // GB Layout for Execution Parameters
        GridBagLayout amountsgrid = new GridBagLayout();
        amountsprop.setLayout(amountsgrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;
        constraints.weightx = 1;

        // Create components

        // Labels
        JLabel execAmount = new JLabel("Number of models:");
        JLabel stateLabel = new JLabel("Number of states:");
        JLabel inputLabel = new JLabel("Number of inputs:");
        JLabel outputLabel = new JLabel("Number of outputs:");
        JLabel transitionsLabel = new JLabel("Number of transitions:");

        // Spinners and listeners
        SpinnerNumberModel stateModel = new SpinnerNumberModel(5, 1, 1000, 1);
        JSpinner stateSpinner = new JSpinner(stateModel);
        settings.setStateNumber(Integer.parseInt(stateSpinner.getValue().toString()));
        stateSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setStateNumber(Integer.parseInt(stateSpinner.getValue().toString()));
            }
        });

        SpinnerNumberModel inputModel = new SpinnerNumberModel(3, 1, 1000, 1);
        JSpinner inputSpinner = new JSpinner(inputModel);
        settings.setInputNumber(Integer.parseInt(inputSpinner.getValue().toString()));
        inputSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setInputNumber(Integer.parseInt(inputSpinner.getValue().toString()));
            }
        });

        SpinnerNumberModel outputModel = new SpinnerNumberModel(3, 1, 1000, 1);
        JSpinner outputSpinner = new JSpinner(outputModel);
        settings.setOutputNumber(Integer.parseInt(outputSpinner.getValue().toString()));
        outputSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setOutputNumber(Integer.parseInt(outputSpinner.getValue().toString()));
            }
        });

        SpinnerNumberModel transitionsModel = new SpinnerNumberModel(0, 0, 1000, 1);
        JSpinner transitionsSpinner = new JSpinner(transitionsModel);
        settings.setTransitionNumber(Integer.parseInt(transitionsSpinner.getValue().toString()));
        settings.setRequestedTransitionNumber(Integer.parseInt(transitionsSpinner.getValue().toString()));
        transitionsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setTransitionNumber(Integer.parseInt(transitionsSpinner.getValue().toString()));
                settings.setRequestedTransitionNumber(Integer.parseInt(transitionsSpinner.getValue().toString()));
            }
        });

        SpinnerNumberModel amountModel = new SpinnerNumberModel(20, 1, 1000, 1);
        JSpinner amountSpinner = new JSpinner(amountModel);
        settings.setModelNumber(Integer.parseInt(amountSpinner.getValue().toString()));
        amountSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings.setModelNumber(Integer.parseInt(amountSpinner.getValue().toString()));
            }
        });

        // Adding components
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 10, 2, 10);
        amountsprop.add(stateLabel);
        amountsgrid.setConstraints(stateLabel, constraints);
        constraints.gridx++;
        amountsprop.add(stateSpinner);
        amountsgrid.setConstraints(stateSpinner, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        amountsprop.add(inputLabel);
        amountsgrid.setConstraints(inputLabel, constraints);
        constraints.gridx++;
        amountsprop.add(inputSpinner);
        amountsgrid.setConstraints(inputSpinner, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        amountsprop.add(outputLabel);
        amountsgrid.setConstraints(outputLabel, constraints);
        constraints.gridx++;
        amountsprop.add(outputSpinner);
        amountsgrid.setConstraints(outputSpinner, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        amountsprop.add(transitionsLabel);
        amountsgrid.setConstraints(transitionsLabel, constraints);
        constraints.gridx++;
        amountsprop.add(transitionsSpinner);
        amountsgrid.setConstraints(transitionsSpinner, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        amountsprop.add(execAmount);
        amountsgrid.setConstraints(execAmount, constraints);
        constraints.gridx++;
        amountsprop.add(amountSpinner);
        amountsgrid.setConstraints(amountSpinner, constraints);

        return amountsprop;
    }

    /**
     * Creates panel with settings for input
     * and output sets to use.
     * @return panel
     */
    public JPanel labelsPropertyPanel()
    {
        JPanel labelsprop = new JPanel();
        labelsprop.setBorder(BorderFactory.createTitledBorder("Label Configuration"));

        // GB Layout for Execution Parameters
        GridBagLayout labelsgrid = new GridBagLayout();
        labelsprop.setLayout(labelsgrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;
        constraints.weightx = 1;

        // Create components
        JLabel inputsLabel = new JLabel("Input labels:");
        JLabel outputsLabel = new JLabel("Output labels:");
        JLabel guideLabel = new JLabel("Labels must be separated by commas");
        guideLabel.setForeground(Color.GRAY);
        guideLabel.setFont(new Font(guideLabel.getFont().getName(), Font.PLAIN, 10));

        inputsField = new JTextField();
        inputsField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                if(inputsField.getText().equals("") || inputsField.getText().equals(" "))
                {
                    settings.getInputs().removeAll(settings.getInputs());
                }
                else
                {
                    List<String> inputs = new ArrayList<>();
                    settings.setInputs(inputs);

                    inputs = Arrays.asList(inputsField.getText().split(","));
                    for (String input : inputs) {
                        if(!input.equals("") && !input.equals(" "))
                        {
                            settings.getInputs().add(input.trim());
                        }
                    }
                }
            }
        });

        outputsField = new JTextField();
        outputsField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                if(outputsField.getText().equals("") || outputsField.getText().equals(" "))
                {
                    settings.getOutputs().removeAll(settings.getOutputs());
                }
                else
                {
                    List<String> outputs = new ArrayList<>();
                    settings.setOutputs(outputs);

                    outputs = Arrays.asList(outputsField.getText().split(","));
                    for (String output : outputs) {
                        if(output != " " && output != "")
                        {
                            settings.getOutputs().add(output.trim());
                        }
                    }
                }
            }
        });

        // Add components
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 10, 2, 10);
        constraints.gridwidth = 1;
        labelsprop.add(inputsLabel);
        labelsgrid.setConstraints(inputsLabel, constraints);

        constraints.gridx++;
        constraints.gridwidth = 10;
        labelsprop.add(inputsField);
        labelsgrid.setConstraints(inputsField, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        labelsprop.add(outputsLabel);
        labelsgrid.setConstraints(outputsLabel, constraints);

        constraints.gridx++;
        constraints.gridwidth = 10;
        labelsprop.add(outputsField);
        labelsgrid.setConstraints(outputsField, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 11;
        labelsprop.add(guideLabel);
        labelsgrid.setConstraints(guideLabel, constraints);

        return labelsprop;
    }

    /**
     * Creates panel with settings for model behavior
     * such as non-quiescent and internal actions.
     * @return panel
     */
    public JPanel modelTypePanel()
    {
        modeltype = new JPanel();

        modeltype.setBorder(BorderFactory.createTitledBorder("Model Behavior"));

        // GB Layout for Model Type Parameters

        GridBagLayout modelgrid = new GridBagLayout();
        modeltype.setLayout(modelgrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;
        constraints.weightx = 1;

        JCheckBox cDeterministic = new JCheckBox("Deterministic");
        JCheckBox cNonQuiescent = new JCheckBox("Non-quiescent (force deltas)");
        JCheckBox cBlackBox = new JCheckBox("Black-box (internal actions)");

        cDeterministic.addActionListener(actionEvent -> {
            settings.setDeterministic(cDeterministic.isSelected());
        });

        cNonQuiescent.addActionListener(actionEvent -> {
            settings.setNonQuiescent(cNonQuiescent.isSelected());
        });

        cBlackBox.addActionListener(actionEvent -> {
            settings.setBlackBox(cBlackBox.isSelected());
        });

        // Adding components
        constraints.gridx = 0;
        constraints.gridy = 0;
        modeltype.add(cDeterministic);
        modelgrid.setConstraints(cDeterministic, constraints);

        constraints.gridy++;
        modeltype.add(cNonQuiescent);
        modelgrid.setConstraints(cNonQuiescent, constraints);

        constraints.gridy++;
        modeltype.add(cBlackBox);
        modelgrid.setConstraints(cBlackBox, constraints);

        return this.modeltype;
    }

    /**
     * Creates panel with settings for files
     * and directory in which to store generated models.
     * @return panel
     */
    public JPanel filePanel()
    {
        JPanel filePanel = new JPanel();
        this.fileprop = filePanel;
        filePanel.setBorder(BorderFactory.createTitledBorder("File Settings"));

        // GB Layout for File Settings

        GridBagLayout filegrid = new GridBagLayout();
        filePanel.setLayout(filegrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;
        constraints.weightx = 1;

        // Creating components

        JLabel nameLabel = new JLabel("File name:");
        JLabel pathLabel = new JLabel("Directory:");
        nameField = new JTextField();
        pathField = new JTextField();

        nameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) { settings.setName(nameField.getText()); }
        });

        dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setCurrentDirectory(settings.getCurrentDir());
        JButton pathBtn = new JButton("");
        pathBtn.setToolTipText("Select directory");
        pathBtn.setPreferredSize(new Dimension(20, 20));
        pathBtn.setBackground(buttonColor);
        pathBtn.setOpaque(true);
        pathBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int option = dirChooser.showOpenDialog(dirChooser);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = dirChooser.getSelectedFile();
                    pathField.setText(file.getAbsolutePath());
                    settings.setPath(file.getPath());
                    settings.setCurrentDir(file);
                    settings.updateDir();
                    dirChooser.setCurrentDirectory(file);
                    fileChooser.setCurrentDirectory(file);
                }else{
                }
            }
        });
        pathBtn.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));

        // Adding components
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 5, 0, 5);
        filePanel.add(nameLabel);
        filegrid.setConstraints(nameLabel, constraints);

        constraints.gridx++;
        constraints.gridy = 0;
        constraints.weightx = 5;
        filePanel.add(nameField);
        filegrid.setConstraints(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weightx = 1;
        filePanel.add(pathLabel);
        filegrid.setConstraints(pathLabel, constraints);

        constraints.gridx++;
        constraints.weightx = 8;
        filePanel.add(pathField);
        filegrid.setConstraints(pathField, constraints);

        constraints.gridx++;
        constraints.weightx = 1;
        filePanel.add(pathBtn);
        filegrid.setConstraints(pathBtn, constraints);

        return filePanel;
    }

    /**
     * Creates panel with settings for conformance generation.
     * @return
     */
    public JPanel conformancePanel()
    {
        JPanel confPanel = new JPanel();
        this.conformprop = confPanel;
        confPanel.setBorder(BorderFactory.createTitledBorder("Conformance Settings"));

        // GB Layout for Conformance Settings

        GridBagLayout confgrid = new GridBagLayout();
        confPanel.setLayout(confgrid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;
        constraints.weightx = 1;

        // Creating components

        JLabel modelLabel = new JLabel("Base Model: ");

        baseField = new JTextField();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(settings.getCurrentDir());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Aldebaran files (.aut)", "aut");
        fileChooser.setFileFilter(filter);
        JButton baseBtn = new JButton("");
        baseBtn.setToolTipText("Select .aut");
        baseBtn.setPreferredSize(new Dimension(20, 20));
        baseBtn.setBackground(buttonColor);
        baseBtn.setOpaque(true);
        baseBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int option = fileChooser.showOpenDialog(fileChooser);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    baseField.setText(file.getAbsolutePath());
                    settings.setPathBase(file.getPath());
                    settings.setCurrentDir(file.getParentFile());
                    settings.updateDir();
                    dirChooser.setCurrentDirectory(file.getParentFile());
                    fileChooser.setCurrentDirectory(file.getParentFile());
                } else{ }
            }
        });
        baseBtn.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));

        ButtonGroup confGroup = new ButtonGroup();
        JRadioButton noneRadio = new JRadioButton("None");
        JRadioButton confRadio = new JRadioButton("Conforms");
        JRadioButton notConfRadio = new JRadioButton("Does not conform");
        confGroup.add(noneRadio);
        confGroup.add(confRadio);
        confGroup.add(notConfRadio);

        noneRadio.setSelected(true);

        noneRadio.addActionListener(action -> {
            if(action.getActionCommand().equals("None"))
            {
                settings.setConformanceType("None");
            }
        });
        confRadio.addActionListener(action -> {
            if(action.getActionCommand().equals("Conforms"))
            {
                settings.setConformanceType("Conforms");
            }
        });
        notConfRadio.addActionListener(action -> {
            if(action.getActionCommand().equals("Does not conform"))
            {
                settings.setConformanceType("Not Conform");
            }
        });

        // Adding components
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.insets = new Insets(10, 5, 5, 5);
        confPanel.add(modelLabel);
        confgrid.setConstraints(modelLabel, constraints);

        constraints.gridx++;
        constraints.weightx = 8;
        confPanel.add(baseField);
        confgrid.setConstraints(baseField, constraints);

        constraints.gridx++;
        constraints.weightx = 1;
        confPanel.add(baseBtn);
        confgrid.setConstraints(baseBtn, constraints);

        constraints.gridy++;
        constraints.gridx = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        confPanel.add(noneRadio);
        confgrid.setConstraints(noneRadio, constraints);

        constraints.gridx++;
        confPanel.add(confRadio);
        confgrid.setConstraints(confRadio, constraints);

        constraints.gridx++;
        confPanel.add(notConfRadio);
        confgrid.setConstraints(notConfRadio, constraints);

        return confPanel;
    }

    /**
     * Command to generate models according to
     * current settings.
     */
    public void executeAction()
    {
        // if(this.parent.indexOfTab("Resultados - IOLTS") != -1) {
        //     this.parent.removeTabAt(this.parent.indexOfTab("Resultados - IOLTS"));
        // }

        // ResultsTab resultsTab = new ResultsTab(this.parent, settings);

        GenerateIOLTS.setSettings(this.settings);
        GenerateIOLTS.run(this, null);
    }

    /**
     * Command to set input and output labels
     * to sets received as parameters.
     * @param inputs set of input labels
     * @param outputs set of output labels
     */
    public void setLabels(List<String> inputs, List<String> outputs)
    {
        inputsField.setText(String.join(", ", inputs));
        outputsField.setText(String.join(", ", outputs));
    }

    public void setCloseButton()
    {
        JPanel panelTab = new JPanel(new GridBagLayout());
        panelTab.setOpaque(false);
        JLabel labelTitle = new JLabel("Model Generation");

        JLabel btnClose = new JLabel("x");
        btnClose.setFont(new Font(btnClose.getFont().getName(), Font.BOLD, 10));
        btnClose.setForeground(Color.DARK_GRAY);
        btnClose.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        new EmptyBorder(3, 5, 2, 0),
                        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)),
                new EmptyBorder(-1, 3, 1, 3))
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        panelTab.add(labelTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        panelTab.add(btnClose, gbc);

        this.parent.setTabComponentAt(this.parent.indexOfTab("Gerar IOLTS"), panelTab);

        CloseActionHandler handler = new CloseActionHandler("Gerar IOLTS");
        handler.setTabPane(this.parent);
        btnClose.addMouseListener(handler);
    }

    public Settings getSettings()
    {
        return this.settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }
}

class CloseActionHandler implements MouseListener {

    private String tabName;
    private JTabbedPane tabPane;

    public CloseActionHandler(String tabName) {
        this.tabName = tabName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabPane(JTabbedPane tabPane)
    {
        this.tabPane = tabPane;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = tabPane.indexOfTab(getTabName());
        if (index >= 0) {
            tabPane.removeTabAt(index);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}