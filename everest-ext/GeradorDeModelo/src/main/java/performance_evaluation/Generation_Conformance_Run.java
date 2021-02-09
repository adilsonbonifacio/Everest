package performance_evaluation;

import algorithm.GenerateIOLTS;
import algorithm.IocoConformance;
import algorithm.LanguageBasedConformance;
import algorithm.Operations;
import dk.brics.automaton.RegExp;
import gui.GenerateTab;
import model.Automaton_;
import model.IOLTS;
import model.LTS;
import parser.ImportAutFile;
import util.Constants;
import util.Settings;
import view.EverestView;
import view.ViewConstants;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.IntStream;

public class Generation_Conformance_Run {
    private static int numSpecs = 10;
    private static int numIuts = 10;
    private static boolean[] conformidades = { true, false };

    // Experimento 1
//    private static int numStatesSpec = 10;
//    private static int[] numStates = {15, 25, 30};
//    private static int[] numInputs = {2, 2, 3, 4, 5, 10};
//    private static int[] numOutputs = {5, 10, 4, 3, 2, 2};

    // Experimento 2.1
//    private static int numStatesSpec = 10;
//    private static int[] numStates = {15, 20, 25, 30, 35, 40, 45, 50};
//    private static int[] numInputs = {5};
//    private static int[] numOutputs = {5};

    // Experimento 2.2
//    private static int numStatesSpec = 20;
//    private static int[] numStates = {25, 30, 35, 40, 45, 50};
//    private static int[] numInputs = {5};
//    private static int[] numOutputs = {5};
//
//    // Experimento 2.3
//    private static int numStatesSpec = 30;
//    private static int[] numStates = {35, 40, 45, 50};
//    private static int[] numInputs = {5};
//    private static int[] numOutputs = {5};

    // Experimento 3 - Stress
    private static int numStatesSpec = 100;
    private static int[] numStates = {110,120,130,140,150,160,170,180,200};
    private static int[] numInputs = {5};
    private static int[] numOutputs = {5};

    private static List<String> pathSpecs = new ArrayList<>();
    private static List<String> pathSpecsDirs = new ArrayList<>();
    private static List<String> pathIuts = new ArrayList<>();
    private static List<String> pathIutsDirs = new ArrayList<>();

    // Caminho para geração dos arquivos de SPECS/IUTS
    private static String defaultPath = Paths.get(System.getProperty("user.dir").toString(), "TestesGeracao").toString(); // Diretório da execução
    //private static String defaultPath = "E:\\Documents\\UEL\\ANO-4\\IC_2020\\TestesGeracao";

    // Propriedades para geração de arquivos de resultados
        // Data atual
    private static String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        // Arquivo .txt com dados detalhados da geração
//    private static File OutFile = new File(Paths.get(defaultPath, new String("Execução-" + timeStamp + ".txt")).toString());
        // Arquivo .csv com dados numéricos
    private static File CsvFile = new File(Paths.get(defaultPath, new String("Execução-" + timeStamp + ".csv")).toString());
        // Arquivo .csv com dados de médias dos experimentos
    private static File CsvMeanFile = new File(Paths.get(defaultPath, new String("MÉDIAS-Execução-" + timeStamp + ".csv")).toString());

        // Variáveis de sistema
//    private static FileWriter fw; // TXT
    private static FileWriter fc; // CSV
    private static FileWriter fm; // CSV MÉDIAS
    private static MathContext mc = new MathContext(6, RoundingMode.HALF_UP);


    /**
     * Método principal
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("Executando script...");

        // Arquivo de saída dos resultados de medidas
        File dir = new File(defaultPath);
        dir.mkdirs();
//        try { fw = new FileWriter(OutFile.getAbsolutePath()); } catch (IOException e) { e.printStackTrace(); }
        try { fc = new FileWriter(CsvFile.getAbsolutePath()); } catch (IOException e) { e.printStackTrace(); }
        try { fm = new FileWriter(CsvMeanFile.getAbsolutePath()); } catch (IOException e) { e.printStackTrace(); }

        // Headers do csv
        try {
            fc.write("Iteração, Verificacao, SPEC, SPEC-Inputs, SPEC-Outputs, SPEC-Estados, IUT, IUT-Estados, IUT-Conformidade, Tempo total (s), Tempo por par (s), Memoria (MB)\n");
            fm.write("Verificacao, SPEC, SPEC-Inputs, SPEC-Outputs, SPEC-Estados, IUT-Estados, IUT-Conformidade, Tempo médio(s), Memoria média(MB)\n");

            //fc.write("Iteração, Verificacao, SPEC, SPEC-Inputs, SPEC-Outputs, SPEC-Estados, IUT-Estados, IUT-Conformidade, Tempo total (s), Tempo por par (s), Memoria (MB)\n");
        } catch (IOException e) { e.printStackTrace(); }

        Date start = new Date();

        // 1. Geração de SPECs
//        GenerationSPEC();

        // 2. Geração de IUTs
//        GenerationIUT();

//        RunIoco("NCONF");
        RunLangBased("NCONF");

//        // 3. Verificação de conformidade ioco
//        RunIoco(null);
//
//        // 4. Verificação de conformidade baseada em linguagens
//        RunLangBased();

        //TesteExperimentos();
        //TesteExperimentos_All();
        //Teste_Par();

        Date end = new Date();
        long time = end.getTime() - start.getTime(); // retorna em milissegundos
//        try {
//            fw.write("----------------------------------------------------\n");
//            fw.write("| TEMPO TOTAL DE EXECUÇÃO (s): " +
//                    new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) +
//                    "\n");
//            fw.write("----------------------------------------------------\n");
//        } catch(Exception ex) { }

        try { fm.close(); } catch (IOException e) { e.printStackTrace(); }
        try { fc.close(); } catch (IOException e) { e.printStackTrace(); }
//        try { fw.close(); } catch (IOException e) { e.printStackTrace(); }

        System.out.println("CONCLUIDO");
    }

    /**
     * Método para geração das SPECS
     */
    public static void GenerationSPEC()
    {
        Runtime rt = Runtime.getRuntime();
        Date start_ = new Date();

        // 1. Selecionar propriedades (input-enabled, determinístico)
        Settings settings = new Settings();
        settings.setDeterministic(true);
        settings.setInputComplete(true);
        settings.setConformanceType("None");

        // Gerar para cada combinação de inputs e outputs
        for(int i = 0; i < numInputs.length; i++)
        {
            // 2. Definir nome e diretório
            settings.setName(
                    "SPEC_" +
                            Integer.toString(numStatesSpec) +
                            "st_" +
                            Integer.toString(numInputs[i]) +
                            "in_" +
                            Integer.toString(numOutputs[i]) +
                            "out"
            );

            settings.setPath(defaultPath);
            settings.setCurrentDir(new File(defaultPath));
            pathSpecsDirs.add(Paths.get(defaultPath, settings.getName() + "_" + timeStamp).toString());

            for(int j = 0; j < numSpecs; j++)
            {
                String fName = settings.getName() + Integer.toString(j+1) + ".aut";
                pathSpecs.add(fName);
            }

//            try {
//                fw.write("\n----------------------------------------------------\n");
//                fw.write("|\t GERAÇÃO DE SPECs\n");
//                fw.write("| Diretório: " + pathSpecs.get(pathSpecs.size()-1) + "\n");
//                fw.write(
//                        "| Inputs: " +
//                                Integer.toString(numInputs[i]) +
//                                " - Outputs: " +
//                                Integer.toString(numOutputs[i]) +
//                                "\n"
//                );
//            } catch(Exception ex) { }

            // 3. Definir quantidades dos componentes
            settings.setStateNumber(numStatesSpec);
            settings.setInputNumber(numInputs[i]);
            settings.setOutputNumber(numOutputs[i]);
            settings.setModelNumber(numSpecs);

            // 4. Executar
            long prevFree = rt.freeMemory();
            Date start = new Date();
            GenerateIOLTS.run(null, settings);
            Date end = new Date();
            long postFree = rt.freeMemory();

            // Calculo do tempo de execução
            long time = end.getTime() - start.getTime(); // retorna em milissegundos

//            try {
//                fw.write("| GERAÇÃO DE SPEC (s): " +
//                        new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) +
//                        "\n");
//                fw.write("| GERAÇÃO DE SPEC (MB): " +
//                        new BigDecimal((prevFree - postFree)).divide(BigDecimal.valueOf(1000000), mc) +
//                        "\n");
//                fw.write("----------------------------------------------------\n");
//            } catch(Exception ex) { }
        }

        Date end_ = new Date();
        long time_ = end_.getTime() - start_.getTime(); // retorna em milissegundos
//        try {
//            fw.write("\n----------------------------------------------------\n");
//            fw.write("| GERAÇÃO DE TODAS AS SPECS (s): " +
//                    new BigDecimal(time_).divide(BigDecimal.valueOf(1000), mc) +
//                    "\n");
//            fw.write("| MÉDIA (s): " +
//                    new BigDecimal(time_).divide(BigDecimal.valueOf((numSpecs*numInputs.length*1000)), mc) +
//                    "\n");
//            fw.write("----------------------------------------------------\n");
//        } catch(Exception ex) { }
    }

