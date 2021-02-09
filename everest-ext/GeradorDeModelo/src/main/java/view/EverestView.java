package view;

import algorithm.IocoConformance;
import algorithm.LanguageBasedConformance;
import algorithm.Operations;
import algorithm.TestGeneration;
import dk.brics.automaton.RegExp;
import gui.GenerateTab;
import model.Automaton_;
import model.IOLTS;
import model.LTS;
import model.State_;
import org.apache.commons.lang.StringUtils;
import parser.ImportAutFile;
import util.AutGenerator;
import util.Constants;
import util.ModelImageGenerator;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class EverestView extends JFrame {
    private JComboBox cbModel;
    private JComboBox cbLabel;
    private JLabel lblImplementation;
    private JLabel lblSpecification;
    private JLabel lblOutput;
    private JLabel lblInput;
    private JLabel lblLabelInp;
    private JLabel lblLabelOut;
    private JLabel lblRotulo;
    private JPanel contentPane;
    private JTextField tfImplementation;
    private JTextField tfSpecification;
    private JLabel lblD;
    private JLabel lblRegexD;
    private JLabel lblF;
    private JLabel lblRegexF;
    private JLabel lblmodelIoco;
    private JLabel lblImplementationIoco;
    private JButton btnViewModelIoco;
    private JButton btnViewImplementationIoco;
    private JButton btnViewModelLang;
    private JButton btnViewImplementationLang;
    private JLabel lblLabelIoco;
    private JLabel lblLabel;
    JLabel lblOutput_1;
    JLabel lblnput;
    TextArea lblWarningLang;
    TextArea lblWarningIoco;
    JButton btnRunMultigraph;
    JLabel lbl_result;
    JLabel lblRotulo2;
    JComboBox cbModel2;
    JLabel lblIolts;
    JLabel label_8;
    JPanel panel_1;
    final JPanel panel = new JPanel();

    // TS Generation
    JButton btnGenerate, btnViewModel_gen, btnViewImplementation_gen;
    JLabel lblModel;
    JLabel lblInputLabel_gen;
    JLabel lblInput_gen, lblM;
    JLabel lblmodel_gen, lblLabel_gen;
    JLabel lblIut, lblimplementation_gen, lblOutput_gen, lblLabelOutput, imgModel_gen, imgImplementation_gen;
    JTextArea taTestCases_gen, taWarning_gen;
    JComboBox cbLabel2;

    // Run Test iut x tp
    JLabel lblTestPurposes;
    JLabel lblSelectFolderContaining;
    JButton btnSelectTp;
    JButton btnSelectFolderTP;
    JRadioButton rdbtnOneTP;
    JRadioButton rdbtnTPbatch;
    JTextArea taWarningRun;
    JRadioButton rdbtnOneIut;
    JRadioButton rdbtnInBatch;

    JButton btnrunTp;
    List<String> testSuite;
    Automaton_ multigraph;
    JLabel lblNumTC;

    private String pathImplementation = null;
    private List<String> pathImplementations = null;
    private String pathSpecification = null;
    BufferedImage pathImageModel = null;
    BufferedImage pathImageImplementation = null;

    private String failPath;    // Caminho único para implementação única
    private List<String> failPaths; // Múltiplos caminhos para múltiplas implementações
    private JFileChooser fc = new JFileChooser();
    private JFileChooser fdc = directoryAutChooser();
    private boolean isDirectoryImpl = false;
    private File implementationDir = null;
    private File currentDir = new File(System.getProperty("user.dir"));
    private File ini = new File(String.join(currentDir.getAbsolutePath(), "settings.ini"));
    private JTextField tfInput;
    private JTextField tfOutput;
    // private final ButtonGroup buttonGroup = new ButtonGroup();
    JLabel lblInputIoco;
    JLabel lblOutputIoco;
    JTextArea taTestCasesIoco;
    JPanel panel_conf;
    JPanel panel_test_generation;
    JPanel panel_test_execution;

    private SystemColor backgroundColor = SystemColor.menu;
    private SystemColor labelColor = SystemColor.windowBorder;
    private SystemColor tipColor = SystemColor.windowBorder;
    private SystemColor borderColor = SystemColor.windowBorder;
    private SystemColor textColor = SystemColor.controlShadow;
    private SystemColor buttonColor = SystemColor.activeCaptionBorder;

    private Long lastModifiedSpec;
    private Long lastModifiedImp;
    List<String> words;
    Automaton_ multgraph;
    boolean iutValid;
    boolean isModelProcess = false;
    boolean isImplementationProcess = false;

    Automaton_ conformidade = null;
    IOLTS S, I = null;
    ArrayList<String> inp = null;
    ArrayList<String> out = null;
    boolean runLTS = false;
    boolean showImplementationImage = true;
    boolean showSpecificationImage = true;
    String typeLabel;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EverestView frame = new EverestView();
                    // frame.setResizable(false);
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /********************
     *      INDEX       |
     * ---------------- |
     *   PATHS/FILES    |
     *   PROCESSING     |
     *   DISPLAY        |
     *   IMAGES         |
     *   VERDICT        |
     *   TESTING        |
     *   MULTIGRAPH     |
     *******************/

    /***********************************************************
     *              PATHS AND FILES SETUP                      |
     ***********************************************************/

    /**
     * Dialog for choosing directories.
     */
    public JFileChooser directoryChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        return fc;
    }

    /**
     * Dialog for choosing aut files and directories.
     */
    public JFileChooser directoryAutChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setAcceptAllFileFilterUsed(false);
        return fc;
    }

    /**
     * Gets IUT path from view and sets up models.
     */
    public void getImplementationPath() {
        // Inicializa failPath
        failPath = "";
        // Limpa quaisquer verdicts exibidos
        cleanVeredict();

        try {
            // Inicia filechooser de .aut e diretório no diretório atual
            fdc = directoryAutChooser();
            fdc.setCurrentDirectory(currentDir);
            int res = fdc.showOpenDialog(EverestView.this);

            if (res == JFileChooser.APPROVE_OPTION) {
                // Exibe nome do arquivo selecionado no campo correspondente
                tfImplementation.setText(fdc.getSelectedFile().getName());

                // Se o arquivo selecionado é um diretório
                if(fdc.getSelectedFile().isDirectory())
                {
                    // Define flag de diretório de IUTs como true
                    isDirectoryImpl = true;
                    // Inicializa vetor de failPaths
                    failPaths = new ArrayList<>();

                    // Designa path do diretório à variável correspondente
                    pathImplementation = fdc.getSelectedFile().getAbsolutePath();

                    // Designa arquivo do diretório à variável correspondente
                    implementationDir = fdc.getSelectedFile();

                    // Atualiza diretório atual para diretório selecionado
                    fdc.setCurrentDirectory(fdc.getSelectedFile());

                    // Inicializa e preenche vetor com paths de cada arquivo .aut
                    pathImplementations = new ArrayList<>();
                    for(File file : implementationDir.listFiles())
                    {
                        if(isAutFile(file))
                        {
                            pathImplementations.add(file.getAbsolutePath());
                        }
                    }

                    // Chama função que irá processar os modelos IUT
                    processModelsFromDir(true, true);
                }
                // Se o arquivo selecionado é um .aut
                else
                {
                    isDirectoryImpl = false;
                    pathImplementation = fdc.getSelectedFile().getAbsolutePath();
                    fdc.setCurrentDirectory(fdc.getSelectedFile().getParentFile());

                    processModels(true, true);
                }

                // Define flag de IUT como falsa
                isImplementationProcess = false;
                // Atualiza timestamp de modificação da IUT
                lastModifiedImp = new File(pathImplementation).lastModified();

                // Exibe nome do arquivo/diretório também em campos correspondentes nas outras views
                lblImplementationIoco.setText(tfImplementation.getText());
                lblimplementationLang.setText(tfImplementation.getText());
                lblimplementation_gen.setText(tfImplementation.getText());
                lbliut_gen.setText(tfImplementation.getText());

                // Permite modificação da combobox para escolher LTS/IOLTS
                label_8.setEnabled(true);
                cbModel2.setEnabled(true);

                // Fecha quaisquer frames abertas
                closeFrame(true);

                // Atualiza diretório atual nas configurações
                currentDir = fdc.getCurrentDirectory();
                updateDir();
            }

        } catch (Exception e) { }
    }

    /**
     * Gets SUT path from view and sets up model.
     */
    public void getSpecificationPath() {
        // Inicializa failPath
        failPath = "";
        // Limpa quaisquer verdicts exibidos
        cleanVeredict();

        try {
            // Permite apenas .aut
            configFilterFile();

            // Inicia filechooser de arquivo no diretório atual
            fc.setCurrentDirectory(currentDir);
            int res = fc.showOpenDialog(EverestView.this);

            if (res == JFileChooser.APPROVE_OPTION) {
                // Exibe nome do arquivo selecionado no campo correspondente
                tfSpecification.setText(fc.getSelectedFile().getName());

                // Designa path à propriedade
                pathSpecification = fc.getSelectedFile().getAbsolutePath();

                // Exibe nome do arquivo também em campos correspondentes nas outras views
                lblmodelIoco.setText(tfSpecification.getText());
                lblmodelLang.setText(tfSpecification.getText());
                lblmodel_gen.setText(tfSpecification.getText());

                // Modifica filechooser para apontar para o diretório utilizado
                currentDir = fc.getCurrentDirectory();
                updateDir();

                // Chama função que irá processar o modelo SUT
                processModels(false, true);
                // Define flag de SUT como falsa
                isModelProcess = false;

                // Atualiza timestamp de modificação da SUT
                lastModifiedSpec = new File(pathSpecification).lastModified();
                // Fecha quaisquer frames abertas
                closeFrame(false);
                specOfMultigraph = false;

                // Permite modificação da combobox para escolher LTS/IOLTS
                lblIolts.setEnabled(true);
                cbModel.setEnabled(true);
            }

        } catch (Exception e) {
        }

    }

    /**
     * Configure .aut file filter
     */
    public void configFilterFile() {
        FileFilter autFilter = new FileTypeFilter(".aut", "Aut Files");

        fc.addChoosableFileFilter(autFilter);
        fdc.addChoosableFileFilter(autFilter);

        fc.setAcceptAllFileFilterUsed(false);
        fdc.setAcceptAllFileFilterUsed(false);
    }

    public class FileTypeFilter extends FileFilter {
        private String extension;
        private String description;

        public FileTypeFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            return file.getName().endsWith(extension);
        }

        public String getDescription() {
            return description + String.format(" (*%s)", extension);
        }
    }

    /**
     * Sets up current directory from settings file.
     */
    private void setupDir()
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

        // Inicializa filechooser no diretório atual de execução
        fc.setCurrentDirectory(currentDir);
    }

    /**
     * Updates initial directory in settings file.
     */
    private void updateDir()
    {
        try {
            FileWriter fileWriter = new FileWriter(ini);
            fileWriter.write("currentDir: " + currentDir.getAbsolutePath());
            fileWriter.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Verifica atualizações nos arquivos de modelos
     */
    public void verifyModelFileChange(boolean ioco, boolean bothModels) {
        // Se path da especificação não é nulo
        if (pathSpecification != null) {
            // Se foi atualizado depois da última modificação registrada
            if (!isModelProcess || lastModifiedSpec != new File(pathSpecification).lastModified()) {
                // Método para processamento do modelo atualizado
                processModels(false, ioco);
                // Fecha quaisquer frames abertos
                closeFrame(false);
                // Atualiza timestamp de modificação do arquivo
                lastModifiedSpec = new File(pathSpecification).lastModified();
                // Ativa visualização da imagem de especificação
                showSpecificationImage = true;
            }
        }

        // Se path da implementação não é nulo
        if (pathImplementation != null && bothModels) {
            // Se o arquivo de IUT foi atualizado após último registro
            if (!isImplementationProcess || lastModifiedImp != new File(pathImplementation).lastModified()) {
                // Se é batch
                if(isDirectoryImpl) { processModelsFromDir(true, ioco); }
                // Se é único
                else{ processModels(true, ioco); }
                // Fecha quaisquer frames abertos
                closeFrame(true);
                // Atualiza timestamp de modificação do arquivo
                lastModifiedImp = new File(pathImplementation).lastModified();
                // Ativa visualização da imagem de especificação
                showImplementationImage = true;
            }
        }

        // Apaga verdicts exibidos
        try { cleanVeredict(); } catch (Exception e) { }
    }


    /***********************************************************
     *                      MODEL PROCESSING                   |
     ***********************************************************/

    /**
     * Processes and set current model.
     */
    public boolean setModel(boolean lts, boolean implementation) {
        boolean modelValid = true;

        try {
            // Se é processamento de LTS - Obtenção do alfabeto
            if (lts) {
                // Se é SUT
                if (!implementation) {
                    tfInput
                            .setText(StringUtils.join(ImportAutFile.autToLTS(pathSpecification, false)
                                    .getAlphabet(), ","));

                }
                // Se é IUT
                else {
                    tfInput
                            .setText(StringUtils.join(ImportAutFile.autToLTS(pathImplementation, false)
                                    .getAlphabet(), ","));
                }
            }

            // Seleção de LTS/IOLTS
            int selectLabel = 0;
            if (implementation)
                // Obtém label de marcador de label da IUT
                selectLabel = cbLabel2.getSelectedIndex();
            else
                // Obtém label de marcador de label da SUT
                selectLabel = cbLabel.getSelectedIndex();

            // Obtenção do alfabeto - Input+Output Manual
            if (selectLabel == 2 || lts) {
                // Obtém labels de input do campo respectivo
                inp = new ArrayList<>(Arrays.asList(tfInput.getText().split(",")));
                // Obtém labels de output do campo respectivo
                out = new ArrayList<>(Arrays.asList(tfOutput.getText().split(",")));

                // remove space after/before alphabet on tfInput/tfOutput
                for (int i = 0; i < inp.size(); i++) {
                    inp.set(i, inp.get(i).trim());
                }
                for (int i = 0; i < out.size(); i++) {
                    out.set(i, out.get(i).trim());
                }

                // Importa SUT do .aut com os alfabetos definidos
                if (!implementation) { S = ImportAutFile.autToIOLTS(pathSpecification, true, inp, out); }
                else {  I = ImportAutFile.autToIOLTS(pathImplementation, true, inp, out); }

                // Se é LTS apaga campo de input
                if (lts) {
                    tfInput.setText("");
                }

                // out = null;
                // inp = null;
            }
            // Obtenção do alfabeto - Input+Output Automático
            else {

                // Importa SUT com alfabeto a ser definido automaticamente
                if (!implementation) {
                    S = ImportAutFile.autToIOLTS(pathSpecification, false, new ArrayList<String>(),
                            new ArrayList<String>());

                }
                // Importa IUT com alfabeto a ser definido automaticamente
                else {
                    I = ImportAutFile.autToIOLTS(pathImplementation, false,
                            new ArrayList<String>(),
                            new ArrayList<String>());
                }

                // Flag de de modelo inválido
                boolean msg = false;
                if (S != null) {
                    if (S.getTransitions().size() == 0 || (S.getOutputs().size() == 0 && S.getInputs().size() == 0)) {
                        msg = true;
                    }
                }
                if (I != null) {
                    if (I.getTransitions().size() == 0 || (I.getOutputs().size() == 0 && I.getInputs().size() == 0)) {
                        msg = true;
                    }
                }

                // Se flag está ativa - Exibe mensagens de erro
                if (msg) {
                    modelValid = false;
                    if (implementation) {
                        if (!lblWarningIoco.getText().contains(ViewConstants.msgImp)) {
                            lblWarningIoco.setText(lblWarningIoco.getText() + ViewConstants.msgImp);
                        }

                        if (!lblWarningLang.getText().contains(ViewConstants.msgImp)) {
                            lblWarningLang.setText(lblWarningLang.getText() + ViewConstants.msgImp);
                        }

                        if (!taWarning_gen.getText().contains(ViewConstants.msgImp)) {
                            taWarning_gen.setText(taWarning_gen.getText() + ViewConstants.msgImp);
                        }
                    } else {
                        if (!lblWarningIoco.getText().contains(ViewConstants.msgModel)) {
                            lblWarningIoco.setText(lblWarningIoco.getText() + ViewConstants.msgModel);
                        }

                        if (!lblWarningLang.getText().contains(ViewConstants.msgModel)) {
                            lblWarningLang.setText(lblWarningLang.getText() + ViewConstants.msgModel);
                        }
                        if (!taWarning_gen.getText().contains(ViewConstants.msgModel)) {
                            taWarning_gen.setText(taWarning_gen.getText() + ViewConstants.msgModel);
                        }
                    }

                }
                // Se flag está inativa - Remove mensagens de erro
                else {
                    if (implementation) {
                        removeMessage(true, ViewConstants.msgModel);
                        removeMessage(false, ViewConstants.msgModel);
                        removeMessageGen(ViewConstants.msgModel);
                    } else {
                        removeMessage(true, ViewConstants.msgImp);
                        removeMessage(false, ViewConstants.msgImp);
                        removeMessageGen(ViewConstants.msgImp);
                    }
                }

            }

            // Se IUT continuou nulo marca processo como falso
            if (I == null) {
                isImplementationProcess = false;
            }
            // Se a IUT foi gerada, adiciona transições quiescentes
            else {
                // IUT with quiescent transitions
                if (implementation) {
                    I.addQuiescentTransitions();
                }
            }

            // Se SUT continuou nulo marca processo como falso
            if (S == null) {
                isModelProcess = false;
            }
            // Se a SUT foi gerada, adiciona transições quiescentes
            else {
                if (!implementation) {
                    S.addQuiescentTransitions();
                }
            }

            // Se SUT e IUT estão definidos, permite verificação
            if(S != null && I != null)
            {
                btnVerifyConf_ioco.setVisible(true);
                btnVerifyConf_lang.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (implementation) {
                isImplementationProcess = false;
                I = null;
            } else {
                isModelProcess = false;
                S = null;
            }

            return false;
        }

        return modelValid;
    }

    /**
     * Method to process single model and enable image display.
     */
    public void processModels(boolean implementation, boolean ioco) {
        // Inicializa flag de LTS como falsa
        boolean lts = false;

        // Verifica se processamento a ser feito é de LTS
        if (cbModel.getSelectedIndex() == 0
                || (cbLabel.getSelectedIndex() == 0 && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST)
                || cbModel.getSelectedItem() == ViewConstants.LTS_CONST
                || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel && tfInput.getText().isEmpty()
                && tfOutput.getText().isEmpty())) {
            // Se for, ativa flag de LTS
            lts = true;
        }

        // Marca tipo de modelo a ser processado por suas respectivas flags
        if (!implementation) {
            isModelProcess = true;
        } else {
            isImplementationProcess = true;
        }

        // Executa método para permitir exibição das imagens dos modelos
        enableShowImage(lts, implementation);

        // Executa método para exibir campos com alfabetos
        try {
            // Se todos os campos estão preenchidos corretamente, exibe
            // alfabeto e/ou inputs e outputs
            if ((ioco && isFormValid(ioco)) || (!ioco && isFormValid(ioco))) {

                if (lts) {
                    showModelLabel_(true);
                } else {
                    showModelLabel_(false);
                }

            }
            // Senão, exibe apenas o que já foi fornecido (como paths)
            else {
                if (lts) {
                    showModelLabel(true);
                } else {
                    showModelLabel(false);
                }
            }

        } catch (Exception e) {  }

    }

    /**
     * Method to process multiple models and enable image display.
     */
    public void processModelsFromDir(boolean implementation, boolean ioco)
    {
        // Inicializa flag de LTS como falsa
        boolean lts = false;
        // Flag "global" para processamento batch
        runLTS = false;

        // Verifica se processamento a ser feito é de LTS
        if (cbModel.getSelectedIndex() == 0
                || (cbLabel.getSelectedIndex() == 0 && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST)
                || cbModel.getSelectedItem() == ViewConstants.LTS_CONST
                || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel && tfInput.getText().isEmpty()
                && tfOutput.getText().isEmpty())) {
            // Se for, ativa flag de LTS
            lts = true;
            runLTS = true;
        }

        // Marca tipo de modelo a ser processado por suas respectivas flags
        if (!implementation) {
            isModelProcess = true;
        } else {
            isImplementationProcess = true;
        }

        // Define primeiro arquivo como modelo inicial IUT
        File file = new File(pathImplementations.get(0));
        // Se modelo é LTS
        if(lts)
        {
            try {
                I = ImportAutFile.autToIOLTS(file.getPath(), true, inp, out);
            } catch (Exception e) {  e.printStackTrace(); }
        }
        // Se modelo é IOLTS
        else {
            try {
                I = ImportAutFile.autToIOLTS(file.getPath(), false, new ArrayList<>(), new ArrayList<>());
            } catch (Exception e) {  e.printStackTrace(); }
        }

        // Executa método para permitir exibição das imagens dos modelos
        btnViewImplementationIoco.setVisible(true);
        btnViewImplementationIoco.setEnabled(true);
        btnViewImplementationLang.setVisible(true);
        btnViewImplementationLang.setEnabled(true);

        // Executa método para exibir campos com alfabetos
        try {
            // Se todos os campos estão preenchidos corretamente, exibe
            // alfabeto e/ou inputs e outputs
            if ((ioco && isFormValid(ioco)) || (!ioco && isFormValid(ioco))) {

                if (lts) {
                    showModelLabel_(true);
                } else {
                    showModelLabel_(false);
                }

            }
            // Senão, exibe apenas o que já foi fornecido (como paths)
            else {
                if (lts) {
                    showModelLabel(true);
                } else {
                    showModelLabel(false);
                }
            }

        } catch (Exception e) {  }
    }

    /**
     * Verifies the validity of filled fields.
     */
    public boolean isFormValid(boolean ioco) {
        // Flag que indica se a soma dos inputs e outputs é igual a soma dos alfabetos
        boolean defineInpOut = true;

        // Se SUT e IUT foram carregados
        if (S != null && I != null) {
            // Cria nova lista para alfabeto
            List<String> inpOut = new ArrayList<>();
            // Adiciona todos os inputs de SUT
            inpOut.addAll(S.getInputs());
            // Adiciona todos os outputs de SUT
            inpOut.addAll(S.getOutputs());
            // Adiciona todos os inputs de IUT
            inpOut.addAll(I.getInputs());
            // Adiciona todos os outputs de IUT
            inpOut.addAll(I.getOutputs());

            // Cria nova lista para alfabeto
            List<String> alphabet = new ArrayList<>();
            // Adiciona o alfabeto de SUT
            alphabet.addAll(S.getAlphabet());
            // Adiciona o alfabeto de IUT
            alphabet.addAll(I.getAlphabet());

            // Verifica se a soma dos inputs e outputs é igual a soma dos alfabetos
            defineInpOut = inpOut.containsAll(alphabet);
        }

        // Verificações:
        //  Paths de IUT e SUT não podem estar vazios
        //  Deve estar selecionado se a SUT é LTS/IOLTS
        // ...
        //  Soma dos inputs/outputs deve ser igual a soma dos alfabetos
        //  Basicamente, retorna se as seleções estão completas para os testes
        return (!tfImplementation.getText().isEmpty() && !tfSpecification.getText().isEmpty()
                && (cbModel.getSelectedIndex() != 0 || (!ioco || cbModel.getSelectedIndex() == 0)))
                && (!ioco || (ioco && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST
                && cbLabel.getSelectedIndex() != 0
                && ((cbLabel.getSelectedItem() == ViewConstants.typeAutomaticLabel)
                || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel
                && (!tfInput.getText().isEmpty() && !tfOutput.getText().isEmpty()))
                && defineInpOut)));// model
    }

    /**
     * Check for valid regex for languages verification.
     */
    public boolean regexIsValid(String exp) {
        try {
            // RegExp regExp = new RegExp(exp);
            // regExp.toAutomaton();
            // regExp = null;
            // RegExp regExp = new RegExp(exp);
            new RegExp(exp).toAutomaton();
            // regExp = null;
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    /***********************************************************
     *                  DISPLAY AND VIEW                        |
     ***********************************************************/

    /**
     * Create the frame.
     */
    public EverestView() {
        // Setup directory for file chooser
        setupDir();

        // Setup frame
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/img/icon.PNG")));
        setTitle(ViewConstants.toolName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 833, 556);
        setMinimumSize(new Dimension(833, 580));
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        getContentPane().setLayout(new BorderLayout());

        setContentPane(contentPane);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        // Ações a executar para cada tab selecionada
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                boolean ioco = false;
                boolean generation = false;

                // Obtém título da tab selecionada
                String tab = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());

                // Se tab de verificação IOCO - Exibe labels respectivas
                if (tab.equals(ViewConstants.tabIOCO)) {
                    ioco = true;
                    lbl_veredict_ioco.setText("[Verdict]");
                    lbl_veredict_ioco.setForeground(SystemColor.windowBorder);
                    if (tfNTestCasesIOCO.getText().isEmpty()) {
                        tfNTestCasesIOCO.setText(Objects.toString(Constants.MAX_TEST_CASES));
                    }
                }
                // Se outra tab - Exibe labels respectivas
                else {
                    // Se tab de verificação lang-based
                    if (tab.equals(ViewConstants.tabLang)) {
                        ioco = false;
                        if (tfNTestCasesLang.getText().isEmpty()) {
                            tfNTestCasesLang.setText(Objects.toString(Constants.MAX_TEST_CASES));
                        }
                    }
                    // Se tab de test suite
                    else {
                        if (tab.equals(ViewConstants.tabTSGeneration)) {
                            generation = true;

                            visibilityRunButtons();
                            errorMessageGen();

                            if (isFormValidGeneration()) {
                                // taWarning_gen.setText("");
                                showModelLabel_(false);
                                verifyInpOutEmpty(false, true);
                                // verifyModelsEmpty(false, false);
                            }
                            // removeMessageGen(ViewConstants.selectImplementation);

                        }
                    }
                }

                // Se tab for de IOCO ou de lang-based
                if (tab.equals(ViewConstants.tabIOCO) || tab.equals(ViewConstants.tabLang)) {
                    // Verifica se modelos selecionados foram modificados
                    verifyModelFileChange(ioco, true);
                    try {
                        if (!isFormValid(ioco)) {
                            errorMessage(ioco);
                        } else {
                            errorMessage(ioco);// clean error message
                            verifyInpOutEmpty(false, false);
                            verifyModelsEmpty(false, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setFont(new Font("Microsoft YaHei Light", Font.PLAIN, 13));
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        panel_conf = new JPanel();
        panel_conf.setForeground(SystemColor.textInactiveText);
        panel_conf.setBackground(backgroundColor);
        panel_conf.setToolTipText("");
        tabbedPane.addTab("Configuration", null, panel_conf, null);

        cbModel = new JComboBox();
        cbModel.setForeground(textColor);
        cbModel.setBackground(backgroundColor);
        cbModel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                actionCbModel(arg0.getItem().toString(), true);
            }
        });

        cbModel.setModel(new DefaultComboBoxModel(ViewConstants.models));
        cbModel.setFont(new Font("Dialog", Font.BOLD, 13));
        cbModel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        cbModel.setEnabled(false);
        // cbModel.setVisible(false);

        lblImplementation = new JLabel("Implementation");
        lblImplementation.setBackground(backgroundColor);
        lblImplementation.setForeground(labelColor);
        lblImplementation.setFont(new Font("Dialog", Font.BOLD, 13));

        lblSpecification = new JLabel("Model");
        lblSpecification.setForeground(SystemColor.controlDkShadow);
        lblSpecification.setFont(new Font("Dialog", Font.BOLD, 13));

        tfImplementation = new JTextField();
        tfImplementation.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (tfImplementation.getText().isEmpty()) {
                    pathImplementation = null;
                    I = null;
                    cbModel2.setSelectedIndex(0);
                    cbLabel2.setSelectedIndex(0);
                    lblImplementationIoco.setText(tfImplementation.getText());
                    lblimplementationLang.setText(tfImplementation.getText());
                    lblimplementation_gen.setText(tfImplementation.getText());

                    label_8.setEnabled(false);
                    cbModel2.setEnabled(false);
                }
            }
        });
        tfImplementation.setForeground(textColor);
        tfImplementation.setBackground(backgroundColor);
        tfImplementation.setToolTipText("accepts .aut files and folders");
        tfImplementation.setFont(new Font("Dialog", Font.BOLD, 13));
        tfImplementation.addMouseListener(new MouseAdapter() {
            /*
             * @Override public void mouseClicked(MouseEvent arg0) {
             * getImplementationPath(); }
             */
            @Override
            public void mousePressed(MouseEvent e) {
                getImplementationPath();
            }
        });
        tfImplementation.setColumns(10);
        tfImplementation.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));

        tfSpecification = new JTextField();
        tfSpecification.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (tfSpecification.getText().isEmpty()) {
                    pathSpecification = null;
                    S = null;
                    cbModel.setSelectedIndex(0);
                    cbLabel.setSelectedIndex(0);

                    lblmodelIoco.setText(tfSpecification.getText());
                    lblmodelLang.setText(tfSpecification.getText());
                    lblmodel_gen.setText(tfSpecification.getText());

                    lblIolts.setEnabled(false);
                    cbModel.setEnabled(false);
                }
            }
        });
        tfSpecification.setForeground(textColor);
        tfSpecification.setBackground(backgroundColor);
        tfSpecification.setToolTipText("accepts only .aut files");
        tfSpecification.setFont(new Font("Dialog", Font.BOLD, 13));

        tfSpecification.addMouseListener(new MouseAdapter() {
            /*
             * @Override public void mouseClicked(MouseEvent e) { getSpecificationPath(); }
             */
            @Override
            public void mousePressed(MouseEvent e) {
                getSpecificationPath();

            }
        });
        tfSpecification.setColumns(10);
        tfSpecification.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));

        JButton btnFolderImp = new JButton("");
        btnFolderImp.setBackground(buttonColor);
        btnFolderImp.setOpaque(true);
        btnFolderImp.addMouseListener(new MouseAdapter() {
            /*
             * @Override public void mouseClicked(MouseEvent e) { getImplementationPath(); }
             */
            @Override
            public void mousePressed(MouseEvent e) {
                getImplementationPath();

            }
        });
        btnFolderImp.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));

        JButton btnFolderSpec = new JButton("");
        btnFolderSpec.setToolTipText("");
        btnFolderSpec.setBackground(buttonColor);
        btnFolderSpec.setOpaque(true);
        btnFolderSpec.addMouseListener(new MouseAdapter() {
            /*
             * @Override public void mouseClicked(MouseEvent e) { getSpecificationPath(); }
             */
            @Override
            public void mousePressed(MouseEvent e) {
                getSpecificationPath();
            }
        });
        btnFolderSpec.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));

        JList list = new JList();

        lblRotulo = new JLabel("Label");
        lblRotulo.setForeground(labelColor);
        lblRotulo.setFont(new Font("Dialog", Font.BOLD, 13));
        lblRotulo.setVisible(false);

        cbLabel = new JComboBox();
        cbLabel.setVisible(false);
        cbLabel.setForeground(textColor);
        cbLabel.setBackground(backgroundColor);
        cbLabel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                actionCbLabel(arg0.getItem().toString(), true);
            }
        });
        cbLabel.setModel(new DefaultComboBoxModel(
                new String[] { "", ViewConstants.typeAutomaticLabel, ViewConstants.typeManualLabel }));
        cbLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        // cbLabel.setVisible(false);
        cbLabel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));

        lblIolts = new JLabel("Model type");
        lblIolts.setForeground(SystemColor.windowBorder);
        lblIolts.setFont(new Font("Dialog", Font.BOLD, 13));
        // lblIolts.setVisible(false);

        label_8 = new JLabel("Model type");
        label_8.setForeground(SystemColor.windowBorder);
        label_8.setFont(new Font("Dialog", Font.BOLD, 13));
        // label_8.setVisible(false);

        lblRotulo2 = new JLabel("Label");
        lblRotulo2.setForeground(SystemColor.windowBorder);
        lblRotulo2.setFont(new Font("Dialog", Font.BOLD, 13));
        lblRotulo2.setVisible(false);
        cbModel2 = new JComboBox();
        cbModel2.setModel(new DefaultComboBoxModel(new String[] { "", "IOLTS", "LTS" }));
        cbModel2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                actionCbModel(arg0.getItem().toString(), false);
            }
        });
        cbModel2.setForeground(SystemColor.controlShadow);
        cbModel2.setFont(new Font("Dialog", Font.BOLD, 13));
        cbModel2.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        cbModel2.setBackground(SystemColor.menu);
        cbModel2.setEnabled(false);
        // cbModel2.setVisible(false);

        cbLabel2 = new JComboBox();
        cbLabel2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                actionCbLabel(e.getItem().toString(), false);
            }
        });
        cbLabel2.setVisible(false);
        cbLabel2.setModel(new DefaultComboBoxModel(new String[] { "", "?in, !out", "define I/O manually" }));
        cbLabel2.setForeground(SystemColor.controlShadow);
        cbLabel2.setFont(new Font("Dialog", Font.BOLD, 13));
        cbLabel2.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        cbLabel2.setBackground(SystemColor.menu);

        panel_1 = new JPanel();
        // panel_1.setForeground(Color.GRAY);
        // //panel_1.setBorder(UIManager.getBorder("TitledBorder.border"));
        // TitledBorder border = new TitledBorder(new TitledBorder("Models alphabet"));
        // border.setTitleFont( new Font("Dialog", Font.BOLD, 13));
        // border.setTitleFont( border.getTitleFont().deriveFont(Font.BOLD +
        // Font.ITALIC) );
        panel_1.setBorder((BorderFactory.createTitledBorder(null, "Models alphabet - I/O manually", TitledBorder.CENTER,
                TitledBorder.TOP, new Font("Dialog", Font.BOLD, 13), Color.GRAY)));

        panel_1.setVisible(false);

        GroupLayout gl_panel_conf = new GroupLayout(panel_conf);
        gl_panel_conf.setHorizontalGroup(gl_panel_conf.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(37)
                        .addComponent(lblSpecification, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE).addGap(542))
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(371).addComponent(list,
                        GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(37)
                        .addComponent(tfSpecification, GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE).addGap(2)
                        .addComponent(btnFolderSpec, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                        .addGap(41))
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(37)
                        .addComponent(lblIolts, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE).addGap(286)
                        .addComponent(lblRotulo, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE).addGap(317))
                .addGroup(
                        gl_panel_conf.createSequentialGroup().addGap(37).addComponent(cbModel, 0, 319, Short.MAX_VALUE)
                                .addGap(69).addComponent(cbLabel, 0, 336, Short.MAX_VALUE).addGap(41))
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(37)
                        .addComponent(lblImplementation, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE).addGap(608))
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(37)
                        .addComponent(label_8, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE).addGap(286)
                        .addComponent(lblRotulo2, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE).addGap(275))
                .addGroup(gl_panel_conf.createSequentialGroup().addGap(37)
                        .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                                .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                                .addGroup(gl_panel_conf.createSequentialGroup()
                                        .addComponent(tfImplementation, GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                                        .addGap(2).addComponent(btnFolderImp, GroupLayout.PREFERRED_SIZE, 39,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_panel_conf.createSequentialGroup()
                                        .addComponent(cbModel2, 0, 299, Short.MAX_VALUE).addGap(89)
                                        .addComponent(cbLabel2, 0, 336, Short.MAX_VALUE)))
                        .addGap(41)));
        gl_panel_conf.setVerticalGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_conf
                .createSequentialGroup().addGap(11)
                .addComponent(lblSpecification, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                .addComponent(list, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE).addGap(8)
                .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_conf.createSequentialGroup().addGap(2).addComponent(tfSpecification,
                                GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnFolderSpec, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                .addGap(13)
                .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_conf.createSequentialGroup().addGap(4).addComponent(lblIolts,
                                GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblRotulo, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                .addGap(6)
                .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                        .addComponent(cbModel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbLabel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(11).addComponent(lblImplementation, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addGap(9)
                .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_conf.createSequentialGroup().addGap(2).addComponent(tfImplementation,
                                GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnFolderImp, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                .addGap(11)
                .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_8, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRotulo2, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                .addGap(11)
                .addGroup(gl_panel_conf.createParallelGroup(Alignment.LEADING)
                        .addComponent(cbModel2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbLabel2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(26).addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        lblInput = new JLabel("Input labels");
        lblInput.setForeground(labelColor);
        lblInput.setFont(new Font("Dialog", Font.BOLD, 13));

        tfInput = new JTextField();
        tfInput.setForeground(textColor);
        tfInput.setBackground(backgroundColor);
        tfInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent arg0) {
                failPath = "";
                cleanVeredict();
                // isModelProcess = false;
                // isImplementationProcess = false;

                if (!tfInput.getText().isEmpty()) {
                    removeMessage(true, ViewConstants.selectInpOut);
                    removeMessage(false, ViewConstants.selectInpOut);
                }
            }
        });
        tfInput.setToolTipText("");
        tfInput.setFont(new Font("Dialog", Font.BOLD, 13));
        tfInput.setColumns(10);
        tfInput.setVisible(false);

        tfInput.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));

        lblLabelInp = new JLabel("(separated by comma)");
        lblLabelInp.setBackground(backgroundColor);
        lblLabelInp.setForeground(tipColor);
        lblLabelInp.setFont(new Font("Dialog", Font.BOLD, 12));

        lblOutput = new JLabel("Output  labels");
        lblOutput.setForeground(labelColor);
        lblOutput.setFont(new Font("Dialog", Font.BOLD, 13));

        tfOutput = new JTextField();
        tfOutput.setForeground(textColor);
        tfOutput.setBackground(backgroundColor);
        tfOutput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                failPath = "";
                cleanVeredict();
                // isModelProcess = false;
                // isImplementationProcess = false;

                if (!tfInput.getText().isEmpty()) {
                    removeMessage(true, ViewConstants.selectInpOut);
                    removeMessage(false, ViewConstants.selectInpOut);
                }
            }
        });
        tfOutput.setFont(new Font("Dialog", Font.BOLD, 13));
        tfOutput.setColumns(10);
        tfOutput.setVisible(false);
        tfOutput.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));

        lblLabelOut = new JLabel("(separated by comma)");
        lblLabelOut.setBackground(backgroundColor);
        lblLabelOut.setForeground(tipColor);
        lblLabelOut.setFont(new Font("Dialog", Font.BOLD, 12));
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup().addGap(4).addComponent(lblInput))
                .addGroup(gl_panel_1.createSequentialGroup().addGap(4)
                        .addComponent(tfInput, GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE).addGap(10))
                .addGroup(gl_panel_1.createSequentialGroup().addContainerGap(565, Short.MAX_VALUE)
                        .addComponent(lblLabelInp, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
                        .addGap(10))
                .addGroup(gl_panel_1.createSequentialGroup().addGap(4)
                        .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                                .addComponent(tfOutput, GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                                .addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 698, GroupLayout.PREFERRED_SIZE))
                        .addGap(10))
                .addGroup(gl_panel_1.createSequentialGroup().addContainerGap(565, Short.MAX_VALUE)
                        .addComponent(lblLabelOut, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                        .addGap(5)));
        gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup().addGap(1).addComponent(lblInput).addGap(7)
                        .addComponent(tfInput, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addGap(11)
                        .addComponent(lblLabelInp, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                        .addGap(14)
                        .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_1.createSequentialGroup().addGap(18).addComponent(tfOutput,
                                        GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addGap(11)
                        .addComponent(lblLabelOut, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)));
        panel_1.setLayout(gl_panel_1);
        lblLabelOut.setVisible(false);
        lblOutput.setVisible(false);
        lblLabelInp.setVisible(false);
        lblInput.setVisible(false);
        panel_conf.setLayout(gl_panel_conf);

        panel_ioco = new JPanel();
        tabbedPane.addTab(ViewConstants.tabIOCO, null, panel_ioco, null);

        tfNTestCasesIOCO = new JTextField();
        tfNTestCasesIOCO.setForeground(SystemColor.controlShadow);
        tfNTestCasesIOCO.setFont(new Font("Dialog", Font.BOLD, 13));
        tfNTestCasesIOCO.setBackground(UIManager.getColor("Button.background"));
        tfNTestCasesIOCO.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfNTestCasesIOCO.setNextFocusableComponent(btnVerifyConf_ioco);
        tfNTestCasesIOCO.setColumns(10);

        btnVerifyConf_ioco = new JButton("Verify");
        btnVerifyConf_ioco.setVisible(false);
        btnVerifyConf_ioco.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                actionVerifyConformance(true);
            }
        });
        btnVerifyConf_ioco.setFont(new Font("Dialog", Font.BOLD, 13));
        btnVerifyConf_ioco.setBackground(Color.LIGHT_GRAY);
        btnVerifyConf_ioco.setNextFocusableComponent(taTestCasesIoco);

        taTestCasesIoco = new JTextArea();
        taTestCasesIoco.setBounds(10, 277, 231, 150);
        JScrollPane scrolltxt = new JScrollPane(taTestCasesIoco);

        lbl_veredict_ioco = new JLabel("");
        lbl_veredict_ioco.setForeground(SystemColor.windowBorder);
        lbl_veredict_ioco.setFont(new Font("Dialog", Font.BOLD, 13));

        JLabel lblModel = new JLabel("Model");
        lblModel.setForeground(SystemColor.windowBorder);
        lblModel.setFont(new Font("Dialog", Font.BOLD, 13));

        JLabel lblImplementation_1 = new JLabel("Implementation");
        lblImplementation_1.setForeground(SystemColor.windowBorder);
        lblImplementation_1.setFont(new Font("Dialog", Font.BOLD, 13));

        lblnput = new JLabel("Input label");
        lblnput.setForeground(SystemColor.windowBorder);
        lblnput.setFont(new Font("Dialog", Font.BOLD, 13));

        lblOutput_1 = new JLabel("Output label");
        lblOutput_1.setForeground(SystemColor.windowBorder);
        lblOutput_1.setFont(new Font("Dialog", Font.BOLD, 13));

        lblmodelIoco = new JLabel("");
        lblmodelIoco.setForeground(SystemColor.controlShadow);

        lblImplementationIoco = new JLabel("");
        lblImplementationIoco.setForeground(SystemColor.controlShadow);

        lblInputIoco = new JLabel("");
        lblInputIoco.setForeground(SystemColor.controlShadow);

        lblOutputIoco = new JLabel("");
        lblOutputIoco.setForeground(SystemColor.controlShadow);

        imgModelIoco = new JLabel("");
        imgModelIoco.setVisible(false);
