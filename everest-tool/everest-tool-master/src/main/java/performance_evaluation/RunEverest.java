package performance_evaluation;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.sikuli.script.SikuliException;

import com.sikulix.tigervnc.Sikulix;

import model.IOLTS;
import parser.ImportAutFile;
import performance_evaluation.RunJTorx.TimeOut;

public class RunEverest {
	public static void main(String[] args) throws Exception {
		try {
			String root_img = new File("src/main/java/performance_evaluation/everet-img").getCanonicalPath() + "\\";
			String batchFileEverest = "C:\\Users\\camil\\Desktop\\everest.bat";
			List<String> headerCSV = Arrays.asList(new String[] { "tool", "model", "iut", "statesModel", "statesIut",
					"transitionsModel", "transitionsIut", "ntestCases", "conform", "variation", "variationType", "time",
					"unity", "memory", "unit", "pathTSSaved" });

			String numTestCaseToGenerate = Integer.MAX_VALUE + "";// Integer.MAX_VALUE + "";// "5";
			String tool = "everest-" + numTestCaseToGenerate + "tc";

			// IOCO CONF TEST
			// int nState = 100;
			// boolean stateVariation = true;// state or percentage
			// String rootPathIUTs = "C:\\Users\\camil\\Google
			// Drive\\UEL\\svn\\ferramenta\\teste
			// desempenho\\models25-3000states-ioco-perc-dif\\" + nState + "\\iut\\";
			// String rootPathAutSpec = "C:\\Users\\camil\\Google
			// Drive\\UEL\\svn\\ferramenta\\teste
			// desempenho\\models250-3000states-ioco-conf\\aut\\spec\\";
			// String rootPathSaveTS = "C:\\Users\\camil\\Google
			// Drive\\UEL\\svn\\ferramenta\\teste
			// desempenho\\models250-3000states-ioco-conf\\result\\";
			// String pathCsv = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
			// desempenho\\models250-3000states-ioco-conf\\result\\novo.csv";//jtorx-everest
			//
			// String errorFolder = rootPathIUTs + "\\error\\";
			// Path errorPath = Paths.get(errorFolder);
			// String successFolder = rootPathIUTs + "\\success\\";
			// Path successPath = Paths.get(successFolder);
			// if (!Files.exists(errorPath)) {
			// Files.createDirectory(errorPath);
			// }
			// if (!Files.exists(successPath)) {
			// Files.createDirectory(successPath);
			// }
			//
			// File folder = new File(rootPathIUTs);
			// File[] listOfFiles = folder.listFiles();
			// String pathSaveTS;
			//
			//
			// // System.out.println(Arrays.asList(listOfFiles));
			// String pathIUT;
			// int count = 0;
			// for (File file : listOfFiles) {
			// if (file.getName().indexOf(".") != -1
			// && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
			// pathIUT = rootPathIUTs + file.getName();
			// pathSaveTS = rootPathSaveTS + "testSuite.csv";
			// count++;
			// Future<String> control = Executors.newSingleThreadExecutor()
			// .submit(new TimeOut(batchFileEverest, root_img, pathIUT,
			// rootPathAutSpec+nState+"states"+file.getName().replace("iut", "spec"),
			// pathSaveTS, headerCSV,
			// pathCsv, stateVariation, numTestCaseToGenerate, tool));
			//
			// try {
			// int limitTime = 30;// 40
			// control.get(limitTime, TimeUnit.MINUTES);
			//
			// Thread.sleep(500);
			// //Files.move(Paths.get(pathIUT), Paths.get(successFolder + file.getName()));
			// } catch (Exception e) {// TimeoutException
			//
			// Runtime.getRuntime().exec("TASKKILL /F /IM java.exe");
			// Thread.sleep(500);
			// // mover arquivo para pasta de erro
			// //Files.move(Paths.get(pathIUT), Paths.get(errorFolder + file.getName()));
			//
			// control.cancel(true);
			//
			// System.exit(0);// arranjar um jeito de parar a execução do sikuli (os
			// comandos continuam mesmo
			// // depois da exception)
			//
			// e.printStackTrace();
			//
			// }
			//
			// }
			//
			// }

			// IOCO NOT CONF
			// String root = "C:\\10-100states\\2pct\\";
			// List<Integer> states = Arrays.asList(10,20,30,40,50,60,70,80,90,100);
			// for (Integer nState : states) {
			//
			// boolean stateVariation = true;// state or percentage
			// //List<Integer> tamAlfabeto = Arrays.asList(4, 6, 8, 10, 12, 14, 16, 18, 20);
			// List<Integer> tamAlfabeto = Arrays.asList(10);
			// String rootPathIUTs, pathAutSpec;
			// File folder;
			// File[] listOfFiles;
			// String pathSaveTS;
			// String pathIUT;
			// int count = 0;
			// String rootPathSaveTS;
			// String ioco = "";//"ioco-nao-conf";//ioco-nao-conf
			// String pathCsv = root+ioco+"\\everest.csv";
			//
			// for (Integer alfabeto : tamAlfabeto) {
			// System.out.println("#######################################");
			// System.out.println(alfabeto);
			// System.out.println("#######################################");
			//
			// for (int i = 1; i <= 10; i++) {
			// count = 0;
			// System.out.println("experimento: " + i);
			//
			// rootPathIUTs = root+ioco+"\\"
			// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\iut\\";
			//
			//
			// pathAutSpec = root+ioco+"\\"
			// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\" +
			// nState
			// + "states_spec.aut";
			//// rootPathSaveTS = root+ioco+"\\"
			//// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\";
			// rootPathSaveTS = root+ioco+"\\"
			// + nState + "states\\";
			// String errorFolder = rootPathIUTs + "\\error\\";
			// Path errorPath = Paths.get(errorFolder);
			// String successFolder = rootPathIUTs + "\\success\\";
			// Path successPath = Paths.get(successFolder);
			// if (!Files.exists(errorPath)) {
			// Files.createDirectory(errorPath);
			// }
			// if (!Files.exists(successPath)) {
			// Files.createDirectory(successPath);
			// }
			//
			// folder = new File(rootPathIUTs);
			// listOfFiles = folder.listFiles();
			//
			// //each file on experimento folder
			// for (File file : listOfFiles) {
			// if (file.getName().indexOf(".") != -1
			// && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
			// pathIUT = rootPathIUTs + file.getName();
			// aux = "iut\\"+file.getName();
			// pathSaveTS = rootPathSaveTS + "testSuite.csv";
			// count++;
			// Future<String> control = Executors.newSingleThreadExecutor()
			// .submit(new TimeOut(batchFileEverest, root_img, pathIUT, pathAutSpec,
			// pathSaveTS, headerCSV,
			// pathCsv, stateVariation, numTestCaseToGenerate, tool));
			//
			// try {
			// int limitTime = 30;// 40
			// control.get(limitTime, TimeUnit.MINUTES);
			//
			// Thread.sleep(500);
			// Files.move(Paths.get(pathIUT), Paths.get(successFolder + file.getName()));
			// } catch (Exception e) {// TimeoutException
			//
			// Runtime.getRuntime().exec("TASKKILL /F /IM java.exe");
			// Thread.sleep(500);
			// // mover arquivo para pasta de erro
			// Files.move(Paths.get(pathIUT), Paths.get(errorFolder + file.getName()));
			//
			// control.cancel(true);
			//
			// System.exit(0);// arranjar um jeito de parar a execução do sikuli (os
			// comandos continuam mesmo
			// // depois da exception)
			//
			// e.printStackTrace();
			//
			// }
			//
			// }
			//
			// }
			//
			//
			// }
			// }
			// }

			
			// IOCO Conf submaquina e IOCO not conf com sub maquina
			String root = "C:\\ioco-n-conf\\10inp-2out\\";//"C:\\ioco-n-conf\\2inp-10out\\";
			List<Integer> states = Arrays.asList(10);// 10,20,30
			// List<Integer> states = Arrays.asList(10,50,100);
			List<Integer> tamIUTs = Arrays.asList(15,25,35);//10,20,30,40,50);
//			List<Integer> tamIUTs = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160,
//					170, 180, 190, 200);
			
//			List<Integer> tamIUTs = Arrays.asList( 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160,170, 180, 190, 200);	
			// List<Integer> tamAlfabeto = Arrays.asList(4, 6, 8, 10, 12, 14, 16, 18, 20);
			List<Integer> tamAlfabeto = Arrays.asList(12);
			String rootPathIUTs, pathAutSpec;
			File folder;
			File[] listOfFiles;
			String pathSaveTS;
			String pathIUT;
			int count = 0;
			String rootPathSaveTS;
			String ioco = "";// "ioco-nao-conf";//ioco-nao-conf
			boolean stateVariation = true;// state or percentage

			for (Integer nState : states) {
				String pathCsv = root + nState + "states\\everest.csv";

				for (Integer alfabeto : tamAlfabeto) {
					System.out.println("#######################################");
					System.out.println(alfabeto);
					System.out.println("#######################################");

					for (Integer iut : tamIUTs) {

						for (int i = 1; i <= 10; i++) {
							count = 0;
							System.out.println("experimento: " + i);

							rootPathIUTs = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto + "\\iut" + iut
									+ "\\experimento" + i + "\\iut\\";

							pathAutSpec = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto + "\\iut" + iut
									+ "\\experimento" + i + "\\" + nState + "states_spec.aut";
							// rootPathSaveTS = root+ioco+"\\"
							// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\";
							rootPathSaveTS = root + ioco + "\\" + nState + "states\\";
							String errorFolder = rootPathIUTs + "\\error\\";
							Path errorPath = Paths.get(errorFolder);
							String successFolder = rootPathIUTs + "\\success\\";
							Path successPath = Paths.get(successFolder);
							if (!Files.exists(errorPath)) {
								Files.createDirectory(errorPath);
							}
							if (!Files.exists(successPath)) {
								Files.createDirectory(successPath);
							}

							folder = new File(rootPathIUTs);
							listOfFiles = folder.listFiles();

							// each file on experimento folder
							for (File file : listOfFiles) {
								if (file.getName().indexOf(".") != -1
										&& file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
									pathIUT = rootPathIUTs + file.getName();
									aux = "iut\\" + file.getName();
									pathSaveTS = rootPathSaveTS + "testSuite.csv";
									count++;
									Future<String> control = Executors.newSingleThreadExecutor()
											.submit(new TimeOut(batchFileEverest, root_img, pathIUT, pathAutSpec,
													pathSaveTS, headerCSV, pathCsv, stateVariation,
													numTestCaseToGenerate, tool));

									try {
										int limitTime = 30;// 40
										control.get(limitTime, TimeUnit.MINUTES);

										Thread.sleep(500);
										Files.move(Paths.get(pathIUT), Paths.get(successFolder + file.getName()));
									} catch (Exception e) {// TimeoutException

										Runtime.getRuntime().exec("TASKKILL /F /IM java.exe");
										Thread.sleep(500);
										// mover arquivo para pasta de erro
										Files.move(Paths.get(pathIUT), Paths.get(errorFolder + file.getName()));

										control.cancel(true);

										System.exit(0);// arranjar um jeito de parar a execução do sikuli (os comandos
														// continuam mesmo
														// depois da exception)

										e.printStackTrace();

									}

								}

							}

						}
					}
				}
			}

			// IOCO CONF same models spec and IUT
			// String root = "C:\\";
			// int nState = 250;// 50,100,150,200,250
			// boolean stateVariation = true;// state or percentage
			// List<Integer> tamAlfabeto = Arrays.asList(4, 6, 8, 10, 12, 14, 16, 18, 20);
			// String rootPathIUTs, pathAutSpec;
			// File folder;
			// File[] listOfFiles;
			// String pathSaveTS;
			// String pathIUT;
			// int count = 0;
			// String rootPathSaveTS;
			// String ioco = "ioco-conf";//ioco-nao-conf
			// String pathCsv = root+ioco+"\\everest.csv";
			//
			// for (Integer alfabeto : tamAlfabeto) {
			// System.out.println("#######################################");
			// System.out.println(alfabeto);
			// System.out.println("#######################################");
			//
			// for (int i = 1; i < 6; i++) {
			// count = 0;
			// System.out.println("experimento: " + i);
			//
			// rootPathIUTs = root+ioco+"\\"
			// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\iut\\";
			//
			//
			// pathAutSpec = root+ioco+"\\"
			// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\" +
			// nState
			// + "states_spec.aut";
			// rootPathSaveTS = root+ioco+"\\"
			// + nState + "states\\alfabeto" + alfabeto + "\\experimento" + i + "\\";
			// String errorFolder = rootPathIUTs + "\\error\\";
			// Path errorPath = Paths.get(errorFolder);
			// String successFolder = rootPathIUTs + "\\success\\";
			// Path successPath = Paths.get(successFolder);
			// if (!Files.exists(errorPath)) {
			// Files.createDirectory(errorPath);
			// }
			// if (!Files.exists(successPath)) {
			// Files.createDirectory(successPath);
			// }
			//
			// folder = new File(rootPathIUTs);
			// listOfFiles = folder.listFiles();
			//
			// //each file on experimento folder
			// for (File file : listOfFiles) {
			// if (file.getName().indexOf(".") != -1
			// && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
			// pathIUT = rootPathIUTs + file.getName();
			// aux = "iut\\"+file.getName();
			// pathSaveTS = rootPathSaveTS + "testSuite.csv";
			// count++;
			// Future<String> control = Executors.newSingleThreadExecutor()
			// .submit(new TimeOut(batchFileEverest, root_img, pathIUT, pathAutSpec,
			// pathSaveTS, headerCSV,
			// pathCsv, stateVariation, numTestCaseToGenerate, tool));
			//
			// try {
			// int limitTime = 30;// 40
			// control.get(limitTime, TimeUnit.MINUTES);
			//
			// Thread.sleep(500);
			// Files.move(Paths.get(pathIUT), Paths.get(successFolder + file.getName()));
			// } catch (Exception e) {// TimeoutException
			//
			// Runtime.getRuntime().exec("TASKKILL /F /IM java.exe");
			// Thread.sleep(500);
			// // mover arquivo para pasta de erro
			// Files.move(Paths.get(pathIUT), Paths.get(errorFolder + file.getName()));
			//
			// control.cancel(true);
			//
			// System.exit(0);// arranjar um jeito de parar a execução do sikuli (os
			// comandos continuam mesmo
			// // depois da exception)
			//
			// e.printStackTrace();
			//
			// }
			//
			// }
			//
			// }
			//
			//
			// }
			// }

			// //run one test
			// String pathSaveTS = "C:\\Users\\camil\\Desktop\\25-100\\100\\result\\ts.csv";
			// String pathCsv =
			// "C:\\Users\\camil\\Desktop\\25-100\\100\\result\\everest.csv";
			//
			// String path = "C:\\Users\\camil\\Desktop\\25-100\\100\\";
			// run( batchFileEverest, root_img, path+"100states_spec.aut",
			// path+"iut\\1pct_iut_0.aut",
			// pathSaveTS, headerCSV, pathCsv, true, numTestCaseToGenerate,tool);
		} catch (Exception e) {
			Runtime.getRuntime().exec("TASKKILL /F /IM java.exe");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static String aux = "";

	public static class TimeOut implements Callable<String> {
		String batchFileEverest, root_img, pathAutSpec, pathAutIUT, pathSaveTS, pathCsv, tool, numTestCaseToGenerate;
		boolean stateVariation;
		List<String> headerCSV;

		public TimeOut(String batchFileEverest, String root_img, String pathIUT, String pathSpec, String pathSaveTS,
				List<String> headerCSV, String pathCsv, boolean stateVariation, String numTestCaseToGenerate,
				String tool) throws Exception {

			this.batchFileEverest = batchFileEverest;
			this.root_img = root_img;
			this.pathAutSpec = pathSpec;
			this.pathAutIUT = pathIUT;
			this.pathSaveTS = pathSaveTS;
			this.headerCSV = headerCSV;
			this.pathCsv = pathCsv;
			this.stateVariation = stateVariation;
			this.tool = tool;
			this.numTestCaseToGenerate = numTestCaseToGenerate;
		}

		@Override
		public String call() throws Exception {
			run(batchFileEverest, root_img, pathAutSpec, pathAutIUT, pathSaveTS, headerCSV, pathCsv, stateVariation,
					numTestCaseToGenerate, tool);
			return "";
		}

	}

	static Screen s = new Screen();

	public static void run(String batchFileEverest, String root_img, String pathAutSpec, String pathAutIUT,
			String pathSaveTS, List<String> headerCSV, String pathCsv, boolean stateVariation,
			String numTestCaseToGenerate, String tool) throws Exception {

		// open jar
		Desktop d = Desktop.getDesktop();
		d.open(new File(batchFileEverest));

		// wait for open
		Thread.sleep(2500);// 2500

		// type spec model

		s.type(root_img + "inp-model.PNG", pathAutSpec);
		s.type(Key.ENTER);

		// type iut model
		s.type(root_img + "inp-iut.PNG", aux);// s.type(root_img + "inp-iut.PNG", pathAutIUT) ***********
		s.type(Key.ENTER);

		// model type (IOLTS)
		s.click(root_img + "cb-modelType.PNG");
		s.type(Key.DOWN);
		s.type(Key.ENTER);

		// label (?in !out)
		s.click(root_img + "cb-label.PNG");
		s.type(Key.DOWN);
		s.type(Key.ENTER);

		// menu ioco
		s.click(root_img + "item-menu-ioco.PNG");

		// wait until open ioco view
		while (true) {
			try {
				System.currentTimeMillis();
				s.find(new Pattern(root_img + "img-processing.PNG").similar(1.0f));
			} catch (FindFailed e) {
				break;
			}
		}

		// set n test case to generate
		s.type(Key.TAB);
		s.type(Key.RIGHT);
		s.type(Key.BACKSPACE);
		s.type(numTestCaseToGenerate);

		
		// verify ioco
		s.click(root_img + "btn-verify.PNG");

		double time_ini, time_end = 0, total_seconds, time2 = 0;
		time_ini = System.nanoTime();

		// wait until finish verification
		while (true) {
			time_end = System.nanoTime();
			try {
				s.find(new Pattern(root_img + "lbl-verdict.PNG").similar(0.75f));
				 //s.find(new Pattern(root_img + "img-processing.PNG").similar(0.5f));//
				time_end = System.nanoTime();
				//System.out.println("while");

			} catch (FindFailed e) {
				break;
			}
		}

		total_seconds = (time_end - time_ini);
		total_seconds = total_seconds / 1e6;
		System.err.println("FINISHED: " + total_seconds + " milliseconds");

		// get memory consumption (cmd)
		String s_ = "";
		Process p = Runtime.getRuntime().exec("TASKLIST /FI \"IMAGENAME eq java.exe\"");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String memory = "", unityMemory = "";
		while ((s_ = stdInput.readLine()) != null) {
			s_ = s_.replaceAll("\\s{2,}", " ").trim();
			String array[] = s_.split(" ");
			if (array.length == 6 && array[0].equals("java.exe")) {
				System.err.println("memory: " + array[4] + " measure: " + array[5]);
				memory = array[4];
				unityMemory = array[5];
			}
		}

		String testSuite = "";
		boolean conform = false;
		// get veredict by label
		if (s.exists(root_img + "lbl-fail-veredict1.PNG") != null) {
			System.err.println("IOCO DOESN'T CONFORM");
			// get test suite
			s.type(Key.TAB);
			s.type(Key.TAB);
			s.type("a", KeyModifier.CTRL);
			s.type("c", KeyModifier.CTRL);
			// s.find(root_img + "btn-verify.PNG");
			Thread.sleep(500);// 1000

			try {
				testSuite = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (s.exists(root_img + "lbl-conform-veredict.PNG") != null) {
				System.err.println("IOCO CONFORM");
				conform = true;
			}
		}

		saveOnCSVFile(pathCsv, pathAutSpec, pathAutIUT, conform, total_seconds, "milliseconds", memory, unityMemory,
				stateVariation, headerCSV, pathSaveTS, testSuite, tool);

		// close everest
		s.click(root_img + "img-close.PNG");
		Runtime.getRuntime().exec("TASKKILL /F /IM java.exe");
	}

	static String delimiterCSV = ",";

	public static void saveOnCSVFile(String pathCsv, String pathModel, String pathIUT, boolean conform, double time,
			String unitTime, String memory, String unityMemory, boolean stateVariation, List<String> headerCSV,
			String pathSaveTS, String testSuite, String tool) {

		try {
			String variationType = "";
			String variation = "";
			// String testCasesCSV = pathCsv.substring(0, pathCsv.lastIndexOf("\\")) +
			// "\\testCases.csv";
			int iniSubstring = pathIUT.lastIndexOf("\\") + 1;
			int endSubstring;

			int numStatesIut, numStatesModel, numTransitionsIut, numTransitionsModel;
			IOLTS iolts_spec = ImportAutFile.autToIOLTS(pathModel, false, null, null);
			numStatesModel = iolts_spec.getStates().size();
			numTransitionsModel = iolts_spec.getTransitions().size();

			IOLTS iolts_iut = ImportAutFile.autToIOLTS(pathIUT, false, null, null);
			numStatesIut = iolts_iut.getStates().size();
			numTransitionsIut = iolts_iut.getTransitions().size();

			if (stateVariation) {
				endSubstring = pathIUT.indexOf("states_iut");
				variationType = "numStates";
				if (endSubstring < 0) {
					endSubstring = pathIUT.indexOf("pct_iut");
					variationType = "percentage";
				}
				if (endSubstring > 0) {
					variation = pathIUT.substring(iniSubstring, endSubstring);
				} else {
					variationType = "";
					variation = "";
				}

			}

			List<String> testCases = new ArrayList<>();
			if (!testSuite.isEmpty()) {
				String[] lines = testSuite.split("\n\n");

				for (String s : lines) {
					if (s.contains("Test case:")) {
						testCases.add(s.replace("Test case: ", "").replaceAll("\n", "").replaceAll("#", ""));
					}
				}
			}

			ArrayList<String> row = new ArrayList<String>();
			row.add(tool);// "everest"
			row.add(pathModel);
			row.add(pathIUT);
			row.add(Objects.toString(numStatesModel));
			row.add(Objects.toString(numStatesIut));
			row.add(Objects.toString(numTransitionsModel));
			row.add(Objects.toString(numTransitionsIut));
			row.add(Objects.toString(testCases.size()));
			row.add(Objects.toString(conform));
			row.add(variation);
			row.add(variationType);
			row.add(String.format("%.2f", time).replace(",", "."));
			row.add(unitTime);
			row.add(Objects.toString(memory));
			row.add(unityMemory);
			row.add(pathSaveTS);

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

			file = new File(pathSaveTS);
			if (!file.exists()) {
				file.createNewFile();
			}

			// TEST CASE CSV
			if (new File(pathSaveTS).length() == 0) {
				csvWriter = new FileWriter(pathSaveTS);
				csvWriter.append("model");
				csvWriter.append(delimiterCSV);
				csvWriter.append("iut");
				csvWriter.append(delimiterCSV);
				csvWriter.append("testCases");
				csvWriter.append(delimiterCSV);
				csvWriter.append("path");
				csvWriter.append("\n");
			} else {
				csvWriter = new FileWriter(pathSaveTS, true);
			}

			row = new ArrayList<String>() {
				{
					add(pathModel);
					add(pathIUT);
					add(Objects.toString(testCases));
					add(Objects.toString(testSuite));
				}
			};

			csvWriter.append(String.join(delimiterCSV, row));
			csvWriter.append("\n");

			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

}
