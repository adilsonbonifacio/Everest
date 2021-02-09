package gui;

import model.IOLTS;
import parser.ImportAutFile;
import util.ModelImageGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class GraphFrame {
    /**
     * Interface for loading .aut files and presenting their directed graphs
     * @param file
     */
    public static void openFile(File file) throws Exception {
        String filename = file.toString();
        String separator = "\\";
        String[] parts = filename.replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
        String actual_name = parts[parts.length-1];
        actual_name = actual_name.replace(".aut", "");

        JFrame graphframe = new JFrame();
        graphframe.setTitle("Gráfico - " + actual_name);
        graphframe.setSize(400,400);
        graphframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        IOLTS iolts = ImportAutFile.autToIOLTS(file.getPath(), true, null, null);
        BufferedImage img = ModelImageGenerator.generateImage(iolts);

        graphframe.getContentPane().setLayout(new FlowLayout());
        graphframe.getContentPane().add(new JLabel(new ImageIcon(img)));
        graphframe.pack();
        graphframe.setVisible(true);
    }
}
