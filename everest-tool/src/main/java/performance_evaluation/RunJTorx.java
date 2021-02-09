package performance_evaluation;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import parser.ImportAutFile;
import performance_evaluation.RunEverest.TimeOut;
import util.Constants;

import org.sikuli.script.*;

import com.android.dx.util.FileUtils;

import model.IOLTS;

public class RunJTorx {

	public static void main(String[] args) {
		try {
			String batchFileJTorx = "C:\\Users\\camil\\Google Drive\\UEL\\jtorx\\jtorx-1.11.2-win\\jtorx.bat";
			String root_img = new File("src/main/java/performance_evaluation/jtorx-img").getCanonicalPath() + "\\";
			List<String> headerCSV = Arrays.asList(new String[] { "tool", "model", "iut", "statesModel", "statesIut",
					"transitionsModel", "transitionsIut", "ntestCases", "conform", "variation", "variationType", "time",
					"unity", "memory", "unit", "pathTSSaved" });
			String numTestCaseToGenerate = Integer.MAX_VALUE + "";// "5";
			String tool = "jtorx-" + numTestCaseToGenerate + "tc";

			// IOCO NOT CONF
			// String aux;
			// String root = "C:\\10-100states\\2pct\\";
			//
			// List<Integer> states = Arrays.asList(10,20,30,40,50,60,70,80,90,100);
			// for (Integer nState : states) {
			// //int nState = 100;// 50,100,150,200,250
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
			// String ioco = "";//ioco-nao-conf
			// String pathCsv = root+ioco+"\\jtorx.csv";
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
			// .submit(new TimeOut(batchFileJTorx, root_img, pathIUT, pathAutSpec,
			// pathSaveTS, headerCSV,
			// pathCsv, stateVariation, numTestCaseToGenerate, tool, nState));
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
			//
			// }

			// IOCO conf submaquina e IOCO not conf com sub maquina
			String aux;
			String root = "C:\\ioco-n-conf\\10inp-2out\\";
			// int nState = 100;// 50,100,150,200,250
			boolean stateVariation = true;// state or percentage
			// List<Integer> tamAlfabeto = Arrays.asList(4, 6, 8, 10, 12, 14, 16, 18, 20);
			List<Integer> tamAlfabeto = Arrays.asList(12);
			String rootPathIUTs, pathAutSpec;
			File folder;
			File[] listOfFiles;
			String pathSaveTS;
			String pathIUT;
			int count = 0;
			String rootPathSaveTS;
			String ioco = "";// ioco-nao-conf
		
			List<Integer> states = Arrays.asList(10);// 50,100
			// List<Integer> states = Arrays.asList(10,50,100);
			
			List<Integer> tamIUTs = Arrays.asList(15,25,35);
			//List<Integer> tamIUTs = Arrays.asList( 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160,170, 180, 190, 200);	
											
//			List<Integer> tamIUTs = Arrays.asList( 110, 120, 130, 140, 150, 160,
//					170, 180, 190, 200);

			
			for (Integer nState : states) {
				String pathCsv = root + nState + "states\\jtorx.csv";

				for (Integer alfabeto : tamAlfabeto) {
					System.out.println("#######################################");
					System.out.println(alfabeto);
					System.out.println("#######################################");

					for (Integer iut : tamIUTs) {
					for (int i = 1; i <= 10; i++) {
						count = 0;
						System.out.println("experimento: " + i);

						rootPathIUTs = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto + "\\iut"+iut+"\\experimento" + i
								+ "\\iut\\";

						pathAutSpec = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto + "\\iut"+iut+"\\experimento" + i
								+ "\\" + nState + "states_spec.aut";
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
										.submit(new TimeOut(batchFileJTorx, root_img, pathIUT, pathAutSpec, pathSaveTS,
												headerCSV, pathCsv, stateVariation, numTestCaseToGenerate, tool,
												nState));

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

			// IOCO CONF -novo
			// String aux;
			// String root = "C:\\";
			// int nState = 200;// 50,100,150,200,250
			// boolean stateVariation = true;// state or percentage
			// List<Integer> tamAlfabeto = Arrays.asList(4, 12, 20);// Arrays.asList(4, 6,
			// 8, 10, 12, 14, 16, 18, 20);
			// String rootPathIUTs, pathAutSpec;
			// File folder;
			// File[] listOfFiles;
			// String pathSaveTS;
			// String pathIUT;
			// int count = 0;
			// String rootPathSaveTS;
			// String ioco = "ioco-conf";// ioco-nao-conf
			// String pathCsv = root + ioco + "\\jtorx.csv";
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
			// rootPathIUTs = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto +
			// "\\experimento" + i
			// + "\\iut\\";
			//
			// pathAutSpec = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto +
			// "\\experimento" + i
			// + "\\" + nState + "states_spec.aut";
			// rootPathSaveTS = root + ioco + "\\" + nState + "states\\alfabeto" + alfabeto
			// + "\\experimento" + i
			// + "\\";
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
			// // each file on experimento folder
			// for (File file : listOfFiles) {
			// if (file.getName().indexOf(".") != -1
			// && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
			// pathIUT = rootPathIUTs + file.getName();
			// aux = "iut\\" + file.getName();
			// pathSaveTS = rootPathSaveTS + "testSuite.csv";
			// count++;
			// Future<String> control = Executors.newSingleThreadExecutor()
			// .submit(new TimeOut(batchFileJTorx, root_img, pathIUT, pathAutSpec,
			// pathSaveTS,
			// headerCSV, pathCsv, stateVariation, numTestCaseToGenerate, tool, nState));
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
			// comandos continuam
			// // mesmo
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
			// }
			// }

			// IOCO CONF TEST
			// int nState = 3000;
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
			// desempenho\\models250-3000states-ioco-conf\\result\\jtorx-everest.csv";
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
			// .submit(new TimeOut(batchFileJTorx, root_img, pathIUT,
			// rootPathAutSpec+nState+"states"+file.getName().replace("iut", "spec"),
			// pathSaveTS, headerCSV,
			// pathCsv, stateVariation, numTestCaseToGenerate, tool,nState));
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

			//// IOCO NOT CONF
			// boolean stateVariation = true;// state or percentage
			// int nState = 250;
			// String rootPathIUTs = "C:\\Users\\camil\\Desktop\\250-3000\\" + nState +
			//// "\\iut\\";
			// String pathAutSpec = "C:\\Users\\camil\\Desktop\\250-3000\\" + nState + "\\"
			//// + nState + "states_spec.aut";
			// String rootPathSaveTS = "C:\\Users\\camil\\Desktop\\250-3000\\" + nState +
			//// "\\result\\maxtc\\";
			// String pathCsv =
			//// "C:\\Users\\camil\\Desktop\\250-3000\\jtorx-everest-maxtc.csv";
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
			// String pathIUT;
			// int count = 0;
			// for (File file : listOfFiles) {
			// if (file.getName().indexOf(".") != -1
			// && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
			// pathIUT = rootPathIUTs + file.getName();
			// pathSaveTS = rootPathSaveTS + count + "_" + file.getName().replace(".aut",
			//// "") + "\\";
			// count++;
			// Future<String> control = Executors.newSingleThreadExecutor()
			// .submit(new TimeOut(batchFileJTorx, root_img, pathIUT, pathAutSpec,
			//// pathSaveTS, headerCSV,
			// pathCsv, stateVariation, numTestCaseToGenerate, tool, nState));
			//
			// try {
			// int limitTime = 1000;
			// control.get(limitTime, TimeUnit.MINUTES);
			// Thread.sleep(500);
			// Files.move(Paths.get(pathIUT), Paths.get(successFolder + file.getName()));
			// } catch (Exception e) {// TimeoutException
			// // mover arquivo para pasta de erro
			// e.printStackTrace();
			// Thread.sleep(500);
			// Files.move(Paths.get(pathIUT), Paths.get(errorFolder + file.getName()));
			// }
			// }
			// }

			// run one test
			// nState = 100;
			//
			// String pathSaveTS =
			// "C:\\Users\\camil\\Desktop\\250-3000\\"+nState+"\\result\\";
			// pathCsv =
			// "C:\\Users\\camil\\Desktop\\250-3000\\"+nState+"\\result\\jtorx.csv";
			//
			// String path = "C:\\Users\\camil\\Desktop\\250-3000\\"+nState+"\\";
			// run( batchFileJTorx, root_img, path+nState+"states_spec.aut",
			// path+"iut\\1pct_iut_0.aut",
			// pathSaveTS, headerCSV, pathCsv, false,numTestCaseToGenerate, tool,nState);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class TimeOut implements Callable<String> {
		String batchFileJTorx, root_img, pathAutSpec, pathAutIUT, pathSaveTS, pathCsv, numTestCaseToGenerate, tool;
		boolean stateVariation;
		int nStates;
		List<String> headerCSV;

		public TimeOut(String batchFileJTorx, String root_img, String pathIUT, String pathSpec, String pathSaveTS,
				List<String> headerCSV, String pathCsv, boolean stateVariation, String numTestCaseToGenerate,
				String tool, int nStates) throws Exception {

			this.batchFileJTorx = batchFileJTorx;
			this.root_img = root_img;
			this.pathAutSpec = pathSpec;
			this.pathAutIUT = pathIUT;
			this.pathSaveTS = pathSaveTS;
			this.headerCSV = headerCSV;
			this.pathCsv = pathCsv;
			this.stateVariation = stateVariation;
			this.numTestCaseToGenerate = numTestCaseToGenerate;
			this.tool = tool;
			this.nStates = nStates;
		}

		@Override
		public String call() throws Exception {
			run(batchFileJTorx, root_img, pathAutSpec, pathAutIUT, pathSaveTS, headerCSV, pathCsv, stateVariation,
					numTestCaseToGenerate, tool, nStates);
			return "";
		}
	}

	static String delimiterCSV = ",";

	public static void run(String batchFileJTorx, String root_img, String pathAutSpec, String pathAutIUT,
			String pathSaveTS, List<String> headerCSV, String pathCsv, boolean stateVariation,
			String numTestCaseToGenerate, String tool, int nStates) throws Exception {

		// numTestCaseToGenerate = "21";

		// run JTorx
		Desktop d = Desktop.getDesktop();

		d.open(new File(batchFileJTorx));
		// wait until open Jtorx
		Thread.sleep(2000);

		// type spec model
		Screen s = new Screen();
		s.type(root_img + "inp-model.PNG", pathAutSpec);

		for (int i = 0; i < 8; i++) {
			s.type(Key.TAB);
		}

		// type iut model
		s.type(pathAutIUT);

		// ?in !out
		s.click(root_img + "cb-interpretation.PNG");
		s.type(Key.DOWN);
		s.type(Key.DOWN);
		s.click();

		// Strace
		s.type(Key.TAB);
		s.type(Key.DOWN);

		// ioco menu click
		s.click(root_img + "item-menu-ioco.PNG");
		Thread.sleep(500);

		// set n test case to generate
		s.type(Key.TAB);
		s.type(Key.RIGHT);
		s.type(Key.BACKSPACE);
		s.type(numTestCaseToGenerate);

		// check button
		s.click(root_img + "btn-check.PNG");

		// time_ini = System.currentTimeMillis();
		double time_end = 0;
		double time_ini = System.nanoTime();

		// try get time mode 1
		while (true) {
			try {
				// System.out.println("wait");
				time_end = System.nanoTime();
				s.find(new Pattern(root_img + "btn-stop3.PNG").similar(0.65f));// "lbl-result.PNG"
				time_end = System.nanoTime();

			} catch (FindFailed e) {
				break;
			}
		}

		double total_seconds = (time_end - time_ini) / 1e6;

		 double total_seconds2;
		 // try get time mode 2
		 // if(total_seconds < 200) {
		 // check button
		 s.click(root_img + "btn-check.PNG");
		 time_ini = System.nanoTime();
		 s.wait(new Pattern(root_img + "btn-check2.PNG").similar(0.75f));
		 time_end = System.nanoTime();
		 total_seconds2 = (time_end - time_ini) / 1e6;
		 // }
		 if (total_seconds2 > total_seconds) {
		 total_seconds = total_seconds2;
		 }

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

		boolean conform = false;
		int count = 0;
		// get veredict by label
		if (s.exists(root_img + "lbl-conform-veredict.PNG") != null) {
			System.err.println("IOCO CONFORM");
			conform = true;
		} else {
			if (s.exists(root_img + "lbl-fail-veredict.PNG") != null) {

				System.err.println("IOCO DOESN'T CONFORM");

				// // save test cases
				// String nameTestCaseAutFile = "";
				// String nameTCFile = "";
				// // create folder to save
				// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
				// if (!Files.exists(Paths.get(pathSaveTS))) {
				// Files.createDirectory(Paths.get(pathSaveTS));
				// }
				//
				// Thread.sleep(700);
				//
				// // first line (testcases)
				// for (int j = 0; j < 4; j++) {
				// s.type(Key.TAB);
				// }
				// s.type(Key.DOWN);
				// s.type(Key.UP);
				// // save test case
				// s.click(root_img + "btn-save.PNG");
				// Thread.sleep(500);
				// nameTestCaseAutFile = pathSaveTS + "1_" + dtf.format(LocalDateTime.now()) +
				// ".aut";
				// Thread.sleep(500);
				// s.type(nameTestCaseAutFile);
				// s.type(Key.ENTER);
				//
				// // second line
				// s.type(Key.TAB);
				// s.type(Key.DOWN);
				// Scanner scanner = null;
				//
				// Thread.sleep(2000);
				//
				// scanner = new Scanner(new File(nameTestCaseAutFile));
				// String previous = scanner.useDelimiter("\\Z").next();
				// scanner.close();
				// String aux = "";
				//
				// // other lines
				// count = 1;
				// while (true) {
				// count++;
				// nameTCFile = count + "_" + dtf.format(LocalDateTime.now()) + ".aut";
				// nameTestCaseAutFile = pathSaveTS + nameTCFile;
				//
				// s.click(root_img + "btn-save.PNG");
				// Thread.sleep(500);
				// s.type(nameTCFile);
				// s.type(Key.ENTER);
				// Thread.sleep(1500);
				// scanner = new Scanner(new File(nameTestCaseAutFile));
				// aux = scanner.useDelimiter("\\Z").next();
				// scanner.close();
				//
				// if (aux.equals(previous)) {
				// Files.delete(Paths.get(nameTestCaseAutFile));
				// count--;
				// break;
				// } else {
				// previous = aux;
				// s.type(Key.TAB);
				// s.type(Key.DOWN);
				// }
				// }

			}
		}

		if (conform) {
			pathSaveTS = "";
		}

		saveOnCSVFile(pathCsv, pathAutSpec, pathAutIUT, conform, total_seconds, "milliseconds", memory, unityMemory,
				stateVariation, headerCSV, count, pathSaveTS, tool);

		// close JTorx
		s.click(root_img + "img-close.PNG");
	}

	public static void saveOnCSVFile(String pathCsv, String pathModel, String pathIUT, boolean conform, double time,
			String unitTime, String memory, String unityMemory, boolean stateVariation, List<String> headerCSV,
			int nTestCase, String pathSaveTS, String tool) {

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

			ArrayList<String> row = new ArrayList<String>();
			row.add(tool);// "jtorx"
			row.add(pathModel);
			row.add(pathIUT);
			row.add(Objects.toString(numStatesModel));
			row.add(Objects.toString(numStatesIut));
			row.add(Objects.toString(numTransitionsModel));
			row.add(Objects.toString(numTransitionsIut));
			row.add(Objects.toString(nTestCase));
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

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

}
