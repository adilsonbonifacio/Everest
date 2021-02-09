package algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.bridj.util.Pair;

import model.Automaton_;
import model.Graph;
import model.IOLTS;
import model.State_;
import model.Transition_;
import model.Transition_.TransitionType;
import parser.ImportAutFile;
import util.AutGenerator;
import util.Constants;
import view.EverestView;

public class TestGeneration {

    private static List<String> tcFault = new ArrayList<>();
    // public static void main(String[] args) {
    // String path = "C:\\Users\\camil\\Documents\\aut-separados\\iolts-spec.aut";
    // try {
    // IOLTS iolts = ImportAutFile.autToIOLTS(path, false, null, null);
    //
    // List<State_> endStates = new ArrayList<>();
    // List<Transition_> endTransitions = new ArrayList<>();
    // iolts.addQuiescentTransitions();
    // // System.out.println(iolts);
    //
    // Automaton_ multgraph = multiGraphD(iolts, 1);
    //
    // // System.out.println(multgraph);//201 transições
    //
    // // *implementar
    // // get word from multgraph
    //
    // // System.out.println(new Date());
    // // List<String> words = Graph.getWords(multgraph);
    // // System.out.println(new Date());
    // //// words = new ArrayList<>(new HashSet<>(words));
    // //
    // // // System.out.println(words.size());
    // //
    // // for (String w : words) {
    // // System.out.println(w);
    // // }
    //
    // // // to decrease the performance of statePath
    // // if (multgraph.getInitialState().getTransitions().size() == 0) {
    // // if
    // //
    // (multgraph.getStates().stream().findAny().orElse(null).getTransitions().size()
    // // == 0) {
    // // for (Transition_ t : multgraph.getTransitions()) {
    // // multgraph.getStates().stream().filter(x ->
    // // x.equals(t.getIniState())).findFirst().orElse(null)
    // // .addTransition(t);
    // // t.setIniState(multgraph.getStates().stream().filter(x ->
    // // x.equals(t.getIniState())).findFirst()
    // // .orElse(null));
    // // t.setEndState(multgraph.getStates().stream().filter(x ->
    // // x.equals(t.getEndState())).findFirst()
    // // .orElse(null));
    // // }
    // // }
    // // multgraph.setInitialState(multgraph.getStates().stream()
    // // .filter(x ->
    // // x.equals(multgraph.getInitialState())).findFirst().orElse(null));
    // // }
    // //
    // // List<String> testCases = new ArrayList<>();
    // // // set words to reach final state, because of the modification of the
    // final
    // // // states
    // // for (String w : words) {
    // // for (List<State_> states_ : Operations.statePath(multgraph, w)) {
    // //
    // // // endState = states_.get(states_.size()-1);//-1
    // // for (Transition_ t : endTransitions.stream()
    // // .filter(x -> x.getIniState().equals(states_.get(states_.size() - 1)))
    // // .collect(Collectors.toList())) {
    // // testCases.add(w + " -> " + t.getLabel());
    // // }
    // // }
    // // }
    // //
    // // // System.out.println(testCases);
    // //
    // // for (String tc : testCases) {
    // // System.out.println(testPurpose(multgraph, tc, iolts.getOutputs(),
    // // iolts.getInputs()));// "a -> x -> b ->
    // // // a -> a -> b
    // // // -> b -> x"
    // // }
    //
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

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

        // self loop pass/fail state
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

