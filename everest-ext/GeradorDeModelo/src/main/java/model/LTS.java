package model;

import algorithm.Operations;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Class LTS
 *
 * @author Camila
 * @author Caroline
 */
public class LTS {
    protected String name;                     // Identifier - file name
    protected List<State_> states;             // List of all states in the LTS
    protected State_ initialState;             // Initial state
    protected List<Transition_> transitions;   // List of all transitions in the LTS
    protected List<String> alphabet;          // List of all symbols used by the LTS

    /**
     * Empty constructor
     */
    public LTS() {
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.initialState = null;
    }

    /**
     * All parameters constructor
     * @param states All states in the LTS
     * @param initialState Initial state
     * @param alphabet  All symbols used by the LTS
     * @param transitions All transitions in the LTS
     */
    public LTS(List<State_> states, State_ initialState, List<String> alphabet, List<Transition_> transitions)
    {
        this.states = states;
        this.initialState = initialState;
        this.alphabet = alphabet;
        this.transitions = transitions;
    }

    /**
     * Sets file name as identifier for the model.
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets model/file identifier.
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Adds new state to the list of states in the LTS
     * @param state new state to add
     * @return list of states
     */
    public List<State_> addState(State_ state)
    {
        for(State_ s : this.states)
        {
            if(s.equals(state)) return this.states;
        }

        this.states.add(state);
        return this.states;
    }

    /**
     * Adds new states from list to list of states in the LTS
     * @param states new states to add
     * @return list of states
     */
    public List<State_> addAllStates(List<State_> states)
    {
        this.states.addAll(states);
        return this.states;
    }

    /**
     * Adds new transition to the list of transitions in the LTS
     * @param t new transition to add
     * @return list of transitions
     */
    public void addTransition(Transition_ t)
    {
        this.transitions.add(t);
        this.addToAlphabet(t.getLabel());
    }

    /**
     * Adds new label to the list of labels in the LTS
     * @param label new label to add
     * @return list of labels
     */
    public List<String> addLabel(String label)
    {
        if(!this.alphabet.contains(label))
        {
            this.alphabet.add(label);
        }
        return this.alphabet;
    }

    /**
     * Getter for states
     * @return list of states in the LTS
     */
    public List<State_> getStates() {
        return states;
    }

    /**
     * Alter set of states
     * @param states set of states
     */
    public void setStates(List<State_> states) {
        this.states = states;
    }

    /**
     * Getter for initial state in the LTS
     * @return initial state
     */
    public State_ getInitialState() {
        return initialState;
    }

    /**
     * Sets initial state for the LTS
     * @param initialState
     */
    public void setInitialState(State_ initialState) {
        this.initialState = initialState;
    }

    /**
     * Sets list as transitions list for the LTS
     * @param transitions
     */
    public void setTransitions(List<Transition_> transitions) { this.transitions = transitions; }

    /**
     * Getter for transitions in the LTS
     * @return list of transitions
     */
    public List<Transition_> getTransitions() {
        return transitions;
    }

    /**
     * Getter for the LTS alphabet
     * @return list of labels
     */
    public List<String> getAlphabet() {
        return alphabet;
    }

    /**
     * Alter alphabet
     * @param alphabet
     */
    public void setAlphabet(List<String> alphabet) {
        this.alphabet = new ArrayList<>(new LinkedHashSet<>(alphabet));
    }

    /***
     * Checks whether there is a transition from the initial state and label
     * received from parameter, and returns true/false
     *
     * @param labelIniState
     * @param labelTransition
     * @return
     */
    public boolean transitionExists(String labelIniState, String labelTransition) {
        return this.getTransitions().stream().parallel()
                .filter(x -> x.getIniState().getName().equals(labelIniState) && x.getLabel().equals(labelTransition))
                .findFirst().orElse(null) != null;
        //return result != null;
    }

    /***
     * Checks whether there is a transition from the initial state and label
     * received from parameter, and returns all state reached by these transitions
     *
     * @param labelIniState
     * @param labelTransition
     * @return list of states reached
     */
    public List<State_> reachedStates(String labelIniState, String labelTransition) {
        // list of reached states
        List<State_> endStates = new ArrayList<State_>();

        // State_ state = states.stream().filter(x ->
        // x.getName().equals(labelIniState)).findFirst().orElse(null);
        // if (state != null) {
        // state = states.stream().filter(x ->
        // x.getName().equals(labelIniState)).findFirst().orElse(null);
        // if (state != null) {
        // List<Transition_> filtredTransitions = state.getTransitions();
        //
        // for (Transition_ t : filtredTransitions) {
        // if (t.getLabel().equals(labelTransition)) {
        // endStates.add(t.getEndState());
        //
        // }
        // }
        // }
        // }

        for (Transition_ t : transitions) {
            // verifies whether the transition contains the iniState of the transition
            // and the label passed parameter
            if (t.getIniState().getName().toString().equals(labelIniState.toString())
                    && t.getLabel().toString().equals(labelTransition.toString())) {
                // adds the status reached
                endStates.add(t.getEndState());

            }
        }

        return endStates;
    }