//        imgModelIoco.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent arg0) {
//                // showModelImage(false);
//            }
//        });
        imgImplementationIoco = new JLabel("");
        imgImplementationIoco.setVisible(false);

//        imgImplementationIoco.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                // showModelImage(true);
//            }
//        });

        btnViewModelIoco = new JButton("View model");
        btnViewModelIoco.setVisible(false);
        btnViewModelIoco.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            if (showSpecificationImage && btnViewModelIoco.isEnabled()) {
                    try {
                        pathImageModel = ModelImageGenerator.generateImage(S);
                    }
                    catch (IOException ioException) { ioException.printStackTrace(); }
                    showModelImage(false);
                }
            }
        });
        btnViewModelIoco.setFont(new Font("Dialog", Font.BOLD, 13));
        btnViewModelIoco.setBackground(Color.LIGHT_GRAY);

        btnViewImplementationIoco = new JButton("View IUT");
        btnViewImplementationIoco.setVerticalAlignment(SwingConstants.BOTTOM);
        btnViewImplementationIoco.setNextFocusableComponent(tfNTestCasesIOCO);
        btnViewImplementationIoco.setVisible(false);
        btnViewImplementationIoco.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showImplementationImage && btnViewImplementationIoco.isEnabled()) {
                    if(isDirectoryImpl)
                    {
                        showImplementationsList();
                    }
                    else{
                        try {
                            pathImageImplementation = ModelImageGenerator.generateImage(I);
                        } catch (IOException ioException) { ioException.printStackTrace(); }
                        showModelImage(true);
                    }
                }

