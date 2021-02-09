/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import algorithm.Operations;
import util.Constants;

/**
 * Class LTS
 * 
 * @author Camila
 */
public class LTS {
	// list of states
	protected List<State_> states;
	// initial state
	protected State_ initialState;
	// list of transitions
	protected List<Transition_> transitions;
	// list of alphabet
	protected List<String> alphabet;

	/***
	 * Constructor with all parameters
	 * 
	 * @param states
	 * @param initialState
	 * @param alphabet
	 * @param transitions
	 */
	public LTS(List<State_> states, State_ initialState, List<String> alphabet, List<Transition_> transitions) {
		this.states = states;
		this.transitions = transitions;
		this.alphabet = alphabet;
		this.initialState = initialState;
	}

	/***
	 * Empty contrutor
	 */
	public LTS() {
		this.states = new ArrayList<State_>();
		this.transitions = new ArrayList<Transition_>();
		this.alphabet = new ArrayList<String>();
		this.initialState = null;
	}

	/**
	 * returns the set of states
	 * 
	 * @return states set of state
	 */
	public List<State_> getStates() {
		return this.states;
	}

	/**
	 * Alter set of states
	 * 
	 * @param states
	 *            set of states
	 * 
	 */
	public void setStates(List<State_> states) {
//this.states = new ArrayList<>();
//		for (State_ state_ : states) {
//			this.states.add(new State_(state_, true));
//		}
		 this.states = states;
	}

	/**
	 * return the initial states
	 * 
	 * @return initialState the initial state
	 */
	public State_ getInitialState() {
		return initialState;
	}

	/**
	 * Alter the initial state
	 * 
	 * @param initialState
	 * 
	 * 
	 */
	public void setInitialState(State_ initialState) {
		this.initialState = initialState;
	}

	/**
	 * Return all transitions
	 * 
	 * @return transitions list of transition
	 */
	public List<Transition_> getTransitions() {
		return transitions;
	}

	/**
	 * Alter list of transition
	 * 
	 * @param transitions
	 *            list of transition
	 * 
	 */
	public void setTransitions(List<Transition_> transitions) {
		// String label = "";
		this.transitions = transitions;

		// for (Transition_ t : this.transitions) {
		// if (!this.states.stream().filter(x ->
		// x.equals(t.getIniState())).findFirst().orElse(null).getTransitions()
		// .contains(t)) {
		// if (t.getLabel().contains(Objects.toString(Constants.INPUT_TAG))
		// || t.getLabel().contains(Objects.toString(Constants.OUTPUT_TAG))) {
		// this.states.stream().filter(x ->
		// x.equals(t.getIniState())).findFirst().orElse(null)
		// .addTransition(new Transition_(t.getIniState(),
		// t.getLabel().replace(Objects.toString(Constants.INPUT_TAG), "")
		// .replace(Objects.toString(Constants.OUTPUT_TAG), ""),
		// t.getEndState()));
		// } else {
		// this.states.stream().filter(x ->
		// x.equals(t.getIniState())).findFirst().orElse(null)
		// .addTransition(t);
		// }
		// }
		// }

	}

	/**
	 * Return alphabet
	 * 
	 * @return alphabet
	 */
	public List<String> getAlphabet() {
		return alphabet;
	}

	/**
	 * Alter alphabet
	 * 
	 * @param alphabet
	 * 
	 */
	public void setAlphabet(List<String> alphabet) {
//		HashSet hashSet_s_ = new LinkedHashSet<>(alphabet);
//		alphabet = new ArrayList<>(hashSet_s_);
//		this.alphabet = alphabet;
//		hashSet_s_ = null;
		
		this.alphabet = new ArrayList<>(new LinkedHashSet<>(alphabet));
	}

	/***
	 * Add the received parameter state to the LTS state list checking first whether
	 * the state already exists
	 * 
	 * @param state
	 * 
	 */
	public void addState(State_ state) {
		// verifies whether the state already exists in the set of LTS states
		if (!this.states.contains(state)) {
			this.states.add(state);
		}
	}

	/***
	 * Add the transition to the transition list, add the transition in alphabet
	 * list
	 * 
	 * @param transition
	 */
	public void addTransition(Transition_ t) {

		// verifies that the transition already exists in the transition list
		//if (!this.transitions.contains(t)) {
			this.transitions.add(t);

			// State_ s = states.stream().filter(x ->
			// x.equals(t.getIniState())).findFirst().orElse(null);
			// if (s != null) {
			//
			// if (t.getLabel().contains(Objects.toString(Constants.INPUT_TAG))
			// || t.getLabel().contains(Objects.toString(Constants.OUTPUT_TAG))) {
			// states.stream().filter(x ->
			// x.equals(t.getIniState())).findFirst().orElse(null)
			// .addTransition(new Transition_(t.getIniState(),
			// t.getLabel().replace(Objects.toString(Constants.INPUT_TAG), "")
			// .replace(Objects.toString(Constants.OUTPUT_TAG), ""),
			// t.getEndState()));
			// } else {
			// states.stream().filter(x ->
			// x.equals(t.getIniState())).findFirst().orElse(null).addTransition(t);
			// }
			// }
			//
	//	}

		// add the label in the alphabet list
		this.addToAlphabet(t.getLabel());
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

}
