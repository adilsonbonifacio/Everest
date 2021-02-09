/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import util.Constants;

/**
 * Class State_
 * 
 * @author Camila
 */
public class State_ {
	// name of state
	private String name;
	// id used for integration with libraries
	private int id;
	// description used in the intersection and to get the words
	private String info;
	// to get path
	private boolean visited;

	private List<Transition_> transitions;

	public void addTransition(Transition_ t) {
		//if (!transitions.contains(t)) {
			//if(t.getLabel().contains(Objects.toString(Constants.INPUT_TAG)) || t.getLabel().contains(Objects.toString(Constants.OUTPUT_TAG))) {
				//transitions.add(new Transition_(t.getIniState(), t.getLabel().replace(Objects.toString(Constants.INPUT_TAG), "").replace(Objects.toString(Constants.OUTPUT_TAG), ""),t.getEndState()));
			//}else {
				transitions.add(t);
			//}			
		//}
	}

	public List<Transition_> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition_> transitions) {		
		//this.transitions = new ArrayList<>(transitions);
		this.transitions =transitions;
	}

	/***
	 * empty constructor
	 */
	public State_() {
		this.name = "";
		//this.transitions = new ArrayList<>();
	}

	/***
	 * Constructor receives a state and generates a copy of it
	 * 
	 * @param state
	 */
	public State_(State_ state) {
		this.id = state.id;
		this.name = state.name;
		this.info = state.info;
		//this.transitions = new ArrayList<>();
	}
	
//	public State_(State_ state, boolean a) {
//		this.id = state.id;
//		this.name = state.name;
//		this.info = state.info;
//		//this.transitions = new ArrayList<>(state.transitions);
//	}

	/***
	 * Constructor receives the state name
	 * 
	 * @param name
	 */
	public State_(String name) {
		this.name = name;
		//this.transitions = new ArrayList<>();
	}

	/***
	 * Constructor receives the state name, info
	 * 
	 * @param name,
	 *            info
	 */
	public State_(String name, String info) {
		this.name = name;
		this.info = info;
		//this.transitions = new ArrayList<>();
	}

	/***
	 * Constructor receives state name and id, used for integration with libraries
	 * 
	 * @param state
	 *            name
	 * @param state
	 *            id
	 */
	public State_(String name, int id) {
		this.name = name;
		this.id = id;
		//this.transitions = new ArrayList<>();
	}

	/**
	 * Return state name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Alter state name
	 * 
	 * @param name
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/***
	 * Return state id
	 * 
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/***
	 * Alter state id
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/***
	 * Retorn info
	 * 
	 * @return info
	 */
	public String getInfo() {
		return info;
	}

	/***
	 * alter info
	 * 
	 * @param info
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/***
	 * Return visited
	 * 
	 * @return visited
	 */
	public boolean isVisited() {
		return visited;
	}

	/***
	 * alter visited
	 * 
	 * @param visited
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/***
	 * Overwrites the equals of the state, equality between states is only
	 * considering the name
	 */
	@Override
	public boolean equals(Object obj) {
		//boolean result;
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			State_ _state = (State_) obj;
			return this.name.equals(_state.name);
		}
		//return result;
	}

	/***
	 * Method to string override
	 * 
	 * @return name of state
	 */
	@Override
	public String toString() {
		return name;
	}

	/***
	 * Overwrites state hash based on name and id
	 */
	@Override
	public int hashCode() {
		final int prime = 7;
		int result = 56;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + id;
		return result;
	}

}
