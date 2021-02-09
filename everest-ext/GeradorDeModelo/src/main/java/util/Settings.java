package util;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Settings {
    private String name;
    private String path;
    private static Random r = new Random();

    private int modelNumber = 0;
    private int stateNumber = 0;
    private int transitionNumber = 0;
    private int inputNumber = 0;
    private int outputNumber = 0;

    private List<String> inputs = new ArrayList<>();
    private List<String> outputs = new ArrayList<>();

    private int requestedTransitionNumber = 0;

    private boolean deterministic = false;
    private boolean inputComplete = false;
    private boolean outputComplete = false;
    private boolean inputDeterminism = false;
    private boolean outputDeterminism = false;
    private boolean acyclic = false;
    private boolean nonQuiescent = false;
    private boolean blackBox = false;

    private String conformanceType = "None";
    private String pathBase = "";

    private JFileChooser fc = new JFileChooser();
    private File currentDir = new File(System.getProperty("user.dir"));
    private File ini = new File(String.join(currentDir.getAbsolutePath(), "settings.ini"));

    /**
     * Empty constructor
     */
    public Settings() {}

    /**
     * Constructor with all parameters defined
     * @param x Number of input labels
     * @param y Number of output labels
     * @param n Number of states
     * @param m Number of transitions
     * @param iC Input-completeness property
     * @param oC Output-completeness property
     * @param iD Input-determinism property
     * @param oD Output-determinism property
     */
    public Settings(int x, int y, int n, int m, boolean iC, boolean oC, boolean iD, boolean oD)
    {
        this.stateNumber = n;
        this.transitionNumber = m;
        this.inputNumber = x;
        this.outputNumber = y;
        this.inputComplete = iC;
        this.outputComplete = oC;
        this.inputDeterminism = iD;
        this.outputDeterminism = oD;
    }

    public int setTransitions()
    {
        // Verifies if conformance-based generation
        if(!this.getConformanceType().equals("None"))
        {
            this.transitionNumber = this.requestedTransitionNumber;
            return this.transitionNumber;
        }

        int min = stateNumber-1;
        int max = stateNumber*(inputNumber+outputNumber);

        // Increases max if not deterministic
        if(!this.deterministic)
        {
            if(this.inputDeterminism)
            {
                max = stateNumber*inputNumber + stateNumber*(stateNumber*outputNumber);
            }
            else if(this.outputDeterminism){
                max = stateNumber*outputNumber + stateNumber*(stateNumber*inputNumber);
            }
            else{
                max = stateNumber * max;
            }
        }

        int m = requestedTransitionNumber > 0 ? requestedTransitionNumber : 0;

        // p omitted
        if(!this.inputComplete && !this.outputComplete)
        {
            if(this.inputDeterminism && this.outputDeterminism)
            {
                // 2n <= m' <= n(x+y)
                // Conditions to redefine number:
                if( (m < 2*stateNumber) || (m > max) )
                {
                    min = 2*stateNumber;
                    if (min == max) m = min;
                    else m = r.nextInt(max - min) + min;
                }
            }
            else if(this.inputDeterminism || this.outputDeterminism)
            {
                // n <= m' <= n(x+y)
                // Conditions to redefine number:
                if( (m < stateNumber) || (m > max) )
                {
                    min = stateNumber;
                    if (min == max) m = min;
                    else m = r.nextInt(max - min) + min;
                }
            }
            else
            {
                // n-1 <= m' <= n(x+y)
                // Conditions to redefine number:
                if( (m < (stateNumber-1)) || (m > max) )
                {
                    min = stateNumber-1;
                    if (min == max) m = min;
                    else m = r.nextInt(max - min) + min;
                }
            }
        }
        // d omitted
        else if(!this.inputDeterminism && !this.outputDeterminism)
        {
            if(this.inputComplete && this.outputComplete)
            {
                // n(x+y)
                m = stateNumber*(inputNumber+outputNumber);
            }
            else if(this.inputComplete)
            {
                // nx <= m' <= n(x+y)
                // Conditions to redefine number:
                if( (m < (stateNumber*inputNumber)) || (m > max) )
                {
                    min = stateNumber*inputNumber;
                    if (min == max) m = min;
                    else m = r.nextInt(max - min) + min;
                }
            }
            else if(this.outputComplete)
            {
                // ny <= m' <= n(x+y)
                // Conditions to redefine number:
                if( (m < (stateNumber*outputNumber)) || (m > max) )
                {
                    min = stateNumber*outputNumber;
                    if (min == max) m = min;
                    else m = r.nextInt(max - min) + min;
                }
            }
            else
            {
                // n-1 <= m' <= n(x+y)
                // Conditions to redefine number:
                if( (m < (stateNumber-1)) || (m > max) )
                {
                    min = stateNumber-1;
                    if (min == max) m = min;
                    else m = r.nextInt(max - min) + min;
                }
            }
        }
        else
        {
            if(this.inputComplete && this.outputComplete)
            {
                // n(x+y)
                m = stateNumber*(inputNumber+outputNumber);
            }
            else if(this.inputComplete)
            {
                if(this.outputDeterminism)
                {
                    // nx + n <= m' <= n(x+y)
                    // Conditions to redefine number:
                    if( (m < (stateNumber*inputNumber) + stateNumber) || (m > max) )
                    {
                        min = (stateNumber*inputNumber) + stateNumber;
                        if (min == max) m = min;
                        else m = r.nextInt(max - min) + min;
                    }
                }
                else
                {
                    // nx <= m' <= n(x+y)
                    // Conditions to redefine number:
                    if( (m < (stateNumber*inputNumber)) || (m > max) )
                    {
                        min = stateNumber*inputNumber;
                        if (min == max) m = min;
                        else m = r.nextInt(max - min) + min;
                    }
                }
            }
            else if(this.outputComplete)
            {
                if(this.inputDeterminism)
                {
                    // ny + n <= m' <= n(x+y)
                    // Conditions to redefine number:
                    if( (m < (stateNumber*outputNumber) + stateNumber) || (m > max) )
                    {
                        min = (stateNumber*outputNumber) + stateNumber;
                        if (min == max) m = min;
                        else m = r.nextInt(max - min) + min;
                    }
                }
                else
                {
                    // ny <= m' <= n(x+y)
                    // Conditions to redefine number:
                    if( (m < (stateNumber*outputNumber)) || (m > max) )
                    {
                        min = stateNumber*outputNumber;
                        if (min == max) m = min;
                        else m = r.nextInt(max - min) + min;
                    }
                }
            }
        }

        this.transitionNumber = m;
        return m;
    }

    /**
     * Sets up current directory from settings file.
     */
    public void setupDir()
    {
        // Verifica existência de arquivo de inicialização
        if(ini.exists())
        {
            // Lê arquivo de inicialização
            try {
                Scanner fileReader = new Scanner(ini);
                while(fileReader.hasNext())
                {
                    String dir = fileReader.nextLine();
                    dir = dir.replace("currentDir: ", "");
                    currentDir = new File(dir);
                }
                fileReader.close();
            }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
        else
        {
            // Cria arquivo de inicialização
            try { ini.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }

            try {
                FileWriter fileWriter = new FileWriter(ini);
                fileWriter.write("currentDir: " + currentDir.getAbsolutePath());
                fileWriter.close();
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    /**
     * Updates initial directory in settings file.
     */
    public void updateDir()
    {
        try {
            FileWriter fileWriter = new FileWriter(ini);
            fileWriter.write("currentDir: " + currentDir.getAbsolutePath());
            fileWriter.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /*
     * GETTERS AND SETTERS
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(int modelNumber) {
        this.modelNumber = modelNumber;
    }

    public int getStateNumber() {
        return stateNumber;
    }

    public void setStateNumber(int stateNumber) {
        this.stateNumber = stateNumber;
    }

    public int getRequestedTransitionNumber() { return requestedTransitionNumber; }

    public void setRequestedTransitionNumber(int requestedTransitionNumber) { this.requestedTransitionNumber = requestedTransitionNumber; }

    public int getTransitionNumber() {
        return transitionNumber;
    }

    public void setTransitionNumber(int transitionNumber) {
        this.transitionNumber = setTransitions();
    }

    public int getInputNumber() {
        return inputNumber;
    }

    public void setInputNumber(int inputNumber) {
        this.inputNumber = inputNumber;
    }

    public int getOutputNumber() {
        return outputNumber;
    }

    public void setOutputNumber(int outputNumber) {
        this.outputNumber = outputNumber;
    }

    public boolean isInputComplete() {
        return inputComplete;
    }

    public void setInputComplete(boolean inputComplete) {
        this.inputComplete = inputComplete;
    }

    public boolean isOutputComplete() {
        return outputComplete;
    }

    public void setOutputComplete(boolean outputComplete) {
        this.outputComplete = outputComplete;
    }

    public boolean isInputDeterminism() {
        return inputDeterminism;
    }

    public void setInputDeterminism(boolean inputDeterminism) {
        this.inputDeterminism = inputDeterminism;
    }

    public boolean isOutputDeterminism() {
        return outputDeterminism;
    }

    public void setOutputDeterminism(boolean outputDeterminism) {
        this.outputDeterminism = outputDeterminism;
    }

    public boolean isAcyclic() {
        return acyclic;
    }

    public void setAcyclic(boolean acyclic) {
        this.acyclic = acyclic;
    }

    public boolean isNonQuiescent() {
        return nonQuiescent;
    }

    public void setNonQuiescent(boolean nonQuiescent) {
        this.nonQuiescent = nonQuiescent;
    }

    public boolean isBlackBox() {
        return blackBox;
    }

    public void setBlackBox(boolean blackBox) {
        this.blackBox = blackBox;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConformanceType() {
        return conformanceType;
    }

    public void setConformanceType(String conformanceType) {
        this.conformanceType = conformanceType;
    }

    public String getPathBase() {
        return pathBase;
    }

    public void setPathBase(String pathBase) {
        this.pathBase = pathBase;
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File currentDir) {
        this.currentDir = currentDir;
    }

    public File getIni() {
        return ini;
    }

    public void setIni(File ini) {
        this.ini = ini;
    }
}
