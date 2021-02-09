package gui;

import algorithm.GenerateIOLTS;
import util.Settings;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CURRENTLY UNUSED
 */
public class ResultsTab {
    JTabbedPane parent;
    JPanel panel;
    Settings settings;

    public ResultsTab(JTabbedPane parent, Settings settings)
    {
        this.parent = parent;
        this.settings = settings;
        setUI();
//        getResults();
    }

    public void setUI()
    {
        this.panel = new JPanel();
        this.panel.add(resultsPanel());

        this.parent.addTab("Resultados - IOLTS", null, this.panel, "Resultados da geração de modelos IOLTS.");
        setCloseButton();

        this.parent.setSelectedIndex(this.parent.getTabCount()-1);
    }

    public JPanel resultsPanel()
    {
        GridBagLayout resultsGrid = new GridBagLayout();
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(resultsGrid);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10,5,10, 5);
        constraints.weightx = 1;
        constraints.weighty = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        JLabel resultsLabel = new JLabel("Modelos gerados com os seguintes parâmetros:");
        resultsPanel.add(resultsLabel);
        resultsGrid.setConstraints(resultsLabel, constraints);

        constraints.gridy++;
        File dir = new File(settings.getName()
                            + "_"
                            + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        JLabel dirName = new JLabel(new String("Diretório destino: " + dir.getAbsolutePath()));
        resultsPanel.add(dirName);
        resultsGrid.setConstraints(dirName, constraints);

        constraints.gridy++;
        constraints.insets = new Insets(3,5,3, 5);
        JLabel amountLabel = new JLabel(new String(settings.getModelNumber() + " modelos IOLTS gerados:"));
        resultsPanel.add(amountLabel);
        resultsGrid.setConstraints(amountLabel, constraints);

        constraints.gridy++;
        constraints.insets = new Insets(3,5,3, 5);
        JLabel stateLabel = new JLabel(new String("‣ " + settings.getStateNumber() + " estados."));
        resultsPanel.add(stateLabel);
        resultsGrid.setConstraints(stateLabel, constraints);

        constraints.gridy++;
        JLabel inputLabel = new JLabel(new String("‣ " + settings.getInputNumber() + " símbolos de entrada."));
        resultsPanel.add(inputLabel);
        resultsGrid.setConstraints(inputLabel, constraints);

        constraints.gridy++;
        JLabel outputLabel = new JLabel(new String("‣ " + settings.getOutputNumber() + " símbolos de saída."));
        resultsPanel.add(outputLabel);
        resultsGrid.setConstraints(outputLabel, constraints);

//        if(settings.isAcyclic())
//        {
//            constraints.gridy++;
//            JLabel acyclicalLabel = new JLabel(new String("‣ Acíclio."));
//            resultsPanel.add(acyclicalLabel);
//            resultsGrid.setConstraints(acyclicalLabel, constraints);
//        }
        if(settings.isInputComplete())
        {
            constraints.gridy++;
            JLabel inputEnLabel = new JLabel(new String("‣ Input-enabled."));
            resultsPanel.add(inputEnLabel);
            resultsGrid.setConstraints(inputEnLabel, constraints);
        }
        if(settings.isOutputComplete())
        {
            constraints.gridy++;
            JLabel outputEnLabel = new JLabel(new String("‣ Output-enabled."));
            resultsPanel.add(outputEnLabel);
            resultsGrid.setConstraints(outputEnLabel, constraints);
        }
        if(settings.isInputDeterminism())
        {
            constraints.gridy++;
            JLabel inputDetLabel = new JLabel(new String("‣ Input determinístico."));
            resultsPanel.add(inputDetLabel);
            resultsGrid.setConstraints(inputDetLabel, constraints);
        }
        if(settings.isOutputDeterminism())
        {
            constraints.gridy++;
            JLabel outputDetLabel = new JLabel(new String("‣ Output determinístico."));
            resultsPanel.add(outputDetLabel);
            resultsGrid.setConstraints(outputDetLabel, constraints);
        }

        String strategyTitle = new String("");

        //TODO: Arrumar esse switch e as estratégias
        switch(/*settings.getStrategy()*/ 1)
        {
            case 1: {
                strategyTitle = new String(" ( Geração aleatória ).");
                break;
            }
            default:{
                strategyTitle = new String(".");
                break;
            }
        }

        constraints.gridy++;
        constraints.insets = new Insets(10,5,10, 5);
        JLabel strategyLabel = new JLabel(new String("Utilizando a estratégia "
                /*+ settings.getStrategy()*/
                + "Aleatória"
                + strategyTitle
        ));

        resultsPanel.add(strategyLabel);
        resultsGrid.setConstraints(strategyLabel, constraints);

        resultsPanel.setBorder(BorderFactory.createEtchedBorder());

        return resultsPanel;
    }

    public void setCloseButton()
    {
        JPanel panelTab = new JPanel(new GridBagLayout());
        panelTab.setOpaque(false);
        JLabel labelTitle = new JLabel("Resultados - IOLTS");

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

        this.parent.setTabComponentAt(this.parent.indexOfTab("Resultados - IOLTS"), panelTab);

        CloseActionHandler handler = new CloseActionHandler("Resultados - IOLTS");
        handler.setTabPane(this.parent);
        btnClose.addMouseListener(handler);
    }

//    public void getResults()
//    {
//        GenerateIOLTS.setSettings(this.settings);
//        GenerateIOLTS.run();
//    }
}
