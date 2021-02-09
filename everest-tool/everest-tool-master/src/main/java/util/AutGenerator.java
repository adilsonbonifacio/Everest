package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.server.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.net.io.SocketOutputStream;

import algorithm.Operations;
import model.IOLTS;
import model.State_;
import model.Transition_;
import parser.ImportAutFile;

public class AutGenerator {
	public static void main(String[] args) throws Exception {
		// GENERATE <ONE> RANDOM MODEL
		// // int nState = 10;
		// List<Integer> nStates = Arrays.asList(55);
		//
		// List<List<Integer>> iutStates =
		// Arrays.asList(Arrays.asList(55,65,75,85,95,105));
		// List<Integer> iutState;
		//
		// List<String> labels = new ArrayList<>();
		// // List<Integer> tamAlfabeto = Arrays.asList(4, 6, 8, 10, 12, 14, 16, 18,20);
		//
		// int cont = 0;
		// int inp = 0;
		// int out = 0;
		// /**
		// * inp/out (2,5) (3,4) (4,3) (5,2)
		// */
		// labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "!x",
		// "!y","!z","!w","!k");;
		// List<Integer> tamAlfabeto = Arrays.asList(labels.size());
		//
		// for (String l : labels) {
		// if (l.charAt(0) == '?') {
		// inp++;
		// } else {
		// if (l.charAt(0) == '!') {
		// out++;
		// }
		// }
		// }
		//
		// for (int nState : nStates) {
		// iutState = iutStates.get(cont);
		//
		// for (int nStateIUT : iutState) {
		// for (Integer alfabeto : tamAlfabeto) {
		// for (int i = 1; i <= 10; i++) {
		// // if (alfabeto == 4) {
		// // labels = Arrays.asList("?a", "?b", "!x", "!y");// 4
		// // }
		// //
		// // if (alfabeto == 6) {
		// // labels = Arrays.asList("?a", "?b", "?c", "!x", "!y", "!z");
		// // }
		// //
		// // if (alfabeto == 8) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "!x", "!y", "!z", "!w");
		// // }
		// //
		// // if (alfabeto == 10) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "!x", "!y","!z","!w",
		// // "!k");
		// // }
		// //
		// // if (alfabeto == 12) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "?f", "!x","!y","!z",
		// // "!w", "!k", "!l");
		// // }
		// //
		// // if (alfabeto == 14) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "?f", "?g","!x","!y",
		// // "!z", "!w", "!k", "!l",
		// // "!m");
		// // }
		// //
		// // if (alfabeto == 16) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "?f", "?g","?h","!x",
		// // "!y", "!z", "!w", "!k",
		// // "!l", "!m", "!n");
		// // }
		// //
		// // if (alfabeto == 18) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "?f", "?g","?h","?i",
		// // "!x", "!y", "!z", "!w",
		// // "!k", "!l", "!m", "!n", "!o");
		// // }
		// //
		// // if (alfabeto == 20) {
		// // labels = Arrays.asList("?a", "?b", "?c", "?d", "?e", "?f", "?g","?h","?i",
		// // "?j", "!x", "!y", "!z",
		// // "!w", "!k", "!l", "!m", "!n", "!o", "!p");
		// // }
		//
		// boolean inputEnabled = true;
		// String tag = "g";
		// String path = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\teste-geração\\1pct\\"
		// + nState + "states\\alfabeto" + alfabeto + "\\iut"
		// + nStateIUT + "\\experimento" + i + "\\";
		// generate(nState, labels, inputEnabled, tag, path, nState + "states_spec",
		// System.currentTimeMillis());
		// }
		// }
		// }
		// cont++;
		// }

		// ************************************************
		// GENERATE <ONE> - PERCENTAGE MODEL
		// generateByPercentage("C:\\Users\\camil\\Desktop\\10-100states\\ioco-conf\\10states\\alfabeto10\\experimento1\\10states_spec.aut",
		// "C:\\Users\\camil\\Desktop\\10-100states\\ioco-conf\\10states\\alfabeto10\\experimento1\\iut\\",
		// "impl10",100,"g",System.currentTimeMillis(), true);

		// GENERATE <IN LOTE> - NUM State
		// int totalModels = 50;// 500;
		// int constDivision = 5;
		// int minStates = 1950;
		// int maxStates = 2050;
		// boolean inputEnabled = false;
		// String tag = "g";
		// String rootPath = "C:\\Users\\camil\\Desktop\\models\\";
		// String iutAutPath = "C:\\Users\\camil\\Desktop\\Nova pasta
		// (2)\\versao3-iut30-specPercentage\\iut30states.aut";
		// IOLTS ioltsModel = ImportAutFile.autToIOLTS(iutAutPath, false, null, null);
		// List<String> labels = new ArrayList<>();
		// for (String l : ioltsModel.getInputs()) {
		// labels.add(Constants.INPUT_TAG + l);
		// }
		// for (String l : ioltsModel.getOutputs()) {
		// labels.add(Constants.OUTPUT_TAG + l);
		// }
		// generateAutInLot_NumStates(totalModels, constDivision, minStates, maxStates,
		// inputEnabled, tag, rootPath,
		// labels);

		// **************************

		// GENERATE <IN LOTE> - PERCENTAGE
		// int percentage = 4;
		//
		// List<String> nStates = Arrays.asList("35"); //*****
		// // String nState = "50";// 10,50,100
		// long seed = System.currentTimeMillis();
		// boolean inputEnabled = true;
		// String rootPathIUTs, rootSpec;
		// int qtdadeModelos = 10;
		// String tag = "g";
		//
		// List<Integer> tamAlfabeto = Arrays.asList(10);
		// List<List<Integer>> tamIUTs = Arrays.asList(Arrays.asList(35,45,55));//
		// ****** Arrays.asList(5,15,25,35,45,55)
		//
		// int cont = 0;
		// List<Integer> tamIUT;
		//
		// for (String nState : nStates) {
		// for (Integer alfabeto : tamAlfabeto) {
		// System.out.println("#######################################");
		// System.out.println(alfabeto);
		// System.out.println("#######################################");
		//
		// tamIUT = tamIUTs.get(cont);
		//
		// for (Integer tIut : tamIUT) {
		// System.out.println(">> IUT: " + tIut);
		// for (int i = 1; i <= 10; i++) {
		// System.out.println("experimento: " + i);
		//
		//
		// rootPathIUTs = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\teste-geração\\"+percentage+"pct\\"
		// + "\\" + nState + "states\\alfabeto" + alfabeto + "\\iut" + tIut +
		// "\\experimento" + i
		// + "\\";
		// rootSpec = "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\teste-geração\\"+percentage+"pct\\"
		// + "\\" + nState + "states\\alfabeto" + alfabeto + "\\iut" + tIut +
		// "\\experimento" + i
		// + "\\" + nState + "states_spec.aut";
		//
		// for (int j = 0; j < qtdadeModelos; j++) {
		// // System.out.println(">>" + j);
		//
		// generateByPercentage(rootSpec, rootPathIUTs, (int) percentage + "pct_iut_" +
		// j, percentage,
		// tag, System.currentTimeMillis(), inputEnabled);
		// }
		//
		// }
		// }
		// }
		//
		// cont++;
		// }

		// **************************

		// geração de modelos ioco conformes, definindo numero de estados da iut
		// // int nStateSpec = 10;// 10,50,100
		// List<Integer> nStateSpecs = Arrays.asList(10);
		//
		// int alfabeto = 12;
		//
		// List<List<Integer>> nStateIUTs = Arrays.asList(Arrays.asList(15, 25, 35));
		// int nExperimentos = 10;
		// int iutPorExperimento = 10;
		// List<Integer> nStateIUT;
		// boolean inputEnabled = true;
		// int cont = 0;
		//
		// for (int nStateSpec : nStateSpecs) {
		//
		// nStateIUT = nStateIUTs.get(cont);
		// for (Integer k : nStateIUT) {
		// for (int j = 1; j <= nExperimentos; j++) {
		// for (int i = 0; i < iutPorExperimento; i++) {
		//
		// generateByNumStatesIocoConf(
		// "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\10-50states\\i-o\\ioco-conf\\2inp-10out\\"
		// + nStateSpec + "states\\alfabeto" + alfabeto + "\\iut" + k + "\\experimento"
		// + j
		// + "\\" + nStateSpec + "states_spec.aut",
		// "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\10-50states\\i-o\\ioco-conf\\2inp-10out\\"
		// + nStateSpec + "states\\alfabeto" + alfabeto + "\\iut" + k + "\\experimento"
		// + j
		// + "\\",
		// k + "states_iut_" + i, k, "g", System.currentTimeMillis(), inputEnabled);
		//
		// }
		// }
		// }
		//
		// cont++;
		// }

		// **************************************

		// alterando modelos da iut adicionando estados, submodelos + estados,
		// alterar o mesmo arquivo da iut
		int percentage = 4;

		List<Integer> nStateSpecs = Arrays.asList(35);
		int alfabeto = 10;
		List<List<Integer>> nStateIUTs = Arrays.asList(Arrays.asList(45, 55));// 15,25,35,45,55

		int nExperimentos = 10;
		int iutPorExperimento = 10;
		boolean inputEnabled = true;
		int cont = 0;

		for (Integer nStateSpec : nStateSpecs) {
			System.out.println(">>>>>>>>>>>>> spec: " + nStateSpec);
			List<Integer> nStateIUT = nStateIUTs.get(cont);
			for (Integer k : nStateIUT) {
				System.out.println(">>>>>>>>>>>>> iut: " + k);
				for (int j = 1; j <= nExperimentos; j++) {
					System.out.println(">>>> experimento: " + j);
					for (int i = 0; i < iutPorExperimento; i++) {
						// System.out.println("> modelo: " + i);
						generateByNumStatesIocoConf(
								"C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste desempenho\\teste-geração\\"
										+ percentage + "pct\\" + nStateSpec + "states\\alfabeto" + alfabeto + "\\iut"
										+ k + "\\experimento" + j + "\\iut\\" + percentage + "pct_iut_" + i + ".aut",
								"C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste desempenho\\teste-geração\\"
										+ percentage + "pct\\" + nStateSpec + "states\\alfabeto" + alfabeto + "\\iut"
										+ k + "\\experimento" + j,
								percentage + "pct_iut_" + i + "", k, "g", System.currentTimeMillis(), inputEnabled);
					}
				}
			}
			cont++;
		}

		// *******************

		// // geração de modelos ioco conformes, definindo numero de estados da iut
		// int nStateSpec = 100;// 10,50,100
		// int alfabeto = 10;
		// // List<Integer> nStateIUT = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90,
		// // 100, 110, 120, 130, 140, 150, 160, 170,
		// // 180, 190, 200);
		// // List<Integer> nStateIUT = Arrays.asList(60, 70, 80, 90, 100, 110, 120,
		// 130,
		// // 140, 150, 160, 170,
		// // 180, 190, 200);
		//
		// List<Integer> nStateIUT = Arrays.asList(110, 120, 130, 140, 150, 160, 170,
		// 180, 190, 200);
		// int nExperimentos = 10;
		// int iutPorExperimento = 10;
		//
		// boolean inputEnabled = true;
		//
		// for (Integer k : nStateIUT) {
		// for (int j = 1; j <= nExperimentos; j++) {
		// for (int i = 0; i < iutPorExperimento; i++) {
		//
		// generateByNumStatesIocoConf(
		// "C:\\Users\\camil\\Desktop\\10-100states\\ioco-conf\\" + nStateSpec +
		// "states\\alfabeto"
		// + alfabeto + "\\iut" + k + "\\experimento" + j + "\\" + nStateSpec
		// + "states_spec.aut",
		// "C:\\Users\\camil\\Desktop\\10-100states\\ioco-conf\\" + nStateSpec +
		// "states\\alfabeto"
		// + alfabeto + "\\iut" + k + "\\experimento" + j + "\\iut\\",
		// k + "states_iut_" + i, k, "g", System.currentTimeMillis(), inputEnabled);
		//
		// }
		// }
		// }

		// // alterando modelos da iut adicionando estados, submodelos + estados,
		// alterar o
		// // mesmo arquivo da iut
		// int nStateSpec = 100;// 10,50,100
		// int alfabeto = 10;
		//// List<Integer> nStateIUT = Arrays.asList(20, 30, 40, 50, 60, 70, 80, 90,
		//// 100, 110, 120, 130, 140, 150, 160, 170,
		//// 180, 190, 200);
		//
		//// List<Integer> nStateIUT = Arrays.asList(60, 70, 80, 90, 100, 110, 120, 130,
		//// 140, 150, 160, 170,
		//// 180, 190, 200);
		//
		// List<Integer> nStateIUT = Arrays.asList(110, 120, 130, 140, 150, 160, 170,
		// 180, 190, 200);
		//
		// int nExperimentos = 10;
		// int iutPorExperimento = 10;
		// int percentage = 2;
		//
		// boolean inputEnabled = true;
		//
		// for (Integer k : nStateIUT) {
		// System.out.println(">>>>>>>>>>>>> iut: " + k);
		// for (int j = 1; j <= nExperimentos; j++) {
		// System.out.println(">>>> experimento: " + j);
		// for (int i = 0; i < iutPorExperimento; i++) {
		// // System.out.println("> modelo: " + i);
		// generateByNumStatesIocoConf(
		// "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\10-100states\\ioco-n-conf\\2pct\\"
		// + nStateSpec + "states\\alfabeto" + alfabeto + "\\iut" + k + "\\experimento"
		// + j
		// + "\\iut\\" + percentage + "pct_iut_" + i + ".aut",
		// "C:\\Users\\camil\\Google Drive\\UEL\\svn\\ferramenta\\teste
		// desempenho\\10-100states\\ioco-n-conf\\2pct\\"
		// + nStateSpec + "states\\alfabeto" + alfabeto + "\\iut" + k + "\\experimento"
		// + j
		// + "\\iut\\",
		// percentage + "pct_iut_" + i + "", k, "g", System.currentTimeMillis(),
		// inputEnabled);
		// }
		// }
		// }

		// for (int j = 0; j < 10; j++) {
		// generateByPercentage("C:\\Users\\camil\\Desktop\\25-100\\3\\" + nState +
		// "states_spec.aut",
		// "C:\\Users\\camil\\Desktop\\25-100\\" + nState + "\\iut", percentage +
		// "pct_iut_" + j, percentage,
		// "g", seed, inputEnabled);
		// }

		// GENERATE <IN LOTE> - PERCENTAGE (models ioco conf)
		// String nState = "100";
		// List<Integer> tamAlfabeto = Arrays.asList(10);// Arrays.asList(4, 6, 8,
		// 10,12, 14, 16, 18, 20);
		// double percentage = 1;
		// String tag = "g";
		// String rootPathIUTs;
		// String rootSpec;
		// File folder;
		// boolean inputEnabled = true;
		// File[] listOfFiles;
		// int qtdadeModelos = 10;
		//
		// for (Integer alfabeto : tamAlfabeto) {
		// for (int i = 1; i <= 10; i++) {
		// rootPathIUTs = "C:\\Users\\camil\\Desktop\\10-100states\\ioco-conf\\" +
		// nState + "states\\alfabeto"
		// + alfabeto + "\\experimento" + i + "\\iut\\";
		// rootSpec = "C:\\Users\\camil\\Desktop\\10-100states\\ioco-conf\\" + nState +
		// "states\\alfabeto"
		// + alfabeto + "\\experimento" + i + "\\" + nState + "states_spec.aut";
		//
		// for (int j = 0; j < qtdadeModelos; j++) {
		// generateByPercentageModelsIocoConf(rootSpec, rootPathIUTs, (int) percentage +
		// "pct_iut_" + j,
		// percentage, tag, System.currentTimeMillis(), inputEnabled);
		// }
		//
		// // folder = new File(rootPathIUTs);
		// // listOfFiles = folder.listFiles();
		// // for (File file : listOfFiles) {
		// // if (file.getName().indexOf(".") != -1
		// // && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
		// // generateByPercentageModelsIocoConf(rootPathIUTs + file.getName(),
		// // rootSpec,
		// // nState + "states" + file.getName().replace("iut", "spec").replace(".aut",
		// // ""),
		// // percentage, tag, System.currentTimeMillis(), inputEnabled);
		//
		// // }
		// //
		// // }
		//
		// }
		// }

		// ************************************

		// print feature models
		// String pathIUT, rootPathIUTs = "C:\\Users\\camil\\Desktop\\aa\\" + nState +
		// "\\iut\\";
		// File folder = new File(rootPathIUTs);
		// File[] listOfFiles = folder.listFiles();
		//
		// for (File file : listOfFiles) {
		// if (file.getName().indexOf(".") != -1
		// && file.getName().substring(file.getName().indexOf(".")).equals(".aut")) {
		// pathIUT = rootPathIUTs + file.getName();
		//
		// IOLTS iolts = ImportAutFile_WithoutThread.autToIOLTS(pathIUT, false, null,
		// null);
		// IOLTS iolts_ = ImportAutFile_WithoutThread.autToIOLTS(
		// "C:\\Users\\camil\\Desktop\\aa\\" + nState + "\\" + nState +
		// "states_spec.aut", false, null,
		// null);
		// iolts_.setAlphabet(ListUtils.union(iolts.getInputs(), iolts.getOutputs()));
		// System.out.println("n-transicao: " + iolts.getTransitions().size() + "
		// iguais: "
		// + iolts_.equalsTransitions(iolts).size() + " - diferentes: "
		// + iolts_.numberDistinctTransitions(iolts) + " - inp Enab: " +
		// iolts.isInputEnabled()
		// + " - determin: " + iolts.ioltsToAutomaton().isDeterministic() + " > " +
		// file);
		// }
		// }

	}

