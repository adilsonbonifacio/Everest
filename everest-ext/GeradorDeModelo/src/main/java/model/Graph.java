package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import junit.runner.StandardTestSuiteLoader;

public class Graph {

    // No. of vertices in graph
    private int v;

    // adjacency list
    private ArrayList<Integer>[] adjList;

    private List<List<Integer>> paths;

    public List<List<Integer>> getPaths() {
        return paths;
    }

    // Constructor
    public Graph(int vertices) {

        // initialize vertex count
        this.v = vertices;

        // initialize adjacency list
        initAdjList();

        paths = new ArrayList<>();

    }

    // utility method to initialize
    // adjacency list
    @SuppressWarnings("unchecked")
    private void initAdjList() {
        adjList = new ArrayList[v];

        for (int i = 0; i < v; i++) {
            adjList[i] = new ArrayList<>();
        }
    }

    // add edge from u to v
    public void addEdge(int u, int v) {
        // Add v to u's list.
        adjList[u].add(v);

    }

    // Prints all paths from
    // 's' to 'd'
    public void getStatePaths(int s, int d) {
        boolean[] isVisited = new boolean[v];
        ArrayList<Integer> pathList = new ArrayList<>();

        // add source to path[]
        pathList.add(s);

        // Call recursive utility
        printAllPathsUtil(s, d, isVisited, pathList);

    }

    // A recursive function to print
    // all paths from 'u' to 'd'.
    // isVisited[] keeps track of
    // vertices in current path.
    // localPathList<> stores actual
    // vertices in the current path
    private void printAllPathsUtil(Integer u, Integer d, boolean[] isVisited, List<Integer> localPathList) {

        // Mark the current node
        isVisited[u] = true;

        if (u.equals(d)) {
            paths.add(new ArrayList<>(localPathList));
            // System.out.println(localPathList);
            // if match found then no need to traverse more till depth
            isVisited[u] = false;
            return;
        }

        // Recur for all the vertices
        // adjacent to current vertex
        for (Integer i : adjList[u]) {
            if (!isVisited[i]) {
                // store current node
                // in path[]
                localPathList.add(i);
                printAllPathsUtil(i, d, isVisited, localPathList);

                // remove current node
                // in path[]
                localPathList.remove(i);
            }
        }

        // Mark the current node
        isVisited[u] = false;
    }

    public static List<String> getWords(Automaton_ a) {



        a.makeInitiallyConnected();
        Map<String, Integer> stateNames = new HashMap<>();
        Map<Integer, String> stateNum = new HashMap<>();
        int numState = 0;

        a.addState(new State_("fail"));

        for (State_ s : a.getStates()) {
            stateNum.put(numState, s.getName());
            stateNames.put(s.getName(), numState);
            numState++;
        }



        // initialize adjacent labels
        ArrayList<String>[][] adjacentLabels = new ArrayList[a.getStates().size()][a.getStates().size()];
        for (int l = 0; l < a.getStates().size(); l++) {
            for (int c = 0; c < a.getStates().size(); c++) {
                adjacentLabels[l][c] = new ArrayList<>();
            }
        }

        // set adjacents labels
        for (Transition_ t : a.getTransitions()) {
            adjacentLabels[stateNames.get(t.getIniState().getName())][stateNames.get(t.getEndState().getName())]
                    .add(t.getLabel());
        }

        Graph g = new Graph(a.getStates().size());
        // add edge to graph
        for (Transition_ t : a.getTransitions()) {
            g.addEdge(stateNames.get(t.getIniState().getName()), stateNames.get(t.getEndState().getName()));
        }

        // get state paths
        for (State_ finalState : a.getFinalStates()) {

            g.getStatePaths(stateNames.get(a.getInitialState().getName()), stateNames.get(finalState.getName()));
        }

        int cont = 0;
        Integer previous = 0;
        // String label;
        // String word = "";
        List<String> words = new ArrayList<>();
        List<String> words_aux = new ArrayList<>();
        List<String> words_aux_ = new ArrayList<>();

        // each state path
        for (List<Integer> l : g.getPaths()) {
            cont = 0;
            // word = "";
            words_aux = new ArrayList<>();

            for (Integer state : l) {// each state from a path
                if (cont != 0) {
                    words_aux_ = new ArrayList<>();
                    // if exists more than one label from previous to state
                    for (String label : adjacentLabels[previous][state]) {
                        if (words_aux.size() > 0) {
                            for (String word : words_aux) {
                                words_aux_.add(word + label + " -> ");
                            }
                        } else {
                            words_aux_.add(label + " -> ");
                        }
                    }
                    words_aux = new ArrayList<>(words_aux_);

                }
                cont++;
                previous = state;

            }
            words.addAll(new ArrayList<>(words_aux));
        }

        words = new ArrayList<>(new HashSet<>(words));

        List<String> sorted = words.stream().sorted((s1, s2) -> s1.length() - s2.length()).collect(Collectors.toList());

        return sorted;
    }