    /***
     * Retrieves the transitions that depart from the state passed by parameter
     *
     * @param state
     * @return transitionsOfState
     */
    public List<Transition_> transitionsByIniState(State_ state) {
        List<Transition_> transitionsOfState = new ArrayList<Transition_>();

        // State_ state_ = states.stream().filter(x ->
        // x.equals(state)).findFirst().orElse(null);
        // if (state_ != null) {
        // List<Transition_> filtredTransitions = state_.getTransitions();
        // for (Transition_ t : filtredTransitions) {
        // transitionsOfState.add(t);
        // }
        // }

        for (Transition_ t : transitions) {
            // verifies that the transition starts from the parameter state
            if (t.getIniState().getName().equals(state.getName())) {
                // add transition to list
                transitionsOfState.add(t);
            }
        }

        return transitionsOfState;
    }

    /***
     * Constructs the deterministic automaton underlying the LTS
     *
     * @return the automaton underlying the LTS
     */
    public Automaton_ ltsToAutomaton() {
        // create automaton
        Automaton_ as = new Automaton_(this.states, this.initialState, this.alphabet, this.states, this.transitions);
        // convert to deterministic
        return Operations.convertToDeterministicAutomaton(as);
    }

    public void makeInitiallyConnected() {
        List<State_> toVisit = new ArrayList<>();
        List<State_> visited = new ArrayList<>();

        State_ current;
        toVisit.add(this.initialState);

        // find initially connected states
        while (toVisit.size() != 0) {
            current = toVisit.remove(0);
            visited.add(current);
            for (Transition_ t : transitionsByIniState(current)) {
                if (!visited.contains(t.getEndState())) {
                    toVisit.add(t.getEndState());
                }
            }
        }

        // find states not initially connected
        List<State_> notInitiallyConected = new ArrayList<>(this.getStates());
        notInitiallyConected.removeAll(visited);

        List<Transition_> transitionsToRemove = new ArrayList<>();
        for (Transition_ t : this.getTransitions()) {
            if (notInitiallyConected.contains(t.getIniState()) || notInitiallyConected.contains(t.getEndState())) {
                transitionsToRemove.add(t);
            }
        }

        this.getStates().removeAll(notInitiallyConected);
        this.getTransitions().removeAll(transitionsToRemove);

        toVisit = null;
        visited = null;
        current = null;
        notInitiallyConected = null;
        transitionsToRemove = null;
    }

    /**
     * Sets all states in the LTS as not visited.
     */
    public void markAllNotVisited()
    {
        for (State_ s : states) {
            s.setVisited(false);
        }
    }

    /**
     * Sets all states' adjacency lists to empty.
     */
    public void resetAdjacencyLists()
    {
        for (State_ s : states) {
            s.getAdj().removeAll(s.getAdj());
        }
    }

    /***
     * ToString Method Overwrite
     *
     * @return the string describing the LTS
     */
    @Override
    public String toString() {
        String s = "";
        // initial state
        s += ("##############################\n");
        s += ("           Initial State \n");
        s += ("##############################\n");
        s += ("[" + initialState.getName() + "]" + "\n\n");

        // states
        s += ("##############################\n");
        s += ("           States \n");
        s += ("##############################\n");
        s += ("Length: " + this.states.size() + "\n");
        for (State_ e : this.states) {
            s += ("[" + e.getName() + "]-");
        }

        // transitions
        s += ("\n\n##############################\n");
        s += ("         Transitions\n");
        s += ("##############################\n");
        s += ("Length: " + this.transitions.size() + "\n");
        for (Transition_ t : this.transitions) {
            s += (t.getIniState().getName() + " - " + t.getLabel() + " - " + t.getEndState().getName() + "\n");
        }

        // alphabet
        s += ("\n##############################\n");
        s += ("         Alphabet\n");
        s += ("##############################\n");
        s += "[";
        for (String t : this.alphabet) {
            s += (t + " - ");
        }
        s += "]\n";

        return s;
    }

    /***
     * Add letter to alphabet
     *
     * @param letter
     *
     */
    public void addToAlphabet(String letter) {
        // verifies whether letter already exists in the alphabet
        if (!this.alphabet.contains(letter)) {
            this.alphabet.add(letter);
        }
    }

}
