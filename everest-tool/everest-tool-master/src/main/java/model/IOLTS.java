/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import util.Constants;

/**
 * Class IOLTS
 * 
 * @author Camila
 */
public class IOLTS extends LTS implements Cloneable {
	private List<String> inputs;
	private List<String> outputs;

	/***
	 * Empty constructor
	 */
	public IOLTS() {
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
	}

	public IOLTS(List<State_> states, State_ initialState, List<String> alphabet, List<Transition_> transitions,
			List<String> inputs, List<String> outputs) {
		super(states, initialState, alphabet, transitions);
		this.inputs = inputs;
		this.outputs = outputs;
	}

	/***
	 * Constructor receives an LTS and changes the attributes of the IOLTS
	 * 
	 * @param lts
	 */
	public IOLTS(LTS lts) {
		// calls super-class methods to initialize the parameters
		this.initialState = lts.getInitialState();
		this.states = lts.getStates();
		this.transitions = (lts.getTransitions());
		this.alphabet = (lts.getAlphabet());
	}

	/**
	 * Returns the inputs label
	 * 
	 * @return input label
	 */
	public List<String> getInputs() {
		return inputs;
	}

	/**
	 * set input labels
	 * 
	 * @param input
	 *            set of input labels
	 */
	public void setInputs(List<String> input) {
		this.inputs = input;
	}

	/**
	 * return the set of output label
	 * 
	 * @return outputs the set of output labels
	 */
	public List<String> getOutputs() {
		return outputs;
	}

	/**
	 * Alter the set of output labels
	 * 
	 * @param outputs
	 *            the set of output labels
	 */
	public void setOutputs(List<String> out) {
		this.outputs = out;
	}

	public void addInput(String inp) {
		if (!this.inputs.contains(inp)) {
			this.inputs.add(inp);
		}
	}

	public void addOutput(String out) {
		if (!this.outputs.contains(out)) {
			this.outputs.add(out);
		}
	}

	/***
	 * builds the underlying LTS from IOLTS
	 * 
	 * @return the underlying LTS
	 */
	public LTS toLTS() {
		// in automato there is no distinction between input and output labels, so the
		// input and output labels are joined to form the alphabet
		// List<String> alphabet = new ArrayList<>(ListUtils.union(this.inputs,
		// this.outputs);
		// Instances an LTS with IOLTS atributtes
		LTS lts = new LTS(this.states, this.initialState, new ArrayList<>(ListUtils.union(this.inputs, this.outputs)),
				this.transitions);

		return lts;
	}

	/***
	 * builds the underlying automaton from IOLTS
	 * 
	 * @return the underlying automaton
	 */
	public Automaton_ ioltsToAutomaton() {

		// build automato from LTS
		return this.toLTS().ltsToAutomaton();
	}

	/***
	 * Add the quiescent transition
	 * 
	 * @param transition
	 */
	public void addQuiescentTransitions() {// quiescent
		this.addToAlphabet(Constants.DELTA);

		for (State_ s : this.quiescentStates()) {
			this.addTransition(new Transition_(s, Constants.DELTA, s));
			this.outputs.add(Constants.DELTA);
		}
	}

	/***
	 * Add the quiescent transition
	 * 
	 * @param transition
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

	public List<State_> quiescentStates() { // quiescent
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
	 * return list of output labels from state received per parameter
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

	// public IOLTS removeDeltaTransitions() {
	// List<Transition_> toRemove = new ArrayList<>();
	// for (Transition_ t : this.transitions) {
	// if(t.getLabel().equals(Constants.DELTA)) {
	// toRemove.add(t);
	// }
	// }
	//
	// this.transitions.removeAll(toRemove);
	//
	// return this;
	// }

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
	 * implements clone method from interface Cloneable
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