    /**
     * Método para geração das IUTs
     */
    public static void GenerationIUT()
    {
        Runtime rt = Runtime.getRuntime();
        Date start_ = new Date();

        // 1. Selecionar propriedades (input-enabled, determinístico)
        Settings settings = new Settings();
        settings.setDeterministic(true);
        settings.setInputComplete(true);
        settings.setConformanceType("None");

        // Para cada conjunto de modelos de SPEC
        for(int i = 0; i < pathSpecsDirs.size(); i++)
        {
            // Para cada modelo individual
            for(int j = 0; j < numSpecs; j++)
            {
                // Endereço da SPEC
                String pSpecDir = pathSpecsDirs.get(i);
                String pSpec = pathSpecs.get((i*numSpecs) + j);

                Path specPath = Paths.get(pSpecDir, pSpec);
                File specFile = new File(specPath.toString());

                if(specFile.exists() && specFile.isFile())
                {
                    // Um conjunto de IUTs conformes e outro de não-conformes
                    for(boolean conf : conformidades)
                    {
                        String confDesc = conf ? "CONF" : "NCONF";

                        // Cada conjunto com IUTs de estados variáveis
                        for(int ns : numStates)
                        {
                            // 2. Definir nome e diretório
                            settings.setName(
                                    "IUT" +
                                    Integer.toString(j+1) +
                                    "_" +
                                    Integer.toString(ns) +
                                    "st_" +
                                    Integer.toString(numInputs[i]) +
                                    "in_" +
                                    Integer.toString(numOutputs[i]) +
                                    "out_" +
                                    confDesc
                            );

                            settings.setPath(defaultPath);
                            settings.setCurrentDir(new File(defaultPath));
                            pathIutsDirs.add(Paths.get(defaultPath, settings.getName() + "_" + timeStamp).toString());

                            for(int k = 0; k < numSpecs; k++)
                            {
                                String fName = settings.getName() + Integer.toString(k+1) + ".aut";
                                pathIuts.add(fName);
                            }

//                            try {
//                                fw.write("\n----------------------------------------------------\n");
//                                fw.write("|\t GERAÇÃO DE IUTs\n");
//                                fw.write("| Diretório: " + specFile.getParent() + "\n");
//                                fw.write("| Saída: " + settings.getName() + "\n");
//                                fw.write(
//                                        "| Inputs: " +
//                                                Integer.toString(numInputs[i]) +
//                                                " - Outputs: " +
//                                                Integer.toString(numOutputs[i]) +
//                                                "\n"
//                                );
//                            } catch(Exception ex) { }

                            // 3. Definir quantidades dos componentes
                            settings.setStateNumber(ns);
                            settings.setInputNumber(numInputs[i]);
                            settings.setOutputNumber(numOutputs[i]);
                            settings.setModelNumber(10);

                            // 4. Definir conformidade
                            String confType = conf ? "Conforms" : "Not Conform";
                            settings.setConformanceType(confType);
                            settings.setPathBase(specFile.toPath().toString());

                            // 5. Executar
                            long prevFree = rt.freeMemory();
                            Date start = new Date();
                            GenerateIOLTS.run(null, settings);
                            Date end = new Date();
                            long postFree = rt.freeMemory();

                            // Calculo do tempo de execução
                            long time = end.getTime() - start.getTime(); // retorna em milissegundos
//                            try {
//                                fw.write("| GERAÇÃO DE IUTs (s): " +
//                                        new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) +
//                                        "\n");
//                                fw.write("| GERAÇÃO DE IUTs (MB): " +
//                                        new BigDecimal((prevFree - postFree)).divide(BigDecimal.valueOf(1000000), mc) +
//                                        "\n");
//                                fw.write("----------------------------------------------------\n");
//                            } catch(Exception ex) { }

                        }
                    }
                }
            }
        }

        Date end_ = new Date();
        long time_ = end_.getTime() - start_.getTime(); // retorna em milissegundos

//        try {
//            fw.write("\n----------------------------------------------------\n");
//            fw.write("| GERAÇÃO DE TODAS AS IUTS (s): " +
//                    new BigDecimal(time_).divide(BigDecimal.valueOf(1000), mc) +
//                    "\n");
//            fw.write("| MÉDIA (s): " +
//                    new BigDecimal(time_).divide(BigDecimal.valueOf(numSpecs * numIuts * numInputs.length * numStates.length * conformidades.length * 1000), mc) +
//                    "\n");
//            fw.write("----------------------------------------------------\n");
//        } catch(Exception ex) { }
    }