//                if (!failPath.equals("")) {
//                    taTestCasesIoco.setText(failPath);
//                }

//                showVeredict(true);
            }
        });
        btnViewImplementationIoco.setFont(new Font("Dialog", Font.BOLD, 13));
        btnViewImplementationIoco.setBackground(Color.LIGHT_GRAY);

        lblLabel = new JLabel("Label");
        lblLabel.setVisible(false);
        lblLabel.setForeground(SystemColor.windowBorder);
        lblLabel.setFont(new Font("Dialog", Font.BOLD, 13));

        lblLabelIoco = new JLabel("");
        lblLabelIoco.setForeground(SystemColor.controlShadow);

        lblWarningIoco = new TextArea("");
        lblWarningIoco.setForeground(SystemColor.controlShadow);

        JLabel lblTestCases = new JLabel("# Test cases");
        lblTestCases.setForeground(SystemColor.windowBorder);
        lblTestCases.setFont(new Font("Dialog", Font.BOLD, 13));

        label_6 = new JLabel("Warnings");
        label_6.setForeground(SystemColor.windowBorder);
        label_6.setFont(new Font("Dialog", Font.BOLD, 13));
        GroupLayout gl_panel_ioco = new GroupLayout(panel_ioco);
        gl_panel_ioco.setHorizontalGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(37)
                        .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblModel, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblmodelIoco, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(76).addComponent(
                                        btnViewModelIoco, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)))
                        .addGap(123)
                        .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_ioco.createSequentialGroup()
                                        .addComponent(lblImplementation_1, GroupLayout.PREFERRED_SIZE, 133,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(10).addComponent(btnViewImplementationIoco, GroupLayout.PREFERRED_SIZE,
                                                154, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lblImplementationIoco, GroupLayout.PREFERRED_SIZE, 367,
                                        GroupLayout.PREFERRED_SIZE)))
                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(37)
                        .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblnput, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblLabel, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
                        .addGap(248)
                        .addComponent(lblOutput_1, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(37)
                        .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblInputIoco, GroupLayout.PREFERRED_SIZE, 378, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(388).addComponent(
                                        lblOutputIoco, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lblLabelIoco, GroupLayout.PREFERRED_SIZE, 755,
                                        GroupLayout.PREFERRED_SIZE)))
                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(10)
                        .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblTestCases, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(10).addComponent(
                                        tfNTestCasesIOCO, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
                        .addGap(10)
                        .addComponent(btnVerifyConf_ioco, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
                        .addGap(10)
                        .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(212).addComponent(
                                        imgImplementationIoco, GroupLayout.PREFERRED_SIZE, 44,
                                        GroupLayout.PREFERRED_SIZE))
                                .addComponent(lbl_veredict_ioco, GroupLayout.PREFERRED_SIZE, 474,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(151).addComponent(imgModelIoco,
                                        GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))))
                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(425).addComponent(label_6,
                        GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(10)
                        .addComponent(scrolltxt, GroupLayout.PREFERRED_SIZE, 405, GroupLayout.PREFERRED_SIZE).addGap(10)
                        .addComponent(lblWarningIoco, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)));
        gl_panel_ioco.setVerticalGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_ioco
                .createSequentialGroup().addGap(5)
                .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_ioco.createSequentialGroup().addGap(6).addComponent(lblModel,
                                GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_ioco.createSequentialGroup().addGap(24).addComponent(lblmodelIoco,
                                GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnViewModelIoco, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panel_ioco.createSequentialGroup()
                                .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_ioco.createSequentialGroup().addGap(6).addComponent(
                                                lblImplementation_1, GroupLayout.PREFERRED_SIZE, 22,
                                                GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnViewImplementationIoco, GroupLayout.PREFERRED_SIZE, 26,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(1).addComponent(lblImplementationIoco, GroupLayout.PREFERRED_SIZE, 26,
                                        GroupLayout.PREFERRED_SIZE)))
                .addGap(11)
                .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblnput, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOutput_1, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                .addGap(3)
                .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblInputIoco, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOutputIoco, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLabelIoco, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(11)
                .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_ioco.createSequentialGroup()
                                .addComponent(lblTestCases, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                .addGap(3).addComponent(tfNTestCasesIOCO, GroupLayout.PREFERRED_SIZE, 32,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_ioco.createSequentialGroup().addGap(5).addComponent(btnVerifyConf_ioco,
                                GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_ioco.createSequentialGroup().addGap(5).addGroup(gl_panel_ioco
                                .createParallelGroup(Alignment.LEADING)
                                .addComponent(imgImplementationIoco, GroupLayout.PREFERRED_SIZE, 44,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_ioco.createSequentialGroup().addGap(12).addComponent(
                                        lbl_veredict_ioco, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                                .addComponent(imgModelIoco, GroupLayout.PREFERRED_SIZE, 44,
                                        GroupLayout.PREFERRED_SIZE))))
                .addGap(7).addComponent(label_6).addGap(10)
                .addGroup(gl_panel_ioco.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrolltxt, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblWarningIoco, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE))));
        panel_ioco.setLayout(gl_panel_ioco);

        /*
         * taTestCasesLang = new JTextArea(); taTestCasesLang.setBounds(10, 282, 584,
         * 197); panel_language.add(taTestCasesLang);
         */

        // cleanVeredict();

        panel_language = new JPanel();
        tabbedPane.addTab(ViewConstants.tabLang, null, panel_language, null);

        lblD = new JLabel("Desirable behavior");
        lblD.setForeground(SystemColor.windowBorder);
        lblD.setFont(new Font("Dialog", Font.BOLD, 13));

        tfD = new JTextField();
        tfD.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                cleanVeredict();
            }
        });
        tfD.setForeground(SystemColor.controlShadow);
        tfD.setFont(new Font("Dialog", Font.BOLD, 13));
        tfD.setColumns(10);
        tfD.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfD.setBackground(SystemColor.menu);

        lblRegexD = new JLabel("Regex example: (a|b)*c");
        lblRegexD.setForeground(SystemColor.windowBorder);
        lblRegexD.setFont(new Font("Dialog", Font.BOLD, 12));

        lblF = new JLabel("Undesirable behavior");
        lblF.setForeground(SystemColor.windowBorder);
        lblF.setFont(new Font("Dialog", Font.BOLD, 13));

        tfF = new JTextField();
        tfF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                cleanVeredict();
            }
        });
        tfF.setForeground(SystemColor.controlShadow);
        tfF.setFont(new Font("Dialog", Font.BOLD, 13));
        tfF.setColumns(10);
        tfF.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfF.setBackground(SystemColor.menu);
        tfF.setNextFocusableComponent(tfNTestCasesLang);

        lblRegexF = new JLabel("Regex example: (a|b)*c");
        lblRegexF.setForeground(SystemColor.windowBorder);
        lblRegexF.setFont(new Font("Dialog", Font.BOLD, 12));

        lbl_veredict_lang = new JLabel("");
        lbl_veredict_lang.setForeground(SystemColor.windowBorder);
        lbl_veredict_lang.setFont(new Font("Dialog", Font.BOLD, 13));

        btnVerifyConf_lang = new JButton("Verify");
        btnVerifyConf_lang.setVisible(false);
        btnVerifyConf_lang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                actionVerifyConformance(false);
            }
        });
        btnVerifyConf_lang.setFont(new Font("Dialog", Font.BOLD, 13));
        btnVerifyConf_lang.setBackground(Color.LIGHT_GRAY);

        label_1 = new JLabel("Model");
        label_1.setForeground(SystemColor.windowBorder);
        label_1.setFont(new Font("Dialog", Font.BOLD, 13));

        lblInput_ = new JLabel("Input label");
        lblInput_.setForeground(SystemColor.windowBorder);
        lblInput_.setFont(new Font("Dialog", Font.BOLD, 13));

        lblInputLang = new JLabel("");
        lblInputLang.setForeground(SystemColor.controlShadow);

        lblmodelLang = new JLabel("");
        lblmodelLang.setForeground(SystemColor.controlShadow);

        label_5 = new JLabel("Implementation");
        label_5.setForeground(SystemColor.windowBorder);
        label_5.setFont(new Font("Dialog", Font.BOLD, 13));

        lblimplementationLang = new JLabel("");
        lblimplementationLang.setForeground(SystemColor.controlShadow);

        lblOutput_ = new JLabel("Output label");
        lblOutput_.setForeground(SystemColor.windowBorder);
        lblOutput_.setFont(new Font("Dialog", Font.BOLD, 13));

        lblOutputLang = new JLabel("");
        lblOutputLang.setForeground(SystemColor.controlShadow);

        taTestCasesLang = new JTextArea();
        taTestCasesLang.setEditable(false);
        taTestCasesLang.setBounds(10, 282, 584, 197);
        // taTestCasesLang.enable(false);
        JScrollPane scrolltxt2 = new JScrollPane(taTestCasesLang);

        imgModelLang = new JLabel("");
        imgModelLang.setVisible(false);
        imgModelLang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // showModelImage(false);
            }
        });
        imgImplementationLang = new JLabel("");
        imgImplementationLang.setVisible(false);
        imgImplementationLang.setVisible(false);
        imgImplementationLang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // showModelImage(true);
            }
        });

        btnViewModelLang = new JButton("View model");
        btnViewModelLang.setVisible(false);
        btnViewModelLang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showSpecificationImage && btnViewModelLang.isEnabled()) {
                    try {
                        pathImageModel = ModelImageGenerator.generateImage(S);
                    } catch (IOException ioException) { ioException.printStackTrace(); }
                    showModelImage(false);
                }
                if (!failPath.equals("")) {
                    taTestCasesLang.setText(failPath);
                }
                showVeredict(false);
            }
        });
        btnViewModelLang.setFont(new Font("Dialog", Font.BOLD, 13));
        btnViewModelLang.setBackground(Color.LIGHT_GRAY);

        btnViewImplementationLang = new JButton("View IUT");
        btnViewImplementationLang.setVisible(false);
        btnViewImplementationLang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showImplementationImage && btnViewImplementationLang.isEnabled()) {
                    if(isDirectoryImpl)
                    {
                        showImplementationsList();
                    }
                    else{
                        try {
                            pathImageImplementation = ModelImageGenerator.generateImage(I);
                        } catch (IOException ioException) { ioException.printStackTrace(); }
                        showModelImage(true);
                    }
                }