    // // Driver program
    // public static void main(String[] args) {
    // // Create a sample graph
    // // Graph g = new Graph(9);
    // // g.addEdge(0,1);
    // // g.addEdge(0,3);
    // // g.addEdge(1,3);
    // // g.addEdge(1,2);
    // // g.addEdge(1,5);
    // // g.addEdge(2,5);
    // // g.addEdge(2,3);
    // // g.addEdge(2,8);
    // // g.addEdge(6,8);
    // // g.addEdge(5,6);
    // // g.addEdge(5,7);
    // // g.addEdge(3,6);
    // // g.addEdge(3,7);
    // // g.addEdge(6,7);
    // // g.addEdge(7,8);
    // // g.addEdge(3,8);
    // // g.addEdge(8,8);
    // // g.addEdge(0,8);
    // //
    // // // arbitrary source
    // // int s = 0;
    // //
    // // // arbitrary destination
    // // int d = 8;
    // //
    // // System.out.println("Following are all different paths from "+s+" to "+d);
    // // g.getStatePaths(s, d);
    //
    // Automaton_ a = new Automaton_();
    // State_ s0 = new State_("s0");
    // State_ s1 = new State_("s1");
    // State_ s2 = new State_("s2");
    // State_ s3 = new State_("s3");
    // State_ s4 = new State_("s4");
    // State_ s5 = new State_("s5");
    // State_ s6 = new State_("s6");
    // State_ s7 = new State_("s7");
    // State_ s8 = new State_("s8");
    //
    // a.setInitialState(s0);
    // a.addFinalStates(s8);
    // a.addState(s0);
    // a.addState(s1);
    // a.addState(s2);
    // a.addState(s3);
    // a.addState(s4);
    // a.addState(s5);
    // a.addState(s6);
    // a.addState(s7);
    // a.addState(s8);
    // a.addTransition(new Transition_(s0, "a", s1));
    // a.addTransition(new Transition_(s0, "b", s3));
    // a.addTransition(new Transition_(s0, "x", s8));
    // a.addTransition(new Transition_(s1, "a", s5));
    // a.addTransition(new Transition_(s1, "x", s2));
    // a.addTransition(new Transition_(s1, "b", s3));
    // a.addTransition(new Transition_(s2, "a", s5));
    // a.addTransition(new Transition_(s2, "b", s3));
    // a.addTransition(new Transition_(s2, "x", s8));
    // a.addTransition(new Transition_(s3, "b", s6));
    // a.addTransition(new Transition_(s3, "a", s7));
    // a.addTransition(new Transition_(s3, "x", s8));
    // a.addTransition(new Transition_(s5, "x", s6));
    // a.addTransition(new Transition_(s5, "b", s7));
    // a.addTransition(new Transition_(s6, "b", s7));
    // a.addTransition(new Transition_(s6, "x", s8));
    // a.addTransition(new Transition_(s7, "x", s8));
    // // a.addTransition(new Transition_(s0,"a",s1));
    // a.addTransition(new Transition_(s8, "x", s8));
    //
    // for (String w : getWords(a)) {
    // System.out.println(w);
    // }
    //
    // }
}