    /**
     * Método para execução de verificação de conformidade ioco
     */
    public static void RunIoco(String conf)
    {
        // Para cada conjunto de modelos de SPEC
        // cada conjunto é definido por um par numInputs e numOutputs
        for(int i = 0; i < numInputs.length; i++)
        {
            // Nome sem data, vai pegar a primeira que achar no diretório padrão
            String dir_name = "SPEC_" +
                    Integer.toString(numStatesSpec) +
                    "st_" +
                    Integer.toString(numInputs[i]) +
                    "in_" +
                    Integer.toString(numOutputs[i]) +
                    "out";

            // Busca diretório de SPECs
            File defaultDir = new File(defaultPath);
            File specDir = null;
            for(File file : defaultDir.listFiles())
            {
                if(file.isDirectory() && file.getName().contains(dir_name))
                {
                    specDir = file;
                    break;
                }
            }

            // Para cada modelo individual
            for(int j = 0; j < numSpecs; j++)
            {
                // Busca SPEC específica número j+1
                String spec_name = dir_name + Integer.toString(j+1);
                File specFile = null;
                for(File file : specDir.listFiles())
                {
                    if(isAutFile(file) && file.isFile() && file.getName().contains(spec_name))
                    {
                        specFile = file;
                        break;
                    }
                }

                if(specFile != null)
                {
                    // Leitura da SPEC para IOLTS
                    IOLTS S = null;
                    int inputs_spec = 0;
                    int outputs_spec = 0;
                    try {
                        S = ImportAutFile.autToIOLTS(
                                specFile.getAbsolutePath(),
                                false,
                                new ArrayList<String>(),
                                new ArrayList<String>());
                        inputs_spec = S.getInputs().size();
                        outputs_spec = S.getOutputs().size();
                        S.addQuiescentTransitions();
                    } catch (Exception e) { e.printStackTrace(); }

                    if(S != null)
                    {
                        // Se é pra conformidade ou não conformidade especificamente
                        if(conf != null)
                        {
                            String confDesc = conf;
                            boolean confVal = conf == "CONF" ? true : false;

                            for(int l = 0; l < numStates.length; l++)
                            {
                                String iut_dir = "IUT" +
                                        Integer.toString(j+1) +
                                        "_" +
                                        Integer.toString(numStates[l]) +
                                        "st_" +
                                        Integer.toString(numInputs[i]) +
                                        "in_" +
                                        Integer.toString(numOutputs[i]) +
                                        "out_" +
                                        confDesc;

                                // Busca diretório do conjunto das IUTs
                                File iutDir = null;
                                for(File file : defaultDir.listFiles())
                                {
                                    if(file.isDirectory() && file.getName().contains(iut_dir))
                                    {
                                        iutDir = file;
                                        break;
                                    }
                                }

                                long total_time = 0;
                                long total_mem = 0;

                                // 10 execuções de cada experimento
                                for(int iteracao = 0; iteracao < 10; iteracao++)
                                {
                                    // Para cada IUT específica
                                    for(int m = 0; m < numIuts; m++)
                                    {
                                        // Busca IUT
                                        String iut_name = iut_dir + Integer.toString(m+1);
                                        File iutFile = null;
                                        for(File file : iutDir.listFiles())
                                        {
                                            if(isAutFile(file) && file.isFile() && file.getName().contains(iut_name))
                                            {
                                                iutFile = file;
                                                break;
                                            }
                                        }

                                        // Converte IUT para IOLTS
                                        IOLTS I = null;
                                        try {
                                            I = ImportAutFile.autToIOLTS(
                                                    iutFile.getAbsolutePath(),
                                                    false,
                                                    new ArrayList<String>(),
                                                    new ArrayList<String>());
                                        } catch (Exception e) { e.printStackTrace(); }

                                        I.addQuiescentTransitions();

                                        // Imprime início de verificação no arquivo TXT
//                                        try {
//                                            fw.write("\n----------------------------------------------------\n");
//                                            fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                                            fw.write("| Diretório padrão: " + defaultDir.toString() + "\n");
//                                            fw.write("| IUTs: " + Integer.toString(numIuts) + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                        // Executar verificação de conformidade
                                        String failPath = "";
                                        Runtime rt = Runtime.getRuntime();
                                        long prevFree = rt.freeMemory();
                                        Date start = new Date();

                                        try {
                                            // Runs conformance test
                                            Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                                            // Adds result to failPaths
                                            failPath = Operations.path(S, I, conformidade, true, false, 1);

                                        } catch (Exception e) { e.printStackTrace(); }

                                        // Calculo do tempo de execução
                                        Date end = new Date();
                                        long postFree = rt.freeMemory();
                                        long time = end.getTime() - start.getTime(); // retorna em milissegundos
                                        total_time += time;
                                        total_mem += (prevFree - postFree);

                                        // Imprime resultados
                                        String result = failPath == "" ? "CONF" : "NCONF";
                                        // TXT
//                                        try {
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                                                    + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                                                    + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                                                    "\n");
//                                            fw.write("| RESULTADO: " + result + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                        // CSV
                                        try {
                                            fc.write(
                                                    "ioco" + "," +
                                                            specFile.getName() + "," +
                                                            Integer.toString(inputs_spec) + "," +
                                                            Integer.toString(outputs_spec) + "," +
                                                            Integer.toString(S.getStates().size()) + "," +
                                                            iutFile.getName() + "," +
                                                            Integer.toString(numStates[l]) + "," +
                                                            Boolean.toString(confVal) + "," +
                                                            new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                            new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                            new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                                            );
                                        } catch (IOException e) { e.printStackTrace(); }
                                    }
                                }

                                // Registra média de tempo
                                try {
                                    BigDecimal mean_time = new BigDecimal(total_time).divide(new BigDecimal(10000), mc); // p/ segundos + média aritmética por nº de experimentos
                                    BigDecimal mean_mem = new BigDecimal(total_mem).divide(new BigDecimal(1000000), mc);

                                    fm.write(
                                            "ioco" + "," +
                                                    specFile.getName() + "," +
                                                    Integer.toString(inputs_spec) + "," +
                                                    Integer.toString(outputs_spec) + "," +
                                                    Integer.toString(S.getStates().size()) + "," +
                                                    Integer.toString(numStates[l]) + "," +
                                                    Boolean.toString(confVal)+ "," +
                                                    mean_time.toString() + "," +
                                                    mean_mem.toString() + "\n"
                                    );
                                } catch (IOException e) { e.printStackTrace(); }

                            }
                        }
                        else {
                            // Um conjunto de IUTs conformes e outro de não-conformes
                            for(int k = 0; k < conformidades.length; k++)
                            {
                                String confDesc = conformidades[k] ? "CONF" : "NCONF";

                                // Cada conjunto com IUTs de estados variáveis
                                for(int l = 0; l < numStates.length; l++)
                                {
                                    String iut_dir = "IUT" +
                                            Integer.toString(j+1) +
                                            "_" +
                                            Integer.toString(numStates[l]) +
                                            "st_" +
                                            Integer.toString(numInputs[i]) +
                                            "in_" +
                                            Integer.toString(numOutputs[i]) +
                                            "out_" +
                                            confDesc;

                                    // Busca diretório do conjunto das IUTs
                                    File iutDir = null;
                                    for(File file : defaultDir.listFiles())
                                    {
                                        if(file.isDirectory() && file.getName().contains(iut_dir))
                                        {
                                            iutDir = file;
                                            break;
                                        }
                                    }

                                    long total_time = 0;
                                    long total_mem = 0;

                                    // 10 execuções de cada experimento
                                    for(int iteracao = 0; iteracao < 10; iteracao++)
                                    {
                                        // Para cada IUT específica
                                        for(int m = 0; m < numIuts; m++)
                                        {
                                            // Busca IUT
                                            String iut_name = iut_dir + Integer.toString(m+1);
                                            File iutFile = null;
                                            for(File file : iutDir.listFiles())
                                            {
                                                if(isAutFile(file) && file.isFile() && file.getName().contains(iut_name))
                                                {
                                                    iutFile = file;
                                                    break;
                                                }
                                            }

                                            // Converte IUT para IOLTS
                                            IOLTS I = null;
                                            try {
                                                I = ImportAutFile.autToIOLTS(
                                                        iutFile.getAbsolutePath(),
                                                        false,
                                                        new ArrayList<String>(),
                                                        new ArrayList<String>());
                                            } catch (Exception e) { e.printStackTrace(); }

                                            I.addQuiescentTransitions();

                                            // Imprime início de verificação no arquivo TXT
//                                        try {
//                                            fw.write("\n----------------------------------------------------\n");
//                                            fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                                            fw.write("| Diretório padrão: " + defaultDir.toString() + "\n");
//                                            fw.write("| IUTs: " + Integer.toString(numIuts) + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                            // Executar verificação de conformidade
                                            String failPath = "";
                                            Runtime rt = Runtime.getRuntime();
                                            long prevFree = rt.freeMemory();
                                            Date start = new Date();

                                            try {
                                                // Runs conformance test
                                                Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                                                // Adds result to failPaths
                                                failPath = Operations.path(S, I, conformidade, true, false, 1);

                                            } catch (Exception e) { e.printStackTrace(); }

                                            // Calculo do tempo de execução
                                            Date end = new Date();
                                            long postFree = rt.freeMemory();
                                            long time = end.getTime() - start.getTime(); // retorna em milissegundos
                                            total_time += time;
                                            total_mem += (prevFree - postFree);

                                            // Imprime resultados
                                            String result = failPath == "" ? "CONF" : "NCONF";
                                            // TXT
//                                        try {
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                                                    + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                                                    + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                                                    "\n");
//                                            fw.write("| RESULTADO: " + result + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                            // CSV
                                            try {
                                                fc.write(
                                                        "ioco" + "," +
                                                                specFile.getName() + "," +
                                                                Integer.toString(inputs_spec) + "," +
                                                                Integer.toString(outputs_spec) + "," +
                                                                Integer.toString(S.getStates().size()) + "," +
                                                                iutFile.getName() + "," +
                                                                Integer.toString(numStates[l]) + "," +
                                                                Boolean.toString(conformidades[k]) + "," +
                                                                new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                                new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                                new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                                                );
                                            } catch (IOException e) { e.printStackTrace(); }
                                        }
                                    }

                                    // Registra média de tempo
                                    try {
                                        BigDecimal mean_time = new BigDecimal(total_time).divide(new BigDecimal(10000), mc); // p/ segundos + média aritmética por nº de experimentos
                                        BigDecimal mean_mem = new BigDecimal(total_mem).divide(new BigDecimal(1000000), mc);

                                        fm.write(
                                                "ioco" + "," +
                                                        specFile.getName() + "," +
                                                        Integer.toString(inputs_spec) + "," +
                                                        Integer.toString(outputs_spec) + "," +
                                                        Integer.toString(S.getStates().size()) + "," +
                                                        Integer.toString(numStates[l]) + "," +
                                                        Boolean.toString(conformidades[k])+ "," +
                                                        mean_time.toString() + "," +
                                                        mean_mem.toString() + "\n"
                                        );
                                    } catch (IOException e) { e.printStackTrace(); }

                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * Método para execução de verificação de conformidade por linguagens
     */
    public static void RunLangBased(String conf)
    {
        // Para cada conjunto de modelos de SPEC
        // cada conjunto é definido por um par numInputs e numOutputs
        // ou por estados
        for(int i = 0; i < numInputs.length; i++)
        {
            // Nome sem data, vai pegar a primeira que achar no diretório padrão
            String dir_name = "SPEC_" +
                    Integer.toString(numStatesSpec) +
                    "st_" +
                    Integer.toString(numInputs[i]) +
                    "in_" +
                    Integer.toString(numOutputs[i]) +
                    "out";

            // Busca diretório de SPECs
            File defaultDir = new File(defaultPath);
            File specDir = null;
            for(File file : defaultDir.listFiles())
            {
                if(file.isDirectory() && file.getName().contains(dir_name))
                {
                    specDir = file;
                    break;
                }
            }

            // Para cada modelo individual
            for(int j = 0; j < numSpecs; j++)
            {
                // Busca SPEC específica número j+1
                String spec_name = dir_name + Integer.toString(j+1);
                File specFile = null;
                for(File file : specDir.listFiles())
                {
                    if(isAutFile(file) && file.isFile() && file.getName().contains(spec_name))
                    {
                        specFile = file;
                        break;
                    }
                }

                if(specFile != null)
                {
                    // Leitura da SPEC para LTS
                    IOLTS S = null;
                    LTS S_ = null;
                    int inputs_spec = 0;
                    int outputs_spec = 0;
                    try {
                        S = ImportAutFile.autToIOLTS(
                                specFile.getAbsolutePath(),
                                false,
                                new ArrayList<String>(),
                                new ArrayList<String>());
                        inputs_spec = S.getInputs().size();
                        outputs_spec = S.getOutputs().size();
                        S.addQuiescentTransitions();
                        S_ = S.toLTS();

                    } catch (Exception e) { e.printStackTrace(); }

                    if(S_ != null)
                    {
                        // Se é para conformes ou não-conformes específicamente
                        if(conf != null)
                        {
                            String confDesc = conf;
                            boolean confVal = conf == "CONF" ? true : false;

                            // Cada conjunto com IUTs de estados variáveis
                            for(int l = 0; l < numStates.length; l++)
                            {
                                String iut_dir = "IUT" +
                                        Integer.toString(j+1) +
                                        "_" +
                                        Integer.toString(numStates[l]) +
                                        "st_" +
                                        Integer.toString(numInputs[i]) +
                                        "in_" +
                                        Integer.toString(numOutputs[i]) +
                                        "out_" +
                                        confDesc;

                                // Busca diretório do conjunto das IUTs
                                File iutDir = null;
                                for(File file : defaultDir.listFiles())
                                {
                                    if(file.isDirectory() && file.getName().contains(iut_dir))
                                    {
                                        iutDir = file;
                                        break;
                                    }
                                }

                                long total_time = 0;
                                long total_mem = 0;

                                // 10 execuções de cada experimento
                                for(int iteracao = 0; iteracao < 10; iteracao++)
                                {
                                    // Para cada IUT específica
                                    for(int m = 0; m < numIuts; m++)
                                    {
                                        // Busca IUT
                                        String iut_name = iut_dir + Integer.toString(m+1);
                                        File iutFile = null;
                                        for(File file : iutDir.listFiles())
                                        {
                                            if(isAutFile(file) && file.isFile() && file.getName().contains(iut_name))
                                            {
                                                iutFile = file;
                                                break;
                                            }
                                        }

                                        // Converte IUT para LTS
                                        LTS I_ = null;
                                        IOLTS I = null;

                                        try {
                                            I = ImportAutFile.autToIOLTS(
                                                    iutFile.getAbsolutePath(),
                                                    false,
                                                    new ArrayList<String>(),
                                                    new ArrayList<String>());
                                        } catch (Exception e) { e.printStackTrace(); }

                                        I.addQuiescentTransitions();
                                        I_ = I.toLTS();

                                        // Imprime início de verificação no arquivo TXT
//                                        try {
//                                            fw.write("\n----------------------------------------------------\n");
//                                            fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                                            fw.write("| Diretório padrão: " + defaultDir.toString() + "\n");
//                                            fw.write("| IUTs: " + Integer.toString(numIuts) + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                        // Executar verificação de conformidade
                                        String failPath = "";
                                        Runtime rt = Runtime.getRuntime();
                                        long prevFree = rt.freeMemory();
                                        Date start = new Date();

                                        // Teste de conformidade
                                        if (S_.getAlphabet().size() != 0 || I_.getAlphabet().size() != 0) {

                                            String D = "(";
                                            List<String> alphabets = new ArrayList<>();
                                            alphabets.addAll(S_.getAlphabet());
                                            alphabets.addAll(I_.getAlphabet());
                                            for (String la : new ArrayList<>(new LinkedHashSet<>(alphabets))) {
                                                D += la + "|";
                                            }
                                            D = D.substring(0, D.length() - 1);
                                            D += ")*";

                                            String F = "";

                                            if (regexIsValid(D) && regexIsValid(F)) {
                                                Automaton_ conformidade = LanguageBasedConformance.verifyLanguageConformance(S_, I_, D, F);

                                                if (conformidade.getFinalStates().size() > 0) {
                                                    failPath = Operations.path(S_, I_, conformidade, false, false, 1);
                                                } else {
                                                    failPath = "";
                                                }

                                            }

                                        }

                                        // Calculo do tempo de execução
                                        Date end = new Date();
                                        long postFree = rt.freeMemory();
                                        long time = end.getTime() - start.getTime(); // retorna em milissegundos
                                        total_time += time;
                                        total_mem += (prevFree - postFree);

                                        // Imprime resultados
                                        String result = failPath == "" ? "CONF" : "NCONF";
                                        // TXT
//                                        try {
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                                                    + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                                                    + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                                                    "\n");
//                                            fw.write("| RESULTADO: " + result + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                        // CSV
                                        try {
                                            fc.write(
                                                    "lang" + "," +
                                                            specFile.getName() + "," +
                                                            Integer.toString(inputs_spec) + "," +
                                                            Integer.toString(outputs_spec) + "," +
                                                            Integer.toString(S.getStates().size()) + "," +
                                                            iutFile.getName() + "," +
                                                            Integer.toString(numStates[l]) + "," +
                                                            Boolean.toString(confVal) + "," +
                                                            new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                            new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                            new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                                            );
                                        } catch (IOException e) { e.printStackTrace(); }
                                    }
                                }

                                // Registra média de tempo
                                try {
                                    BigDecimal mean_time = new BigDecimal(total_time).divide(new BigDecimal(10000), mc); // p/ segundos + média aritmética por nº de experimentos
                                    BigDecimal mean_mem = new BigDecimal(total_mem).divide(new BigDecimal(1000000), mc);

                                    fm.write(
                                            "lang" + "," +
                                                    specFile.getName() + "," +
                                                    Integer.toString(inputs_spec) + "," +
                                                    Integer.toString(outputs_spec) + "," +
                                                    Integer.toString(S.getStates().size()) + "," +
                                                    Integer.toString(numStates[l]) + "," +
                                                    Boolean.toString(confVal)+ "," +
                                                    mean_time.toString() + "," +
                                                    mean_mem.toString() + "\n"
                                    );
                                } catch (IOException e) { e.printStackTrace(); }

                            }
                        }
                        else {
                            // Um conjunto de IUTs conformes e outro de não-conformes
                            for(int k = 0; k < conformidades.length; k++)
                            {
                                String confDesc = conformidades[k] ? "CONF" : "NCONF";

                                // Cada conjunto com IUTs de estados variáveis
                                for(int l = 0; l < numStates.length; l++)
                                {
                                    String iut_dir = "IUT" +
                                            Integer.toString(j+1) +
                                            "_" +
                                            Integer.toString(numStates[l]) +
                                            "st_" +
                                            Integer.toString(numInputs[i]) +
                                            "in_" +
                                            Integer.toString(numOutputs[i]) +
                                            "out_" +
                                            confDesc;

                                    // Busca diretório do conjunto das IUTs
                                    File iutDir = null;
                                    for(File file : defaultDir.listFiles())
                                    {
                                        if(file.isDirectory() && file.getName().contains(iut_dir))
                                        {
                                            iutDir = file;
                                            break;
                                        }
                                    }

                                    long total_time = 0;
                                    long total_mem = 0;

                                    // 10 execuções de cada experimento
                                    for(int iteracao = 0; iteracao < 10; iteracao++)
                                    {
                                        // Para cada IUT específica
                                        for(int m = 0; m < numIuts; m++)
                                        {
                                            // Busca IUT
                                            String iut_name = iut_dir + Integer.toString(m+1);
                                            File iutFile = null;
                                            for(File file : iutDir.listFiles())
                                            {
                                                if(isAutFile(file) && file.isFile() && file.getName().contains(iut_name))
                                                {
                                                    iutFile = file;
                                                    break;
                                                }
                                            }

                                            // Converte IUT para LTS
                                            LTS I_ = null;
                                            IOLTS I = null;

                                            try {
                                                I = ImportAutFile.autToIOLTS(
                                                        iutFile.getAbsolutePath(),
                                                        false,
                                                        new ArrayList<String>(),
                                                        new ArrayList<String>());
                                            } catch (Exception e) { e.printStackTrace(); }

                                            I.addQuiescentTransitions();
                                            I_ = I.toLTS();

                                            // Imprime início de verificação no arquivo TXT
//                                        try {
//                                            fw.write("\n----------------------------------------------------\n");
//                                            fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                                            fw.write("| Diretório padrão: " + defaultDir.toString() + "\n");
//                                            fw.write("| IUTs: " + Integer.toString(numIuts) + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                            // Executar verificação de conformidade
                                            String failPath = "";
                                            Runtime rt = Runtime.getRuntime();
                                            long prevFree = rt.freeMemory();
                                            Date start = new Date();

                                            // Teste de conformidade
                                            if (S_.getAlphabet().size() != 0 || I_.getAlphabet().size() != 0) {

                                                String D = "(";
                                                List<String> alphabets = new ArrayList<>();
                                                alphabets.addAll(S_.getAlphabet());
                                                alphabets.addAll(I_.getAlphabet());
                                                for (String la : new ArrayList<>(new LinkedHashSet<>(alphabets))) {
                                                    D += la + "|";
                                                }
                                                D = D.substring(0, D.length() - 1);
                                                D += ")*";

                                                String F = "";

                                                if (regexIsValid(D) && regexIsValid(F)) {
                                                    Automaton_ conformidade = LanguageBasedConformance.verifyLanguageConformance(S_, I_, D, F);

                                                    if (conformidade.getFinalStates().size() > 0) {
                                                        failPath = Operations.path(S_, I_, conformidade, false, false, 1);
                                                    } else {
                                                        failPath = "";
                                                    }

                                                }

                                            }

                                            // Calculo do tempo de execução
                                            Date end = new Date();
                                            long postFree = rt.freeMemory();
                                            long time = end.getTime() - start.getTime(); // retorna em milissegundos
                                            total_time += time;
                                            total_mem += (prevFree - postFree);

                                            // Imprime resultados
                                            String result = failPath == "" ? "CONF" : "NCONF";
                                            // TXT
//                                        try {
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                                                    + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                                            fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                                                    + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                                                    "\n");
//                                            fw.write("| RESULTADO: " + result + "\n");
//                                            fw.write("----------------------------------------------------\n");
//                                        } catch(Exception ex) { }

                                            // CSV
                                            try {
                                                fc.write(
                                                        "lang" + "," +
                                                                specFile.getName() + "," +
                                                                Integer.toString(inputs_spec) + "," +
                                                                Integer.toString(outputs_spec) + "," +
                                                                Integer.toString(S.getStates().size()) + "," +
                                                                iutFile.getName() + "," +
                                                                Integer.toString(numStates[l]) + "," +
                                                                Boolean.toString(conformidades[k]) + "," +
                                                                new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                                new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                                new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                                                );
                                            } catch (IOException e) { e.printStackTrace(); }
                                        }
                                    }

                                    // Registra média de tempo
                                    try {
                                        BigDecimal mean_time = new BigDecimal(total_time).divide(new BigDecimal(10000), mc); // p/ segundos + média aritmética por nº de experimentos
                                        BigDecimal mean_mem = new BigDecimal(total_mem).divide(new BigDecimal(1000000), mc);

                                        fm.write(
                                                "lang" + "," +
                                                        specFile.getName() + "," +
                                                        Integer.toString(inputs_spec) + "," +
                                                        Integer.toString(outputs_spec) + "," +
                                                        Integer.toString(S.getStates().size()) + "," +
                                                        Integer.toString(numStates[l]) + "," +
                                                        Boolean.toString(conformidades[k])+ "," +
                                                        mean_time.toString() + "," +
                                                        mean_mem.toString() + "\n"
                                        );
                                    } catch (IOException e) { e.printStackTrace(); }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Teste para comparação, experimentos específicos
     */
    public static void TesteExperimentos()
    {
        File spec = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-nao-conf\\50states\\alfabeto4\\experimento1\\50states_spec.aut");
        File iutDir = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-nao-conf\\50states\\alfabeto4\\experimento1\\iut");

        Runtime rt = Runtime.getRuntime();
        Date start_ = new Date();

        // Leitura da SPEC para IOLTS
        IOLTS S = null;
        int inputs_spec = 0;
        int outputs_spec = 0;
        try {
            S = ImportAutFile.autToIOLTS(
                    spec.getAbsolutePath(),
                    false,
                    new ArrayList<String>(),
                    new ArrayList<String>());
            inputs_spec = S.getInputs().size();
            outputs_spec = S.getOutputs().size();
            S.addQuiescentTransitions();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(S != null)
        {

            // Selecionar IUTs do diretório e converter para IOLTS
            List<IOLTS> ListI = new ArrayList<>();
            List<String> failPaths = new ArrayList<>();
            List<String> iutPaths = new ArrayList<>();

            for(File file : iutDir.listFiles())
            {
                if(isAutFile(file))
                {
                    IOLTS I = null;
                    try {
                        I = ImportAutFile.autToIOLTS(
                                file.getAbsolutePath(),
                                false,
                                new ArrayList<String>(),
                                new ArrayList<String>());
                    } catch (Exception e) { e.printStackTrace(); }

                    I.addQuiescentTransitions();
                    ListI.add(I);
                    iutPaths.add(file.getAbsolutePath());
                }
            }

//            try {
//                fw.write("\n----------------------------------------------------\n");
//                fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                fw.write("| Diretório: " + spec.getParent().toString() + "\n");
//                fw.write("| IUTs: " + iutPaths.size() + "\n");
//            } catch(Exception ex) { }

            // Executar verificação de conformidade
            long prevFree = rt.freeMemory();
            Date start = new Date();

            for(int f = 0; f < iutPaths.size(); f++)
            {
                IOLTS I = ListI.get(f);

                try {
                    // Runs conformance test
                    Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                    // Adds result to failPaths
                    String failPath = Operations.path(S, I, conformidade, true, false, 1);
                    failPaths.add(failPath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Date end = new Date();

            // Arquivos de resultados
            if(failPaths.size() > 0)
            {
                // Cria pasta de outputs
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String outputDirPath = String
                        .join(File.separator,
                                iutDir.getAbsolutePath(),
                                "conformIoco_" + timeStamp);
                File outputDir = new File(outputDirPath);
                outputDir.mkdirs();
                String taOutput = ""; // Output pra exibir na tela

                // Cria um arquivo para cada saída
                int iocoFails = 0;
                for(int m = 0; m < failPaths.size(); m++)
                {
                    // Cria arquivos de output
                    File currFile = new File(iutPaths.get(m));
                    List<String> output = new ArrayList<>();
                    String fname = "ioco-";
                    fname = fname.concat(currFile.getName()
                            .replace("aut", "conf"));
                    File newOutput = new File(String.join(File.separator,
                            outputDirPath,
                            fname));

                    // Conteúdo variável do arquivo
                    if (!failPaths.get(m).equals("")) {
                        iocoFails++;
                        String msg = "The IUT "
                                + fname.replace(".conf", "")
                                +  " does not conform to the specification.\n"
                                + "-----*-----\n\n";
                        output.add(msg.concat(failPaths.get(m)));
                    }
                    else{
                        String msg = "The IUT "
                                + fname.replace(".conf", "")
                                + " conforms to the specification.\n"
                                + "-----*-----\n\n";
                        output.add(msg);
                    }

                    // Escreve no arquivo
                    try {
                        Files.write(newOutput.toPath(), output, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            // Calculo do tempo de execução
            long postFree = rt.freeMemory();
            long time = end.getTime() - start.getTime(); // retorna em milissegundos

            // TXT
//            try {
//                fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                        + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                        + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                        "\n");
//                fw.write("----------------------------------------------------\n");
//            } catch(Exception ex) { }

            // CSV
            try {
                fc.write(
                        "ioco" + "," +
                                spec.getName() + "," +
                                Integer.toString(inputs_spec) + "," +
                                Integer.toString(outputs_spec) + "," +
                                Integer.toString(S.getStates().size()) + "," +
                                Integer.toString(50) + "," +
                                "CONF" + "," +
                                new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                new BigDecimal(time).divide(BigDecimal.valueOf(10000), mc) + "," +
                                new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                );
            } catch (IOException e) { e.printStackTrace(); }

        }
    }

    /**
     * Teste para comparação, todos os experimentos
     * com breaks para restrição
     */
    public static void TesteExperimentos_All()
    {
        // Pastas de conformidade
        File confDir = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-conf");
        File nonConfDir = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-nao-conf");

        // Para modelos conformes
        // Para cada número de estados
        for(File statesDir : confDir.listFiles())
        {
            if(statesDir.isDirectory())
            {
                // Para cada combinação de alfabeto
                for(File alphabetDir : statesDir.listFiles())
                {
                    if(alphabetDir.isDirectory())
                    {
                        // Para cada experimento
                        for(File experimentDir : alphabetDir.listFiles())
                        {
                            // Encontra arquivo .aut da SPEC e diretório de IUTs
                            File specFile = null;
                            File iutDir = null;
                            for(File file : experimentDir.listFiles())
                            {
                                if(file.isFile() && isAutFile(file))
                                {
                                    specFile = file;
                                }
                                else if(file.isDirectory()) {
                                    iutDir = file;
                                }
                            }

                            // Leitura da SPEC para IOLTS
                            IOLTS S = null;
                            int inputs_spec = 0;
                            int outputs_spec = 0;
                            try {
                                S = ImportAutFile.autToIOLTS(
                                        specFile.getAbsolutePath(),
                                        false,
                                        new ArrayList<String>(),
                                        new ArrayList<String>());
                                inputs_spec = S.getInputs().size();
                                outputs_spec = S.getOutputs().size();
                                S.addQuiescentTransitions();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Abre diretório com IUTs
                            Runtime rt = Runtime.getRuntime();
                            Date start_ = new Date();

                            if(S != null)
                            {
                                // Selecionar IUTs do diretório e converter para IOLTS
                                List<IOLTS> ListI = new ArrayList<>();
                                List<String> failPaths = new ArrayList<>();
                                List<String> iutPaths = new ArrayList<>();

                                for(File file : iutDir.listFiles())
                                {
                                    if(isAutFile(file))
                                    {
                                        IOLTS I = null;
                                        try {
                                            I = ImportAutFile.autToIOLTS(
                                                    file.getAbsolutePath(),
                                                    false,
                                                    new ArrayList<String>(),
                                                    new ArrayList<String>());
                                        } catch (Exception e) { e.printStackTrace(); }

                                        I.addQuiescentTransitions();
                                        ListI.add(I);
                                        iutPaths.add(file.getAbsolutePath());
                                    }
                                }

                                // Repete 10 vezes o experimento
                                for(int i = 0; i < 10; i++)
                                {
                                    failPaths.clear();

//                                    try {
//                                        fw.write("\n----------------------------------------------------\n");
//                                        fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                                        fw.write("| Diretório: " + specFile.getParent().toString() + "\n");
//                                        fw.write("| IUTs: " + iutPaths.size() + "\n");
//                                    } catch(Exception ex) { }

                                    // Executar verificação de conformidade
                                    long prevFree = rt.freeMemory();
                                    Date start = new Date();

                                    for(int f = 0; f < iutPaths.size(); f++)
                                    {
                                        IOLTS I = ListI.get(f);

                                        try {
                                            // Runs conformance test
                                            Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                                            // Adds result to failPaths
                                            String failPath = Operations.path(S, I, conformidade, true, false, 1);
                                            failPaths.add(failPath);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    Date end = new Date();

                                    // Arquivos de resultados
                                    if(failPaths.size() > 0)
                                    {
                                        // Cria pasta de outputs
                                        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                        String outputDirPath = String
                                                .join(File.separator,
                                                        iutDir.getAbsolutePath(),
                                                        "conformIoco_" + timeStamp);
                                        File outputDir = new File(outputDirPath);
                                        outputDir.mkdirs();
                                        String taOutput = ""; // Output pra exibir na tela

                                        // Cria um arquivo para cada saída
                                        int iocoFails = 0;
                                        for(int m = 0; m < failPaths.size(); m++)
                                        {
                                            // Cria arquivos de output
                                            File currFile = new File(iutPaths.get(m));
                                            List<String> output = new ArrayList<>();
                                            String fname = "ioco-";
                                            fname = fname.concat(currFile.getName()
                                                    .replace("aut", "conf"));
                                            File newOutput = new File(String.join(File.separator,
                                                    outputDirPath,
                                                    fname));

                                            // Conteúdo variável do arquivo
                                            if (!failPaths.get(m).equals("")) {
                                                iocoFails++;
                                                String msg = "The IUT "
                                                        + fname.replace(".conf", "")
                                                        +  " does not conform to the specification.\n"
                                                        + "-----*-----\n\n";
                                                output.add(msg.concat(failPaths.get(m)));
                                            }
                                            else{
                                                String msg = "The IUT "
                                                        + fname.replace(".conf", "")
                                                        + " conforms to the specification.\n"
                                                        + "-----*-----\n\n";
                                                output.add(msg);
                                            }

                                            // Escreve no arquivo
                                            try {
                                                if(newOutput.exists()) { newOutput.delete(); }
                                                Files.write(newOutput.toPath(), output, StandardCharsets.UTF_8);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }

                                    // Calculo do tempo de execução
                                    long postFree = rt.freeMemory();
                                    long time = end.getTime() - start.getTime(); // retorna em milissegundos

                                    // TXT
//                                    try {
//                                        fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                                                + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                                        fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                                                + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                                                "\n");
//                                        fw.write("----------------------------------------------------\n");
//                                    } catch(Exception ex) { }

                                    // CSV
                                    try {
                                        fc.write(
                                                Integer.toString(i) + "," +
                                                    "ioco" + "," +
                                                        specFile.getName() + "," +
                                                        Integer.toString(inputs_spec) + "," +
                                                        Integer.toString(outputs_spec) + "," +
                                                        Integer.toString(S.getStates().size()) + "," +
                                                        Integer.toString(ListI.get(0).getStates().size()) + "," +
                                                        "CONF" + "," +
                                                        new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                        new BigDecimal(time).divide(BigDecimal.valueOf(10000), mc) + "," +
                                                        new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                                        );
                                    } catch (IOException e) { e.printStackTrace(); }
                                }

                            }

                            // Interrompe após primeiro experimento
                            break;
                        }
                    }

                    // Interrompe após primeiro alfabeto (provavelmente 10)
                    break;
                }
            }

            // Interrompe após primeiro número de estados (provavelmente 100)
            break;
        }

        // Para modelos não-conformes
        for(File statesDir : nonConfDir.listFiles())
        {
            if(statesDir.isDirectory())
            {
                // Para cada combinação de alfabeto
                for(File alphabetDir : statesDir.listFiles())
                {
                    if(alphabetDir.isDirectory())
                    {
                        // Para cada experimento
                        for(File experimentDir : alphabetDir.listFiles())
                        {
                            // Encontra arquivo .aut da SPEC e diretório de IUTs
                            File specFile = null;
                            File iutDir = null;
                            for(File file : experimentDir.listFiles())
                            {
                                if(file.isFile() && isAutFile(file))
                                {
                                    specFile = file;
                                }
                                else if(file.isDirectory()) {
                                    iutDir = file;
                                }
                            }

                            // Leitura da SPEC para IOLTS
                            IOLTS S = null;
                            int inputs_spec = 0;
                            int outputs_spec = 0;
                            try {
                                S = ImportAutFile.autToIOLTS(
                                        specFile.getAbsolutePath(),
                                        false,
                                        new ArrayList<String>(),
                                        new ArrayList<String>());
                                inputs_spec = S.getInputs().size();
                                outputs_spec = S.getOutputs().size();
                                S.addQuiescentTransitions();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Abre diretório com IUTs
                            Runtime rt = Runtime.getRuntime();
                            Date start_ = new Date();

                            if(S != null)
                            {
                                // Selecionar IUTs do diretório e converter para IOLTS
                                List<IOLTS> ListI = new ArrayList<>();
                                List<String> failPaths = new ArrayList<>();
                                List<String> iutPaths = new ArrayList<>();

                                for(File file : iutDir.listFiles())
                                {
                                    if(isAutFile(file))
                                    {
                                        IOLTS I = null;
                                        try {
                                            I = ImportAutFile.autToIOLTS(
                                                    file.getAbsolutePath(),
                                                    false,
                                                    new ArrayList<String>(),
                                                    new ArrayList<String>());
                                        } catch (Exception e) { e.printStackTrace(); }

                                        I.addQuiescentTransitions();
                                        ListI.add(I);
                                        iutPaths.add(file.getAbsolutePath());
                                    }
                                }

                                // Repete 10 vezes o experimento
                                for(int i = 0; i < 10; i++)
                                {
                                    failPaths.clear();

//                                    try {
//                                        fw.write("\n----------------------------------------------------\n");
//                                        fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                                        fw.write("| Diretório: " + specFile.getParent().toString() + "\n");
//                                        fw.write("| IUTs: " + iutPaths.size() + "\n");
//                                    } catch(Exception ex) { }

                                    // Executar verificação de conformidade
                                    long prevFree = rt.freeMemory();
                                    Date start = new Date();

                                    for(int f = 0; f < iutPaths.size(); f++)
                                    {
                                        IOLTS I = ListI.get(f);

                                        try {
                                            // Runs conformance test
                                            Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                                            // Adds result to failPaths
                                            String failPath = Operations.path(S, I, conformidade, true, false, 1);
                                            failPaths.add(failPath);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    Date end = new Date();

                                    // Arquivos de resultados
                                    if(failPaths.size() > 0)
                                    {
                                        // Cria pasta de outputs
                                        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                        String outputDirPath = String
                                                .join(File.separator,
                                                        iutDir.getAbsolutePath(),
                                                        "conformIoco_" + timeStamp);
                                        File outputDir = new File(outputDirPath);
                                        outputDir.mkdirs();
                                        String taOutput = ""; // Output pra exibir na tela

                                        // Cria um arquivo para cada saída
                                        int iocoFails = 0;
                                        for(int m = 0; m < failPaths.size(); m++)
                                        {
                                            // Cria arquivos de output
                                            File currFile = new File(iutPaths.get(m));
                                            List<String> output = new ArrayList<>();
                                            String fname = "ioco-";
                                            fname = fname.concat(currFile.getName()
                                                    .replace("aut", "conf"));
                                            File newOutput = new File(String.join(File.separator,
                                                    outputDirPath,
                                                    fname));

                                            // Conteúdo variável do arquivo
                                            if (!failPaths.get(m).equals("")) {
                                                iocoFails++;
                                                String msg = "The IUT "
                                                        + fname.replace(".conf", "")
                                                        +  " does not conform to the specification.\n"
                                                        + "-----*-----\n\n";
                                                output.add(msg.concat(failPaths.get(m)));
                                            }
                                            else{
                                                String msg = "The IUT "
                                                        + fname.replace(".conf", "")
                                                        + " conforms to the specification.\n"
                                                        + "-----*-----\n\n";
                                                output.add(msg);
                                            }

                                            // Escreve no arquivo
                                            try {
                                                if(newOutput.exists()) { newOutput.delete(); }
                                                Files.write(newOutput.toPath(), output, StandardCharsets.UTF_8);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }

                                    // Calculo do tempo de execução
                                    long postFree = rt.freeMemory();
                                    long time = end.getTime() - start.getTime(); // retorna em milissegundos

                                    // TXT
//                                    try {
//                                        fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                                                + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                                        fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                                                + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                                                "\n");
//                                        fw.write("----------------------------------------------------\n");
//                                    } catch(Exception ex) { }

                                    // CSV
                                    try {
                                        fc.write(
                                                    Integer.toString(i) + "," +
                                                        "ioco" + "," +
                                                        specFile.getName() + "," +
                                                        Integer.toString(inputs_spec) + "," +
                                                        Integer.toString(outputs_spec) + "," +
                                                        Integer.toString(S.getStates().size()) + "," +
                                                        Integer.toString(ListI.get(0).getStates().size()) + "," +
                                                        "NONCONF" + "," +
                                                        new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                                        new BigDecimal(time).divide(BigDecimal.valueOf(10000), mc) + "," +
                                                        new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                                        );
                                    } catch (IOException e) { e.printStackTrace(); }
                                }

                            }

                            // Interrompe após primeiro experimento
                            break;
                        }
                    }

                    // Interrompe após primeiro alfabeto (provavelmente 10)
                    break;
                }
            }

            // Interrompe após primeiro número de estados (provavelmente 100)
            break;
        }
    }

    /**
     * Teste para comparação, apenas um par SPEC/IUT
     */
    public static void Teste_Par()
    {
        // Pastas de conformidade
        File confSpec = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-conf\\100states\\alfabeto10\\experimento1\\100states_spec.aut");
        File confIut = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-conf\\100states\\alfabeto10\\experimento1\\iut\\1pct_iut_0.aut");
        File nonConfSpec = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-nao-conf\\100states\\alfabeto10\\experimento1\\100states_spec.aut");
        File nonConfIut = new File("E:\\Documents\\UEL\\SVN\\Implementação\\Testes-Camila\\novos-modelos\\ioco-nao-conf\\100states\\alfabeto10\\experimento1\\iut\\1pct_iut_0.aut");

        // 1. Conforme
        // Leitura das SPEC para IOLTS
        IOLTS S = null;
        int inputs_spec = 0;
        int outputs_spec = 0;
        try {
            S = ImportAutFile.autToIOLTS(
                    confSpec.getAbsolutePath(),
                    false,
                    new ArrayList<String>(),
                    new ArrayList<String>());
            inputs_spec = S.getInputs().size();
            outputs_spec = S.getOutputs().size();
            S.addQuiescentTransitions();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Abre diretório com IUTs
        Runtime rt = Runtime.getRuntime();
        Date start_ = new Date();


        if(S != null)
        {
            // Selecionar IUTs do diretório e converter para IOLTS
            IOLTS I = null;
            try {
                I = ImportAutFile.autToIOLTS(
                        confIut.getAbsolutePath(),
                        false,
                        new ArrayList<String>(),
                        new ArrayList<String>());
            } catch (Exception e) { e.printStackTrace(); }

            I.addQuiescentTransitions();

            String failPath = null;

//            try {
//                fw.write("\n----------------------------------------------------\n");
//                fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                fw.write("| Diretório: " + confSpec.getParent().toString() + "\n");
//                fw.write("| IUTs: 1\n");
//            } catch(Exception ex) { }

            for(int i = 0; i < 10; i++)
            {
                // Executar verificação de conformidade
                long prevFree = rt.freeMemory();
                Date start = new Date();

                try {
                    // Runs conformance test
                    Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                    // Adds result to failPaths
                    failPath = Operations.path(S, I, conformidade, true, false, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Date end = new Date();

                // Arquivos de resultados
                // Cria pasta de outputs
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String outputDirPath = String
                        .join(File.separator,
                                confIut.getParent(),
                                "conformIoco_" + timeStamp);
                File outputDir = new File(outputDirPath);
                outputDir.mkdirs();
                String taOutput = ""; // Output pra exibir na tela

                // Calculo do tempo de execução
                long postFree = rt.freeMemory();
                long time = end.getTime() - start.getTime(); // retorna em milissegundos

                // TXT
//                try {
//                    fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                            + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                    fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                            + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                            "\n");
//                    fw.write("----------------------------------------------------\n");
//                } catch(Exception ex) { }

                // CSV
                try {
                    fc.write(
                                Integer.toString(i) + "," +
                                    "ioco" + "," +
                                    confSpec.getName() + "," +
                                    Integer.toString(inputs_spec) + "," +
                                    Integer.toString(outputs_spec) + "," +
                                    Integer.toString(S.getStates().size()) + "," +
                                    Integer.toString(I.getStates().size()) + "," +
                                    "CONF" + "," +
                                    new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                    new BigDecimal(time).divide(BigDecimal.valueOf(10000), mc) + "," +
                                    new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                    );
                } catch (IOException e) { e.printStackTrace(); }
            }

        }

        // 2. Não conforme
        S = null;
        inputs_spec = 0;
        outputs_spec = 0;
        try {
            S = ImportAutFile.autToIOLTS(
                    nonConfSpec.getAbsolutePath(),
                    false,
                    new ArrayList<String>(),
                    new ArrayList<String>());
            inputs_spec = S.getInputs().size();
            outputs_spec = S.getOutputs().size();
            S.addQuiescentTransitions();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Abre diretório com IUTs
        rt = Runtime.getRuntime();
        start_ = new Date();

        if(S != null)
        {
            // Selecionar IUTs do diretório e converter para IOLTS
            IOLTS I = null;
            try {
                I = ImportAutFile.autToIOLTS(
                        nonConfIut.getAbsolutePath(),
                        false,
                        new ArrayList<String>(),
                        new ArrayList<String>());
            } catch (Exception e) { e.printStackTrace(); }

            I.addQuiescentTransitions();

            String failPath = null;

//            try {
//                fw.write("\n----------------------------------------------------\n");
//                fw.write("|\t VERIFICAÇÃO DE CONFORMIDADE IOCO\n");
//                fw.write("| Diretório: " + nonConfSpec.getParent().toString() + "\n");
//                fw.write("| IUTs: 1\n");
//            } catch(Exception ex) { }

            for(int i = 0; i < 10; i++)
            {
                // Executar verificação de conformidade
                long prevFree = rt.freeMemory();
                Date start = new Date();

                try {
                    // Runs conformance test
                    Automaton_ conformidade = IocoConformance.verifyIOCOConformance(S, I);

                    // Adds result to failPaths
                    failPath = Operations.path(S, I, conformidade, true, false, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Date end = new Date();

                // Calculo do tempo de execução
                long postFree = rt.freeMemory();
                long time = end.getTime() - start.getTime(); // retorna em milissegundos

                // TXT
//                try {
//                    fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (s): "
//                            + new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "\n");
//                    fw.write("| VERIFICAÇÃO DE CONFORMIDADE IOCO (MB): "
//                            + new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) +
//                            "\n");
//                    fw.write("----------------------------------------------------\n");
//                } catch(Exception ex) { }

                // CSV
                try {
                    fc.write(
                                Integer.toString(i) + "," +
                                    "ioco" + "," +
                                    nonConfSpec.getName() + "," +
                                    Integer.toString(inputs_spec) + "," +
                                    Integer.toString(outputs_spec) + "," +
                                    Integer.toString(S.getStates().size()) + "," +
                                    Integer.toString(I.getStates().size()) + "," +
                                    "CONF" + "," +
                                    new BigDecimal(time).divide(BigDecimal.valueOf(1000), mc) + "," +
                                    new BigDecimal(time).divide(BigDecimal.valueOf(10000), mc) + "," +
                                    new BigDecimal(prevFree - postFree).divide(BigDecimal.valueOf(1000000), mc) + "\n"
                    );
                } catch (IOException e) { e.printStackTrace(); }
            }

        }

    }

    /**
     * Método auxiliar para detectar extensão de arquivo
     * @param f
     * @return
     */
    public static boolean isAutFile(File f) {
        return (f.getName().indexOf(".") != -1 && f.getName().substring(f.getName().indexOf(".")).equals(".aut"));
    }

    /**
     * Método auxiliar para validar regex
     * @param exp
     * @return
     */
    public static boolean regexIsValid(String exp) {
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
}


