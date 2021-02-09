/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import util.Constants;

/**
 * Class Transition_
 *
 * @author Camila
 */
public class Transition_ {
    // transition label
    private String label;
    // type of transition INPUT/OUTPUT
    //private TransitionType tType;
    // state from
    private State_ iniState;
    // state to
    private State_ endState;
    // id used for libraries
    private String id;

    private TransitionType type;

    /***
     * empty constructor
     */
//	public Transition_() {
//	}

    /***
     * Contrast with all parameters
     *
     * @param id
     * @param iniState
     * @param label
     * @param endState
     */
    public Transition_(String id, State_ iniState, String label, State_ endState) {
        this.id = id;
        this.iniState = iniState;
        this.label = label;
        this.endState = endState;
    }

    /***
     * Contrutor with inistate, label, endState
     *
     * @param iniState
     * @param label
     * @param endState
     */
    public Transition_(State_ iniState, String label, State_ endState) {
        this.iniState = iniState;
        this.label = label;
        this.endState = endState;
    }

    public Transition_(State_ iniState, String label, State_ endState, TransitionType t) {
        this.iniState = iniState;
        this.label = label;
        this.endState = endState;
        this.type = t;
    }

    public Transition_(Transition_  t) {
        this.iniState = t.getIniState();
        this.label = t.getLabel();
        this.endState = t.getEndState();
    }

    /**
     * Returns the transition label
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Alter label
     *
     * @param label
     *
     */
    public void setLabel(String label) {
        this.label = label;
    }

//	/**
//	 * Retorn type of transition
//	 *
//	 * @return tType
//	 */
//	public TransitionType getType() {
//		return tType;
//	}
//
//	/**
//	 * Alter o type of transition
//	 *
//	 * @param tType
//	 *
//	 */
//	public void setType(TransitionType tType) {
//		this.tType = tType;
//	}

    /**
     * Retorn the iniState
     *
     * @return iniState
     */
    public State_ getIniState() {
        return iniState;
    }

    /**
     * Alter the iniState
     *
     * @param iniState
     *
     */
    public void setIniState(State_ iniState) {
        this.iniState = iniState;
    }

    /**
     * Retorn the endState
     *
     * @return endState
     */
    public State_ getEndState() {
        return endState;
    }

    /**
     * Alter the endState
     *
     * @param endState
     *
     */
    public void setEndState(State_ endState) {
        this.endState = endState;
    }

    /***
     * Retorn id
     *
     * @return id
     */
    private String getId() {
        return id;
    }

    /***
     * Alter id
     *
     * @param id
     */
    private void setId(String id) {
        this.id = id;
    }

    /***
     * Equals method overwrite
     *
     * @return if the transitions are the same
     */
    @Override
    public boolean equals(Object obj) {

        //boolean result;
        // if the transition is null, or of different class
        if (obj == null || obj.getClass() != getClass()) {
            // transition not is equal
            return false;
        } else {
            Transition_ t = (Transition_) obj;

            // returns if the initial state, label, and final state are equal
            return t.iniState.equals(iniState) && t.endState.equals(endState) && label.equals(t.label);
        }
        //return result;
    }

    /***
     * ToString method overwrite
     */
    @Override
    public String toString() {
        String ini_ = getIniState().getName().replace(",", "_");
        String end_ = getEndState().getName().replace(",", "_");
        String newline = System.getProperty("line.separator");

        if (getLabel().equals(Constants.DELTA)) {
            return "(" + ini_ + "," + Constants.DELTA_TXT + "," + end_ + ")" + newline;// t.getLabel()
        } else {

            if (getType()==null) {
                return "[" + iniState + " - " + label + " - " + endState + "] \n";
            }

            if (getType().equals(TransitionType.INPUT)) {
                return "(" + ini_ + "," + Constants.INPUT_TAG + getLabel() + "," + end_
                        + ")" + newline;
            }

            if (getType().equals(TransitionType.OUTPUT)) {
                return"(" + ini_ + "," + Constants.OUTPUT_TAG + getLabel() + "," + end_
                        + ")" + newline;
            }
        }

        return "";
        //return "[" + iniState + " - " + label + " - " + endState + "] \n";
    }

    private TransitionType getType() {
        return type;
    }

    private void setType(TransitionType type) {
        this.type = type;
    }

    /***
     * Enum type of transition if it is IN or OUT
     *
     * @author camila
     *
     */
    public enum TransitionType {
        INPUT, OUTPUT
    };

}
