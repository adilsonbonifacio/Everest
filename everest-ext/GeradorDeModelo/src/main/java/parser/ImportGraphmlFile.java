package parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

import model.State_;
import model.IOLTS;
import model.LTS;
import model.Transition_;
import util.Constants;

/***
 * Class import and convert .graphml file (Yed software) into   * LTS and IOLTS
 * object, uses the tinkerpop library
 *
 * @author camila
 *
 */
public class ImportGraphmlFile {
    /***
     * method that uses the tinkerpop library to read the graphml file and convert
     * in Graph
     *
     * @param path
     *            file directory
     * @return graph graph generated from the .graphml file
     */
    public static Graph readGraphmlFile(String path) {
        Graph graph = new TinkerGraph();
        GraphMLReader reader = new GraphMLReader(graph);
        InputStream is;
        try {
            is = new BufferedInputStream(new FileInputStream(path));
            reader.inputGraph(is);
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("Error processing file");
        }
        return graph;
    }

    /***
     * Converts the .graphml file to LTS to set the initial state: (i) is necessary
     * to create a state that points to the initial state and transition must be
     * unlabeled; (ii) the initial state name must be "1". NOTE: When using the yed
     * tool, at each state and transition the name must be defined in the
     * "description"
     *
     * @param filePath
     * @return LTS underlying the file
     */
    public static LTS graphToLTS(String filePath) {
        // reads the .graphml file and converts it to Graph (object that is in the
        // library
        // tinkerpop)
        Graph graph = ImportGraphmlFile.readGraphmlFile(filePath);

        LTS lts = new LTS();
        // state "invisible" that points to the initial state, because yed does not
        // allow
        // create a stateless edge start and end
        State_ invisibleState = null;
        String label;
        State_ iniState, endState;
        for (Edge edge : graph.getEdges()) {
            if (edge.getProperty("description") != null) {
                label = edge.getProperty("description").toString();
                iniState = new State_(edge.getVertex(Direction.OUT).getProperty("description").toString());
                endState = new State_(edge.getVertex(Direction.IN).getProperty("description").toString());

                // creates a new transition with the data retrieved from the
                // graphml
                Transition_ transicao = new Transition_(edge.getId().toString(), iniState, label, endState);
                // add the states
                lts.addState(iniState);
                lts.addState(endState);

                // check if the label has information (if it is not the edge that points to the
                // state
                // initial)
                // add transition
                if (!label.equals("")) {
                    lts.addTransition(transicao);
                }

                // add alphabet
                if (!lts.getAlphabet().contains(label) && !label.equals("")) {
                    lts.addToAlphabet(label);
                }

                // edge without label that points to the initial state
                if (label.equals("")) {
                    // save the state to be removed, since it is the invisible state that points to
                    // the
                    // initial state
                    invisibleState = iniState;
                    // defines the initial state, as what is pointed out by the unlabeled transition
                    lts.setInitialState(endState);
                }
            }
        }

        // if the graph does not use an arrow to indicate the initial state
        // it uses the label "1" as the state name
        if (lts.getInitialState() == null) {
            // verifies among the nodes what is the initial state
            for (Vertex vertex : graph.getVertices()) {
                if (vertex.getProperty("description") != null) {
                    State_ e = new State_(vertex.getProperty("description").toString());// , vertex.getId().toString()
                    if (e.getName().equals("1")) {
                        lts.setInitialState(e);
                    }
                }
            }
        } else {
            // if the graph was created with the arrow pointing to the initial state
            // removes invisible state
            lts.getStates().remove(invisibleState);
        }

        return lts;
    }

    /***
     * Converts the iolts from the .graphml file to an IOLTS object
     *
     * @param pathFile
     *            file directory
     * @param inputOutputParam
     *            if the inputs and outputs are differentiated by the symbols? /!
     * @param inputs
     *            the input alphabet
     * @param outputs
     *            the output alphabet
     * @return IOLTS underlying the .aut
     */
    public static IOLTS graphToIOLTS(String pathFile, boolean inputOutputParam, ArrayList<String> inputs,
                                     ArrayList<String> outputs) {
        // convert .graphml into LTS
        LTS lts = graphToLTS(pathFile);
        // creates a new LTS based IOLTS
        IOLTS iolts = new IOLTS(lts);

        ArrayList<String> e = new ArrayList<String>();
        ArrayList<String> s = new ArrayList<String>();

        if (!inputOutputParam) {
            List<String> alphabet = new ArrayList<>();
            for (String l : iolts.getAlphabet()) {
                alphabet.add(l.substring(1, l.length()));
            }
            iolts.setAlphabet(alphabet);

            List<Transition_> transitions = new ArrayList<>();
            for (Transition_ t : iolts.getTransitions()) {
                transitions.add(new Transition_(t.getIniState(), t.getLabel().substring(1, t.getLabel().length()),
                        t.getEndState()));
            }
            iolts.setTransitions(transitions);
        }

        // changes the set of inputs and outputs
        // if the input and output labels are differentiated by the symbols? /!
        if (!inputOutputParam) {
            for (String a : lts.getAlphabet()) {
                // if it starts with ! so it's an exit symbol
                if (a.charAt(0) == Constants.OUTPUT_TAG) {
                    s.add(a.substring(1, a.length()));
                }

                // if it starts with ? so it's an entrance symbol
                if (a.charAt(0) == Constants.INPUT_TAG) {
                    e.add(a.substring(1, a.length()));
                }
            }

            // add IOLTS inputs and outputs
            iolts.setInputs(e);
            iolts.setOutputs(s);
        } else {// if the symbols do not differentiate
            // the set of input and output are those passed by parameter
            iolts.setInputs(inputs);
            iolts.setOutputs(outputs);
        }

        return iolts;
    }
}