	// criar specs com base na iut
	public static void generateByNumStatesIocoConf(String pathSpecBase, String pathNewFile, String autFileName,
			int nState, String tag, long seed, boolean inputEnabled) {
		try {

			File dir = new File(pathNewFile + "//iut//");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			pathNewFile += "//iut//";

			Random rand = new Random();
			rand.setSeed(seed * System.currentTimeMillis());
			IOLTS iolts = ImportAutFile.autToIOLTS(pathSpecBase, false, null, null);

			int contState = iolts.getStates().size();

			State_ iniState, endState;
			List<Transition_> transitions = new ArrayList<>();

			do {
				iniState = new State_(tag + Objects.toString(contState));
				iolts.addState(iniState);

				for (String l : iolts.getAlphabet()) {
					endState = new State_(tag + Objects.toString(rand.nextInt(iolts.getStates().size() - 1)));
					if (inputEnabled) {
						if (iolts.getInputs().contains(l)) {
							transitions.add(new Transition_(iniState, l, endState));
						}
					} else {

						transitions.add(new Transition_(iniState, l, endState));

					}

				}
				contState++;
			} while (iolts.getStates().size() != nState);

			for (Transition_ transition : transitions) {
				iolts.addTransition(transition);
			}

			// System.out.println("estados: " + iolts.getStates().size());
			File file = new File(pathNewFile, autFileName + ".aut");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(ioltsToAut(iolts));
			writer.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// criar specs com base na iut
	public static void generateByPercentageIocoConf(String pathSpecBase, String pathNewFile, String autFileName,
			double percentage, String tag, long seed, boolean inputEnabled) {
		try {
			Random rand = new Random();
			rand.setSeed(seed * System.currentTimeMillis());
			IOLTS iolts = ImportAutFile.autToIOLTS(pathSpecBase, false, null, null);

			int numberTransitionsToAdd = (int) ((Math.ceil(iolts.getTransitions().size()) * percentage) / 100);

			if (numberTransitionsToAdd == 0) {
				numberTransitionsToAdd = 1;
			}

			if (inputEnabled) {
				numberTransitionsToAdd = (int) Math.ceil(numberTransitionsToAdd / iolts.getInputs().size());
				numberTransitionsToAdd = numberTransitionsToAdd == 0 ? iolts.getInputs().size()
						: numberTransitionsToAdd;
			}

			// System.out.println(pathSpecBase + "," + pathNewFile + autFileName + "," +
			// iolts.getTransitions().size()
			// + "," + numberTransitionsToAdd + "," + percentage);

			int contState = iolts.getStates().size();
			State_ iniState, endState;
			List<Transition_> transitions = new ArrayList<>();

			for (int i = 0; i < numberTransitionsToAdd; i++) {
				iniState = new State_(tag + Objects.toString(contState));
				iolts.addState(iniState);

				for (String l : iolts.getAlphabet()) {
					endState = new State_(tag + Objects.toString(rand.nextInt(iolts.getStates().size() - 1)));
					if (inputEnabled) {
						if (iolts.getInputs().contains(l)) {
							transitions.add(new Transition_(iniState, l, endState));
						}
					} else {

						transitions.add(new Transition_(iniState, l, endState));

					}

				}

				if (numberTransitionsToAdd == transitions.size()) {
					break;
				}
				contState++;
			}

			for (Transition_ transition : transitions) {
				iolts.addTransition(transition);
			}

			System.out.println("estados: " + iolts.getStates().size());
			File file = new File(pathNewFile, autFileName + ".aut");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(ioltsToAut(iolts));
			writer.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void generateByPercentage(String pathSpecBase, String pathNewFile, String autFileName,
			double percentage, String tag, long seed, boolean inputEnabled) {

		File dir = new File(pathNewFile + "/iut");
		if (!dir.exists())
			dir.mkdirs();
		new File(pathNewFile + "/iut");
		pathNewFile += "//iut";

		// BufferedReader reader;
		String thisLine = null;
		String[] split;
		int randNum;
		boolean runAgain;
		// int transitionsPercentageToAdd, percentageAdd = 0;
		// int percentageToAdd = 5;

		State_ sourceState, targetState;
		IOLTS iolts = new IOLTS();
		IOLTS iolts_base = new IOLTS();
		try {

			int totalTransitions = 0;

			iolts = ImportAutFile.autToIOLTS(pathSpecBase, false, null, null);
			iolts.setAlphabet(ListUtils.union(iolts.getInputs(), iolts.getOutputs()));

			iolts_base = ImportAutFile.autToIOLTS(pathSpecBase, false, null, null);
			iolts_base.setAlphabet(ListUtils.union(iolts.getInputs(), iolts.getOutputs()));

			totalTransitions = iolts.getTransitions().size();

			// define max num transition to add,
			// transitionsPercentageToAdd = (totalTransitions*percentageToAdd)/100;

			// define number of transitions to modify
			List<Integer> lines = new ArrayList<>();
			int numberLinesToChange = (int) (((totalTransitions) * percentage) / 100);
			if (numberLinesToChange == 0) {
				numberLinesToChange = 1;
			}

			// System.out.println(pathModelBase + "," + pathNewFile + autFileName + "," +
			// totalTransitions + ","
			// + numberLinesToChange + "," + percentage);

			System.err.println("modificadas: " + numberLinesToChange);
			int line = 0;

			Random rand = new Random();
			rand.setSeed(seed * System.currentTimeMillis());
			// choose lines to change
			// while (lines.size() < totalTransitions) {
			// line = rand.nextInt(totalTransitions);
			// if (!lines.contains(line))
			// lines.add(line);
			// }

			int numberOfStates = iolts.getStates().size();
			List<Integer> transitionsToRemove = new ArrayList<>();
			Transition_ transition;

			State_ randState;
			// int j = 0;
			State_ s_aux;
			List<String> l_aux = null;
			List<Transition_> addTransitions = new ArrayList<>();
			int sortedLine = -1;

			// modify transitions
			while (iolts.numberDistinctTransitions(iolts_base) < numberLinesToChange) {
				// System.out.println("numberDistinctTransitions: " +
				// iolts.numberDistinctTransitions(iolts_base) + " numberLinesToChange:
				// "+numberLinesToChange);

				// transitionsToRemove = new ArrayList<>();
				lines = new ArrayList<>();
				for (Transition_ t : iolts.equalsTransitions(iolts_base)) {
					for (int i = 0; i < iolts.getTransitions().size(); i++) {
						if (t.equals(iolts.getTransitions().get(i))) {
							lines.add(i);
							lines = lines.stream().distinct().collect(Collectors.toList());

						}
					}
				}

				lines = new ArrayList<>(lines);

				// 0)remove, 1)add or 2)alter transition
				// randNum = rand.nextInt(2)+1;//without remove
				// randNum = rand.nextInt(3);

				// // remove transition
				// if (randNum == 0) {
				//
				// do {
				//
				// sortedLine = rand.nextInt(lines.size() - 1);
				//
				// transition = iolts.getTransitions().get(lines.get(sortedLine));
				// } while (Integer
				// .parseInt(transition.getEndState().toString().replace(tag,
				// "")) == (Integer.parseInt(transition.getIniState().toString().replace(tag,
				// "")) + 1)
				// || transitionsToRemove.contains(lines.get(sortedLine)));
				//
				// transitionsToRemove.add(lines.get(sortedLine));
				// lines.remove(sortedLine);
				//
				// } else {
				// // add transition
				// if (randNum == 1) {
				//
				// int count = 0;
				// // dont add transition deterministic
				// do {
				// runAgain = false;
				// s_aux = iolts.getStates().get(rand.nextInt(numberOfStates));
				// l_aux = iolts.labelNotDefinedOnState(s_aux.getName());
				//
				// if (l_aux.size() == 0) {
				// runAgain = true;
				// break;
				// } else {
				// for (Transition_ t : addTransitions) {
				// if (t.getIniState().equals(s_aux)) {
				// if (l_aux.contains(t.getLabel())) {
				// l_aux.remove(t.getLabel());
				// if (l_aux.size() == 0) {
				// runAgain = true;
				// break;
				// }
				// }
				// }
				// }
				// }
				//
				// count++;
				//
				// } while (runAgain && count < 15);
				//
				// if (!runAgain) {
				// String label = l_aux.get(rand.nextInt(l_aux.size()));
				// addTransitions.add(new Transition_(s_aux, label.substring(1, label.length()),
				// iolts.getStates().get(rand.nextInt(numberOfStates))));
				// }

				// } else {
				// alter transition
				// 0)souce state, 1)label or 2)target state
				randNum = rand.nextInt(3);

				if (lines.size() - 1 > 0) {
					sortedLine = rand.nextInt(lines.size() - 1);
				} else {
					sortedLine = rand.nextInt(lines.size());
				}

				// System.out.println("n-transição: " + iolts.getTransitions().size() + "
				// sorted-line: " + sortedLine + " n-lines: " + lines.size() + " line-get: " +
				// lines.get(sortedLine));
				transition = iolts.getTransitions().get(lines.get(sortedLine));

				// if (randNum == 0) {// alter source state
				// do {
				// randState = iolts.getStates().get(rand.nextInt(numberOfStates));
				//
				// } while (randState.equals(transition.getIniState()));
				//
				// iolts.getTransitions().set(lines.get(sortedLine),
				// new Transition_(randState, transition.getLabel(), transition.getEndState()));
				// lines.remove(sortedLine);
				// } else {
				// dont alter transition that keeps iolts initially connected
				while (Integer.parseInt(transition.getEndState().toString().replace(tag,
						"")) == (Integer.parseInt(transition.getIniState().toString().replace(tag, "")) + 1)) {

					sortedLine = rand.nextInt(lines.size() - 1);
					transition = iolts.getTransitions().get(lines.get(sortedLine));
				}
				do {
					randState = iolts.getStates().get(rand.nextInt(numberOfStates));

				} while (randState.equals(transition.getEndState()));
				iolts.getTransitions().set(lines.get(sortedLine),
						new Transition_(transition.getIniState(), transition.getLabel(), randState));

				lines.remove(sortedLine);
				// }

				// }
				// }

				// Collections.sort(transitionsToRemove, Collections.reverseOrder());
				//
				// // remove sorted transitions
				// for (Integer i : transitionsToRemove) {
				// Transition_ t = iolts.getTransitions().get(i);// (i > 0 ? i - 1 : i);
				// iolts.getTransitions().remove(t);
				// }

				int addInp = 0;
				if (inputEnabled) {
					for (String l : iolts.getInputs()) {
						for (State_ s : iolts.getStates()) {
							if (iolts.reachedStates(s.getName(), l).size() == 0) {
								iolts.getTransitions().add(new Transition_(s, l, iolts.getStates()
										.get(getRandomNumberInRange(0, iolts.getStates().size() - 1, seed))));
								addInp++;
							}
						}
					}
				}

				if (addInp < addTransitions.size()) {
					// add added transitions
					iolts.getTransitions().addAll(addTransitions.subList(addInp, addTransitions.size()));
				}

				int removeToDet = 0;
				// if non deterministic
				for (int i = 0; i < iolts.getTransitions().size(); i++) {
					transition = iolts.getTransitions().get(i);
					if (iolts.reachedStates(transition.getIniState().getName(), transition.getLabel()).size() > 1) {
						iolts.getTransitions().remove(transition);
						removeToDet++;
					}
				}

			}

			File file = new File(pathNewFile, autFileName + ".aut");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(ioltsToAut(iolts));
			writer.close();
			// System.out.println("ALTERADO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			// System.out.println(iolts);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void generate(int numberOfStates, List<String> labels, boolean inputEnabled, String tag, String path,
			String autFileName, long seed) throws Exception {

		if (!Files.exists(Paths.get(path))) {
			Files.createDirectory(Paths.get(path));
		}

		int qtTransition = 0;
		String transitions = "";
		File file = new File(path, autFileName + ".aut");
		List<String> notVisited = new ArrayList<String>();
		int countState = 0;
		String endState = "", iniState = "";

		notVisited.add(tag + countState);

		Random rand = new Random();
		rand.setSeed(seed * System.currentTimeMillis());
		BufferedWriter writer = null;
		String newline = System.getProperty("line.separator");
		boolean teraTransicao;

		int idx = 0;

		iniState = notVisited.remove(0);

		while (iniState != null && (countState != (numberOfStates))) {// enquanto não haver a quantidade de estados

			idx = rand.nextInt(labels.size());

			for (String l : labels) {

				if (l.charAt(0) == Constants.OUTPUT_TAG) {// output label
					teraTransicao = rand.nextInt(2) == 1 ? true : false;

				} else {
					teraTransicao = inputEnabled;// input complete
					if (!teraTransicao && rand.nextInt(2) == 1) {// ter transição com este rótulo
						teraTransicao = true;
					}
				}

				if (labels.indexOf(l) == idx || teraTransicao) {// ter transição com este rótulo

					if (countState + 1 != (numberOfStates) && labels.indexOf(l) == idx) {
						countState++;
						endState = tag + countState;
						notVisited.add(endState);
					} else {

						if (countState > 0) {
							endState = tag + rand.nextInt(countState);
						} else {
							endState = iniState;
						}

					}

					transitions += "(" + iniState + ", " + l + ", " + endState + ")" + newline;
					qtTransition++;

				}

			}
			if (notVisited.size() == 0 && countState < numberOfStates) {
				countState++;
				notVisited.add(tag + countState);
			}
			if (notVisited.size() > 0) {
				iniState = notVisited.remove(0);
			} else {
				iniState = null;
			}

		}

		try {
			String header = "des(" + tag + "0," + qtTransition + ", " + numberOfStates + ")" + newline;
			String aut = header + transitions;

			writer = new BufferedWriter(new FileWriter(file));
			writer.write(aut);

			// System.out.println(aut);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	public static String ioltsToAut(IOLTS iolts) {
		String newline = System.getProperty("line.separator");
		String iniState = iolts.getInitialState().getName().replace(",", "_");
		String aut = "des(" + iniState + "," + iolts.getTransitions().size() + "," + iolts.getStates().size() + ")"
				+ newline;
		iolts.getInitialState().setName(iniState);

		for (State_ s : iolts.getStates()) {
			s.setName(s.getName().replace(",", "_"));
		}
		
		for (Transition_ t : iolts.getTransitions()) {
			// if name of states contains COMMA replace to _
			t.setIniState(new State_(t.getIniState().getName().replace(",", "_")));
			t.setEndState(new State_(t.getEndState().getName().replace(",", "_")));

			if (t.getLabel().equals(Constants.DELTA)) {
				aut += "(" + t.getIniState() + "," + Constants.DELTA_TXT + "," + t.getEndState() + ")" + newline;// t.getLabel()
			} else {
				if (iolts.getInputs().contains(t.getLabel())) {
					aut += "(" + t.getIniState() + "," + Constants.INPUT_TAG + t.getLabel() + "," + t.getEndState()
							+ ")" + newline;
				}

				if (iolts.getOutputs().contains(t.getLabel())) {
					aut += "(" + t.getIniState() + "," + Constants.OUTPUT_TAG + t.getLabel() + "," + t.getEndState()
							+ ")" + newline;
				}
			}
		}
		return aut;
	}

	public static void generateAutInLot_PercentageStates(int totalModels, String rootPath, String iutAutPath, long seed,
			boolean inputEnabled) throws IOException {
		int[] percentageVariation = { 20, 40, 60, 80, 100 };
		// String currentFolder;
		int constDivision = (totalModels / percentageVariation.length);
		int count = 0;
		for (int i = 0; i < percentageVariation.length; i++) {
			// new folder
			// currentFolder = rootPath + "/" + percentageVariation[i] + "percent";
			// Files.createDirectories(Paths.get(currentFolder));

			// aut per group
			for (int j = 0; j < constDivision; j++) {
				generateByPercentage(iutAutPath, rootPath, percentageVariation[i] + "pct_spec" + "_" + count,
						percentageVariation[i], "g", seed, inputEnabled);// currentFolder
				count++;
			}
		}

	}

	public static void generateAutInLot_NumStates(int totalModels, int constDivision, int minStates, int maxStates,
			boolean inputEnabled, String tag, String rootPath, List<String> labels, long seed) throws Exception {
		// int quantityGroups = totalModels / constDivision;
		int variationNumStates = (maxStates - minStates) / constDivision;
		int countStates = minStates;
		int randomNumStates;

		int residual = ((maxStates - minStates) % constDivision) + 1;
		int count = 0;
		// String currentFolder = "";

		// group (limit num states)
		for (int i = 0; i < constDivision; i++) {
			// new folder
			// if (residual != 0 && i == constDivision - 1) {
			// currentFolder = rootPath + "/" + countStates + "-" + (countStates +
			// variationNumStates - 1 + residual)
			// + "states";
			// } else {
			// currentFolder = rootPath + "/" + countStates + "-" + (countStates +
			// variationNumStates - 1) + "states";
			// }
			// Files.createDirectories(Paths.get(currentFolder));

			// aut per group
			for (int j = 0; j < (totalModels / constDivision); j++) {
				if (residual != 0 && i == constDivision - 1) {
					randomNumStates = getRandomNumberInRange(countStates,
							countStates + variationNumStates - 1 + residual, seed);

				} else {
					randomNumStates = getRandomNumberInRange(countStates, countStates + variationNumStates - 1, seed);
				}

				generate(randomNumStates, labels, inputEnabled, tag, rootPath,
						randomNumStates + "states_spec" + "_" + count, seed);// currentFolder

				count++;
			}

			countStates += variationNumStates;
		}
	}

	private static int getRandomNumberInRange(int min, int max, long seed) {

		Random r = new Random();
		r.setSeed(seed * System.currentTimeMillis());
		return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

	}

	// public static void generateByPercentage(String pathModelBase, String
	// pathNewFile, String autFileName,
	// double percentage) {
	// // BufferedReader reader;
	// String thisLine = null;
	// String[] split;
	// int randNum;
	// // int transitionsPercentageToAdd, percentageAdd = 0;
	// // int percentageToAdd = 5;
	//
	// State_ sourceState, targetState;
	// IOLTS iolts = new IOLTS();
	// try {
	// // reader = new BufferedReader(new FileReader(pathModelBase));
	// int totalTransitions = 0;
	//
	// iolts = ImportAutFile.autToIOLTS(pathModelBase, false, null, null);
	// iolts.setAlphabet(ListUtils.union(iolts.getInputs(), iolts.getOutputs()));
	//
	// // System.out.println("ORIGINAL >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	// // System.out.println(iolts);
	// totalTransitions = iolts.getTransitions().size();
	//
	// // define max num transition to add,
	// // transitionsPercentageToAdd = (totalTransitions*percentageToAdd)/100;
	//
	// // define number of transitions to modify
	// List<Integer> lines = new ArrayList<>();
	// int numberLinesToChange = (int) (((totalTransitions) * percentage) / 100);
	// int line = 0;
	//
	// Random rand = new Random();
	// // choose lines to change
	// while (lines.size() < numberLinesToChange) {
	// line = rand.nextInt(totalTransitions);
	// if (!lines.contains(line))
	// lines.add(line);
	// }
	//
	// int numberOfStates = iolts.getStates().size();
	// List<Transition_> transitionsToRemove = new ArrayList<>();
	// Transition_ transition;
	// State_ randState;
	// // modify transitions
	// for (int i = 0; i < lines.size(); i++) {
	// // 0)remove, 1)add or 2)alter transition
	// randNum = rand.nextInt(3);
	//
	// // remove transition
	// if (randNum == 0) {
	// transitionsToRemove.add(iolts.getTransitions().get(lines.get(i)));
	// } else {
	// // add transition
	// if (randNum == 1) {
	// iolts.addTransition(new
	// Transition_(iolts.getStates().get(rand.nextInt(numberOfStates)),
	// iolts.getAlphabet().get(rand.nextInt(iolts.getAlphabet().size())),
	// iolts.getStates().get(rand.nextInt(numberOfStates))));
	// } else {
	// // alter transition
	// // 0)souce state, 1)label or 2)target state
	// randNum = rand.nextInt(3);
	// transition = iolts.getTransitions().get(lines.get(i));
	// randState = iolts.getStates().get(rand.nextInt(numberOfStates));
	// if (randNum == 0) {// alter source state
	// iolts.getTransitions().set(lines.get(i),
	// new Transition_(randState, transition.getLabel(), transition.getEndState()));
	// } else {
	// if (randNum == 1) {// alter label
	// iolts.getTransitions().set(lines.get(i),
	// new Transition_(transition.getIniState(),
	// iolts.getAlphabet().get(rand.nextInt(iolts.getAlphabet().size())),
	// transition.getEndState()));
	// } else {// alter target state
	// iolts.getTransitions().set(lines.get(i),
	// new Transition_(transition.getIniState(), transition.getLabel(), randState));
	// }
	// }
	// }
	// }
	// }
	//
	// for (Transition_ t : transitionsToRemove) {
	// iolts.getTransitions().remove(t);
	//
	// }
	//
	// File file = new File(pathNewFile, autFileName + ".aut");
	// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	// writer.write(ioltsToAut(iolts));
	// writer.close();
	// // System.out.println("ALTERADO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	// // System.out.println(iolts);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
}