    /***
     *
     * @param S
     *            the specification model
     * @param m
     *            max states of specification
     * @return
     */
    public static Automaton_ multiGraphD(IOLTS S, int m) {
        int level = 0;
        IOLTS D = new IOLTS();
        State_ iniState = new State_(S.getInitialState() + Constants.COMMA + level);
        D.setInitialState(iniState);
        D.addState(iniState);
        State_ current;

        Map<String, List<String>> map_state_out_transition = new HashMap();
        List<String> labels;

        List<State_> toVisit = new ArrayList<>();
        toVisit.add(S.getInitialState());

        List<State_> toVisit_aux = new ArrayList<>(toVisit);

        int totalLevels = S.getStates().size() * m + 1;

        List<String> L = new ArrayList<>(S.getInputs());
        L.addAll(S.getOutputs());

        D.setAlphabet(L);

        List<Pair<String, String>> nextLevel = new ArrayList<>();
        State_ d;
        String name;
        Transition_ transition;
        // String aut = "";

        boolean inp, delta;

        // System.out.println("a");
        // construct first level
        while (toVisit.size() > 0) {
            current = toVisit.remove(0);// remove from the beginning of queue
            for (Transition_ t : S.transitionsByIniState(current)) {
                inp = S.getInputs().contains(t.getLabel());
                delta = t.getLabel().equals(Constants.DELTA);

                if (!toVisit_aux.contains(t.getEndState())) {
                    toVisit.add(t.getEndState());
                    toVisit_aux.add(t.getEndState());
                }

                if (current.equals(t.getEndState()) || nextLevel.contains(new Pair(current, t.getEndState()))) {
                    d = new State_(t.getEndState() + Constants.COMMA + "1");
                } else {
                    for (Transition_ t2 : S.transitionsByIniState(t.getEndState())) {
                        if (t2.getEndState().equals(current)) {
                            nextLevel.add(new Pair(t.getEndState(), current));
                        }
                    }
                    d = new State_(t.getEndState() + Constants.COMMA + "0");
                }

                D.addState(d);
                name = current + Constants.COMMA + "0";
                if (S.getOutputs().contains(t.getLabel())) {
                    transition = new Transition_(new State_(name), t.getLabel(), d, TransitionType.OUTPUT);
                } else {
                    if (S.getInputs().contains(t.getLabel())) {
                        transition = new Transition_(new State_(name), t.getLabel(), d, TransitionType.INPUT);
                    } else {
                        transition = new Transition_(new State_(name), t.getLabel(), d);
                    }
                }
                D.addTransition(transition);
                // aut += transitionAut(current + Constants.UNDERLINE + "0", t.getLabel(),
                // d.getName(), inp, delta);

                if (S.getOutputs().contains(t.getLabel())) {
                    if (map_state_out_transition.keySet().contains(name)) {
                        labels = new ArrayList<>(map_state_out_transition.get(name));
                        labels.add(t.getLabel());
                        map_state_out_transition.put(name, labels);
                    } else {
                        map_state_out_transition.put(name, Arrays.asList(t.getLabel()));
                    }
                }

            }
        }

        int leveld, leveld2 = 0;
        String named, named2;
        State_ ini, end;
        List<Transition_> transitions = new ArrayList<>();
        List<State_> states = new ArrayList<>();

        int cont = 0;
        // System.out.println("b");
        // construct rest of levels
        for (Transition_ t : D.getTransitions()) {
            named = t.getIniState().getName().split(Constants.COMMA)[0];
            named2 = t.getEndState().getName().split(Constants.COMMA)[0];

            leveld = Integer.parseInt(t.getIniState().getName().split(Constants.COMMA)[1]);
            leveld2 = Integer.parseInt(t.getEndState().getName().split(Constants.COMMA)[1]);
            inp = S.getInputs().contains(t.getLabel());
            delta = t.getLabel().equals(Constants.DELTA);
            while (leveld2 + 1 < totalLevels) {
                leveld++;
                leveld2++;

                ini = new State_(named + Constants.COMMA + leveld);
                end = new State_(named2 + Constants.COMMA + leveld2);

                // transition = new Transition_(ini, t.getLabel(), end);

                if (S.getOutputs().contains(t.getLabel())) {
                    transition = new Transition_(ini, t.getLabel(), end, TransitionType.OUTPUT);
                } else {
                    if (S.getInputs().contains(t.getLabel())) {
                        transition = new Transition_(ini, t.getLabel(), end, TransitionType.INPUT);
                    } else {
                        transition = new Transition_(ini, t.getLabel(), end);
                    }
                }

                transitions.add(transition);

                if (S.getOutputs().contains(transition.getLabel())) {
                    if (map_state_out_transition.keySet().contains(transition.getIniState().getName())) {
                        labels = new ArrayList<>(map_state_out_transition.get(transition.getIniState().getName()));
                        labels.add(transition.getLabel());
                        map_state_out_transition.put(transition.getIniState().getName(), labels);
                    } else {
                        map_state_out_transition.put(transition.getIniState().getName(),
                                Arrays.asList(transition.getLabel()));
                    }
                }
                // aut += transitionAut(named + Constants.UNDERLINE + leveld, t.getLabel(),
                // named2 + Constants.UNDERLINE + leveld2, inp, delta);

                // aut += transition.toString();

                // transitions.add(new Transition_(ini, t.getLabel(), end));

                // transitions.add(new Transition_(ini, t.getLabel(), end));

                // if (!states.contains(ini)) {
                // states.add(ini);
                // }

                // if (!states.contains(end)) {
                states.add(end);
                // }

                // states.add(ini);
                // states.add(end);
                // System.out.println("transitions: " +transitions.size()+" - states: " +
                // states.size() + " - level: " + leveld2 + " - max level:"+totalLevels);
            }
            // System.out.println("total: " + D.getTransitions().size() + " - atual: " +
            // cont);
            // System.out.println("transitions: " + transitions.size() + " - states: " +
            // states.size() + " - level: "
            // + leveld2 + " - max level:" + totalLevels);
            cont++;
        }

        // System.out.println("c");
        D.getTransitions().addAll(new HashSet<>(transitions));
        D.getStates().addAll(new ArrayList<>(new HashSet<>(states)));

        State_ fail = new State_("fail");
        D.addState(fail);

        // for (State_ s : D.getStates()) {
        // for (String l : S.getOutputs()) {
        // if (!D.transitionExists(s.getName(), l)) {
        // D.addTransition(new Transition_(s, l, fail));
        // }
        // }
        // }

        for (String key : map_state_out_transition.keySet()) {
            for (String l : S.getOutputs()) {
                if (!map_state_out_transition.get(key).contains(l)) {
                    transition = new Transition_(new State_(key), l, fail, TransitionType.OUTPUT);
                    D.addTransition(transition);
                    // aut += transitionAut(key, l, fail.getName(), false,
                    // l.equals(Constants.DELTA));
                }
            }
        }

        // System.out.println("d");
        Automaton_ a = new Automaton_(D.getStates(), D.getInitialState(), D.getAlphabet(), D.getStates(),
                D.getTransitions());
        a.setFinalStates(Arrays.asList(new State_[] { fail }));
        // System.out.println("e");

        return a;
    }