//                if (!failPath.equals("")) {
//                    taTestCasesLang.setText(failPath);
//                }
//                showVeredict(false);
            }
        });
        btnViewImplementationLang.setFont(new Font("Dialog", Font.BOLD, 13));
        btnViewImplementationLang.setBackground(Color.LIGHT_GRAY);

        lblLabelLang = new JLabel("");
        lblLabelLang.setForeground(SystemColor.controlShadow);

        lblLabel_ = new JLabel("label");
        lblLabel_.setVisible(false);
        lblLabel_.setForeground(SystemColor.windowBorder);
        lblLabel_.setFont(new Font("Dialog", Font.BOLD, 13));

        JLabel label = new JLabel("Warnings");
        label.setForeground(SystemColor.windowBorder);
        label.setFont(new Font("Dialog", Font.BOLD, 13));

        lblWarningLang = new TextArea("");
        lblWarningLang.setForeground(SystemColor.controlShadow);

        label_2 = new JLabel("# Test cases");
        label_2.setForeground(SystemColor.windowBorder);
        label_2.setFont(new Font("Dialog", Font.BOLD, 13));

        tfNTestCasesLang = new JTextField();
        tfNTestCasesLang.setForeground(SystemColor.controlShadow);
        tfNTestCasesLang.setFont(new Font("Dialog", Font.BOLD, 13));
        tfNTestCasesLang.setColumns(10);
        tfNTestCasesLang.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfNTestCasesLang.setBackground(SystemColor.menu);
        tfNTestCasesLang.setNextFocusableComponent(btnVerifyConf_lang);
        GroupLayout gl_panel_language = new GroupLayout(panel_language);
        gl_panel_language.setHorizontalGroup(gl_panel_language.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel_language
                        .createSequentialGroup().addGap(
                                37)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_language.createSequentialGroup()
                                        .addComponent(label_1, GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                                        .addGap(300))
                                .addComponent(lblmodelLang, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE).addGroup(
                                        gl_panel_language.createSequentialGroup().addGap(76)
                                                .addComponent(btnViewModelLang, GroupLayout.DEFAULT_SIZE, 154,
                                                        Short.MAX_VALUE)
                                                .addGap(122)))
                        .addGap(36)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(143)
                                        .addComponent(btnViewImplementationLang, GroupLayout.DEFAULT_SIZE, 154,
                                                Short.MAX_VALUE)
                                        .addGap(70))
                                .addComponent(lblimplementationLang, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                                .addGroup(gl_panel_language.createSequentialGroup()
                                        .addComponent(label_5, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                        .addGap(234)))
                        .addGap(44))
                .addGroup(gl_panel_language.createSequentialGroup().addGap(37)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblLabel_, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblInput_, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                        .addGap(111).addComponent(lblOutput_, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        .addGap(244))
                .addGroup(gl_panel_language.createSequentialGroup().addGap(37).addGroup(gl_panel_language
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_language.createSequentialGroup()
                                .addComponent(lblInputLang, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE).addGap(377))
                        .addComponent(lblLabelLang, GroupLayout.PREFERRED_SIZE, 755, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panel_language.createSequentialGroup().addGap(388).addComponent(lblOutputLang,
                                GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)))
                        .addGap(44))
                .addGroup(gl_panel_language.createSequentialGroup().addGap(37)
                        .addComponent(lblD, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE).addGap(144)
                        .addComponent(lblF, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE).addGap(211))
                .addGroup(gl_panel_language.createSequentialGroup().addGap(11).addGroup(gl_panel_language
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_language.createSequentialGroup().addGap(10).addComponent(tfNTestCasesLang,
                                GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                        .addGroup(gl_panel_language.createSequentialGroup()
                                .addComponent(label_2, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE).addGap(22)))
                        .addGap(15).addComponent(btnVerifyConf_lang, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        .addGap(4)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(360).addComponent(
                                        imgModelLang, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lbl_veredict_lang, GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(342).addComponent(
                                        imgImplementationLang, GroupLayout.PREFERRED_SIZE, 44,
                                        GroupLayout.PREFERRED_SIZE)))
                        .addGap(44))
                .addGroup(Alignment.LEADING,
                        gl_panel_language.createSequentialGroup()
                                .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_language.createSequentialGroup().addGap(37).addComponent(tfD,
                                                GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                                        .addGroup(
                                                Alignment.TRAILING,
                                                gl_panel_language.createSequentialGroup().addContainerGap()
                                                        .addComponent(lblRegexD, GroupLayout.PREFERRED_SIZE, 162,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.RELATED)))
                                .addGroup(
                                        gl_panel_language.createParallelGroup(Alignment.LEADING)
                                                .addGroup(gl_panel_language.createSequentialGroup().addGap(36)
                                                        .addComponent(tfF, GroupLayout.DEFAULT_SIZE, 367,
                                                                Short.MAX_VALUE)
                                                        .addGap(44))
                                                .addGroup(Alignment.TRAILING,
                                                        gl_panel_language.createSequentialGroup().addGap(263)
                                                                .addComponent(lblRegexF, GroupLayout.PREFERRED_SIZE,
                                                                        184, GroupLayout.PREFERRED_SIZE))))
                .addGroup(
                        gl_panel_language.createSequentialGroup().addGap(11)
                                .addComponent(scrolltxt2, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE).addGap(10)
                                .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_language.createSequentialGroup()
                                                .addComponent(label, GroupLayout.PREFERRED_SIZE, 93,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addGroup(Alignment.TRAILING,
                                                gl_panel_language
                                                        .createSequentialGroup().addComponent(lblWarningLang,
                                                        GroupLayout.PREFERRED_SIZE, 366, Short.MAX_VALUE)
                                                        .addGap(44)))));
        gl_panel_language.setVerticalGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_language.createSequentialGroup().addGap(5).addGroup(gl_panel_language
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_language.createSequentialGroup().addGap(6)
                                .addComponent(label_1, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                .addGap(4)
                                .addComponent(lblmodelLang, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnViewModelLang, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnViewImplementationLang, GroupLayout.PREFERRED_SIZE, 26,
                                GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panel_language.createSequentialGroup().addGap(24).addComponent(
                                lblimplementationLang, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_language.createSequentialGroup().addGap(6)
                                .addComponent(label_5, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
                        .addGap(16)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblLabel_, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblInput_, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblOutput_, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                        .addGap(3)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(3).addComponent(lblInputLang,
                                        GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lblLabelLang, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblOutputLang, GroupLayout.PREFERRED_SIZE, 26,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGap(8)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblD, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(3).addComponent(lblF,
                                        GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)))
                        .addGap(7)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addComponent(tfD, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addComponent(tfF, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.TRAILING)
                                .addGroup(gl_panel_language.createSequentialGroup()
                                        .addComponent(lblRegexF, GroupLayout.PREFERRED_SIZE, 36,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(11))
                                .addGroup(gl_panel_language.createSequentialGroup()
                                        .addComponent(lblRegexD, GroupLayout.PREFERRED_SIZE, 36,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(18)))
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(12).addComponent(
                                        tfNTestCasesLang, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                .addComponent(label_2, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnVerifyConf_lang, GroupLayout.PREFERRED_SIZE, 44,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_language.createSequentialGroup().addGap(8).addGroup(gl_panel_language
                                        .createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_language.createSequentialGroup().addGap(1).addComponent(
                                                imgModelLang, GroupLayout.PREFERRED_SIZE, 36,
                                                GroupLayout.PREFERRED_SIZE))
                                        .addComponent(lbl_veredict_lang, GroupLayout.PREFERRED_SIZE, 20,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGroup(gl_panel_language.createSequentialGroup().addGap(1).addComponent(
                                                imgImplementationLang, GroupLayout.PREFERRED_SIZE, 36,
                                                GroupLayout.PREFERRED_SIZE)))))
                        .addGap(12).addComponent(label, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel_language.createParallelGroup(Alignment.LEADING)
                                .addComponent(scrolltxt2, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(lblWarningLang, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE))
                        .addContainerGap()));
        panel_language.setLayout(gl_panel_language);

        // Panel generation
        panel_test_generation = new JPanel();
        tabbedPane.addTab(ViewConstants.tabTSGeneration, null, panel_test_generation, null);

        btnGenerate = new JButton("Generate");
        btnGenerate.setToolTipText("");
        btnGenerate.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent arg0) {
                saveMultigraphAndTP();
            }
        });
        btnGenerate.setFont(new Font("Dialog", Font.BOLD, 13));
        btnGenerate.setBackground(Color.LIGHT_GRAY);
        btnGenerate.setVisible(false);

        lblModel_1 = new JLabel("Model");
        lblModel_1.setForeground(SystemColor.windowBorder);
        lblModel_1.setFont(new Font("Dialog", Font.BOLD, 13));

        lblInputLabel_gen = new JLabel("Input label");
        lblInputLabel_gen.setForeground(SystemColor.windowBorder);
        lblInputLabel_gen.setFont(new Font("Dialog", Font.BOLD, 13));

        lblInput_gen = new JLabel("");
        lblInput_gen.setForeground(SystemColor.controlShadow);

        lblmodel_gen = new JLabel("");
        lblmodel_gen.setForeground(SystemColor.controlShadow);

        lblIut = new JLabel("Implementation");
        lblIut.setForeground(SystemColor.windowBorder);
        lblIut.setFont(new Font("Dialog", Font.BOLD, 13));
        lblIut.setBounds(425, 11, 133, 22);
        // panel_test_generation.add(lblIut);

        lblimplementation_gen = new JLabel("");
        lblimplementation_gen.setForeground(SystemColor.controlShadow);
        lblimplementation_gen.setBounds(425, 29, 367, 26);
        // panel_test_generation.add(lblimplementation_gen);

        lblLabelOutput = new JLabel("Output label");
        lblLabelOutput.setForeground(SystemColor.windowBorder);
        lblLabelOutput.setFont(new Font("Dialog", Font.BOLD, 13));

        lblOutput_gen = new JLabel("");
        lblOutput_gen.setForeground(SystemColor.controlShadow);

        taTestCases_gen = new JTextArea();
        taTestCases_gen.setBounds(10, 277, 231, 150);
        JScrollPane scrolltxt3 = new JScrollPane(taTestCases_gen);

        imgModel_gen = new JLabel("");
        imgModel_gen.setVisible(false);
        imgModel_gen.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showModelImage(false);
            }
        });
        imgImplementation_gen = new JLabel("");
        imgImplementation_gen.setVisible(false);
        imgImplementation_gen.setVisible(false);
        imgImplementation_gen.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showModelImage(true);
            }
        });

        btnViewModel_gen = new JButton("View model");
        btnViewModel_gen.setVisible(false);
        btnViewModel_gen.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showSpecificationImage && btnViewModel_gen.isEnabled()) {
                    showModelImage(false);
                }
                if (!failPath.equals("")) {
                    taTestCases_gen.setText(failPath);
                }
                showVeredict(false);
            }
        });
        btnViewModel_gen.setFont(new Font("Dialog", Font.BOLD, 13));
        btnViewModel_gen.setBackground(Color.LIGHT_GRAY);

        btnViewImplementation_gen = new JButton("view IUT");
        btnViewImplementation_gen.setVisible(false);
        btnViewImplementation_gen.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showImplementationImage && btnViewImplementation_gen.isEnabled()) {
                    showModelImage(true);
                }
                if (!failPath.equals("")) {
                    taTestCases_gen.setText(failPath);
                }
                showVeredict(false);
            }
        });
        btnViewImplementation_gen.setFont(new Font("Dialog", Font.BOLD, 13));
        btnViewImplementation_gen.setBackground(Color.LIGHT_GRAY);
        btnViewImplementation_gen.setVisible(false);

        lblLabel_gen = new JLabel("");
        lblLabel_gen.setForeground(SystemColor.controlShadow);

        taWarning_gen = new JTextArea("");
        taWarning_gen.setForeground(SystemColor.controlShadow);
        taWarning_gen.setBounds(426, 312, 366, 136);

        JScrollPane scrolltxt4 = new JScrollPane(taWarning_gen);

        // JScrollPane sampleScrollPane = new JScrollPane (taWarning_gen,
        // JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        // JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // panel_test_generation.add(taWarning_gen);
        // panel_test_generation.add(sampleScrollPane);

        lblM = new JLabel("# Max IUT states");
        lblM.setForeground(SystemColor.windowBorder);
        lblM.setFont(new Font("Dialog", Font.BOLD, 13));

        tfM = new JTextField();
        tfM.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                lblNumTC.setText("#Extracted test cases: ");
                taTestCases_gen.setText("");
                // btnMultigraph.setVisible(false);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                visibilityRunButtons();
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
                multigraph = null;
            }
        });
        tfM.setForeground(SystemColor.controlShadow);
        tfM.setFont(new Font("Dialog", Font.BOLD, 13));
        tfM.setColumns(10);
        tfM.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfM.setBackground(SystemColor.menu);

        btnrunTp = new JButton("Run TPs");
        btnrunTp.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // System.setProperty("apple.awt.fileDialogForDirectories", "true");
                // JFileChooser fc = directoryChooser();
                // fc.showOpenDialog(EverestView.this);
                // String folder = fc.getSelectedFile().getAbsolutePath();

                TestGeneration.setTcFault(new ArrayList<>());
                javafx.util.Pair<List<String>, Boolean> fault = TestGeneration.run(tpFolder, true, false,
                        pathImplementation, tpFolder, I);

                // nonconf verdict
                if (fault.getValue()) {

                    lblNumTC.setText("#Extracted test cases: " + fault.getKey().size());
                    // lblRunVerdict.setText(ViewConstants.genRun_fault);
                    lblRunVerdict.setText(ViewConstants.genRun_fault + " Test cases: ["
                            + StringUtils.join(TestGeneration.getTcFault(), ",") + "]");
                    lblRunVerdict.setForeground(new Color(178, 34, 34));

                    taTestCases_gen.setText(StringUtils.join(fault.getKey(), "\n"));

                    lblNumTC.setVisible(true);
                    lblNumTC.setText("#Extracted test cases: " + fault.getKey().size());
                } else {
                    lblRunVerdict.setText(ViewConstants.genRun_noFault);
                    lblRunVerdict.setForeground(new Color(0, 128, 0));
                }
                lblRunVerdict.setOpaque(true);
                lblRunVerdict.setVisible(true);
                lbl_result.setVisible(true);
                btnrunTp.setVisible(false);
            }
        });
        btnrunTp.setFont(new Font("Dialog", Font.BOLD, 13));
        btnrunTp.setBackground(Color.LIGHT_GRAY);
        btnrunTp.setVisible(false);

        lblNumTC = new JLabel("#Extracted test purposes: ");
        lblNumTC.setForeground(SystemColor.windowBorder);
        lblNumTC.setFont(new Font("Dialog", Font.BOLD, 13));
        lblNumTC.setVisible(false);

        JLabel lblTestCases_1 = new JLabel("#  Test purposes");
        lblTestCases_1.setForeground(SystemColor.windowBorder);
        lblTestCases_1.setFont(new Font("Dialog", Font.BOLD, 13));

        tfNTestCases_gen = new JTextField();
        tfNTestCases_gen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // multigraph = null;
                visibilityRunButtons();
            }
        });

        tfNTestCases_gen.setForeground(SystemColor.controlShadow);
        tfNTestCases_gen.setFont(new Font("Dialog", Font.BOLD, 13));
        tfNTestCases_gen.setColumns(10);
        tfNTestCases_gen.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfNTestCases_gen.setBackground(SystemColor.menu);

        label_4 = new JLabel("Implementation");
        label_4.setForeground(SystemColor.windowBorder);
        label_4.setFont(new Font("Dialog", Font.BOLD, 13));

        lbliut_gen = new JLabel("");
        lbliut_gen.setForeground(SystemColor.controlShadow);

        lblMultigraph = new JLabel("Multigraph");
        lblMultigraph.setForeground(SystemColor.controlDkShadow);
        lblMultigraph.setFont(new Font("Dialog", Font.BOLD, 13));

        tfMultigraph = new JTextField();
        tfMultigraph.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (tfMultigraph.getText().isEmpty()) {
                    pathMultigraph = "";
                    fileNameMultigraph = "";
                    multigraph = null;
                }
                visibilityRunButtons();
            }
        });
        tfMultigraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                getMultigraphPaph();
                if (tfMultigraph.getText().isEmpty()) {
                    pathMultigraph = "";
                    fileNameMultigraph = "";
                    multigraph = null;
                }
            }
        });
        tfMultigraph.setToolTipText("accepts only .aut files");
        tfMultigraph.setForeground(SystemColor.controlShadow);
        tfMultigraph.setFont(new Font("Dialog", Font.BOLD, 13));
        tfMultigraph.setColumns(10);
        tfMultigraph.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfMultigraph.setBackground(SystemColor.menu);

        btnRunMultigraph = new JButton("Run");
        btnRunMultigraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // if (isFormValidGeneration()) {
                lblNumTC.setVisible(false);

                lblRunVerdict.setVisible(false);
                lbl_result.setVisible(false);

                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                JFileChooser fc = directoryChooser();
                fc.showOpenDialog(EverestView.this);
                String folder = fc.getSelectedFile().getAbsolutePath();

                // File file = new File(folder + "\\TPs\\");--
                File file = new File(folder, "TPs - " + fileNameMultigraph);
                if (!file.exists()) {
                    file.mkdir();
                }

                JFrame loading = null;
                try {

                    loading = loadingDialog();
                    loading.setVisible(true);

                    try {

                        // loadMultigraph(folder);
                        TestGeneration.setTcFault(new ArrayList<>());
                        javafx.util.Pair<List<String>, Boolean> result = TestGeneration.getTcAndSaveTP(multigraph,
                                (!tfNTestCases_gen.getText().isEmpty()) ? Integer.parseInt(tfNTestCases_gen.getText())
                                        : null,
                                folder, I.getInputs(), I.getOutputs(), pathImplementation, fileNameMultigraph, I);

                        testSuite = result.getKey();

                        // nonconf verdict
                        if (result.getValue()) {

                            lblRunVerdict.setText(ViewConstants.genRun_fault + " Test cases: ["
                                    + StringUtils.join(TestGeneration.getTcFault(), ",") + "]");
                            // lblRunVerdict.setText(ViewConstants.genRun_fault );
                            lblRunVerdict.setForeground(new Color(178, 34, 34));
                            lblNumTC.setVisible(true);
                            lblNumTC.setText("#Extracted test purposes: " + TestGeneration.getTcFault().size());
                            taTestCases_gen.setText(StringUtils.join(result.getKey(), "\n"));
                        } else {
                            lblRunVerdict.setText(ViewConstants.genRun_noFault);
                            lblRunVerdict.setForeground(new Color(0, 128, 0));
                        }
                        lblRunVerdict.setOpaque(true);
                        lbl_result.setVisible(true);
                        lblRunVerdict.setVisible(true);

                    } catch (IOException ee) {
                        // TODO Auto-generated catch block
                        ee.printStackTrace();
                    }

                } catch (NumberFormatException ee) {
                    taWarning_gen.setText(taWarning_gen.getText() + ViewConstants.mInteger);
                } catch (OutOfMemoryError ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(null, "OutOfMemoryError");
                } catch (Exception ee) {
                    ee.printStackTrace();
                } finally {
                    if (loading != null)
                        loading.dispose();
                }

                btnRunMultigraph.setVisible(false);
                // }
            }
        });
        btnRunMultigraph.setFont(new Font("Dialog", Font.BOLD, 13));
        btnRunMultigraph.setBackground(Color.LIGHT_GRAY);
        btnRunMultigraph.setVisible(false);

        btnRunGenerate = new JButton("Test Generation  + Run");
        btnRunGenerate.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                saveMultigraphTPAndVerdict();
            }
        });
        btnRunGenerate.setFont(new Font("Dialog", Font.BOLD, 13));
        btnRunGenerate.setBackground(Color.LIGHT_GRAY);
        btnRunGenerate.setVisible(false);

        lblRunVerdict = new JLabel("");
        lblRunVerdict.setForeground(SystemColor.windowBorder);
        lblRunVerdict.setFont(new Font("Dialog", Font.BOLD, 13));

        button = new JButton("");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getMultigraphPaph();
            }
        });
        button.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        button.setOpaque(true);
        button.setBackground(SystemColor.activeCaptionBorder);

        JLabel lblTpFolder = new JLabel("Test purpose folder ");
        lblTpFolder.setForeground(SystemColor.controlDkShadow);
        lblTpFolder.setFont(new Font("Dialog", Font.BOLD, 13));

        tfTPFolder = new JTextField();
        tfTPFolder.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (tfTPFolder.getText().isEmpty()) {
                    tpFolder = null;
                    visibilityRunButtons();
                }
            }
        });
        tfTPFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                selectTPFolder();
            }
        });
        tfTPFolder.setToolTipText("accepts only .aut files");
        tfTPFolder.setForeground(SystemColor.controlShadow);
        tfTPFolder.setFont(new Font("Dialog", Font.BOLD, 13));
        tfTPFolder.setColumns(10);
        tfTPFolder.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfTPFolder.setBackground(SystemColor.menu);

        JButton button_2 = new JButton("");
        button_2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                selectTPFolder();
            }
        });
        button_2.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        button_2.setOpaque(true);
        button_2.setBackground(SystemColor.activeCaptionBorder);

        lbl_result = new JLabel("Verdict:");
        lbl_result.setForeground(SystemColor.controlDkShadow);
        lbl_result.setFont(new Font("Dialog", Font.BOLD, 13));

        label_7 = new JLabel("Warnings");
        label_7.setForeground(SystemColor.windowBorder);
        label_7.setFont(new Font("Dialog", Font.BOLD, 13));
        GroupLayout gl_panel_test_generation = new GroupLayout(panel_test_generation);
        gl_panel_test_generation.setHorizontalGroup(gl_panel_test_generation.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel_test_generation
                        .createSequentialGroup().addGap(
                                37)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(lblModel_1, GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                                        .addGap(300))
                                .addGroup(
                                        gl_panel_test_generation.createSequentialGroup().addGap(76)
                                                .addComponent(btnViewModel_gen, GroupLayout.DEFAULT_SIZE, 154,
                                                        Short.MAX_VALUE)
                                                .addGap(122))
                                .addComponent(lblmodel_gen, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                        .addGap(36)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(label_4, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                        .addGap(234))
                                .addComponent(lbliut_gen, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(143)
                                        .addComponent(btnViewImplementation_gen, GroupLayout.DEFAULT_SIZE, 154,
                                                Short.MAX_VALUE)
                                        .addGap(70)))
                        .addGap(10))
                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(37)
                        .addComponent(lblInputLabel_gen, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE).addGap(111)
                        .addComponent(lblLabelOutput, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE).addGap(210))
                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(37).addGroup(gl_panel_test_generation
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(388)
                                .addComponent(lblOutput_gen, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                .addComponent(lblInput_gen, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE).addGap(377))
                        .addComponent(lblLabel_gen, GroupLayout.PREFERRED_SIZE, 755, GroupLayout.PREFERRED_SIZE))
                        .addGap(10))
                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(10)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addComponent(tfM, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(lblM, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE).addGap(16)))
                        .addGap(23)
                        .addGroup(
                                gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                                .addComponent(lblTestCases_1, GroupLayout.DEFAULT_SIZE, 117,
                                                        Short.MAX_VALUE)
                                                .addGap(23))
                                        .addComponent(tfNTestCases_gen, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addGap(10).addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE).addGap(10)
                        .addComponent(btnRunGenerate, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE).addGap(187))
                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(10).addGroup(gl_panel_test_generation
                        .createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panel_test_generation.createSequentialGroup().addGroup(gl_panel_test_generation
                                .createParallelGroup(Alignment.LEADING)
                                .addGroup(Alignment.TRAILING, gl_panel_test_generation.createSequentialGroup()
                                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                                        .addComponent(lblTpFolder, GroupLayout.DEFAULT_SIZE, 189,
                                                                Short.MAX_VALUE)
                                                        .addGap(258))
                                                .addComponent(tfTPFolder, GroupLayout.DEFAULT_SIZE, 447,
                                                        Short.MAX_VALUE)
                                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                                        .addComponent(lbl_result, GroupLayout.DEFAULT_SIZE, 58,
                                                                Short.MAX_VALUE)
                                                        .addGap(389)))
                                        .addGap(3)
                                        .addComponent(button_2, GroupLayout.PREFERRED_SIZE, 39, Short.MAX_VALUE)
                                        .addGap(10))
                                .addGroup(
                                        gl_panel_test_generation.createSequentialGroup().addGap(52)
                                                .addComponent(lblRunVerdict, GroupLayout.DEFAULT_SIZE, 437,
                                                        Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)))
                                .addComponent(btnrunTp, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                                .addComponent(lblMultigraph, GroupLayout.DEFAULT_SIZE, 93,
                                                        Short.MAX_VALUE)
                                                .addGap(354))
                                        .addComponent(tfMultigraph, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE))
                                .addGap(3).addComponent(button, GroupLayout.PREFERRED_SIZE, 39, Short.MAX_VALUE)
                                .addGap(10)
                                .addComponent(btnRunMultigraph, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)))
                        .addGap(45)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addComponent(imgImplementation_gen, GroupLayout.PREFERRED_SIZE, 44,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(18).addComponent(
                                        imgModel_gen, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)))
                        .addGap(80))
                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(10).addGroup(gl_panel_test_generation
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                .addComponent(scrolltxt3, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE).addGap(11))
                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                .addComponent(lblNumTC, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE).addGap(129)))
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.TRAILING)
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(label_7, GroupLayout.PREFERRED_SIZE, 93,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(283))
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(scrolltxt4, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                                        .addGap(10)))));
        gl_panel_test_generation.setVerticalGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(5).addGroup(gl_panel_test_generation
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(6).addComponent(lblModel_1,
                                GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnViewModel_gen, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(24).addComponent(lblmodel_gen,
                                GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(2)
                                .addComponent(label_4, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbliut_gen, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnViewImplementation_gen, GroupLayout.PREFERRED_SIZE, 26,
                                GroupLayout.PREFERRED_SIZE))
                        .addGap(16)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblInputLabel_gen, GroupLayout.PREFERRED_SIZE, 14,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblLabelOutput, GroupLayout.PREFERRED_SIZE, 14,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGap(3)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblOutput_gen, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(3).addComponent(
                                        lblInput_gen, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lblLabel_gen, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addGap(6)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(12).addComponent(tfM,
                                        GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                .addComponent(lblM, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(lblTestCases_1, GroupLayout.PREFERRED_SIZE, 14,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(4).addComponent(tfNTestCases_gen, GroupLayout.PREFERRED_SIZE, 26,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(5).addComponent(
                                        btnGenerate, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(5).addComponent(
                                        btnRunGenerate, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)))
                        .addGap(6)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_test_generation.createSequentialGroup()
                                        .addComponent(lblMultigraph, GroupLayout.PREFERRED_SIZE, 14,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(3).addComponent(tfMultigraph, GroupLayout.PREFERRED_SIZE, 26,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(23).addComponent(
                                        button, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(7).addComponent(
                                        btnRunMultigraph, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(
                                gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel_test_generation.createSequentialGroup().addGap(6)
                                                .addGroup(gl_panel_test_generation
                                                        .createParallelGroup(Alignment.LEADING)
                                                        .addComponent(imgImplementation_gen, GroupLayout.PREFERRED_SIZE,
                                                                36, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(imgModel_gen, GroupLayout.PREFERRED_SIZE, 36,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(gl_panel_test_generation.createSequentialGroup()
                                                                .addGroup(gl_panel_test_generation
                                                                        .createParallelGroup(Alignment.LEADING)
                                                                        .addGroup(gl_panel_test_generation
                                                                                .createSequentialGroup()
                                                                                .addComponent(lblTpFolder,
                                                                                        GroupLayout.PREFERRED_SIZE, 14,
                                                                                        GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(3).addComponent(tfTPFolder,
                                                                                        GroupLayout.PREFERRED_SIZE, 26,
                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(gl_panel_test_generation
                                                                                .createSequentialGroup().addGap(19)
                                                                                .addComponent(button_2,
                                                                                        GroupLayout.PREFERRED_SIZE, 28,
                                                                                        GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(1)
                                                                .addGroup(gl_panel_test_generation
                                                                        .createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(lblRunVerdict,
                                                                                GroupLayout.PREFERRED_SIZE, 14,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lbl_result,
                                                                                GroupLayout.PREFERRED_SIZE, 14,
                                                                                GroupLayout.PREFERRED_SIZE)))))
                                        .addGroup(gl_panel_test_generation
                                                .createSequentialGroup().addGap(13).addComponent(btnrunTp,
                                                        GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)))
                        .addGap(11)
                        .addGroup(
                                gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                        .addComponent(lblNumTC, GroupLayout.PREFERRED_SIZE, 14,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_7))
                        .addGap(11)
                        .addGroup(gl_panel_test_generation.createParallelGroup(Alignment.LEADING)
                                .addComponent(scrolltxt3, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                                .addComponent(scrolltxt4, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                        .addGap(11)));
        panel_test_generation.setLayout(gl_panel_test_generation);

        // Panel run test
        panel_test_execution = new JPanel();
        // tabbedPane.addTab(ViewConstants.tabTestRun, null, panel_test_execution,
        // null);
        panel_test_execution.setLayout(null);

        lblSelectFolderContaining = new JLabel("Test purposes folder");
        lblSelectFolderContaining.setForeground(SystemColor.windowBorder);
        lblSelectFolderContaining.setFont(new Font("Dialog", Font.BOLD, 13));
        lblSelectFolderContaining.setBounds(413, 44, 231, 14);
        panel_test_execution.add(lblSelectFolderContaining);

        rdbtnOneIut = new JRadioButton("An implementation");
        rdbtnOneIut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearRadioButtonIut();
                lblOneIut.setVisible(true);
                tfOneIut.setVisible(true);
                btnOneIut.setVisible(true);
            }
        });
        rdbtnOneIut.setBounds(243, 120, 155, 23);
        rdbtnOneIut.setForeground(SystemColor.windowBorder);
        rdbtnOneIut.setFont(new Font("Dialog", Font.BOLD, 13));
        panel_test_execution.add(rdbtnOneIut);

        JLabel lblImplementation_2 = new JLabel("Run mode implementation");
        lblImplementation_2.setForeground(SystemColor.windowBorder);
        lblImplementation_2.setFont(new Font("Dialog", Font.BOLD, 13));
        lblImplementation_2.setBounds(35, 120, 188, 22);
        panel_test_execution.add(lblImplementation_2);

        rdbtnInBatch = new JRadioButton("Implementations in batch");
        rdbtnInBatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearRadioButtonIut();
                tfFolderIut.setVisible(true);
                lblFolderIut.setVisible(true);
                btnFolderIut.setVisible(true);
            }
        });
        rdbtnInBatch.setForeground(SystemColor.windowBorder);
        rdbtnInBatch.setFont(new Font("Dialog", Font.BOLD, 13));
        rdbtnInBatch.setBounds(413, 120, 194, 23);
        panel_test_execution.add(rdbtnInBatch);

        ButtonGroup groupIut = new ButtonGroup();
        groupIut.add(rdbtnOneIut);
        groupIut.add(rdbtnInBatch);

        tfTpFolder = new JTextField();
        tfTpFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTPsFolder();
            }
        });
        tfTpFolder.setToolTipText("accepts only .aut files");
        tfTpFolder.setForeground(SystemColor.controlShadow);
        tfTpFolder.setFont(new Font("Dialog", Font.BOLD, 13));
        tfTpFolder.setColumns(10);
        tfTpFolder.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfTpFolder.setBackground(SystemColor.menu);
        tfTpFolder.setBounds(413, 69, 324, 26);
        panel_test_execution.add(tfTpFolder);

        btnSelectFolderTP = new JButton("");
        btnSelectFolderTP.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                selectTPsFolder();
            }
        });
        btnSelectFolderTP.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        btnSelectFolderTP.setOpaque(true);
        btnSelectFolderTP.setBackground(SystemColor.activeCaptionBorder);
        btnSelectFolderTP.setBounds(736, 69, 39, 28);
        panel_test_execution.add(btnSelectFolderTP);

        lblOneIut = new JLabel("Implementation");
        lblOneIut.setForeground(SystemColor.windowBorder);
        lblOneIut.setFont(new Font("Dialog", Font.BOLD, 13));
        lblOneIut.setBounds(35, 163, 231, 14);
        lblOneIut.setVisible(false);
        panel_test_execution.add(lblOneIut);

        lblFolderIut = new JLabel("Implementations folder");
        lblFolderIut.setForeground(SystemColor.windowBorder);
        lblFolderIut.setFont(new Font("Dialog", Font.BOLD, 13));
        lblFolderIut.setBounds(413, 163, 231, 14);
        lblFolderIut.setVisible(false);
        panel_test_execution.add(lblFolderIut);

        tfOneIut = new JTextField();
        tfOneIut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getOneIutPath();
            }
        });
        tfOneIut.setToolTipText("accepts only .aut files");
        tfOneIut.setForeground(SystemColor.controlShadow);
        tfOneIut.setFont(new Font("Dialog", Font.BOLD, 13));
        tfOneIut.setColumns(10);
        tfOneIut.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfOneIut.setBackground(SystemColor.menu);
        tfOneIut.setBounds(35, 192, 324, 26);
        tfOneIut.setVisible(false);
        panel_test_execution.add(tfOneIut);

        tfFolderIut = new JTextField();
        tfFolderIut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getIutFolder();

            }
        });
        tfFolderIut.setToolTipText("accepts only .aut files");
        tfFolderIut.setForeground(SystemColor.controlShadow);
        tfFolderIut.setFont(new Font("Dialog", Font.BOLD, 13));
        tfFolderIut.setColumns(10);
        tfFolderIut.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfFolderIut.setBackground(SystemColor.menu);
        tfFolderIut.setBounds(413, 192, 324, 26);
        tfFolderIut.setVisible(false);
        panel_test_execution.add(tfFolderIut);

        btnOneIut = new JButton("");
        btnOneIut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getOneIutPath();
            }
        });
        btnOneIut.setOpaque(true);
        btnOneIut.setBackground(SystemColor.activeCaptionBorder);
        btnOneIut.setBounds(359, 190, 39, 28);
        btnOneIut.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        btnOneIut.setVisible(false);
        panel_test_execution.add(btnOneIut);

        btnFolderIut = new JButton("");
        btnFolderIut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getIutFolder();
            }
        });
        btnFolderIut.setOpaque(true);
        btnFolderIut.setBackground(SystemColor.activeCaptionBorder);
        btnFolderIut.setBounds(736, 192, 39, 28);
        btnFolderIut.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        btnFolderIut.setVisible(false);
        panel_test_execution.add(btnFolderIut);

        btnRun = new JButton("Run");
        btnRun.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                runTest();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                runTest();
            }
        });
        btnRun.setFont(new Font("Dialog", Font.BOLD, 13));
        btnRun.setBackground(Color.LIGHT_GRAY);
        btnRun.setBounds(426, 240, 167, 44);
        panel_test_execution.add(btnRun);

        JLabel label_3 = new JLabel("Warnings");
        label_3.setForeground(SystemColor.windowBorder);
        label_3.setFont(new Font("Dialog", Font.BOLD, 13));
        label_3.setBounds(35, 301, 93, 23);
        panel_test_execution.add(label_3);

        taWarningRun = new JTextArea("");
        taWarningRun.setForeground(SystemColor.controlShadow);
        taWarningRun.setBounds(31, 325, 375, 123);
        panel_test_execution.add(taWarningRun);

        lblPathToSave = new JLabel("Save verdicts on");
        lblPathToSave.setForeground(SystemColor.windowBorder);
        lblPathToSave.setFont(new Font("Dialog", Font.BOLD, 13));
        lblPathToSave.setBounds(35, 240, 167, 14);
        panel_test_execution.add(lblPathToSave);

        tfVerdictSavePath = new JTextField();
        tfVerdictSavePath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getSaveVerdictRun();
            }
        });
        tfVerdictSavePath.setToolTipText("accepts only .aut files");
        tfVerdictSavePath.setForeground(SystemColor.controlShadow);
        tfVerdictSavePath.setFont(new Font("Dialog", Font.BOLD, 13));
        tfVerdictSavePath.setColumns(10);
        tfVerdictSavePath.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfVerdictSavePath.setBackground(SystemColor.menu);
        tfVerdictSavePath.setBounds(35, 254, 324, 26);
        panel_test_execution.add(tfVerdictSavePath);

        JButton button_1 = new JButton("");
        button_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getSaveVerdictRun();
            }
        });
        button_1.setOpaque(true);
        button_1.setBackground(SystemColor.activeCaptionBorder);
        button_1.setBounds(359, 254, 39, 28);
        button_1.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        panel_test_execution.add(button_1);

        lblTestPurposes = new JLabel("Test purpose");
        lblTestPurposes.setForeground(SystemColor.windowBorder);
        lblTestPurposes.setFont(new Font("Dialog", Font.BOLD, 13));
        lblTestPurposes.setBounds(35, 44, 231, 14);
        panel_test_execution.add(lblTestPurposes);

        tfOneTp = new JTextField();
        tfOneTp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getOneTpPath();

            }
        });
        tfOneTp.setToolTipText("accepts only .aut files");
        tfOneTp.setForeground(SystemColor.controlShadow);
        tfOneTp.setFont(new Font("Dialog", Font.BOLD, 13));
        tfOneTp.setColumns(10);
        tfOneTp.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfOneTp.setBackground(SystemColor.menu);
        tfOneTp.setBounds(35, 69, 322, 26);
        panel_test_execution.add(tfOneTp);

        btnSelectTp = new JButton("");
        btnSelectTp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getOneTpPath();
            }
        });
        btnSelectTp.setOpaque(true);
        btnSelectTp.setBackground(SystemColor.activeCaptionBorder);
        btnSelectTp.setBounds(359, 67, 39, 28);
        btnSelectTp.setIcon(new ImageIcon(this.getClass().getResource(ViewConstants.folderIconPath)));
        panel_test_execution.add(btnSelectTp);

        lblRunModeTest = new JLabel("Run mode test purpose");
        lblRunModeTest.setForeground(SystemColor.windowBorder);
        lblRunModeTest.setFont(new Font("Dialog", Font.BOLD, 13));
        lblRunModeTest.setBounds(35, 11, 167, 22);
        panel_test_execution.add(lblRunModeTest);

        rdbtnOneTP = new JRadioButton("A test purpose");
        rdbtnOneTP.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearRadioButtonTP();
                lblTestPurposes.setVisible(true);
                tfOneTp.setVisible(true);
                btnSelectTp.setVisible(true);

            }
        });
        rdbtnOneTP.setForeground(SystemColor.windowBorder);
        rdbtnOneTP.setFont(new Font("Dialog", Font.BOLD, 13));
        rdbtnOneTP.setBounds(243, 11, 155, 23);
        panel_test_execution.add(rdbtnOneTP);

        rdbtnTPbatch = new JRadioButton("Test purpose in batch");
        rdbtnTPbatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearRadioButtonTP();
                lblSelectFolderContaining.setVisible(true);
                tfTpFolder.setVisible(true);
                btnSelectFolderTP.setVisible(true);
            }
        });
        rdbtnTPbatch.setForeground(SystemColor.windowBorder);
        rdbtnTPbatch.setFont(new Font("Dialog", Font.BOLD, 13));
        rdbtnTPbatch.setBounds(413, 11, 194, 23);
        panel_test_execution.add(rdbtnTPbatch);
        panel_test_execution.setVisible(false);

        ButtonGroup groupTp = new ButtonGroup();
        groupTp.add(rdbtnTPbatch);
        groupTp.add(rdbtnOneTP);

        clearRadioButtonIut();
        clearRadioButtonTP();

        // IOLTS MODEL GENERATOR TAB
        new GenerateTab(tabbedPane);
    }

    /**
     * Closes image frames.
     */
    public void closeFrame(boolean implementation) {
        // Frame[] allFrames = Frame.getFrames();
        for (Frame frame : Frame.getFrames()) {
            if (implementation) {
                if (frame.getTitle().startsWith(ViewConstants.titleFrameImgImplementation)) {
                    showImplementationImage = true;
                    frame.setVisible(false);
                    frame.dispose();
                    // enableShowImage(implementation);
                }
            } else {
                if (frame.getTitle().startsWith(ViewConstants.titleFrameImgSpecification)) {
                    showSpecificationImage = true;
                    frame.setVisible(false);
                    frame.dispose();
                }
            }

        }
        // allFrames = null;
    }

    /**
     * Exibe alfabetos em cada view.
     */
    public void showModelLabel(boolean label) {
        lblLabel.setVisible(label);
        lblLabelIoco.setVisible(label);
        lblLabel_.setVisible(label);
        lblLabelLang.setVisible(label);
        lblLabel_gen.setVisible(label);
        lblLabel_gen.setVisible(label);

        lblnput.setVisible(!label);
        lblOutput_.setVisible(!label);
        lblInput_.setVisible(!label);
        lblOutput_1.setVisible(!label);
        // lblLabelInp.setVisible(!label);
        // lblLabelOut.setVisible(!label);

        lblInputIoco.setVisible(!label);
        lblOutputIoco.setVisible(!label);
        lblInputLang.setVisible(!label);
        lblOutputLang.setVisible(!label);
        lblInput_gen.setVisible(!label);
        lblOutput_gen.setVisible(!label);
    }

    /**
     * Define alfabetos a serem exibidos.
     */
    public void showModelLabel_(boolean lts) {
        List<String> a = new ArrayList<>();

        // Se modelo é LTS
        if (lts) {
            // Se SUT definida, pega alfabeto dela
            if (S != null) {
                a.addAll(S.getAlphabet());
            }

            // Se IUT definida, pega alfabeto dela
            if (I != null) {
                a.addAll(I.getAlphabet());
            }

            // Cria nova lista ligada baseada no alfabeto até o momento
            a = new ArrayList<>(new LinkedHashSet<>(a));

            // Une as labels do alfabeto com "," e exibe nas views
            lblLabelIoco.setText(StringUtils.join(a, ","));
            lblLabelLang.setText(StringUtils.join(a, ","));

            // Ativa exibição dos campos excluindo input/output
            showModelLabel(true);
        }
        // Se o modelo é IOLTS
        else {
            // Se SUT definida, pega inputs dela
            if (S != null) {
                a.addAll(S.getInputs());
            }

            // Se IUT definida, pega inputs dela
            if (I != null) {
                a.addAll(I.getInputs());
            }

            // Cria nova lista ligada baseada nos inputs até o momento
            a = new ArrayList<>(new LinkedHashSet<>(a));

            // Une as labels de input com "," e exibe nas views
            lblInputIoco.setText(StringUtils.join(a, ","));
            lblInputLang.setText(StringUtils.join(a, ","));
            lblInput_gen.setText(StringUtils.join(a, ","));

            // Reinicia alfabeto para outputs
            a = new ArrayList<>();

            // Se SUT definida, pega outputs dela
            if (S != null) {
                a.addAll(S.getOutputs());
            }

            // Se IUT definida, pega outputs dela
            if (I != null) {
                a.addAll(I.getOutputs());
            }

            // Cria nova lista ligada baseada nos inputs até o momento
            a = new ArrayList<>(new LinkedHashSet<>(a));

            // Une as labels de input com "," e exibe nas views
            lblOutputIoco.setText(StringUtils.join(a, ","));
            lblOutputLang.setText(StringUtils.join(a, ","));
            lblOutput_gen.setText(StringUtils.join(a, ","));

            // Ativa exibição dos campos incluindo input/output
            showModelLabel(false);
        }

        a = null;
    }

    public void actionCbLabel(String label, boolean isModel) {
        typeLabel = label;
        if (label.equals(ViewConstants.typeManualLabel)) {
            setInputOutputField(true);
            removeMessage(true, ViewConstants.msgImp);
            removeMessage(false, ViewConstants.msgImp);

        } else {
            if (cbLabel.getSelectedIndex() != 2 && cbLabel2.getSelectedIndex() != 2) {
                setInputOutputField(false);

            }
        }
        if (isModel) {
            isModelProcess = false;
        } else {
            isImplementationProcess = false;
        }
    }

    public void actionCbModel(String model, boolean isModel) {
        if (model.equals(ViewConstants.IOLTS_CONST)) {
            if (isModel) {
                cbLabel.setVisible(true);
                lblRotulo.setVisible(true);
                actionCbLabel(cbLabel.getSelectedItem().toString(), isModel);
                isModelProcess = false;
            } else {
                cbLabel2.setVisible(true);
                lblRotulo2.setVisible(true);
                actionCbLabel(cbLabel2.getSelectedItem().toString(), isModel);
                isImplementationProcess = false;
            }

        } else {
            if (isModel) {
                cbLabel.setVisible(false);
                lblRotulo.setVisible(false);
                isModelProcess = false;
            } else {
                cbLabel2.setVisible(false);
                lblRotulo2.setVisible(false);
                isImplementationProcess = false;
            }
            if (cbLabel.getSelectedIndex() != 2 && cbLabel2.getSelectedIndex() != 2)
                setInputOutputField(false);

        }

        failPath = "";
        cleanVeredict();

    }

    /**
     *  Displays a loading dialog.
     */
    private JFrame loadingDialog() {
        JFrame jframe = new JFrame("Processing...");
        // JLabel lblLoading = new JLabel("Processing ...");
        // lblLoading.setFont(new Font("Tahoma", Font.PLAIN, 15));
        // lblLoading.setBounds(186, 11, 89, 26);
        // lblLoading.setVisible(true);
        // JProgressBar progressBar = new JProgressBar();
        // progressBar.setIndeterminate(true);
        // progressBar.setBounds(22, 36, 389, 14);
        // progressBar.setVisible(true);
        //
        //
        // JPanel contentPane = new JPanel();
        // contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        // contentPane.setLayout(null);
        // contentPane.add(lblLoading);
        // contentPane.add(progressBar);

        // jframe.setBounds(100, 100, 450, 105);
        int width = 350;
        int height = 105;

        jframe.setBounds(getX() + (getWidth() - width) / 2, getY() + (getHeight() - height) / 2 + 50, width, height);
        return jframe;

    }

    public void visibilityRunButtons() {

        lblRunVerdict.setVisible(false);
        lbl_result.setVisible(false);

        taTestCases_gen.setText("");
        lblNumTC.setVisible(false);

        if (tfMultigraph.getText().isEmpty()) {
            pathMultigraph = null;
        }

        boolean iutValid = true, specValid = true;
        if (pathImplementation != null && !pathImplementation.isEmpty())
            iutValid = setModel(false, true);
        if (pathSpecification != null && !pathSpecification.isEmpty())
            specValid = setModel(false, false);

        boolean removeMessage = false;

        if ((S != null && !tfM.getText().isEmpty() && iutValid && specValid) // && !specOfMultigraph
                || (pathMultigraph != null && !pathMultigraph.isEmpty() && !tfNTestCases_gen.getText().isEmpty())) {// !tfNTestCases_gen.getText().isEmpty()
            // &&
            btnGenerate.setVisible(true);
            removeMessage = true;
            btnGenerate.setEnabled(true);

            if (!tfM.getText().isEmpty() && S != null && multigraph == null) {
                btnGenerate.setToolTipText(ViewConstants.btnGenerateTip1);
                if (!tfNTestCases_gen.getText().isEmpty()) {
                    btnGenerate.setToolTipText(btnGenerate.getToolTipText() + "and TPs");
                }
            } else {
                if (!tfNTestCases_gen.getText().isEmpty() && pathMultigraph != null && !pathMultigraph.isEmpty()) {
                    btnGenerate.setToolTipText(ViewConstants.btnGenerateTip2);
                }else {
                    btnGenerate.setEnabled(false);
                }
            }

        } else {
            // if (S != null) {
            btnGenerate.setVisible(true);
            btnGenerate.setEnabled(false);
            btnGenerate.setToolTipText("");
            // }
        }

        if (S != null && I != null && !tfM.getText().isEmpty() && !tfNTestCases_gen.getText().isEmpty() && iutValid
                && specValid) {
            btnRunGenerate.setVisible(true);
            removeMessage = true;
            if (multigraph == null) {
                btnRunGenerate.setToolTipText(ViewConstants.btnRunGenerateTip1);
            } else {
                btnRunGenerate.setToolTipText(ViewConstants.btnRunGenerateTip2);
            }

        } else {
            btnRunGenerate.setVisible(false);
        }

        if (pathMultigraph != null && I != null && !tfNTestCases_gen.getText().isEmpty() && iutValid && specValid) {
            btnRunMultigraph.setVisible(true);
            btnRunMultigraph.setToolTipText(ViewConstants.btnRunMultigraphTip);
            removeMessage = true;
        } else {
            btnRunMultigraph.setVisible(false);
        }

        if (tpFolder != null && !tpFolder.isEmpty() && I != null && iutValid && specValid) {
            btnrunTp.setVisible(true);
            btnrunTp.setToolTipText(ViewConstants.btnrunTpTip);
        } else {
            btnrunTp.setVisible(false);
        }

        // if (removeMessage) {
        // removeMessageGen(ViewConstants.generation);
        // removeMessageGen(ViewConstants.run_generation);
        // removeMessageGen(ViewConstants.multigraph_generation);
        // removeMessageGen(ViewConstants.generation_mult);
        // }
    }

    public void setInputOutputField(boolean visibility) {
        lblInput.setVisible(visibility);
        lblOutput.setVisible(visibility);
        tfInput.setVisible(visibility);
        lblLabelInp.setVisible(visibility);
        lblLabelOut.setVisible(visibility);
        tfOutput.setVisible(visibility);
        tfInput.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        tfOutput.setBorder(new MatteBorder(0, 0, 1, 0, (Color) borderColor));
        panel_1.setVisible(visibility);
        // tfInput.setText("");
        // tfOutput.setText("");
        failPath = "";
        cleanVeredict();

    }

    public void setFieldRegex(boolean visibility) {
        lblD.setVisible(visibility);
        tfD.setVisible(visibility);
        lblRegexD.setVisible(visibility);
        lblF.setVisible(visibility);
        tfF.setVisible(visibility);
        lblRegexF.setVisible(visibility);
        tfD.setText("");
        tfF.setText("");
    }


    /***********************************************************
     *              IMAGE AND PREVIEW GENERATION               |
     ***********************************************************/

    /** Enables show image buttons for SUT or IUT. */
    public void enableShowImage(boolean lts, boolean implementation) {
        // Se é processamento batch
        if(isDirectoryImpl)
        {
            try {
                // Verifica validade de seleções
                // Se atender aos requerimentos
                if (((!tfImplementation.getText().isEmpty() && implementation)
                        || (!tfSpecification.getText().isEmpty() && !implementation))
                        && ((cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST
                        && ((cbLabel.getSelectedItem() == ViewConstants.typeAutomaticLabel)
                        || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel
                        && !tfInput.getText().isEmpty() && !tfOutput.getText().isEmpty()))))
                        || lts)
                {
                    // Se é IUT e existe algum path de IUT
                    if (implementation && pathImplementations != null) {
                        // Não configura modelo atual
                        // Não gera imagens
                        // Ambos serão feitos ao selecionar uma imagem
                        // No método showImplementationsList()

                        // Fecha quaisquer frames abertos
                        closeFrame(implementation);

                        // Ativa botões de visualização
                        btnViewImplementationIoco.setVisible(true);
                        btnViewImplementationLang.setVisible(true);
                        btnViewImplementationIoco.setEnabled(true);
                        btnViewImplementationLang.setEnabled(true);
                    }
                    // Se é SUT
                    else if (S.getTransitions().size() > 0){
                        // Configura modelo atual
                        setModel(lts, implementation);
                        // Fecha quaisquer frames abertos
                        closeFrame(implementation);
                        // Ativa botões de visualização
                        btnViewModelIoco.setVisible(true);
                        btnViewModelLang.setVisible(true);
                        btnViewModelIoco.setEnabled(true);
                        btnViewModelLang.setEnabled(true);
                    }
                }
                // Se não atende aos requerimentos
                else {
                    // Define botões de IUT como desativados
                    if (implementation) {
                        btnViewImplementationIoco.setVisible(true);
                        btnViewImplementationLang.setVisible(true);
                        btnViewImplementationIoco.setEnabled(false);
                        btnViewImplementationLang.setEnabled(false);

                        isImplementationProcess = false;
                    }
                    // Define botões de SUT como desativados
                    else {
                        btnViewModelIoco.setVisible(true);
                        btnViewModelLang.setVisible(true);
                        btnViewModelIoco.setEnabled(false);
                        btnViewModelLang.setEnabled(false);

                        isModelProcess = false;
                    }
                }
            } catch (Exception ex) { }

        }
        // Se é processamento único
        else{
            try {
                // Verifica validade de seleções
                // Se atender aos requerimentos
                if (((!tfImplementation.getText().isEmpty() && implementation)
                        || (!tfSpecification.getText().isEmpty() && !implementation))
                        && ((cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST
                        && ((cbLabel.getSelectedItem() == ViewConstants.typeAutomaticLabel)
                        || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel
                        && !tfInput.getText().isEmpty() && !tfOutput.getText().isEmpty()))))
                        || lts)
                {
                    // Configura modelo atual
                    setModel(lts, implementation);

                    // Se é IUT e ela é válida
                    if (implementation && I.getTransitions().size() > 0) {
                        // Fecha qualquer frame aberta
                        closeFrame(implementation);
                        // Ativa botões de visualização
                        btnViewImplementationIoco.setVisible(true);
                        btnViewImplementationLang.setVisible(true);
                        btnViewImplementationIoco.setEnabled(true);
                        btnViewImplementationLang.setEnabled(true);
                    }
                    // Se é SUT
                    else if (S.getTransitions().size() > 0){
                        // Fecha quaisquer frames abertos
                        closeFrame(implementation);
                        // Ativa botões de visualização
                        btnViewModelIoco.setVisible(true);
                        btnViewModelLang.setVisible(true);
                        btnViewModelIoco.setEnabled(true);
                        btnViewModelLang.setEnabled(true);
                    }
                }
                // Se não atende aos requerimentos
                else {
                    // Define botões de IUT como desativados
                    if (implementation) {
                        btnViewImplementationIoco.setVisible(true);
                        btnViewImplementationLang.setVisible(true);
                        btnViewImplementationIoco.setEnabled(false);
                        btnViewImplementationLang.setEnabled(false);

                        isImplementationProcess = false;
                    }
                    // Define botões de SUT como desativados
                    else {
                        btnViewModelIoco.setVisible(true);
                        btnViewModelLang.setVisible(true);
                        btnViewModelIoco.setEnabled(false);
                        btnViewModelLang.setEnabled(false);

                        isModelProcess = false;
                    }
                }
            } catch (Exception ex) { }

        }
    }

    /**
     * Displays a list of IUTs, which the user can select for viewing.
     */
    public void showImplementationsList()
    {
        JFrame iutwindow = new JFrame("Choose an IUT");
        iutwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        iutwindow.setResizable(false);

        // Layout/Panel
        JPanel pane = new JPanel();
        GridBagLayout grid = new GridBagLayout();
        iutwindow.setLayout(grid);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        pane.setLayout(grid);

        // Scrollable
        JScrollPane scrollFrame = new JScrollPane(pane);
        pane.setAutoscrolls(true);
        scrollFrame.setPreferredSize(new Dimension( 280,300));
        iutwindow.add(scrollFrame);

        // Label
        JLabel selectLabel = new JLabel("Select an IUT for viewing:");
        constraints.insets = new Insets(20, 65, 15, 0);
        pane.add(selectLabel);
        grid.setConstraints(selectLabel, constraints);

        // Buttons
        for(int i = 0; i < pathImplementations.size(); i++)
        {
            File file = new File(pathImplementations.get(i));
            JButton btn = new JButton(file.getName().replace(".aut", ""));

            // Ação
            int finalI = i;
            btn.addActionListener(actionEvent -> {
                try {
                    if(runLTS)
                    {
                        I = ImportAutFile.autToIOLTS(pathImplementations.get(finalI),false, new ArrayList<>(), new ArrayList<>());
                    }
                    else {
                        I = ImportAutFile.autToIOLTS(pathImplementations.get(finalI),true, inp, out);
                    }
                    I.addQuiescentTransitions();
                    pathImplementation = pathImplementations.get(finalI);
                    setModel(runLTS, true);
                    pathImageImplementation = ModelImageGenerator.generateImage(I);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(pathImageImplementation != null)
                {
                    showModelImage(true);
                }
                else {
                    JOptionPane.showMessageDialog(iutwindow,
                            "Could not generate model image.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            });

            // Adiciona botão à janela
            constraints.gridy++;
            constraints.insets = new Insets(5, 5, 5, 5);
            pane.add(btn);
            grid.setConstraints(btn, constraints);
        }

        // Adiciona componentes à janela
        iutwindow.pack();
        iutwindow.setVisible(true);
    }

    /**
     * Displays image of the selected model
     */
    public void showModelImage(boolean implementation) {
        int size = 550;

        int width;
        int height;

        verifyModelFileChange(true, true);
        verifyModelFileChange(false, true);

        String model = (implementation) ? ViewConstants.titleFrameImgImplementation + tfImplementation.getText()
                : ViewConstants.titleFrameImgSpecification + tfSpecification.getText();

        try {
            JFrame frame = new JFrame(model);
            frame.setVisible(true);
            frame.setResizable(true);

            // Ao fechar frame, apaga imagem gerada
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    if(implementation){
                        pathImageImplementation = null;
                    }
                    else {
                        pathImageModel = null;
                    }
                }
            });

            JPanel panel = new JPanel();

            JLabel jl = new JLabel();

            if (implementation) {
                // bimg = ImageIO.read(new File(pathImageImplementation));
                width = pathImageImplementation.getWidth();
                height = pathImageImplementation.getHeight();
                frame.setSize(width + 50, height + 50);

                jl.setIcon(new ImageIcon(new ImageIcon(pathImageImplementation).getImage().getScaledInstance(width,
                        height, Image.SCALE_DEFAULT)));
                showImplementationImage = false;
            } else {
                // bimg = ImageIO.read(new File(pathImageModel));
                width = pathImageModel.getWidth();
                height = pathImageModel.getHeight();

                frame.setSize(width + 50, height + 50);
                jl.setIcon(new ImageIcon(new ImageIcon(pathImageModel).getImage().getScaledInstance(width, height,
                        Image.SCALE_DEFAULT)));
                showSpecificationImage = false;
            }

            panel.add(jl);
            JScrollPane scrolltxt = new JScrollPane(panel);
            scrolltxt.setBounds(3, 3, width / (width % size), height / (height % size));
            // panel.add(scrolltxt);
            frame.getContentPane().add(scrolltxt);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                // when image closed
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    if (frame.getTitle().startsWith(ViewConstants.titleFrameImgImplementation)) {
                        showImplementationImage = true;
                    }

                    if (frame.getTitle().startsWith(ViewConstants.titleFrameImgSpecification)) {
                        showSpecificationImage = true;
                    }

                }
            });
        } catch (Exception e) { }
    }


    /***********************************************************
     *                  VERDICT AND ERRORS                      |
     ***********************************************************/

    /**
     * Removes verdict messages
     */
    public void cleanVeredict() {
        lbl_veredict_lang.setText("");

        taTestCasesIoco.setText("");
        taTestCasesLang.setText("");

        btnrunTp.setVisible(false);
        taTestCases_gen.setText("");

        lblNumTC.setVisible(false);
    }

    /**
     * Display verdict messages.
     */
    public void showVeredict(boolean ioco) {
        if (conformidade != null) {// verified compliance
            if (S.getTransitions().size() > 0 || I.getTransitions().size() > 0) {
                if (ioco) {
                    if(isDirectoryImpl && failPaths != null)
                    {
                        // Cria pasta de outputs
                        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        String outputDirPath = String
                                .join(File.separator,
                                        implementationDir.getAbsolutePath(),
                                        "conformIoco_" + timeStamp);
                        File outputDir = new File(outputDirPath);
                        outputDir.mkdirs();
                        String taOutput = ""; // Output pra exibir na tela

                        // Cria um arquivo para cada saída
                        int iocoFails = 0;
                        for(int i = 0; i < failPaths.size(); i++)
                        {
                            // Cria arquivos de output
                            File currFile = new File(pathImplementations.get(i));
                            List<String> output = new ArrayList<>();
                            String fname = "ioco-";
                            fname = fname.concat(currFile.getName()
                                    .replace("aut", "conf"));
                            File newOutput = new File(String.join(File.separator,
                                    outputDirPath,
                                    fname));

                            // Adiciona nome no output de tela
                            String str = "| IUT : " + fname.replace(".conf", "") + " |";
                            int fill = 67 - str.length();

                            taOutput = taOutput
                                    + "------------------------------------------------------------\n";
                            taOutput = taOutput
                                    + "| IUT : " + fname.replace(".conf", "");

                            for(int j = 0; j < fill; j++)
                            {
                                taOutput = taOutput + " ";
                            }

                            taOutput = taOutput + " |\n";
                            taOutput = taOutput
                                    + "------------------------------------------------------------\n\n";

                            // Conteúdo variável do arquivo
                            if (!failPaths.get(i).equals("")) {
                                iocoFails++;
                                String msg = "The IUT "
                                        + fname.replace(".conf", "")
                                        +  " does not conform to the specification.\n"
                                        + "-----*-----\n\n";
                                output.add(msg.concat(failPaths.get(i)));
                            }
                            else{
                                String msg = "The IUT "
                                        + fname.replace(".conf", "")
                                        + " conforms to the specification.\n"
                                        + "-----*-----\n\n";
                                output.add(msg);
                            }

                            taOutput = taOutput + output.get(0); // Adiciona resultado ao output da tela

                            // Escreve no arquivo
                            try {
                                Files.write(newOutput.toPath(), output, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // Exibe resultado
                        if(iocoFails == 0)
                        {
                            lbl_veredict_ioco.setText(Constants.MSG_TOTAL_CONFORM);
                        }
                        else if(iocoFails == failPaths.size())
                        {
                            lbl_veredict_ioco.setText(Constants.MSG_TOTAL_NONCONFORM);
                        }
                        else{
                            lbl_veredict_ioco.setText(Constants.MSG_PARTIAL_CONFORM);
                        }

                        // Exibe fail paths
                        taTestCasesIoco.setText(taOutput);
                    }
                    else
                    {
                        if (!failPath.equals("")) {
                            lbl_veredict_ioco.setText(Constants.MSG_NOT_CONFORM);
                        } else {
                            lbl_veredict_ioco.setText(Constants.MSG_CONFORM);
                        }

                        // Exibe fail path
                        taTestCasesIoco.setText(failPath);
                    }

                    // Seta mensagem de resultado e cor
                    if (lbl_veredict_ioco.getText().equals(Constants.MSG_CONFORM) ||
                            lbl_veredict_ioco.getText().equals(Constants.MSG_TOTAL_CONFORM)) {
                        lbl_veredict_ioco.setForeground(new Color(0, 128, 0));
                    }
                    else if(lbl_veredict_ioco.getText().equals(Constants.MSG_PARTIAL_CONFORM)) {
                        lbl_veredict_ioco.setForeground(new Color(200, 150, 0));
                    }
                    else if(lbl_veredict_ioco.getText().equals(Constants.MSG_TOTAL_NONCONFORM)) {
                        lbl_veredict_ioco.setForeground(new Color(178, 34, 34));
                    }

                    if(!isDirectoryImpl)
                    {
                        if (lbl_veredict_ioco.getText().equals(Constants.MSG_NOT_CONFORM) && !failPath.equals("")) {
                            lbl_veredict_ioco.setForeground(new Color(178, 34, 34));
                        } else {
                            if (lbl_veredict_ioco.getText().equals(Constants.MSG_NOT_CONFORM) && failPath.equals("")) {
                                lbl_veredict_ioco.setForeground(new Color(0, 128, 0));
                                lbl_veredict_ioco.setText(Constants.MSG_CONFORM);
                            }
                        }
                    }
                }
                else {
                    if(isDirectoryImpl && failPaths.size() > 0)
                    {
                        // Cria pasta de outputs
                        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        String outputDirPath = String
                                .join(File.separator,
                                        implementationDir.getAbsolutePath(),
                                        "conformLanguage_" + timeStamp);
                        File outputDir = new File(outputDirPath);
                        outputDir.mkdirs();
                        String taOutput = ""; // Output pra exibir na tela

                        // Cria um arquivo para cada saída
                        int langFails = 0;
                        for(int i = 0; i < failPaths.size(); i++)
                        {
                            // Cria arquivos de output
                            File currFile = new File(pathImplementations.get(i));
                            List<String> output = new ArrayList<>();
                            String fname = "lang-";
                            fname = fname.concat(currFile.getName()
                                    .replace("aut", "conf"));
                            File newOutput = new File(String.join(File.separator,
                                    outputDirPath,
                                    fname));

                            // Adiciona nome no output de tela
                            String str = "| IUT : " + fname.replace(".conf", "") + " |";
                            int fill = 67 - str.length();

                            taOutput = taOutput + "------------------------------------------------------------\n";
                            taOutput = taOutput + "| IUT : " + fname.replace(".conf", "");

                            for(int j = 0; j < fill; j++)
                            {
                                taOutput = taOutput + " ";
                            }

                            taOutput = taOutput + " |\n";
                            taOutput = taOutput + "------------------------------------------------------------\n\n";

                            // Conteúdo variável do arquivo
                            if (!failPaths.get(i).equals("")) {
                                langFails++;
                                String msg = "The IUT "
                                        + fname.replace(".conf", "")
                                        +  " does not conform to the specification.\n"
                                        + "-----*-----\n\n";
                                output.add(msg.concat(failPaths.get(i)));
                            }
                            else{
                                String msg = "The IUT "
                                        + fname.replace(".conf", "")
                                        +  " conforms to the specification.\n"
                                        + "-----*-----\n\n";
                                output.add(msg);
                            }

                            taOutput = taOutput + output.get(0); // Adiciona resultado ao output da tela

                            // Escreve no arquivo
                            try {
                                Files.write(newOutput.toPath(), output, StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Exibe resultado
                            if(langFails == 0)
                            {
                                lbl_veredict_lang.setText(Constants.MSG_TOTAL_CONFORM);
                            }
                            else if(langFails == failPaths.size())
                            {
                                lbl_veredict_lang.setText(Constants.MSG_TOTAL_NONCONFORM);
                            }
                            else{
                                lbl_veredict_lang.setText(Constants.MSG_PARTIAL_CONFORM);
                            }

                            // Exibe fail paths
                            taTestCasesLang.setText(taOutput);
                        }
                    }
                    else
                    {
                        if (!failPath.equals("")) {
                            lbl_veredict_lang.setText(Constants.MSG_NOT_CONFORM);
                            taTestCasesLang.setText(failPath);
                        } else {
                            lbl_veredict_lang.setText(Constants.MSG_CONFORM);
                        }
                    }


                    // Seta mensagem de resultado e cor
                    if (lbl_veredict_lang.getText().equals(Constants.MSG_CONFORM) ||
                            lbl_veredict_lang.getText().equals(Constants.MSG_TOTAL_CONFORM)) {
                        lbl_veredict_lang.setForeground(new Color(0, 128, 0));
                    }
                    else if(lbl_veredict_lang.getText().equals(Constants.MSG_PARTIAL_CONFORM)) {
                        lbl_veredict_lang.setForeground(new Color(200, 150, 0));
                    }
                    else if(lbl_veredict_lang.getText().equals(Constants.MSG_TOTAL_NONCONFORM)) {
                        lbl_veredict_lang.setForeground(new Color(178, 34, 34));
                    }

                    if(!isDirectoryImpl)
                    {
                        if (lbl_veredict_lang.getText().equals(Constants.MSG_NOT_CONFORM) && !failPath.equals("")) {
                            lbl_veredict_lang.setForeground(new Color(178, 34, 34));
                        } else {
                            if (lbl_veredict_lang.getText().equals(Constants.MSG_NOT_CONFORM) && failPath.equals("")) {
                                lbl_veredict_lang.setForeground(new Color(0, 128, 0));
                                lbl_veredict_lang.setText(Constants.MSG_CONFORM);
                            }
                        }
                    }
                }
            }
        }
    }

    public void errorMessage(boolean ioco) {
        boolean model = cbModel.getSelectedIndex() == 0;
        String msg = "";

        verifyModelsEmpty(ioco, true);

        if (!constainsMessage(ioco, ViewConstants.selectModel) && model) {
            msg += ViewConstants.selectModel;
        } else {
            if (!model) {
                removeMessage(ioco, ViewConstants.selectModel);
            }
        }

        boolean lts = cbModel.getSelectedItem() == ViewConstants.LTS_CONST;

        if (ioco) {

            if (!constainsMessage(ioco, ViewConstants.selectIolts) && lts) {
                msg += ViewConstants.selectIolts;
            } else {
                if (!lts) {
                    removeMessage(ioco, ViewConstants.selectIolts);
                }
            }

        }
        verifyInpOutEmpty(ioco, false);

        boolean defineInpOut = true;
        if (S != null && I != null) {
            List<String> inpOut = new ArrayList<>();
            inpOut.addAll(S.getInputs());
            inpOut.addAll(S.getOutputs());
            inpOut.addAll(I.getInputs());
            inpOut.addAll(I.getOutputs());

            List<String> alphabet = new ArrayList<>();
            alphabet.addAll(S.getAlphabet());
            alphabet.addAll(I.getAlphabet());
            HashSet hashSet_s_ = new LinkedHashSet<>(alphabet);
            alphabet = new ArrayList<>(hashSet_s_);
            alphabet.remove(Constants.DELTA);
            defineInpOut = inpOut.containsAll(alphabet);

        }
        if (!constainsMessage(ioco, ViewConstants.labelInpOut) && !defineInpOut) {
            msg += ViewConstants.labelInpOut;
        } else {
            if (!lts) {
                removeMessage(ioco, ViewConstants.labelInpOut);
            }
        }

        boolean ioltsLabel = cbLabel.getSelectedIndex() == 0 && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST;

        if (!constainsMessage(ioco, ViewConstants.selectIoltsLabel) && ioltsLabel) {
            msg += ViewConstants.selectIoltsLabel;
        } else {
            if (!ioltsLabel) {
                removeMessage(ioco, ViewConstants.selectIoltsLabel);
            }
        }

        if (ioco) {
            lblWarningIoco.setText(lblWarningIoco.getText() + msg);
        } else {
            lblWarningLang.setText(lblWarningLang.getText() + msg);
        }

    }

    public void removeMessage(boolean ioco, String msg) {
        if (ioco) {
            lblWarningIoco.setText(lblWarningIoco.getText().replace(msg, ""));
        } else {
            lblWarningLang.setText(lblWarningLang.getText().replace(msg, ""));
        }
    }


    /***********************************************************
     *                 CONFORMANCE TESTING                      |
     ***********************************************************/


    /**
     * Run selected conformance test.
     */
    public void actionVerifyConformance(boolean ioco) {

        verifyModelFileChange(ioco, true);
        errorMessage(ioco);

        if (isFormValid(ioco)) {

            if (S != null && I != null) {

                JFrame loading = loadingDialog();
                loading.setVisible(true);
                if (ioco) {
                    iocoConformance();
                } else {
                    languageBasedConformance();
                }
                loading.dispose();

                System.gc();

                showVeredict(ioco);
            }

        }

    }

    /**
     * Runs IOCO conformance process.
     */
    public void iocoConformance() {
        conformidade = null;
        int nTestCase = Integer.MAX_VALUE;

        if (!tfNTestCasesIOCO.getText().isEmpty()) {
            try {
                nTestCase = Integer.parseInt(tfNTestCasesIOCO.getText());

            } catch (Exception e) {
                nTestCase = Integer.MAX_VALUE;
            }
        } else {
            nTestCase = 0;
        }

        if (S.getTransitions().size() != 0 || I.getTransitions().size() != 0) {

            if(isDirectoryImpl)
            {
                // LTS check
                boolean lts = false;
                if (cbModel.getSelectedIndex() == 0
                        || (cbLabel.getSelectedIndex() == 0 && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST)
                        || cbModel.getSelectedItem() == ViewConstants.LTS_CONST
                        || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel && tfInput.getText().isEmpty()
                        && tfOutput.getText().isEmpty())) {
                    lts = true;
                }

                failPaths = new ArrayList<>();
                for (String path : pathImplementations) {
                    // Converts to model
                    if(lts){
                        try {
                            I = ImportAutFile.autToIOLTS(path,false, new ArrayList<>(), new ArrayList<>());
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    else{
                        try {
                            I = ImportAutFile.autToIOLTS(path,true, inp, out);
                        } catch (Exception e) { e.printStackTrace(); }
                    }

                    // Add quiescent transitions
                    I.addQuiescentTransitions();

                    // Set as model
                    pathImplementation = path;
                    setModel(lts, true);

                    // Sets file name
                    File iutFile = new File(path);
                    I.setName(iutFile.getName().replace("aut", "conf"));

                    // Runs conformance test
                    conformidade = IocoConformance.verifyIOCOConformance(S, I);

                    // Adds result to failPaths
                    failPath = Operations.path(S, I, conformidade, true, false, nTestCase);
                    failPaths.add(failPath);
                }
            }
            else
            {
                failPath = "";
                conformidade = IocoConformance.verifyIOCOConformance(S, I);// , nTestCase

                // if (conformidade.getFinalStates().size() > 0) {
                failPath = Operations.path(S, I, conformidade, true, false, nTestCase);
                // }
            }
        }

    }

    /**
     * Runs language-based process.
     */
    public void languageBasedConformance() {
        boolean lts = false;
        conformidade = null;
        LTS S_, I_ = null;
        try {

            // when the model type is not selected or IOLTS is selected but not specified
            // how to differentiate the inputs and outputs
            if (cbModel.getSelectedIndex() == 0
                    || (cbLabel.getSelectedIndex() == 0 && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST)
                    || cbModel.getSelectedItem() == ViewConstants.LTS_CONST
                    || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel && tfInput.getText().isEmpty()
                    && tfOutput.getText().isEmpty())) {
                lts = true;
            }

            // Verificação de batch de IUTs
            if(isDirectoryImpl)
            {
                // Inicializa failPaths
                failPaths = new ArrayList<>();

                // Converte especificação para LTS
                if (!lts) { // IOLTS
                    S_ = S.toLTS();
                }
                // Ou apenas reimporta, se for LTS
                else {
                    S_ = ImportAutFile.autToLTS(pathSpecification, false);
                }

                for (String path : pathImplementations)
                {
                    // Converte implementação para LTS
                    if(!lts) {
//                        // Lê do arquivo no path
//                        I = ImportAutFile.autToIOLTS(path,true, inp, out);
//                        // Adiciona transiçõe squiescentes
//                        I.addQuiescentTransitions();
//                        // Define nome do modelo de acordo com o arquivo
//                        File iutFile = new File(path);
//                        I.setName(iutFile.getName().replace("aut", "conf"));
//                        // Converts to LTS
//                        I_ = I.toLTS();
                        pathImplementation = path;
                        setModel(lts, true);
                        I_ = I.toLTS();
                    }
                    // Ou apenas reimporta, se for LTS
                    else {
                        // Reads from file to model
                        I_ = ImportAutFile.autToIOLTS(path,false, new ArrayList<>(), new ArrayList<>());
                    }

                    // Define nome do modelo de acordo com o arquivo
                    File iutFile = new File(path);
                    I_.setName(iutFile.getName().replace("aut", "conf"));

                    // Teste de conformidade
                    if (S_.getAlphabet().size() != 0 || I_.getAlphabet().size() != 0) {
                        String D = "";
                        D = tfD.getText();
                        if (tfD.getText().isEmpty() && tfF.getText().isEmpty()) {
                            D = "(";
                            List<String> alphabets = new ArrayList<>();
                            alphabets.addAll(S_.getAlphabet());
                            alphabets.addAll(I_.getAlphabet());
                            for (String l : new ArrayList<>(new LinkedHashSet<>(alphabets))) {
                                D += l + "|";
                            }
                            D = D.substring(0, D.length() - 1);
                            D += ")*";
                        }
                        tfD.setText(D);
                        String F = tfF.getText();

                        if (regexIsValid(D) && regexIsValid(F)) {
                            int nTestCase = Integer.MAX_VALUE;
                            if (!tfNTestCasesLang.getText().isEmpty()) {
                                try {
                                    nTestCase = Integer.parseInt(tfNTestCasesLang.getText());
                                } catch (Exception e) {
                                    nTestCase = Integer.MAX_VALUE;
                                }
                            } else {
                                nTestCase = 0;
                            }

                            conformidade = LanguageBasedConformance.verifyLanguageConformance(S_, I_, D, F);
                            if (conformidade.getFinalStates().size() > 0) {
                                failPath = Operations.path(S_, I_, conformidade, false, false, nTestCase);
                            } else {
                                failPath = "";
                            }
                            failPaths.add(failPath);

                            removeMessage(false, ViewConstants.invalidRegex);
                        } else {
                            if (!lblWarningLang.getText().contains(ViewConstants.invalidRegex)) {
                                lblWarningLang.setText(lblWarningLang.getText() + ViewConstants.invalidRegex);
                            }

                            return;
                        }
                    }
                }
            }
            else
            {
                if (!lts) { // IOLTS
                    S_ = S.toLTS();
                    I_ = I.toLTS();
                }
                else {
                    S_ = ImportAutFile.autToLTS(pathSpecification, false);
                    I_ = ImportAutFile.autToLTS(pathImplementation, false);
                }

                if (S_.getAlphabet().size() != 0 || I_.getAlphabet().size() != 0) {
                    String D = "";
                    D = tfD.getText();
                    if (tfD.getText().isEmpty() && tfF.getText().isEmpty()) {
                        D = "(";
                        List<String> alphabets = new ArrayList<>();
                        alphabets.addAll(S_.getAlphabet());
                        alphabets.addAll(I_.getAlphabet());
                        for (String l : new ArrayList<>(new LinkedHashSet<>(alphabets))) {
                            D += l + "|";
                        }
                        D = D.substring(0, D.length() - 1);
                        D += ")*";

                    }
                    tfD.setText(D);
                    String F = tfF.getText();

                    if (regexIsValid(D) && regexIsValid(F)) {
                        int nTestCase = Integer.MAX_VALUE;
                        if (!tfNTestCasesLang.getText().isEmpty()) {
                            try {
                                nTestCase = Integer.parseInt(tfNTestCasesLang.getText());

                            } catch (Exception e) {
                                nTestCase = Integer.MAX_VALUE;
                            }
                        } else {
                            nTestCase = 0;
                        }

                        conformidade = LanguageBasedConformance.verifyLanguageConformance(S_, I_, D, F);// ,Integer.MAX_VALUE
                        if (conformidade.getFinalStates().size() > 0) {
                            failPath = Operations.path(S_, I_, conformidade, false, false, nTestCase);
                        } else {
                            failPath = "";
                        }
                        removeMessage(false, ViewConstants.invalidRegex);
                    } else {
                        // JOptionPane.showMessageDialog(panel, "Invalid regex!", "Warning",
                        // JOptionPane.WARNING_MESSAGE);
                        if (!lblWarningLang.getText().contains(ViewConstants.invalidRegex)) {
                            lblWarningLang.setText(lblWarningLang.getText() + ViewConstants.invalidRegex);
                        }

                        return;
                    }
                }
            }

            removeMessage(false, ViewConstants.exceptionMessage);
        } catch (Exception e_) {
            if (!lblWarningLang.getText().contains(ViewConstants.exceptionMessage)) {
                lblWarningLang.setText(lblWarningLang.getText() + ViewConstants.exceptionMessage);
            }
            // JOptionPane.showMessageDialog(panel, e_.getMessage(), "Warning",
            // JOptionPane.WARNING_MESSAGE);
            e_.printStackTrace();
            return;
        }
    }


    /***********************************************************
     *                        MULTIGRAPH                        |
     ***********************************************************/

    String pathMultigraph;
    boolean specOfMultigraph = false;

    public void getMultigraphPaph() {
        try {
            configFilterFile();
            fc.showOpenDialog(EverestView.this);
            tfMultigraph.setText(fc.getSelectedFile().getName());
            pathMultigraph = fc.getSelectedFile().getAbsolutePath();
            fileNameMultigraph = fc.getSelectedFile().getName().replace(".aut", "");

            loadMultigraph(pathMultigraph);
            specOfMultigraph = true;

            visibilityRunButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runTest() {
        if (formRunIsValid()) {
            JFrame loading = loadingDialog();
            loading.setVisible(true);

            TestGeneration.run(rdbtnOneTP.isSelected() ? pathTP : tpFolder, rdbtnOneIut.isSelected(),
                    rdbtnOneTP.isSelected(), rdbtnOneIut.isSelected() ? pathImplementation : iutFolder, saveFolderRun,
                    I);

            loading.dispose();
        }
    }

    public void saveTP() {
        JFrame loading = loadingDialog();
        try {
            JFileChooser fc = directoryChooser();
            fc.showOpenDialog(EverestView.this);

            loading.setVisible(true);

            // fc.getSelectedFile().getName()
            String path = fc.getSelectedFile().getAbsolutePath();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

            new File(path + "/Tp " + dateFormat.format(new Date())).mkdirs();
            path = path + "/Tp " + dateFormat.format(new Date());
            File file;
            BufferedWriter writer;
            int count = 0;
            for (String tc : testSuite) {
                file = new File(path, "tp" + count + ".aut");
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(AutGenerator
                        .ioltsToAut(TestGeneration.testPurpose(multigraph, tc, S.getInputs(), S.getOutputs())));
                writer.close();
                // System.out.println(TestGeneration.testPurpose(multigraph, tc, S.getOutputs(),
                // S.getInputs()));
                count++;

                // if (count == 4) {
                // break;
                // }

            }

        } catch (Exception e) {

        } finally {
            if (loading != null) {
                loading.dispose();
            }
        }
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    public void saveMultigraphTPAndVerdict() {
        if (isFormValidGeneration()) {
            lblNumTC.setVisible(false);

            lblRunVerdict.setVisible(false);
            lbl_result.setVisible(false);

            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            JFileChooser fc = directoryChooser();
            fc.showOpenDialog(EverestView.this);
            String folder = fc.getSelectedFile().getAbsolutePath();

            JFrame loading = null;
            try {

                loading = loadingDialog();
                loading.setVisible(true);
                // System.out.println(S);
                removeMessageGen(ViewConstants.mInteger);

                if (multigraph == null) {
                    multigraph = TestGeneration.multiGraphD(S, Integer.parseInt(tfM.getText()));
                    saveMultigraphFile(folder);
                } else {
                    loadMultigraph(pathMultigraph);
                }

                File file = new File(folder, "TPs - " + fileNameMultigraph);
                if (!file.exists()) {
                    file.mkdir();
                }
                // multigraph.setInitialState(new
                // State_(multigraph.getInitialState().getName().replace(",", "_")));

                try {
                    TestGeneration.setTcFault(new ArrayList<>());
                    javafx.util.Pair<List<String>, Boolean> result = TestGeneration.getTcAndSaveTP(multigraph,

                            (!tfNTestCases_gen.getText().isEmpty()) ? Integer.parseInt(tfNTestCases_gen.getText())
                                    : null,
                            folder, S.getInputs(), S.getOutputs(), pathImplementation, fileNameMultigraph, I);

                    testSuite = result.getKey();
                    taTestCases_gen.setText(StringUtils.join(testSuite, "\n"));
                    lblNumTC.setVisible(true);
                    lblNumTC.setText("#Extracted test purposes: " + testSuite.size());

                    // nonconf verdict
                    if (result.getValue()) {
                        lblRunVerdict.setText(ViewConstants.genRun_fault + " Test cases: ["
                                + StringUtils.join(TestGeneration.getTcFault(), ",") + "]");
                        lblRunVerdict.setForeground(new Color(178, 34, 34));
                    } else {
                        lblRunVerdict.setText(ViewConstants.genRun_noFault);
                        lblRunVerdict.setForeground(new Color(0, 128, 0));
                    }
                    lblRunVerdict.setVisible(true);
                    lbl_result.setVisible(true);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (NumberFormatException e) {
                taWarning_gen.setText(taWarning_gen.getText() + ViewConstants.mInteger);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "OutOfMemoryError");
            } finally {
                if (loading != null)
                    loading.dispose();
            }

        }

        btnRunGenerate.setVisible(false);
        btnGenerate.setVisible(false);
    }

    String fileNameMultigraph;

    public void saveMultigraphFile(String folder) {
        try {
            String fileContent = "";
            // save m
            fileContent += Constants.MAX_IUT_STATES + tfM.getText() + "] \n";
            // save spec
            // fileContent += AutGenerator.ioltsToAut(new IOLTS(S.getStates(),
            // S.getInitialState(), S.getAlphabet(),
            // S.getTransitions(), S.getInputs(), S.getOutputs()).removeDeltaTransitions());
            if (typeLabel.equals(ViewConstants.typeManualLabel)) {
                fileContent += AutGenerator.ioltsToAut(ImportAutFile.autToIOLTS(pathSpecification, true,
                        new ArrayList<>(S.getInputs()), new ArrayList<>(S.getOutputs())));
            } else {
                fileContent += AutGenerator.ioltsToAut(
                        ImportAutFile.autToIOLTS(pathSpecification, false, new ArrayList<>(), new ArrayList<>()));
            }

            // save multigraph
            fileContent += Constants.SEPARATOR_MULTIGRAPH_FILE;

            fileContent += "des(" + multigraph.getInitialState().getName().replace(",", "_") + ","
                    + multigraph.getTransitions().size() + "," + multigraph.getStates().size() + ")"
                    + System.getProperty("line.separator");
            // fileContent += aut_transitions;

            fileContent += StringUtils.join(multigraph.getTransitions(), "");

            // fileContent += AutGenerator.ioltsToAut(new IOLTS(multigraph.getStates(),
            // multigraph.getInitialState(),
            // multigraph.getAlphabet(), multigraph.getTransitions(), S.getInputs(),
            // S.getOutputs()));
            fileNameMultigraph = "spec-multigraph_" + dateFormat.format(new Date());
            File file = new File(folder, "spec-multigraph_" + dateFormat.format(new Date()) + ".aut");

            pathMultigraph = file.getAbsolutePath();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContent);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMultigraphAndTP() {
        if (isFormValidGeneration()) {

            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            JFileChooser fc = directoryChooser();
            fc.showOpenDialog(EverestView.this);
            String folder = fc.getSelectedFile().getAbsolutePath();
            File file;

            JFrame loading = null;
            IOLTS iolts_aux = new IOLTS();
            try {

                loading = loadingDialog();
                loading.setVisible(true);
                // System.out.println(S);
                removeMessageGen(ViewConstants.mInteger);

                // construct multigraph with param S and m
                if ((!tfM.getText().isEmpty() && S != null && multigraph == null)) {//

                    multigraph = TestGeneration.multiGraphD(S, Integer.parseInt(tfM.getText()));
                    // multigraph.setInitialState(new
                    // State_(multigraph.getInitialState().getName().replace(",", "_")));
                    saveMultigraphFile(folder);
                }

                // read multgraph from multigraph field
                if ((pathMultigraph != null && !pathMultigraph.isEmpty() && !tfNTestCases_gen.getText().isEmpty())) {
                    try {
                        // loadMultigraph(folder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {

                    if (!tfNTestCases_gen.getText().isEmpty()) {// generate just multigraph if #test cases is empty
                        // file = new File(folder + "\\TPs\\");
                        file = new File(folder, "TPs - " + fileNameMultigraph);
                        if (!file.exists()) {
                            file.mkdir();
                        }

                        if ((!tfM.getText().isEmpty() && S != null)) {
                            testSuite = TestGeneration
                                    .getTcAndSaveTP(multigraph, Integer.parseInt(tfNTestCases_gen.getText()), folder,
                                            S.getInputs(), S.getOutputs(), null, fileNameMultigraph, I)
                                    .getKey();
                        } else {
                            if (S != null) {
                                testSuite = TestGeneration.getTcAndSaveTP(multigraph,
                                        Integer.parseInt(tfNTestCases_gen.getText()), folder, iolts_aux.getInputs(),
                                        iolts_aux.getOutputs(), null, fileNameMultigraph, I).getKey();
                            } else {
                                testSuite = TestGeneration
                                        .getTcAndSaveTP(multigraph, Integer.parseInt(tfNTestCases_gen.getText()),
                                                folder, multigraph_iolts.getInputs(), multigraph_iolts.getOutputs(),
                                                null, fileNameMultigraph, I)
                                        .getKey();
                            }

                        }
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    taWarning_gen.setText(taWarning_gen.getText() + ViewConstants.ntcInteger);
                }

                if (!tfNTestCases_gen.getText().isEmpty()) {
                    testSuite.sort(Comparator.comparing(String::length));
                    taTestCases_gen.setText(StringUtils.join(testSuite, "\n"));
                    // btnMultigraph.setVisible(true);

                    lblNumTC.setVisible(true);
                    lblNumTC.setText("#Extracted test purposes: " + testSuite.size());
                }

            } catch (NumberFormatException e) {
                taWarning_gen.setText(taWarning_gen.getText() + ViewConstants.mInteger);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "OutOfMemoryError");
            } finally {
                if (loading != null)
                    loading.dispose();
            }
        }

        btnGenerate.setVisible(false);
    }

    public boolean formRunIsValid() {
        String msg = "";

        if (!(rdbtnOneTP.isSelected() || rdbtnTPbatch.isSelected())) {
            msg += ViewConstants.selectTpRunMode;
        } else {
            removeMessageRunForm(ViewConstants.selectTpRunMode);

            if ((rdbtnOneTP.isSelected() && tfOneTp.getText().isEmpty())
                    || (rdbtnTPbatch.isSelected() && tfTpFolder.getText().isEmpty())) {
                if (rdbtnOneTP.isSelected()) {
                    // one tp
                    if (!tfOneTp.getText().isEmpty()) {
                        removeMessageRunForm(ViewConstants.selectOneTp);
                    } else {
                        msg += ViewConstants.selectOneTp;
                    }
                } else {
                    // tp in batch
                    if (!tfTpFolder.getText().isEmpty()) {
                        removeMessageRunForm(ViewConstants.selectTpFolder);
                    } else {
                        msg += ViewConstants.selectTpFolder;
                    }
                }
            } else {
                removeMessageRunForm(ViewConstants.selectOneTp);
                removeMessageRunForm(ViewConstants.selectTpFolder);
            }
        }

        if (!(rdbtnOneIut.isSelected() || rdbtnInBatch.isSelected())) {
            msg += ViewConstants.selectIutRunMode;

        } else {
            removeMessageRunForm(ViewConstants.selectIutRunMode);

            if ((rdbtnOneIut.isSelected() && tfOneIut.getText().isEmpty())
                    || (rdbtnInBatch.isSelected() && tfFolderIut.getText().isEmpty())) {
                if (rdbtnOneIut.isSelected()) {
                    // one tp
                    if (!tfOneIut.getText().isEmpty()) {
                        removeMessageRunForm(ViewConstants.selectOneIut);
                    } else {
                        msg += ViewConstants.selectOneIut;
                    }
                } else {
                    // tp in batch
                    if (!tfFolderIut.getText().isEmpty()) {
                        removeMessageRunForm(ViewConstants.selectIutFolder);
                    } else {
                        msg += ViewConstants.selectIutFolder;
                    }
                }
            } else {
                removeMessageRunForm(ViewConstants.selectOneIut);
                removeMessageRunForm(ViewConstants.selectIutFolder);
            }

        }

        if (tfVerdictSavePath.getText().isEmpty()) {
            msg += ViewConstants.selectPathSaveVerdict;
        } else {
            removeMessageRunForm(ViewConstants.selectPathSaveVerdict);
        }

        taWarningRun.setText(msg);

        return msg.isEmpty();
    }

    public void removeMessageRunForm(String msg) {
        taWarningRun.setText(taWarningRun.getText().replace(msg, ""));
    }

    String pathTP;

    public void getOneTpPath() {

        try {
            configFilterFile();
            fc.showOpenDialog(EverestView.this);

            tfOneTp.setText(fc.getSelectedFile().getName());
            pathTP = fc.getSelectedFile().getAbsolutePath();
            fc.setCurrentDirectory(fc.getSelectedFile().getParentFile());

            closeFrame(true);

        } catch (Exception e) {

        }
    }

    public void clearRadioButtonTP() {
        // if (all || (oneIut != null && !oneIut)) {
        lblTestPurposes.setVisible(false);
        tfOneTp.setVisible(false);
        btnSelectTp.setVisible(false);
        // }
        // if (all || (oneIut != null && oneIut)) {
        lblSelectFolderContaining.setVisible(false);
        tfTpFolder.setVisible(false);
        btnSelectFolderTP.setVisible(false);
        // }
    }

    public static boolean isAutFile(File f) {
        return (f.getName().indexOf(".") != -1 && f.getName().substring(f.getName().indexOf(".")).equals(".aut"));
    }

    String iutFolder;

    public void getIutFolder() {
        JFileChooser fc = directoryChooser();
        fc.showOpenDialog(EverestView.this);
        iutFolder = fc.getSelectedFile().getAbsolutePath();
        tfFolderIut.setText(fc.getSelectedFile().getName());
    }

    String saveFolderRun;

    public void getSaveVerdictRun() {
        JFileChooser fc = directoryChooser();
        fc.showOpenDialog(EverestView.this);
        saveFolderRun = fc.getSelectedFile().getAbsolutePath();
        tfVerdictSavePath.setText(fc.getSelectedFile().getName());
    }

    public void getOneIutPath() {

        try {
            configFilterFile();
            fc.showOpenDialog(EverestView.this);

            tfOneIut.setText(fc.getSelectedFile().getName());
            pathImplementation = fc.getSelectedFile().getAbsolutePath();
            fc.setCurrentDirectory(fc.getSelectedFile().getParentFile());

            lastModifiedImp = new File(pathImplementation).lastModified();

            closeFrame(true);

        } catch (Exception e) {

        }
    }

    public void clearRadioButtonIut() {
        // if (all || (oneIut != null && !oneIut)) {
        tfFolderIut.setVisible(false);
        lblFolderIut.setVisible(false);
        btnFolderIut.setVisible(false);
        // }
        // if (all || (oneIut != null && oneIut)) {
        tfOneIut.setVisible(false);
        lblOneIut.setVisible(false);
        btnOneIut.setVisible(false);
        // }
    }

    String tpFolder;

    public void selectTPsFolder() {
        JFileChooser fc = directoryChooser();
        fc.showOpenDialog(EverestView.this);
        tpFolder = fc.getSelectedFile().getAbsolutePath();
        tfTpFolder.setText(fc.getSelectedFile().getName());
    }

    private JPanel panel_language;
    private JPanel panel_ioco;
    private JTextField tfD;
    private JTextField tfF;
    private JButton btnVerifyConf_ioco;
    private JLabel lbl_veredict_lang;
    private JLabel lbl_veredict_ioco;
    private JButton btnVerifyConf_lang;
    private JLabel label_1;
    private JLabel lblInput_;
    private JLabel lblInputLang;
    private JLabel lblmodelLang;
    private JLabel label_5;
    private JLabel lblimplementationLang;
    private JLabel lblOutput_;
    private JLabel lblOutputLang;
    private JTextArea taTestCasesLang;
    private JLabel imgModelIoco;
    private JLabel imgImplementationIoco;
    private JLabel imgModelLang;
    private JLabel imgImplementationLang;
    private JLabel lblLabelLang;
    private JLabel lblLabel_;
    private JTextField tfNTestCasesIOCO;
    private JLabel label_2;
    private JTextField tfNTestCasesLang;
    private JTextField tfM;
    private JTextField tfTpFolder;
    private JLabel lblOneIut;
    private JLabel lblFolderIut;
    private JTextField tfOneIut;
    private JTextField tfFolderIut;
    private JButton btnOneIut;
    private JButton btnFolderIut;
    private JButton btnRun;
    private JLabel lblPathToSave;
    private JTextField tfVerdictSavePath;
    private JTextField tfOneTp;
    private JLabel lblRunModeTest;
    private JTextField tfNTestCases_gen;
    private JLabel label_4;
    private JLabel lbliut_gen;
    private JLabel lblMultigraph;
    private JTextField tfMultigraph;
    private JButton btnRunGenerate;
    private JLabel lblRunVerdict;
    private JButton button;
    private JTextField tfTPFolder;
    private JLabel lblModel_1;
    private JLabel label_6;
    private JLabel label_7;

    public boolean isFormValidGeneration() {

        boolean defineInpOut = true;
        List<String> inpOut = new ArrayList<>();
        List<String> alphabet = new ArrayList<>();
        if (S != null) {
            inpOut.addAll(S.getInputs());
            inpOut.addAll(S.getOutputs());
            alphabet.addAll(S.getAlphabet());
            alphabet.remove(Constants.DELTA);
            defineInpOut = inpOut.containsAll(alphabet);
        }
        if (I != null) {
            inpOut.addAll(I.getInputs());
            inpOut.addAll(I.getOutputs());
            alphabet.addAll(I.getAlphabet());
            alphabet.remove(Constants.DELTA);
            defineInpOut = inpOut.containsAll(alphabet);
        }

        return (tfSpecification.getText().isEmpty() && tfImplementation.getText().isEmpty())
                || ((!tfSpecification.getText().isEmpty() || !tfImplementation.getText().isEmpty())
                && (cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST
                && ((cbLabel.getSelectedItem() == ViewConstants.typeAutomaticLabel)
                || (cbLabel.getSelectedItem() == ViewConstants.typeManualLabel
                && (!tfInput.getText().isEmpty() && !tfOutput.getText().isEmpty()))
                && defineInpOut)));

    }

    public boolean constainsMessage(boolean ioco, String msg) {
        if (ioco && lblWarningIoco.getText().contains(msg)) {
            return true;
        } else {
            if (lblWarningLang.getText().contains(msg)) {
                return true;
            }
        }

        return false;
    }

    public boolean constainsMessage_gen(String msg) {
        return taWarning_gen.getText().contains(msg);
    }

    public void removeMessageGen(String msg) {
        taWarning_gen.setText(taWarning_gen.getText().replace(msg, ""));

    }

    public void errorMessageGen() {
        boolean model = cbModel.getSelectedIndex() == 0 && (S != null || I != null);
        String msg = "";

        // verifyModelsEmpty(true, false);

        if (!tfImplementation.getText().isEmpty()) {
            setModel(false, true);

        }
        if (!tfSpecification.getText().isEmpty()) {
            setModel(false, false);

        }

        // if (!constainsMessage_gen(ViewConstants.selectSpecification_iut) && (S ==
        // null && I == null)) {
        // msg += ViewConstants.selectSpecification_iut;
        // } else {
        // removeMessageGen(ViewConstants.selectSpecification_iut);
        // }

        if (!constainsMessage_gen(ViewConstants.selectModel) && model) {
            msg += ViewConstants.selectModel;
        } else {
            if (!model) {
                removeMessageGen(ViewConstants.selectModel);
            }
        }

        boolean ioltsLabel = cbLabel.getSelectedIndex() == 0 && cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST;

        if (!constainsMessage_gen(ViewConstants.selectIoltsLabel) && ioltsLabel) {
            msg += ViewConstants.selectIoltsLabel;
        } else {
            if (!ioltsLabel) {
                removeMessageGen(ViewConstants.selectIoltsLabel);
            }
        }

        boolean lts = cbModel.getSelectedItem() == ViewConstants.LTS_CONST;

        if (!constainsMessage_gen(ViewConstants.selectIolts_gen) && lts) {
            msg += ViewConstants.selectIolts_gen;
        } else {
            if (!lts) {
                removeMessageGen(ViewConstants.selectIolts_gen);
            }
        }

        verifyInpOutEmpty(true, true);

        boolean defineInpOut = true;
        List<String> alphabet = new ArrayList<>();
        List<String> inpOut = new ArrayList<>();
        HashSet hashSet_s_;
        if (S != null) {
            inpOut.addAll(S.getInputs());
            inpOut.addAll(S.getOutputs());

            alphabet.addAll(S.getAlphabet());

            hashSet_s_ = new LinkedHashSet<>(alphabet);
            alphabet = new ArrayList<>(hashSet_s_);
            alphabet.remove(Constants.DELTA);
            defineInpOut = inpOut.containsAll(alphabet);
        }

        if (I != null) {
            inpOut.addAll(I.getInputs());
            inpOut.addAll(I.getOutputs());

            alphabet.addAll(I.getAlphabet());

            hashSet_s_ = new LinkedHashSet<>(alphabet);
            alphabet = new ArrayList<>(hashSet_s_);
            alphabet.remove(Constants.DELTA);
            defineInpOut = inpOut.containsAll(alphabet);
        }

        if (!constainsMessage_gen(ViewConstants.labelInpOut) && !defineInpOut && !model
                && (cbModel.getSelectedIndex() != 1 && cbLabel.getSelectedIndex() != 0)) {
            msg += ViewConstants.labelInpOut;
        } else {
            if (!lts) {
                removeMessageGen(ViewConstants.labelInpOut);
            }
        }

        if (!constainsMessage_gen(ViewConstants.generation))
            msg += ViewConstants.generation;

        if (!constainsMessage_gen(ViewConstants.run_generation))
            msg += ViewConstants.run_generation;

        if (!constainsMessage_gen(ViewConstants.multigraph_generation))
            msg += ViewConstants.multigraph_generation;

        if (!constainsMessage_gen(ViewConstants.generation_mult))
            msg += ViewConstants.generation_mult;

        if (!constainsMessage_gen(ViewConstants.generation_tp_from_multi))
            msg += ViewConstants.generation_tp_from_multi;

        if (!constainsMessage_gen(ViewConstants.run_tp))
            msg += ViewConstants.run_tp;

        taWarning_gen.setText(taWarning_gen.getText() + msg);

    }

    public void verifyModelsEmpty(boolean ioco, boolean both) {
        String msg = "";

        boolean specification = tfSpecification.getText().isEmpty();

        if (!constainsMessage(ioco, ViewConstants.selectSpecification) && specification) {
            msg += ViewConstants.selectSpecification;
        } else {
            if (!specification) {
                removeMessage(ioco, ViewConstants.selectSpecification);
            }
        }

        if (both) {
            boolean implementation = tfImplementation.getText().isEmpty();
            if (!constainsMessage(ioco, ViewConstants.selectImplementation) && implementation) {
                msg += ViewConstants.selectImplementation;
            } else {
                if (!implementation) {
                    removeMessage(ioco, ViewConstants.selectImplementation);
                }
            }
            if (ioco) {
                lblWarningIoco.setText(lblWarningIoco.getText() + msg);
            } else {
                lblWarningLang.setText(lblWarningLang.getText() + msg);
            }
        } else {
            removeMessageGen(ViewConstants.selectImplementation);

            taWarning_gen.setText(lblWarningLang.getText() + msg);
        }

    }

    public void verifyInpOutEmpty(boolean ioco, boolean generation) {
        String msg = "";
        boolean defInpuOut = (cbModel.getSelectedItem() == ViewConstants.IOLTS_CONST
                && cbLabel.getSelectedItem() == ViewConstants.typeManualLabel
                && (tfInput.getText().isEmpty() || tfOutput.getText().isEmpty()));

        if (!generation) {
            if (!constainsMessage(ioco, ViewConstants.selectInpOut) && defInpuOut) {
                msg += ViewConstants.selectInpOut;
            } else {
                if (!defInpuOut) {
                    removeMessage(ioco, ViewConstants.selectInpOut);
                }
            }
            if (ioco) {
                lblWarningIoco.setText(lblWarningIoco.getText() + msg);
            } else {
                lblWarningLang.setText(lblWarningLang.getText() + msg);
            }
        } else {
            if (!constainsMessage(true, ViewConstants.selectInpOut) && defInpuOut) {
                msg += ViewConstants.selectInpOut;
            } else {
                if (!defInpuOut) {
                    removeMessage(ioco, ViewConstants.selectInpOut);
                }
            }

            taWarning_gen.setText(taWarning_gen.getText() + msg);
        }

    }

    public void selectTPFolder() {
        JFileChooser fc = directoryChooser();
        fc.showOpenDialog(EverestView.this);
        tpFolder = fc.getSelectedFile().getAbsolutePath();
        tfTPFolder.setText(fc.getSelectedFile().getName());

        visibilityRunButtons();
    }

    public void loadMultigraph(String folder) {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(pathMultigraph)));

            // get and set param m
            tfM.setText(contents.substring(
                    contents.lastIndexOf(Constants.MAX_IUT_STATES) + Constants.MAX_IUT_STATES.length(),
                    contents.indexOf("]")));

            // get and set specification
            // File file = new File(new
            // File(folder.substring(0,folder.lastIndexOf(System.getProperty("file.separator")))).getAbsolutePath(),
            // "spec.aut");
            File file = new File(
                    new File(folder.substring(0, folder.lastIndexOf(System.getProperty("file.separator"))))
                            .getAbsolutePath(),
                    "specification-" + folder.substring(folder.lastIndexOf(System.getProperty("file.separator")) + 1,
                            folder.length()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(contents.substring(contents.indexOf("des("),
                    contents.lastIndexOf(Constants.SEPARATOR_MULTIGRAPH_FILE)));
            writer.close();

            // set spec fields
            tfSpecification.setText(file.getName());
            pathSpecification = file.getAbsolutePath();
            lblmodelIoco.setText(tfSpecification.getText());
            lblmodelLang.setText(tfSpecification.getText());
            lblmodel_gen.setText(tfSpecification.getText());
            cbModel.setSelectedIndex(1);
            cbLabel.setSelectedIndex(1);
            setModel(false, false);
            // file.delete();

            contents = contents.substring(contents.lastIndexOf(Constants.SEPARATOR_MULTIGRAPH_FILE));
            contents = contents.substring(contents.indexOf('\n') + 1);
            String tempFileName = "multigraph_" + dateFormat.format(new Date()) + ".aut";
            file = new File(new File(folder.substring(0, folder.lastIndexOf(System.getProperty("file.separator"))))
                    .getAbsolutePath(), tempFileName);
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(contents);
            writer.close();

            multigraph_iolts = ImportAutFile.autToIOLTS(file.getAbsolutePath(), false, new ArrayList<>(),
                    new ArrayList<>());
            multigraph = multigraph_iolts.ioltsToAutomaton();
            file.delete();
            multigraph.setFinalStates(new ArrayList<>());
            multigraph.addFinalStates(new State_("fail"));
            multigraph.addFinalStates(new State_("pass"));

            visibilityRunButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    IOLTS multigraph_iolts;

    /***********************************************************
     *               GETTERS/SETTERS                           |
     ***********************************************************/

    public String getPathImplementation() {
        return pathImplementation;
    }

    public void setPathImplementation(String pathImplementation) {
        this.pathImplementation = pathImplementation;
    }

    public List<String> getPathImplementations() {
        return pathImplementations;
    }

    public void setPathImplementations(List<String> pathImplementations) {
        this.pathImplementations = pathImplementations;
    }

    public String getPathSpecification() {
        return pathSpecification;
    }

    public void setPathSpecification(String pathSpecification) {
        this.pathSpecification = pathSpecification;
    }

    /***********************************************************
     *                        UNUSED                           |
     ***********************************************************/

    public void standardizeLabelsIOLTS(boolean implementation) {
        // List<String> alphabet = new ArrayList();
        // HashSet hashSet_s_;
        if (implementation) {
            // alphabet.addAll(I.getAlphabet());
            // hashSet_s_ = new LinkedHashSet<>(alphabet);
            // alphabet = new ArrayList<>(hashSet_s_);
            // I.setAlphabet(alphabet);
            I.setAlphabet(new ArrayList<>(new LinkedHashSet<>(I.getAlphabet())));

            // alphabet = new ArrayList();
            // alphabet.addAll(I.getInputs());
            // hashSet_s_ = new LinkedHashSet<>(alphabet);
            // alphabet = new ArrayList<>(hashSet_s_);
            // I.setInputs(alphabet);
            I.setInputs(new ArrayList<>(new LinkedHashSet<>(I.getInputs())));

            // alphabet = new ArrayList();
            // alphabet.addAll(I.getOutputs());
            // hashSet_s_ = new LinkedHashSet<>(alphabet);
            // alphabet = new ArrayList<>(hashSet_s_);
            // I.setOutputs(alphabet);
            I.setInputs(new ArrayList<>(new LinkedHashSet<>(I.getOutputs())));

        } else {
            // alphabet.addAll(S.getAlphabet());
            // hashSet_s_ = new LinkedHashSet<>(alphabet);
            // alphabet = new ArrayList<>(hashSet_s_);
            // S.setAlphabet(alphabet);
            //
            // alphabet = new ArrayList();
            // alphabet.addAll(S.getInputs());
            // hashSet_s_ = new LinkedHashSet<>(alphabet);
            // alphabet = new ArrayList<>(hashSet_s_);
            // S.setInputs(alphabet);
            //
            // alphabet = new ArrayList();
            // alphabet.addAll(S.getOutputs());
            // hashSet_s_ = new LinkedHashSet<>(alphabet);
            // alphabet = new ArrayList<>(hashSet_s_);
            // S.setOutputs(alphabet);

            S.setAlphabet(new ArrayList<>(new LinkedHashSet<>(S.getAlphabet())));
            S.setInputs(new ArrayList<>(new LinkedHashSet<>(S.getInputs())));
            S.setInputs(new ArrayList<>(new LinkedHashSet<>(S.getOutputs())));
        }

        // alphabet = null;
    }

}
