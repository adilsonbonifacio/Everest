package model;

import org.apache.commons.collections.ListUtils;
import util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class IOLTS
 *
 * @author Camila
 * @author Caroline
 */
public class IOLTS extends LTS implements Cloneable{
    private List<String> inputs;            // List of all labels that are inputs
    private List<String> outputs;           // List of all labels that are outputs
    private List<State_> reachableStates;    // List of all states that are reachable through transitions

    /**
     * Empty constructor
     */
    public IOLTS() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.reachableStates = new ArrayList<>();
    }

    /**
     * Copies lts parameter's proprerties as underlying LTS for IOLTS model
     * @param lts LTS model
     */
    public IOLTS(LTS lts)
    {
        this.alphabet = lts.alphabet;
        this.initialState = lts.initialState;
        this.states = lts.states;
        this.transitions = lts.transitions;
    }

    /**
     * Constructor with all defined attributes as parameters
     * @param states
     * @param initialState
     * @param alphabet
     * @param transitions
     * @param inputs
     * @param outputs
     */
    public IOLTS(List<State_> states, State_ initialState, List<String> alphabet, List<Transition_> transitions, List<String> inputs, List<String> outputs) {
        this.states = states;
        this.initialState = initialState;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.inputs = inputs;
        this.outputs = outputs;

        this.reachableStates = new ArrayList<>();
    }

    /***
     * builds the underlying LTS from IOLTS
     * @return the underlying LTS
     */
    public LTS toLTS() {
        // Instances an LTS with IOLTS attributes
        LTS lts = new LTS(this.states, this.initialState, new ArrayList<>(ListUtils.union(this.inputs, this.outputs)),
                this.transitions);
        return lts;
    }

    /**
     * Adds new input label to the list of inputs.
     * @param input new input label to be added
     * @return list of all input labels
     */
    public List<String> addInput(String input)
    {
        if(!this.alphabet.contains(input))
        {
            this.alphabet.add(input);
            this.inputs.add(input);
        }
        return this.inputs;
    }

    /**
     * Adds new input labels from list to list of inputs
     * @param inputs new input labels to be added
     * @return list of all input labels
     */
    public List<String> addAllInputs(List<String> inputs)
    {
        for(String input : inputs)
        {
            if(!this.alphabet.contains(input))
            {
                this.alphabet.add(input);
                this.inputs.add(input);
            }
        }
        return this.inputs;
    }

    /**
     * Adds new output label to the list of inputs.
     * @param output new output label to be added
     * @return list of all output labels
     */
    public List<String> addOutput(String output)
    {
        if(!this.alphabet.contains(output))
        {
            this.alphabet.add(output);
            this.outputs.add(output);
        }
        return this.outputs;
    }

    /**
     * Adds new output labels from list to list of output
     * @param outputs new ouput labels to be added
     * @return list of all output labels
     */
    public List<String> addAllOutputs(List<String> outputs)
    {
        for(String output : outputs){
            if(!this.alphabet.contains(output))
            {
                this.alphabet.add(output);
                this.outputs.add(output);
            }
        }
        return this.outputs;
    }

    /**
     * Adds state as reachable state
     * @param state new reachable state
     * @return list of reachable states
     */
    public List<State_> addReachableState(State_ state)
    {
        this.reachableStates.add(state);
        return this.reachableStates;
    }

    /**
     * Getter for inputs
     * @return input list
     */
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * set input labels
     * @param input set of input labels
     */
    public void setInputs(List<String> input) {
        this.inputs = input;
    }

    /**
     * Getter for outputs
     * @return output list
     */
    public List<String> getOutputs() {
        return outputs;
    }

    /**
     * Alter the set of output labels
     * @param outputs the set of output labels
     */
    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    /**
     * Getter for reachable states
     * @return reachable states list
     */
    public List<State_> getReachableStates() {
        return reachableStates;
    }

    /**
     * Getter for input transitions.
     */
    public List<Transition_> getInputTransitions()
    {
        List<Transition_> itrs = new ArrayList<>();

        for(Transition_ transition : this.transitions)
        {
            if(this.inputs.contains(transition.getLabel()))
            {
                itrs.add(transition);
            }
        }

        return itrs;
    }

    /**
     * Getter for output transitions.
     */
    public List<Transition_> getOutputTransitions()
    {
        List<Transition_> otrs = new ArrayList<>();

        for(Transition_ transition : this.transitions)
        {
            if(this.outputs.contains(transition.getLabel()))
            {
                otrs.add(transition);
            }
        }

        return otrs;
    }

    /***
     * builds the underlying automaton from IOLTS
     *
     * @return the underlying automaton
     */
    public Automaton_ ioltsToAutomaton() {

        // build automaton from LTS
        return this.toLTS().ltsToAutomaton();
    }

    /**
     * Returns list of quiescent states
     * @return
     */
    public List<State_> quiescentStates() {
        List<State_> notQuiescentStates = new ArrayList<>();
        for (Transition_ t : transitions) {
            // checks whether the transition contains the initial state of the transition
            // and the
            // output label
            if (outputs.contains(t.getLabel())) {
                notQuiescentStates.add(t.getIniState());
            }
        }
        List<State_> quiescentStates = new ArrayList<>(this.getStates());
        quiescentStates.removeAll(notQuiescentStates);
        return quiescentStates;
    }

    /***
     * Return list of output labels from state received as parameter
     *
     * @param e
     *            state
     * @return list of string containing output labels
     */
    public List<String> outputsOfState(State_ e) {
        List<String> label = new ArrayList<String>();

        if (e != null) {
            for (Transition_ t : transitions) {
                // checks whether the transition contains the initial state of the transition
                // and the
                // output label
                if (t.getIniState().getName().toString().equals(e.getName().toString())
                        && ((outputs.contains(t.getLabel())) || t.getLabel().equals(Constants.DELTA))) {
                    label.add(t.getLabel());
                }
            }
        }

        return label;
    }

    /**
     * Returns whether or not model is input-enabled
     * @return
     */
    public boolean isInputEnabled() {
        for (String l : getInputs()) {
            for (State_ s : getStates()) {
                if (reachedStates(s.getName(), l).size() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ???
     * @param labelIniState
     * @return
     */
    public List<String> labelNotDefinedOnState(String labelIniState) {
        List<String> alphab = new ArrayList(this.getAlphabet());
        alphab.removeAll(transitionsByIniState(new State_(labelIniState)));

        List<String> alphabet_new = new ArrayList<>();
        for (String a : alphab) {
            if (this.getInputs().contains(a)) {
                alphabet_new.add(Constants.INPUT_TAG + a);
            } else {
                alphabet_new.add(Constants.OUTPUT_TAG + a);
            }
        }

        alphab = null;
        return alphabet_new;
    }

    /**
     * ???
     * @param param_iolts
     * @return
     */
    public int numberDistinctTransitions(IOLTS param_iolts) {
        int this_n_transition = new Integer(getTransitions().size());
        int param_n_transition = new Integer(param_iolts.getTransitions().size());
        List<Transition_> transitions_max, transitions_min;
        int n_distinct_transitions = 0;
        if (this_n_transition <= param_n_transition) {
            transitions_max = new ArrayList<>(param_iolts.getTransitions());
            transitions_min = new ArrayList<>(getTransitions());
        } else {
            transitions_max = new ArrayList<>(getTransitions());
            transitions_min = new ArrayList<>(param_iolts.getTransitions());
        }

        for (Transition_ t : transitions_max) {
            if (!transitions_min.contains(t)) {
                n_distinct_transitions++;
            }
        }

        transitions_max = null;
        return n_distinct_transitions;
    }

    /**
     * ???
     * @param param_iolts
     * @return
     */
    public List<Transition_> equalsTransitions(IOLTS param_iolts) {
        List<Transition_> transitions_ = new ArrayList<>();

        int this_n_transition = new Integer(getTransitions().size());
        int param_n_transition = new Integer(param_iolts.getTransitions().size());
        List<Transition_> transitions_max, transitions_min;
        // int n_distinct_transitions = 0;
        if (this_n_transition <= param_n_transition) {
            transitions_max = new ArrayList<>(param_iolts.getTransitions());
            transitions_min = new ArrayList<>(getTransitions());
        } else {
            transitions_max = new ArrayList<>(getTransitions());
            transitions_min = new ArrayList<>(param_iolts.getTransitions());
        }

        for (Transition_ t : transitions_max) {
            if (!transitions_min.contains(t)) {
                // n_distinct_transitions++;
            } else {
                transitions_.add(t);
            }
        }

        transitions_max = null;
        return transitions_;
    }

    /***
     * Overwriting the toString method, with the separation between the input and
     * output label
     *
     * @return the string describing the IOLTS
     */
    @Override
    public String toString() {
        // calls LTS toString
        String s = super.toString();
        // add description of input labels
        s += ("##############################\n");
        s += ("           Inputs \n");
        s += ("##############################\n");
        s += ("length: " + this.getInputs().size() + "\n");
        for (String e : this.getInputs()) {
            s += ("[" + e + "] - ");
        }
        // add description of output labels
        s += ("\n##############################\n");
        s += ("           Outputs \n");
        s += ("##############################\n");
        s += ("length: " + this.getOutputs().size() + "\n");
        for (String e : this.getOutputs()) {
            s += ("[" + e + "] - ");
        }
        return s;
    }

    /**
     * Generates copy of IOLTS and all components
     * @param base
     * @return
     */
    public static IOLTS copy(IOLTS base) {
        // Cria novo modelo
        IOLTS copy = new IOLTS();
        copy.setAlphabet(new ArrayList<>());
        copy.setInputs(new ArrayList<>());
        copy.setOutputs(new ArrayList<>());
        copy.setTransitions(new ArrayList<>());

        // Copia labels
        for(String in : base.getInputs())
        {
            String input = new String(in);
            copy.addInput(input);
            copy.addLabel(input);
        }

        for(String out : base.getOutputs())
        {
            String output = new String(out);
            copy.addOutput(output);
            copy.addLabel(output);
        }

        // Copia estados
        for(State_ state : base.getStates())
        {
            // Identificação do estado
            State_ s = new State_();
            s.setId(state.getId());
            s.setInfo(state.getInfo());
            s.setName(state.getName());
            s.setVisited(state.isVisited());
            copy.addState(s);

            // Verifica se é o estado inicial
            if(s.getName().equals(base.getInitialState().getName()))
            {
                copy.setInitialState(s);
                copy.addReachableState(s);
            }
        }

        // Copia transições
        for(Transition_ t : base.getTransitions())
        {
            State_ s1 = null;
            State_ s2 = null;

            // Busca estado s1
            for(State_ state : copy.getStates())
            {
                if(t.getIniState().getName().equals(state.getName()))
                {
                    s1 = state;
                    break;
                }
            }

            // Busca estado s2
            for(State_ state : copy.getStates())
            {
                if(t.getEndState().getName().equals(state.getName()))
                {
                    s2 = state;
                    break;
                }
            }

            // Cria nova transição
            Transition_ transition = new Transition_(s1, t.getLabel(), s2);
            copy.addTransition(transition);

            // Adiciona transição ao estado inicial
            s1.addTransition(transition);

            // Adiciona adjacências da origem
            s1.addAdj(s2);

            // Adiciona estado destino aos alcançáveis
            copy.addReachableState(s2);

            // Adiciona aos conjuntos de inputs ou outputs do estado
            if(copy.getInputs().contains(t.getLabel()))
            {
                s1.addIn(t.getLabel());
            }

            if(copy.getOutputs().contains(t.getLabel()))
            {
                s1.addOut(t.getLabel());
            }
        }

        return copy;
    }

    /**
     * Checks if state is reachable
     * returns list of states with transitions to it
     * @param state
     * @return
     */
    public List<State_> checkReachable(State_ state)
    {
        List<State_> adjacent = new ArrayList<>();
        for(State_ s : states)
        {
            if(s.getAdj().contains(state))
            {
                adjacent.add(s);
            }
        }
        return adjacent;
    }

    /**
     * implements clone method from interface Cloneable
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /***
     * Add the quiescent transition
     *
     */
    public void addQuiescentTransitions() {// quiescent
        this.addToAlphabet(Constants.DELTA);

        for (State_ s : this.quiescentStates()) {
            this.addTransition(new Transition_(s, Constants.DELTA, s));
            this.outputs.add(Constants.DELTA);
        }
    }

    /***
     * Removes quiescent transitions
     *
     */
    public void removeQuiescentTransitions() {// quiescent
        this.addToAlphabet(Constants.DELTA);

        List<Transition_> toRemove = new ArrayList<>();

        for (Transition_ t : this.transitions) {
            if (t.getLabel().equals(Constants.DELTA))
                toRemove.add(t);
        }
        this.transitions.removeAll(toRemove);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        } else {
            IOLTS iolts = (IOLTS) obj;

            // Compara estado inicial
            if(!iolts.getInitialState().equals(initialState))
            {
                return false;
            }

            // Compara estados
            for(State_ s : iolts.getStates())
            {
                boolean result = false;

                for(State_ state : states)
                {
                    if(state.equals(s))
                    {
                        result = true;
                        break;
                    }
                }

                if(!result)
                    return false;
            }

            // Compara transições
            for(Transition_ t : iolts.getTransitions())
            {
                boolean result = false;

                for(Transition_ tr: transitions)
                {
                    // Compara componentes
                    if(t.getIniState().equals(tr.getIniState())
                        && t.getEndState().equals(tr.getEndState())
                        && t.getLabel().equals(tr.getLabel()))
                    {
                        result = true;
                        break;
                    }
                }

                if(!result)
                    return false;
            }

            return true;
        }
    }

    public List<Transition_> incoming(State_ s)
    {
        List<Transition_> transitions = new ArrayList<>();

        for (State_ origin : this.states)
        {
            for (Transition_ t : origin.getTransitions()) {
                if(t.getEndState().equals(s)) {
                    transitions.add(t);
                }
            }
        }

        return transitions;
    }

}