    public static javafx.util.Pair<List<String>, Boolean> run(String pathTp, boolean oneIut, boolean oneTP, String pathIut, String pathCsv, IOLTS iut) {
        boolean fault = false;

        javafx.util.Pair<List<String>, Boolean> result;
        List<String> TCs = new ArrayList<>();

        if (oneTP) {
            if (EverestView.isAutFile(new File(pathTp)))
                return runAllIutTp(new File(pathTp), oneIut, pathIut, pathCsv, iut);
            else {
                return null;
            }

        } else {
            File tpFolderF = new File(pathTp);
            File[] listOfTpFiles = tpFolderF.listFiles();

            // each tp
            for (File fileTp : listOfTpFiles) {
                if (EverestView.isAutFile(fileTp)) {
                    // run( pathTp + "//" + fileTp.getName(), fileTp, oneIut, pathImplementation,
                    // pathCsv);
                    if (!fault) {
                        result = runAllIutTp(fileTp, oneIut, pathIut, pathCsv, iut);
                        TCs.addAll(result.getKey());
                        fault = result.getValue();
                    }
                }
            }
            return new javafx.util.Pair<List<String>, Boolean>(TCs,fault);
        }



    }

    public static javafx.util.Pair<List<String>, Boolean> runAllIutTp(File fileTp, boolean oneIut, String pathIut, String pathCsv, IOLTS iut) {// String pathTp,
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
        List<String> wordsTp=new ArrayList<>();

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
            tpAutomaton.addFinalStates(new State_("pass"));
            wordsTp = Graph.getWords(tpAutomaton);
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
            // wordsTp = new ArrayList<>(new HashSet<>(wordsTp));

            for (String word : wordsTp) {

                // one iut
                if (oneIut) {
                    result = runIutTp(pathIut, word, fileTp, tpAutomaton,iut);// pathTp
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
                                result = runIutTp(pathIut + "//" + fileIut.getName(), word, fileTp, tpAutomaton,iut);
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


        return new javafx.util.Pair<List<String>, Boolean> (wordsTp,fault);//fault
    }

    public static javafx.util.Pair<List<List<String>>, Boolean> runIutTp(String pathIut, String word, File fileTp,
                                                                         Automaton_ tp, IOLTS iut) {
        List<List<String>> toSave = new ArrayList<>();

        boolean nonconformance = false;
        tp.addState(new State_("fail"));
        Operations.addTransitionToStates(tp);
        try {
            //IOLTS iut;
//			iut = ImportAutFile.autToIOLTS(pathIut, false, null, null);
//			iut.addQuiescentTransitions();

            List<String> partialResult = new ArrayList<>();
            List<List<State_>> statesPath, statesPath_tp;
            // System.out.println(word);
            Operations.addTransitionToStates(iut);
            statesPath = Operations.statePath(iut, word);
            statesPath_tp = Operations.statePath(tp, word);

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

                    if (statesPath_tp.get(0).get(statesPath_tp.get(0).size() - 1).getName().contains("fail")) {
                        // not conform
                        partialResult.add(Constants.RUN_VERDICT_NON_CONFORM);
                        tcFault.add(word);
                        nonconformance = true;
                    } else {
                        if (statesPath_tp.get(0).get(statesPath_tp.get(0).size() - 1).getName().contains("pass")) {
                            // pass
                            partialResult.add(Constants.RUN_VERDICT_PASS);
                        }
                    }
                }

                toSave.add(partialResult);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new javafx.util.Pair<List<List<String>>, Boolean>(toSave, nonconformance);
    }

    static volatile int level;
    static volatile Transition_ aa = null;


    public static javafx.util.Pair<List<String>, Boolean> getTcAndSaveTP(Automaton_ multigraph, Integer nTP,
                                                                         String absolutePath, List<String> li, List<String> lu, String pathIUT, String multigraphName, IOLTS iolts)
            throws IOException {

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
        Automaton_ tp_automaton;
        File file;
        BufferedWriter writer;
        File tpFile;
        javafx.util.Pair<List<String>, Boolean> result;
        //boolean result;
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
                        if (nTP == null || nTP == Integer.MAX_VALUE) {
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

                        boolean exists = false;
                        for (String tc : aux) {
                            if (!words.contains(tc)) {
                                totalTC += 1;
                            } else {
                                exists = true;
                            }
                            if (!exists) {
                                // save tp
                                tp = TestGeneration.testPurpose(multigraph, tc, li, lu);
                                tp_automaton = tp.ioltsToAutomaton();
                                tp_automaton.setFinalStates(new ArrayList<>());
                                tp_automaton.addFinalStates(new State_("fail"));
                                tp_automaton.addFinalStates(new State_("pass"));
                                // tpFile = TestGeneration.saveTP(absolutePath + "\\TPs\\", tp);
                                tpFile = TestGeneration.saveTP(absolutePath, tp, multigraphName);

                                // if run TP x IUT
                                if (pathIUT != null) {
                                    // result = TestGeneration.runIutTp(pathIUT, tc, tpFile, tp_automaton);--
                                    result = TestGeneration.run(tpFile.getAbsolutePath(), true, true, pathIUT,
                                            absolutePath+System.getProperty("file.separator")+"TPs - " + multigraphName+System.getProperty("file.separator"), iolts);
                                    // TestGeneration.saveOnCSVFile(result.getKey(), absolutePath);//
                                    // "\\runVerdicts.csv"

                                    words.add(tc);
                                    // no conformance
                                    if (result.getValue()) {
                                        nonConf = result.getValue();
                                        break end;
                                    }
                                } //else {
                                words.add(tc);

                                //}
                                if (nTP != null && nTP == totalTC) {
                                    break end;
                                }
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
                    boolean exists = false;
                    // not selfloop of final state
                    if (!current.getEndState().getName().equals(current.getIniState().getName())) {
                        // if is a test case
                        if (!words.contains(current.getLabel())) {
                            totalTC += 1;
                        } else {
                            exists = true;
                        }
                        if (!exists) {
                            // fw.write(String.join("\n", Arrays.asList(current.getLabel()))); // file
                            // fw.write("\n");
                            // save tp
                            tp = TestGeneration.testPurpose(multigraph, current.getLabel(), li, lu);
                            tp_automaton = tp.ioltsToAutomaton();
                            tp_automaton.setFinalStates(new ArrayList<>());
                            tp_automaton.addFinalStates(new State_("fail"));
                            tp_automaton.addFinalStates(new State_("pass"));

                            // tpFile = TestGeneration.saveTP(absolutePath + "\\TPs\\", tp);
                            tpFile = TestGeneration.saveTP(absolutePath, tp, multigraphName);

                            // if run TP x IUT
                            if (pathIUT != null) {
                                // result = TestGeneration.runIutTp(pathIUT, current.getLabel(), tpFile,
                                // tp_automaton);--
                                // TestGeneration.saveOnCSVFile(result.getKey(), absolutePath);// +
                                // "\\runVerdicts.csv"

                                result = TestGeneration.run(tpFile.getAbsolutePath(), true, true, pathIUT,
                                        absolutePath+System.getProperty("file.separator")+"TPs - " +multigraphName+System.getProperty("file.separator"),iolts);
                                nonConf = result.getValue();
                                // no conformance
                                if (result.getValue()) {
                                    break end;
                                }
                            } //else {
                            words.add(current.getLabel());

                            //}

                            if (nTP != null && nTP == totalTC) {
                                break end;
                            }

                            // y += (String.join("\n", Arrays.asList(current.getLabel()))) + "\n";
                            // fw.close();
                            // System.out.print(String.join("\n", Arrays.asList(current.getLabel()))+"\n");
                        }
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
                for (String m : map.keySet()) {
                    if (m.contains("," + level)) {

                        toRemove.add(m);
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

        words = new ArrayList<>(new HashSet<>(words));
        return new javafx.util.Pair<List<String>, Boolean>(words, nonConf);
        // return String.join(",", words);
    }

    public static File saveTP(String tpFolder, IOLTS tp, String multigraphName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-S");

        // File file = new File(new File(tpFolder, "TPs").getAbsolutePath(), "tp_" +
        // dateFormat.format(new Date()) + "-"
        // + Constants.ALPHABET_[new Random().nextInt(Constants.ALPHABET_.length)]
        // +Constants.ALPHABET_[new Random().nextInt(Constants.ALPHABET_.length)]+
        // ".aut");
        File file = new File(new File(tpFolder, "TPs - " + multigraphName).getAbsolutePath(),
                "tp_" + java.util.UUID.randomUUID() + ".aut");

        // File file = new File(tpFolder, "tp_" + dateFormat.format(new Date()) +
        // "-"+Constants.ALPHABET_[new
        // Random().nextInt(Constants.ALPHABET_.length)]+".aut");

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(AutGenerator.ioltsToAut(tp));
            writer.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return file;
    }

    static String lastTP = "";

    public static void saveOnCSVFile(List<List<String>> toSave, String pathCsv) {
        boolean firstLine = false;

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
            headerCSV.add("verdict");

            FileWriter csvWriter;

            //System.out.println(pathCsv);
            File file = new File(pathCsv);
            if (!file.exists()) {
                file.createNewFile();
            }

            if (new File(pathCsv).length() == 0) {
                firstLine = true;
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

                if (!lastTP.equals(row.get(1))) {//
                    csvWriter.append("\n" + row.get(1) + "\n");
                }
                csvWriter.append(String.join(delimiterCSV, row));
                lastTP = row.get(1);
            }

            csvWriter.append("\n");
            csvWriter.flush();
            csvWriter.close();

        } catch (Exception e)

        {
            e.printStackTrace();
        }
    }

    public static List<String> getTcFault() {
        return tcFault;
    }

    public static void setTcFault(List<String> tcFault) {
        TestGeneration.tcFault = tcFault;
    }
}
