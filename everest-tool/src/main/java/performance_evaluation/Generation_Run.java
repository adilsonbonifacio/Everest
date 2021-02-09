package performance_evaluation;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.server.Operation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Graph;

import algorithm.Operations;
import algorithm.TestGeneration;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import model.Automaton_;
import model.IOLTS;
import model.State_;
import model.Transition_;
import parser.ImportAutFile;

import parser.ImportGraphmlFile;
import util.AutGenerator;
import util.Constants;
import view.EverestView;

public class Generation_Run {

	public static void main(String[] args) throws Exception {

//		String rootPath = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste desempenho\\10-50states\\result\\i-o\\ioco-n-conf-2pct\\10inp-2out\\";
//		String tool = "everest";
//		String path = rootPath + tool + ".csv";
//		String COMMA_DELIMITER = ",";
//		boolean states;
//		String alfabeto, experimento;
//		List<String> values;
//		String header = "";
//
//		List<List<String>> records = new ArrayList<>();
//		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//			String line;
//			header = br.readLine();// pula cabeçalho
//			header += "alphabet" + COMMA_DELIMITER + "experiment";
//			records.add(Arrays.asList(header.split(COMMA_DELIMITER)));
//			while ((line = br.readLine()) != null) {
//				alfabeto = "";
//				experimento = "";
//				values = new ArrayList<String>();
//				values.addAll(Arrays.asList(line.split(COMMA_DELIMITER)));
//
//				// values.add("");//jtorx ioco
//				for (String l : Arrays.asList(values.get(1).replace("\\", "@").split("@"))) {
//					if (l.contains("alfabeto")) {
//						String s = (l.replace("alfabeto", ""));
//						values.add(s);
//					}
//
//					if (l.contains("experimento")) {
//						values.add(l.replace("experimento", ""));
//					}
//				}
//				records.add(values);
//
//			}
//		}
//
//		FileWriter csvWriter = new FileWriter(rootPath + "new-" + tool + ".csv");
//		for (List<String> rowData : records) {
//			csvWriter.append(String.join(COMMA_DELIMITER, rowData));
//			csvWriter.append("\n");
//		}
//
//		csvWriter.flush();
//		csvWriter.close();
		
		String pct = "4";
		String rootPath = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste desempenho\\teste-geração\\run-tp-geral"+pct+"pct.csv";
		
		String COMMA_DELIMITER = ";";
		boolean states;
		String alfabeto, experimento;
		List<String> values;
		String header = "";

		List<List<String>> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(rootPath))) {
			String line;
			header = br.readLine();// pula cabeçalho
			header += "pct" + COMMA_DELIMITER + "statesModel";
			records.add(Arrays.asList(header.split(COMMA_DELIMITER)));
			while ((line = br.readLine()) != null) {
				alfabeto = "";
				experimento = "";
				values = new ArrayList<String>();
				values.addAll(Arrays.asList(line.split(COMMA_DELIMITER)));

				// values.add("");//jtorx ioco
				for (String l : Arrays.asList(values.get(1).replace("\\", "@").split("@"))) {
					if (l.contains("pct")) {
						String s = (l.replace("pct", ""));
						values.add(s);
					}

					if (l.contains("states")) {
						values.add(l.replace("states", ""));
					}
				}
				records.add(values);

			}
		}

		FileWriter csvWriter = new FileWriter("C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste desempenho\\teste-geração\\new-run-tp-geral"+pct+"pct.csv");
		for (List<String> rowData : records) {
			csvWriter.append(String.join(COMMA_DELIMITER, rowData));
			csvWriter.append("\n");
		}

		csvWriter.flush();
		csvWriter.close();

		// ########################### Gerar multigrafo
		// List<Integer> m_array = Arrays.asList(5, 15, 25, 35, 45, 55, 65, 75, 85, 95,
		// 105);
		//
		// List<Integer> spec_states = Arrays.asList(5);// 5, 15, 25, 35
		// // 45,55
		// List<Integer> n_experimentos = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// IOLTS S, multigraph;
		// String multigraphName;
		// double time_ini, time_end = 0, total_seconds;
		// // Get the Java runtime
		// Runtime runtime = Runtime.getRuntime();
		// double memory;
		//
		// String root = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\teste-geração\\1pct\\";
		// String multigraph_root_path;
		//
		// end: for (int spec_state : spec_states) {
		// int iut_state = spec_state;
		// // for (int iut_state : m_array) {
		// for (int experiment : n_experimentos) {
		//
		// File folder = new File(root + spec_state + "states\\alfabeto10\\iut" +
		// iut_state + "\\experimento"
		// + experiment + "\\" + spec_state + "states_spec.aut");
		// S = ImportAutFile.autToIOLTS(folder.getAbsolutePath(), false, new
		// ArrayList<>(), new ArrayList<>());
		// for (int m : m_array) {
		//
		// multigraph_root_path = root + spec_state + "states\\alfabeto10\\iut" +
		// iut_state + "\\experimento"
		// + experiment + "\\multigraph"+m+"\\";
		// File dir = new File(multigraph_root_path);
		// if (!dir.exists())
		// dir.mkdirs();
		//
		// // Get the Java runtime
		// runtime = Runtime.getRuntime();
		// // Run the garbage collector
		// runtime.gc();
		//
		// // generate multigraph
		// time_ini = System.nanoTime();
		// multigraph = TestGeneration.multiGraphD(S, m).toIOLTS(S.getInputs(),
		// S.getOutputs());
		//
		// multigraph.setInitialState(new
		// State_(multigraph.getInitialState().getName().replace(",", "_")));
		// multigraphName = saveMultigraphFile(multigraph_root_path, m,
		// folder.getAbsolutePath(), multigraph, S);
		//
		// time_end = System.nanoTime();
		//
		// total_seconds = (time_end - time_ini);
		// total_seconds = (total_seconds / 1e6);// 1e6 milisegundos/
		// System.out.println("tempo: " + total_seconds);
		// memory = (runtime.totalMemory() - runtime.freeMemory());
		//
		// saveOnCSVFile(root + "performance-multigrafo.csv", "everest",
		// folder.getAbsolutePath(),
		// S.getStates().size(),
		// S.getTransitions().size(), multigraphName, multigraph.getStates().size(),
		// multigraph.getTransitions().size(), total_seconds, "miliseconds", memory,
		// "bytes",
		// S.getAlphabet().size(), Objects.toString(experiment), m);
		//
		// }
		// }
		//
		// }

		// ########################### Gerar TPs

		// List<Integer> m_array = Arrays.asList( 35, 45, 55);
		// List<Integer> spec_states = Arrays.asList(35);// 5, 15, 25, 35, 45,55
		//
		// List<Integer> n_experimentos = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		//
		// String multigraphName;
		// double time_ini, time_end = 0, total_seconds;
		// // Get the Java runtime
		// Runtime runtime = Runtime.getRuntime();
		// double memory;
		//
		// String tempFolder = "C:\\Users\\camil\\Desktop\\aaa\\";
		// String root = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\teste-geração\\";
		// String multigraph_root_path = "";
		// String tp_root_path;
		// int nTestCase = 1000;
		// List<String> testSuite;
		// IOLTS S;
		// Automaton_ multigraph;
		// int alphabet = 10;
		// String specPath;
		//
		// File folder;
		// File[] listOfFiles;
		//
		// end: for (int spec_state : spec_states) {
		// int iut_state = spec_state;
		// // for (int iut_state : m_array) {
		// for (int experiment : n_experimentos) {
		//
		// for (int m : m_array) {
		// System.out.println("experimento: " + experiment + " - " + "m: " + m);
		//
		// specPath = root + "\\multigrafos-tps\\" + spec_state +
		// "states-spec\\experimento" + experiment
		// + "//" + spec_state + "states_spec.aut";
		// S = ImportAutFile.autToIOLTS(specPath, false, null, null);
		// multigraph = TestGeneration.multiGraphD(S, m);
		//
		// // \5states\multigrafo-tp
		// multigraph_root_path = root + "\\multigrafos-tps\\" + spec_state +
		// "states-spec\\experimento"
		// + experiment + "\\multigraph" + m + "\\";
		// tp_root_path = root + "\\multigrafos-tps\\" + spec_state +
		// "states-spec\\experimento" + experiment
		// + "\\multigraph" + m + "\\TPs\\";
		//
		// File dir = new File(tp_root_path);
		// if (!dir.exists())
		// dir.mkdirs();
		//
		//// folder = new File(multigraph_root_path);
		//// listOfFiles = folder.listFiles();
		//
		// // for (File multigraphFileName : listOfFiles) {
		// // if ((multigraphFileName.getName().indexOf(".") != -1 &&
		// // multigraphFileName.getName()
		// // .substring(multigraphFileName.getName().indexOf(".")).equals(".aut"))) {
		// // Get the Java runtime
		// runtime = Runtime.getRuntime();
		// // Run the garbage collector
		// runtime.gc();
		//
		// // generate multigraph
		// time_ini = System.nanoTime();
		//
		// testSuite = getTcAndSaveTP(multigraph, nTestCase, multigraph_root_path,
		// S.getInputs(),
		// S.getOutputs(), null, S.getStates().size(), S.getTransitions().size(),
		// specPath, "everest",
		// multigraph.getStates().size(), multigraph.getTransitions().size(), alphabet,
		// experiment, m,root )
		// .getKey();
		//
		// time_end = System.nanoTime();
		//
		// total_seconds = (time_end - time_ini);
		// total_seconds = (total_seconds / 1e6);// 1e6 milisegundos/ 1e9 milisegundos
		// System.err.println("tempo: " + total_seconds + " milisegundos " );
		// memory = (runtime.totalMemory() - runtime.freeMemory());
		//
		// saveOnCSVFile_TpGen(root + "tp-gen-geral.csv", "everest",
		// multigraph_root_path,
		// multigraph.getStates().size(), multigraph.getTransitions().size(),
		// total_seconds,
		// "miliseconds", memory, "bytes", S.getAlphabet().size(),
		// Objects.toString(experiment), m,
		// testSuite.size());
		// // }
		// // }
		//
		//
		//
		// }
		// }
		//
		// }

		// // ########################### Executar Tps x Iuts
		// List<Integer> m_array = Arrays.asList(25);// 5, 15, 25, 35, 45,55
		// List<Integer> spec_states = Arrays.asList(25);// 5,15,25,35
		// String pct = "4";
		//
		// List<Integer> n_experimentos = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// double time_ini, time_end = 0, total_seconds;
		// // Get the Java runtime
		// Runtime runtime = Runtime.getRuntime();
		// double memory;
		// String root = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\teste-geração\\";
		// String tp_root_path;
		// int alphabet = 10;
		// String iut_path;
		// File folder_tp, folder_iut;
		// File[] listOfFiles_tp, listOfFiles_iut;
		// boolean verdict, v_aux;
		//
		//
		// double time_ini_unt, time_end_unt = 0, total_seconds_unt;
		//
		// end: for (int spec_state : spec_states) {
		// int iut_state = spec_state;
		// // for (int iut_state : m_array) {
		// for (int experiment : n_experimentos) {
		//
		// for (int m : m_array) {
		// System.out.println("experimento: " + experiment + " - " + "m: " + m);
		//
		// tp_root_path = root + "\\multigrafos-tps\\" + spec_state +
		// "states-spec\\experimento" + experiment
		// + "\\multigraph" + m + "\\TPs\\";
		//
		// iut_path = root + pct + "pct\\" + spec_state + "states\\alfabeto" + alphabet
		// + "\\iut" + m
		// + "\\experimento" + experiment + "\\iut\\";
		//
		// folder_iut = new File(iut_path);
		// listOfFiles_iut = folder_iut.listFiles();
		//
		// // pega uma iut roda tds os TPs
		// for (File iut : listOfFiles_iut) {
		// verdict = false;
		// v_aux = false;
		//
		// // generate multigraph
		// time_ini = System.nanoTime();
		//
		// folder_tp = new File(tp_root_path);
		// listOfFiles_tp = folder_tp.listFiles();
		//
		// for (File tp : listOfFiles_tp) {
		// // if ((multigraphFileName.getName().indexOf(".") != -1 &&
		// // multigraphFileName.getName()
		// // .substring(multigraphFileName.getName().indexOf(".")).equals(".aut"))) {
		// // Get the Java runtime
		// runtime = Runtime.getRuntime();
		// // Run the garbage collector
		// runtime.gc();
		//
		// time_ini_unt = System.nanoTime();
		// v_aux = runAllIutTp(tp, true, iut.getAbsolutePath(), root);
		//
		// if (verdict == false && v_aux) {
		// verdict = false;
		// }
		// time_end_unt = System.nanoTime();
		//
		// total_seconds_unt = (time_end_unt - time_ini_unt);
		// total_seconds_unt = (total_seconds_unt / 1e6);// 1e6 milisegundos/ 1e9
		// milisegundos
		// memory = (runtime.totalMemory() - runtime.freeMemory());
		//
		// saveOnCSVFile_runTPGeneral(root + "run-tp-unitario"+pct+"pct.csv", "everest",
		// iut.getAbsolutePath(),
		// tp.getAbsolutePath(), total_seconds_unt, "miliseconds", memory, "byte",
		// alphabet,
		// experiment, pct, listOfFiles_tp.length, verdict);
		//
		// // }
		//
		// }
		//
		// time_end = System.nanoTime();
		//
		// total_seconds = (time_end - time_ini);
		// total_seconds = (total_seconds / 1e6);// 1e6 milisegundos/ 1e9 milisegundos
		// System.err.println("tempo: " + total_seconds + " milisegundos ");
		// memory = (runtime.totalMemory() - runtime.freeMemory());
		//
		// saveOnCSVFile_runTPGeneral(root + "run-tp-geral"+pct+"pct.csv", "everest",
		// iut.getAbsolutePath(),
		// tp_root_path, total_seconds, "miliseconds", memory, "byte", alphabet,
		// experiment, pct,
		// listOfFiles_tp.length, verdict);
		//
		//
		//
		// }
		// }
		// }
		//
		// }

		// ***************************************************************

	}

	// public static String saveMultigraphFile(String folder, int m, String
	// pathSpecification, IOLTS multigraph, IOLTS S) {
	// try {
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	//
	// String fileContent = "";
	// // save m
	// fileContent += Constants.MAX_IUT_STATES + m + "] \n";
	// // save spec
	// // fileContent += AutGenerator.ioltsToAut(new IOLTS(S.getStates(),
	// // S.getInitialState(), S.getAlphabet(),
	// // S.getTransitions(), S.getInputs(),
	// S.getOutputs()).removeDeltaTransitions());
	//
	// fileContent += AutGenerator.ioltsToAut(
	// ImportAutFile.autToIOLTS(pathSpecification, false, new ArrayList<>(), new
	// ArrayList<>()));
	// // save multigraph
	// fileContent += Constants.SEPARATOR_MULTIGRAPH_FILE;
	// fileContent += AutGenerator.ioltsToAut(new IOLTS(multigraph.getStates(),
	// multigraph.getInitialState(),
	// multigraph.getAlphabet(), multigraph.getTransitions(), S.getInputs(),
	// S.getOutputs()));
	// String fileName = "spec-multigraph_" + dateFormat.format(new Date()) + " - m"
	// + m + ".aut";
	// File file = new File(folder, fileName);
	// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	// writer.write(fileContent);
	// writer.close();
	// return fileName;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "";
	// }

	static IOLTS S;

	public static Automaton_ loadMultigraph(String folder, String pathMultigraph) throws Exception {
		String contents = new String(Files.readAllBytes(Paths.get(pathMultigraph)));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

		File file = new File(
				new File(folder.substring(0, folder.lastIndexOf(System.getProperty("file.separator"))))
						.getAbsolutePath(),
				"specification-" + folder.substring(folder.lastIndexOf(System.getProperty("file.separator")) + 1,
						folder.length()));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(contents.substring(contents.indexOf("des("),
				contents.lastIndexOf(Constants.SEPARATOR_MULTIGRAPH_FILE)));
		writer.close();

		String pathSpecification = file.getAbsolutePath();
		;
		S = ImportAutFile.autToIOLTS(pathSpecification, false, new ArrayList<>(), new ArrayList<>());

		contents = contents.substring(contents.lastIndexOf(Constants.SEPARATOR_MULTIGRAPH_FILE));
		contents = contents.substring(contents.indexOf('\n') + 1);
		String tempFileName = "multigraph_" + dateFormat.format(new Date()) + ".aut";
		file = new File(new File(folder.substring(0, folder.lastIndexOf(System.getProperty("file.separator"))))
				.getAbsolutePath(), tempFileName);
		writer = new BufferedWriter(new FileWriter(file));
		writer.write(contents);
		writer.close();

		IOLTS multigraph = ImportAutFile.autToIOLTS(file.getAbsolutePath(), false, new ArrayList<>(),
				new ArrayList<>());

		Automaton_ multigraph_a = new Automaton_(multigraph.getStates(), multigraph.getInitialState(),
				multigraph.getAlphabet(), new ArrayList<>(), multigraph.getTransitions());
		file.delete();
		multigraph_a.setFinalStates(Arrays.asList(new State_("fail")));

		return multigraph_a;

	}

	public static String saveMultigraphFile(String folder, int m, String pathSpecification, IOLTS multigraph, IOLTS S) {
		String graphName = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		try {
			String fileContent = "";
			// save m
			fileContent += Constants.MAX_IUT_STATES + m + "] \n";
			// save spec
			// fileContent += AutGenerator.ioltsToAut(new IOLTS(S.getStates(),
			// S.getInitialState(), S.getAlphabet(),
			// S.getTransitions(), S.getInputs(), S.getOutputs()).removeDeltaTransitions());
			fileContent += AutGenerator.ioltsToAut(
					ImportAutFile.autToIOLTS(pathSpecification, false, new ArrayList<>(), new ArrayList<>()));
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
			graphName = "spec-multigraph_" + dateFormat.format(new Date()) + " - m" + m + ".aut";
			File file = new File(folder, graphName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return graphName;
	}

	public static void saveOnCSVFile_multigraphGen(String pathCsv, String tool, String pathModel, int statesModel,
			int transitionsModels, String pathMultigraph, int statesMultigraph, int transitionsMultigraph, double time,
			String unitTime, double memory, String unityMemory, int alphabet, String experiment, int m) {

		List<String> headerCSV = Arrays.asList("tool", "model", "statesModel", "transitionsModels", "multigraph",
				"statesMultigraph", "transitionsMultigraph", "time", "unit", "memory", "unit", "alphabet", "experiment",
				"m");
		String delimiterCSV = ";";
		try {

			ArrayList<String> row = new ArrayList<String>();
			row.add(tool);
			row.add(pathModel);
			row.add(Objects.toString(statesModel));
			row.add(Objects.toString(transitionsModels));
			row.add(pathMultigraph);
			row.add(Objects.toString(statesMultigraph));
			row.add(Objects.toString(transitionsMultigraph));
			row.add(String.format("%.2f", time).replace(",", "."));// .replace(",", "."))
			// row.add(Objects.toString(time));
			row.add(unitTime);
			row.add(Objects.toString(memory));
			row.add(unityMemory);
			row.add(Objects.toString(alphabet));
			row.add(experiment);
			row.add(Objects.toString(m));

			FileWriter csvWriter;

			File file = new File(pathCsv);
			if (!file.exists()) {
				file.createNewFile();

			}

			// first record
			if (new File(pathCsv).length() == 0) {
				csvWriter = new FileWriter(pathCsv);

				for (String header : headerCSV) {
					csvWriter.append(header);
					csvWriter.append(delimiterCSV);
				}
				csvWriter.append("\n");
			} else {
				csvWriter = new FileWriter(pathCsv, true);
			}

			csvWriter.append(String.join(delimiterCSV, row));
			csvWriter.append("\n");

			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

	public static void saveOnCSVFile_runTPGeneral(String pathCsv, String tool, String pathIut, String rootPathTp,
			double time, String unitTime, double memory, String unityMemory, int alphabet, int experiment, String pct,
			int ntps, boolean verdict) {

		List<String> headerCSV = Arrays.asList("tool", "iut", "rootPathTP", "time", "unit", "memory", "unit",
				"alphabet", "experiment", "pct_iut", "nTPs", "verdict");
		String delimiterCSV = ";";
		try {

			ArrayList<String> row = new ArrayList<String>();
			row.add(tool);
			row.add(pathIut);
			row.add(rootPathTp);
			row.add(String.format("%.2f", time).replace(",", "."));// .replace(",", "."))
			// row.add(Objects.toString(time));
			row.add(unitTime);
			row.add(Objects.toString(memory));
			row.add(unityMemory);
			row.add(Objects.toString(alphabet));
			row.add(Objects.toString(experiment));
			row.add(pct);
			row.add(Objects.toString(ntps));
			row.add(Objects.toString(verdict));

			FileWriter csvWriter;

			File file = new File(pathCsv);
			if (!file.exists()) {
				file.createNewFile();

			}

			// first record
			if (new File(pathCsv).length() == 0) {
				csvWriter = new FileWriter(pathCsv);

				for (String header : headerCSV) {
					csvWriter.append(header);
					csvWriter.append(delimiterCSV);
				}
				csvWriter.append("\n");
			} else {
				csvWriter = new FileWriter(pathCsv, true);
			}

			csvWriter.append(String.join(delimiterCSV, row));
			csvWriter.append("\n");

			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

	public static void saveOnCSVFile_TpGen(String pathCsv, String tool, String pathMultigraph, int statesMultigraph,
			int transitionsMultigraph, double time, String unitTime, double memory, String unityMemory, int alphabet,
			String experiment, int m, int ntps) {

		List<String> headerCSV = Arrays.asList("tool", "multigraph", "statesMultigraph", "transitionsMultigraph",
				"time", "unit", "memory", "unit", "alphabet", "experiment", "m", "nTps");
		String delimiterCSV = ";";
		try {

			ArrayList<String> row = new ArrayList<String>();
			row.add(tool);
			row.add(pathMultigraph);
			row.add(Objects.toString(statesMultigraph));
			row.add(Objects.toString(transitionsMultigraph));
			row.add(String.format("%.2f", time).replace(",", "."));// .replace(",", "."))
			// row.add(Objects.toString(time));
			row.add(unitTime);
			row.add(Objects.toString(memory));
			row.add(unityMemory);
			row.add(Objects.toString(alphabet));
			row.add(experiment);
			row.add(Objects.toString(m));
			row.add(Objects.toString(ntps));

			FileWriter csvWriter;

			File file = new File(pathCsv);
			if (!file.exists()) {
				file.createNewFile();

			}

			// first record
			if (new File(pathCsv).length() == 0) {
				csvWriter = new FileWriter(pathCsv);

				for (String header : headerCSV) {
					csvWriter.append(header);
					csvWriter.append(delimiterCSV);
				}
				csvWriter.append("\n");
			} else {
				csvWriter = new FileWriter(pathCsv, true);
			}

			csvWriter.append(String.join(delimiterCSV, row));
			csvWriter.append("\n");

			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

	static volatile int level;
	static volatile Transition_ aa = null;

	public static javafx.util.Pair<List<String>, Boolean> getTcAndSaveTP(Automaton_ multigraph, Integer nTC,
			String absolutePath, List<String> li, List<String> lu, String pathIUT, int specState, int specTransition,
			String specPath, String tool, int multigraphState, int multgraphTransition, int alphabet, int experimento,
			int m, String pathCsvTime) throws IOException {

		time = System.nanoTime();
		List<Transition_> toVisit = multigraph.transitionsByIniState(multigraph.getInitialState());
		Map<String, List<String>> map = new HashMap<>();
		// Map<String, String> map = new HashMap<>();
		List<Transition_> toVisit_aux = new ArrayList<>(toVisit);
		boolean nonConf = false;

		// String filename = "C:\\Users\\camil\\Desktop\\teste.txt";
		// FileWriter fw = new FileWriter(filename, true);

		List<String> aux;
		// String aux;
		List<String> words = new ArrayList<>();
		Transition_ current;
		List<State_> states = new ArrayList<>();
		level = 0;
		List<String> toRemove = new ArrayList<>();

		List<Transition_> selfloopFailState = multigraph.transitionsByIniState(multigraph.getFinalStates().get(0));

		states.add(multigraph.getInitialState());
		int totalTC = 0;
		IOLTS tp;
		File file;
		BufferedWriter writer;
		File tpFile;
		javafx.util.Pair<List<List<String>>, Boolean> result;
		int contSelfLoopFinalState = selfloopFailState.size() - 1;

		end: while (!toVisit.isEmpty()) {

			current = toVisit.get(0);
			aa = current;

			// if has path to endState
			if (map.containsKey(current.getIniState().getName())) {
				aux = new ArrayList<>();
				// aux = "";

				// get all path to iniState + current label
				// System.out.println(current.getIniState().getName() + " -
				// "+map.get(current.getIniState().getName()).size());
				for (String e : map.get(current.getIniState().getName())) {// Arrays.asList(map.get(current.getIniState().getName()).split("\\s*,\\s*"))
					// aux.add(e + " -> " + current.getLabel());

					if (current.getEndState().getName().equals(multigraph.getFinalStates().get(0).getName())) {
						// if # test cases was informed not add self loop on every TC
						if (nTC == null || nTC == Integer.MAX_VALUE) {
							// add label of fail self loop on each TC
							for (Transition_ transition_ : selfloopFailState) {
								// aux += e + " -> " + current.getLabel() + " -> " + transition_.getLabel() +
								// ",";
								aux.add(e + " -> " + current.getLabel() + " -> " + transition_.getLabel());
							}
						} else {
							// if has selfloop not coverage
							if (contSelfLoopFinalState >= 0) {
								aux.add(e + " -> " + current.getLabel() + " -> "
										+ selfloopFailState.get(contSelfLoopFinalState).getLabel());
								contSelfLoopFinalState--;
							} else {
								aux.add(e + " -> " + current.getLabel());
							}
						}

					} else {
						// aux += e + " -> " + current.getLabel() + ",";
						aux.add(e + " -> " + current.getLabel());
					}

					// System.out.println();
				}

				// Arrays.asList(map.get(current.getIniState().getName()).split("\\s*,\\s*")).stream()
				// .map(Person::getName)
				// .collect(Collectors.toList());

				// if end state has path, add
				if (map.containsKey(current.getEndState().getName())
						&& !(multigraph.getFinalStates().contains(current.getEndState())
								&& current.getEndState().getName().equals(current.getIniState().getName()))) {
					aux.addAll(map.get(current.getEndState().getName()));

					// aux += String.join(",", map.get(current.getEndState().getName()));

				}

				//
				// if (toVisit.stream().filter(x ->
				// x.getIniState().getName().equals(aa.getIniState().getName()))
				// .collect(Collectors.toList()).size() == 1 &&
				// !S.getFinalStates().contains(aa.getIniState())) {// &&
				// // !S.getFinalStates().contains(aa.getIniState())
				// map.remove(current.getIniState().getName());
				// }

				// if current state is not final state, the words is not a tc
				if (!current.getEndState().getName().equals(multigraph.getFinalStates().get(0).getName())) {
					// map.put(current.getEndState().getName(), aux);
					// map.put(current.getEndState().getName(),
					// Arrays.asList(aux.split("\\s*,\\s*")));
					map.put(current.getEndState().getName(), aux);
				} else {
					// not selfloop of final state
					if (!current.getEndState().getName().equals(current.getIniState().getName())) {
						// if is a test case
						// totalTC += aux.size();
						// // totalTC += (int) Arrays.asList(aux.split("\\s*,\\s*")).size();
						// // fw.write(String.join("\n", Arrays.asList(aux.split("\\s*,\\s*"))));//
						// appends
						// // the string to the
						// // file]
						// fw.write(String.join("\n", aux));// appends the string to the file
						// fw.write("\n");
						// // y += ((String.join("\n", Arrays.asList(aux.split("\\s*,\\s*")))) + "\n");
						// // fw.close();
						// // System.out.print(String.join("\n", aux)+"\n");

						for (String tc : aux) {
							totalTC += 1;

							// save tp
							tp = testPurpose(multigraph, tc, li, lu);
							// tpFile = TestGeneration.saveTP(absolutePath + "\\TPs\\", tp);
							tpFile = saveTP(absolutePath, tp, specState, specTransition, specPath, tool,
									multigraphState, multgraphTransition, alphabet, experimento, m, pathCsvTime);

							// if run TP x IUT
							if (pathIUT != null) {
								result = runIutTp(pathIUT, tc, tpFile);
								saveOnCSVFile(result.getKey(), absolutePath);// "\\runVerdicts.csv"

								words.add(tc);
								// no conformance
								if (result.getValue()) {
									nonConf = result.getValue();
									break end;
								}
							} else {
								words.add(tc);

							}
							if (nTC != null && nTC == totalTC) {
								break end;
							}

						}

					}
				}

			} else {
				// if current state is not final state, the words is not a tc
				if (!current.getEndState().getName().equals(multigraph.getFinalStates().get(0).getName())) {

					map.put(current.getEndState().getName(), Arrays.asList(current.getLabel()));
					// map.put(current.getEndState().getName(), current.getLabel());
				} else {
					// not selfloop of final state
					if (!current.getEndState().getName().equals(current.getIniState().getName())) {
						// if is a test case
						totalTC += 1;
						// fw.write(String.join("\n", Arrays.asList(current.getLabel()))); // file
						// fw.write("\n");
						// save tp
						tp = testPurpose(multigraph, current.getLabel(), li, lu);
						// tpFile = TestGeneration.saveTP(absolutePath + "\\TPs\\", tp);
						tpFile = saveTP(absolutePath, tp, specState, specTransition, specPath, tool, multigraphState,
								multgraphTransition, alphabet, experimento, m, pathCsvTime);

						// if run TP x IUT
						if (pathIUT != null) {
							result = runIutTp(pathIUT, current.getLabel(), tpFile);
							saveOnCSVFile(result.getKey(), absolutePath);// + "\\runVerdicts.csv"
							nonConf = result.getValue();
							// no conformance
							if (result.getValue()) {
								break end;
							}
						} else {
							words.add(current.getLabel());

						}

						if (nTC != null && nTC == totalTC) {
							break end;
						}

						// y += (String.join("\n", Arrays.asList(current.getLabel()))) + "\n";
						// fw.close();
						// System.out.print(String.join("\n", Arrays.asList(current.getLabel()))+"\n");

					}
				}
			}

			toVisit.remove(current);

			// add transition of endState of current transition
			if (!states.contains(current.getEndState())) {
				toVisit.addAll(multigraph.transitionsByIniState(current.getEndState()));
				toVisit_aux.addAll(multigraph.transitionsByIniState(current.getEndState()));
			}

			states.add(current.getEndState());

			// System.out.println(map.size()+ "-"+ map);

			// avoid steackoverflow, remove from map, levels that are not be used/visited
			toVisit.stream().filter(x -> x.getIniState().getName().contains("," + level)
					&& x.getEndState().getName().contains("," + level)).collect(Collectors.toList());
			// toRemove = states.stream().filter(x -> x.getName().contains("," +
			// level)).collect(Collectors.toList());
			if (toVisit.stream()
					.filter(x -> x.getIniState().getName().contains("," + level)
							&& x.getEndState().getName().contains("," + level))
					.collect(Collectors.toList()).size() == 0) {
				// remove from map all states at level
				for (String m_ : map.keySet()) {
					if (m_.contains("," + level)) {

						toRemove.add(m_);
					}
				}

				map.remove(toRemove);
				level++;
				aux = null;
				// fw=null;
				System.gc();
			}

			// System.err.println(map.size()+ "-"+map.keySet() +" - "+
			// current.getEndState());
			// if(map.containsKey(S.getFinalStates().get(0).toString()))
			// System.out.println(map.get(S.getFinalStates().get(0).toString()).size()+"tcs:
			// " + map.get(S.getFinalStates().get(0).toString()));

		}
		// fw.close();

		// System.err.println("Total tc: " + totalTC);

		// words = new ArrayList<>();

		// for (State_ s : S.getFinalStates()) {
		//
		// if (map.containsKey(s.getName()))
		// words.addAll(map.get(s.getName()));
		// }

		return new javafx.util.Pair<List<String>, Boolean>(words, nonConf);
		// return String.join(",", words);
	}

	public static IOLTS testPurpose(Automaton_ multgraph, String testCase, List<String> li, List<String> lu) {
		li = new ArrayList<String>(new HashSet(new ArrayList<String>(li)));
		lu = new ArrayList<String>(new HashSet(new ArrayList<String>(lu)));
		IOLTS tp = new IOLTS();
		li = new ArrayList<>(new HashSet<>(li));
		lu = new ArrayList<>(new HashSet<>(lu));
		tp.setInputs(li);
		tp.setOutputs(lu);

		State_ ini = new State_(multgraph.getInitialState().getName());
		tp.setInitialState(ini);

		State_ pass = new State_("pass");
		tp.addState(ini);
		tp.addState(pass);

		State_ current = ini;

		for (String w : testCase.split(" -> ")) {
			for (State_ s : multgraph.reachedStates(current.getName(), w)) {
				tp.addState(s);
				tp.addTransition(new Transition_(current, w, s));
				current = new State_(s.getName());
			}
		}

		boolean existsLu;
		for (State_ s : tp.getStates().stream().filter(x -> !x.getName().equals("pass") && !x.getName().equals("fail"))
				.collect(Collectors.toList())) {
			existsLu = false;

			for (String l : li) {
				if (!tp.transitionExists(s.getName(), l)) {
					tp.addTransition(new Transition_(s, l, pass));
				}
			}

			for (String l : lu) {
				if (tp.transitionExists(s.getName(), l)) {
					existsLu = true;
				}
			}

			if (existsLu == false) {
				tp.addTransition(new Transition_(s, lu.get(0), pass));
			}
		}

		State_ fail = new State_("fail");
		for (String l : lu) {// li
			tp.addTransition(new Transition_(pass, l, pass));
			tp.addTransition(new Transition_(fail, l, fail));
		}

		// invert inp/out to generate tp
		// List<String> inp = tp.getInputs();
		// tp.setInputs(tp.getOutputs());
		// tp.setOutputs(inp);

		return tp;
	}

	static long time = System.nanoTime();

	public static File saveTP(String tpFolder, IOLTS tp, int specState, int specTransition, String specPath,
			String tool, int multigraphState, int multgraphTransition, int alphabet, int experimento, int m,
			String pathCsvTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-S");

		String tpName;
		// File file = new File(new File(tpFolder, "TPs").getAbsolutePath(), "tp_" +
		// dateFormat.format(new Date()) + "-"
		// + Constants.ALPHABET_[new Random().nextInt(Constants.ALPHABET_.length)]
		// +Constants.ALPHABET_[new Random().nextInt(Constants.ALPHABET_.length)]+
		// ".aut");
		File file = new File(new File(tpFolder, "TPs").getAbsolutePath(), "tp_" + java.util.UUID.randomUUID() + ".aut");
		tpName = file.getAbsolutePath();
		// File file = new File(tpFolder, "tp_" + dateFormat.format(new Date()) +
		// "-"+Constants.ALPHABET_[new
		// Random().nextInt(Constants.ALPHABET_.length)]+".aut");

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(AutGenerator.ioltsToAut(tp));
			writer.close();

			double total_seconds = (System.nanoTime() - time);
			total_seconds = (total_seconds / 1e6);// milisecond
			String unity = "milisecond";

			// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			pathCsvTime += System.getProperty("file.separator") + "tp-gen-unitario.csv";// +dateFormat.format(new
			// Date())+".csv";
			String delimiterCSV = ",";

			ArrayList<String> headerCSV = new ArrayList<String>();
			headerCSV.add("tool");
			headerCSV.add("model");
			headerCSV.add("stateModel");
			headerCSV.add("transitionModel");
			headerCSV.add("multigraphState");
			headerCSV.add("multigraphTransition");
			headerCSV.add("time");
			headerCSV.add("unity");
			headerCSV.add("alphabet");
			headerCSV.add("experiment");
			headerCSV.add("m");
			headerCSV.add("tpPath");

			FileWriter csvWriter;

			file = new File(pathCsvTime);
			if (!file.exists()) {
				file.createNewFile();
			}

			if (new File(pathCsvTime).length() == 0) {
				csvWriter = new FileWriter(pathCsvTime);

				for (String header : headerCSV) {
					csvWriter.append(header);
					csvWriter.append(delimiterCSV);
				}
				csvWriter.append("\n");
			} else {
				csvWriter = new FileWriter(pathCsvTime, true);
			}

			ArrayList<String> row = new ArrayList<String>();

			row.add(tool);
			row.add(specPath);
			row.add(Objects.toString(specState));
			row.add(Objects.toString(specTransition));
			row.add(Objects.toString(multigraphState));
			row.add(Objects.toString(multgraphTransition));
			row.add(String.format("%.2f", total_seconds).replace(",", "."));
			row.add(unity);
			row.add(Objects.toString(alphabet));
			row.add(Objects.toString(experimento));
			row.add(Objects.toString(m));
			row.add(tpName);

			csvWriter.append(String.join(delimiterCSV, row));

			csvWriter.append("\n");
			csvWriter.flush();
			csvWriter.close();

			time = System.nanoTime();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file;
	}

	public static boolean run(String pathTp, boolean oneIut, boolean oneTP, String pathIut, String pathCsv) {
		boolean fault = false;

		if (oneTP) {

			return runAllIutTp(new File(pathTp), oneIut, pathIut, pathCsv);
		} else {
			File tpFolderF = new File(pathTp);
			File[] listOfTpFiles = tpFolderF.listFiles();

			// each tp
			for (File fileTp : listOfTpFiles) {
				if (EverestView.isAutFile(fileTp)) {
					// run( pathTp + "//" + fileTp.getName(), fileTp, oneIut, pathImplementation,
					// pathCsv);
					if (!fault)
						fault = runAllIutTp(fileTp, oneIut, pathIut, pathCsv);
				}
			}

		}

		return fault;

	}

	public static boolean runAllIutTp(File fileTp, boolean oneIut, String pathIut, String pathCsv) {// String pathTp,
																									// File fileTp,
																									// boolean oneIut,
																									// String
																									// pathImplementation,
																									// String pathCsv
		File iutFolderF;
		File[] listOfIutFiles;
		IOLTS tp;
		boolean fault = false;

		javafx.util.Pair<List<List<String>>, Boolean> result;
		Automaton_ tpAutomaton;
		List<String> wordsTp;

		List<List<String>> toSave = new ArrayList<>();
		List<String> wordsTp_aux = new ArrayList<>();
		try {

			tp = ImportAutFile.autToIOLTS(fileTp.getAbsolutePath(), false, new ArrayList<>(), // pathTp
					new ArrayList<>());
			// tpAutomaton = tp.ioltsToAutomaton();
			tpAutomaton = new Automaton_(tp.getStates(), tp.getInitialState(), tp.getAlphabet(), new ArrayList<>(),
					tp.getTransitions());
			tpAutomaton.setFinalStates(new ArrayList<>());
			tpAutomaton.addFinalStates(new State_("fail"));
			wordsTp = model.Graph.getWords(tpAutomaton);
			wordsTp_aux = new ArrayList<>();

			// word selfloop on final states
			for (Transition_ t : tpAutomaton.transitionsByIniState(new State_("fail"))) {
				if (t.getIniState().equals(t.getEndState())) {
					for (String w : wordsTp) {
						wordsTp_aux.add(w + t.getLabel());
					}
				}
			}

			wordsTp.addAll(wordsTp_aux);

			for (String word : wordsTp) {

				// one iut
				if (oneIut) {
					result = runIutTp(pathIut, word, fileTp);// pathTp
					toSave = result.getKey();
					if (!fault)
						fault = result.getValue();

					saveOnCSVFile(toSave, pathCsv);
				} else {
					// iut in batch
					if (!oneIut) {
						iutFolderF = new File(pathIut);
						listOfIutFiles = iutFolderF.listFiles();

						// for each iut
						for (File fileIut : listOfIutFiles) {
							if (EverestView.isAutFile(fileIut)) {
								result = runIutTp(pathIut + "//" + fileIut.getName(), word, fileTp);
								toSave = result.getKey();

								if (!fault)
									fault = result.getValue();
								saveOnCSVFile(toSave, pathCsv);

							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fault;
	}

	public static javafx.util.Pair<List<List<String>>, Boolean> runIutTp(String pathIut, String word, File fileTp) {
		List<List<String>> toSave = new ArrayList<>();
		boolean nonconformance = false;
		try {
			IOLTS iut;
			iut = ImportAutFile.autToIOLTS(pathIut, false, null, null);
			iut.addQuiescentTransitions();

			List<String> partialResult = new ArrayList<>();
			List<List<State_>> statesPath;
			// System.out.println(word);
			Operations.addTransitionToStates(iut);
			statesPath = Operations.statePath(iut, word);
			for (List<State_> statePath : statesPath) {
				partialResult = new ArrayList<>();
				partialResult.add(pathIut);
				partialResult.add(fileTp.getAbsolutePath());
				partialResult.add(word);
				partialResult.add(statePath.toString().replaceAll(Constants.COMMA, " -> "));

				if (statePath.get(statePath.size() - 1).getName().contains(Constants.NO_TRANSITION)) {
					// inconclusive
					partialResult.add(Constants.RUN_VERDICT_INCONCLUSIVE);

				} else {
					// not conform
					partialResult.add(Constants.RUN_VERDICT_NON_CONFORM);
					nonconformance = true;

				}

				toSave.add(partialResult);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new javafx.util.Pair<List<List<String>>, Boolean>(toSave, nonconformance);
	}

	public static void saveOnCSVFile(List<List<String>> toSave, String pathCsv) {

		try {
			// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			pathCsv += System.getProperty("file.separator") + "run-everest-result.csv";// +dateFormat.format(new
																						// Date())+".csv";
			String delimiterCSV = ",";

			ArrayList<String> headerCSV = new ArrayList<String>();
			headerCSV.add("iut file");
			headerCSV.add("tp file");
			headerCSV.add("test case");
			headerCSV.add("state path");
			headerCSV.add("fault");

			FileWriter csvWriter;

			File file = new File(pathCsv);
			if (!file.exists()) {
				file.createNewFile();
			}

			if (new File(pathCsv).length() == 0) {
				csvWriter = new FileWriter(pathCsv);

				for (String header : headerCSV) {
					csvWriter.append(header);
					csvWriter.append(delimiterCSV);
				}
				csvWriter.append("\n");
			} else {
				csvWriter = new FileWriter(pathCsv, true);
			}

			for (List<String> row : toSave) {
				csvWriter.append(String.join(delimiterCSV, row));
			}

			csvWriter.append("\n");
			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

}
