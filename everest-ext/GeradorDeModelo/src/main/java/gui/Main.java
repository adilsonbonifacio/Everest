package gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        setupUI();
    }

    public static void setupUI()
    {
        // Setting up main window
        JFrame mainWindow = new JFrame();
        mainWindow.setTitle("Gerador de Modelo");
        mainWindow.setSize(700,480);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting up menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        menuBar.add(fileMenu);
        JMenu genMenu = new JMenu("Gerador");
        menuBar.add(genMenu);

        // Setting up content panel
        JPanel contentPanel = new JPanel();
        JPanel spacerPanel = new JPanel();
        spacerPanel.setMaximumSize(new Dimension(600, 20));
        contentPanel.add(spacerPanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Setting up tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        contentPanel.add(tabbedPane);

        // Menu Items - File
        JMenuItem openBtn = new JMenuItem("Abrir");

        openBtn.addActionListener(actionEvent -> {
            JFileChooser fc = new JFileChooser(".");
            fc.setFileFilter(new ExtensionFilter());

            fc.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fc.showOpenDialog(mainWindow);

            if(result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();

                try {
                    GraphFrame.openFile(selectedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JMenuItem saveBtn = new JMenuItem("Salvar");
        saveBtn.addActionListener(actionEvent -> System.out.println("Salvar"));

        JMenuItem closeBtn = new JMenuItem("Fechar");
        closeBtn.addActionListener(actionEvent -> {
            mainWindow.setVisible(false);
            mainWindow.dispose();
            System.exit(0);
        });

        fileMenu.add(openBtn);
        fileMenu.add(saveBtn);
        fileMenu.add(closeBtn);

        // Menu Item - Generate
        JMenuItem genBtn = new JMenuItem("Gerar IOLTS");
        genBtn.addActionListener(actionEvent -> {
            if(tabbedPane.indexOfTab("Gerar IOLTS") == -1 ) { GenerateTab generateTab = new GenerateTab(tabbedPane); }
            else { tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Gerar IOLTS")); }
        });

        genMenu.add(genBtn);

        // Launch main window
        mainWindow.getContentPane().add(BorderLayout.NORTH, menuBar);
        mainWindow.getContentPane().add(BorderLayout.CENTER, contentPanel);
        mainWindow.setVisible(true);
    }

}

class ExtensionFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if(file.isDirectory()) { return true; }
        return file.getAbsolutePath().toLowerCase().endsWith(".aut");
    }

    @Override
    public String getDescription() {
        return ".aut (Aldebaran)";
    }
}