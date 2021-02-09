/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.collections.ListUtils;
import model.Automaton_;
import model.State_;
import model.IOLTS;
import model.Transition_;
import util.Constants;
import util.ModelImageGenerator;

/**
 * Class IOCO Conformance
 * 
 * @author Camila
 */
public class IocoConformance {

	/***
	 * Verify if a implementation is IOCO conforms to specification, returns the
	 * resultant automaton of this verification
	 * 
	 * @param S
	 *            IOLTS model of specification
	 * @param I
	 *            IOLTS implementation
	 * @return
	 */
	public static Automaton_ verifyIOCOConformance(IOLTS S, IOLTS I) {//, int nTestCases
		// set alphabet from models
		S.setAlphabet(new ArrayList<>(new LinkedHashSet<>(ListUtils.union(I.getAlphabet(),S.getAlphabet()))));
		I.setAlphabet(new ArrayList<>(new LinkedHashSet<>(ListUtils.union(I.getAlphabet(),S.getAlphabet()))));
		
		// build the fault model, containing all fail behaviors based on specification
		Automaton_ at = faultModelIoco(S);
	
		// automaton underlying the implementation
		Automaton_ ai = I.ioltsToAutomaton();
		
//		//make iut inputenabled
//		for(State_ s : ai.getStates()) {
//			for (String l: I.getInputs()) {
//				if(!I.transitionExists(s.getName(), l)) {
//					ai.addTransition(new Transition_(s,l,s));
//				}
//			}
//		}

		// intersection between the implementation and failure model to find fault
		Automaton_ ab = Operations.intersection(at, ai);// , nTestCases

		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// System.out.println("<<<<<<<<<<<<<<<<<<<< verification IOCO conformance
		// >>>>>>>>>>>>>>>>>>>>>");
		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		// System.out.println("Fault model");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		// System.out.println(at);
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		// System.out.println("implementation");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		// System.out.println(ai);
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		// System.out.println("Intersection [Fault model X Implementation]");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		// System.out.println(ab);

		Operations.addTransitionToStates(S, I);

		at = null;
		ai = null;
		System.gc();
		return ab;
	}

	/***
	 * Build the fault model based on specification model
	 * 
	 * @param S
	 *            specification model
	 * 
	 * @return the fault model automaton
	 */
	private static Automaton_ faultModelIoco(IOLTS S) {

		// automaton underlying the specification IOLTS S
		Automaton_ as = S.ioltsToAutomaton();

		// automaton complement of specification
		Automaton_ aCompS = Operations.complement(new Automaton_(as));

		// automaton D with the desired behaviors
		Automaton_ ad = modelD(S);

		Automaton_ faultModel = Operations.intersection(ad, aCompS);//, null

		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// System.out.println("<<<<<<<<<<<<<<<<<<<<
		// modeloDeFalha>>>>>>>>>>>>>>>>>>>>>");
		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		// System.out.println("Automato Especificação");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		// System.out.println(as);
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		// System.out.println("Automato Complemento Especificação");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		// System.out.println(aCompS);
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		// System.out.println("Automato D");
		// System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		// System.out.println(ad);

		as = null;
		ad = null;
		aCompS = null;
		// intersection between the desirable behavior and the complement of the
		// specification (which is not in the specification)
		return faultModel;
	}

	/***
	 * Construct the automaton D with the desired behaviors based on the
	 * specification received by parameter
	 * 
	 * @param S
	 *            automaton underlying the specification
	 * 
	 * @return automaton D with the desirable behaviors
	 */
	private static Automaton_ modelD(IOLTS S) {
		// automaton D
		Automaton_ as = new Automaton_();
		// define the initial state
		as.setInitialState(new State_(S.getInitialState().getName()));
		// define the states
		as.setStates(new ArrayList<>(S.getStates()));
		// create new fail state
		State_ d = new State_("fail");
		as.addState(d);
		// define the alphabet like the union beteen inputs and outputs
		as.setAlphabet(new ArrayList(ListUtils.union(S.getInputs(), S.getOutputs())));
		// add transitions, process when have TAU
		// as.setTransitions(Operations.processTauTransition(S.getTransitions()));
		as.setTransitions(new ArrayList(S.getTransitions()));
		// define the fail state like the unique final state
		as.addFinalStates(d);

		for (State_ e : new ArrayList<>(as.getStates())) {
			for (String l : new ArrayList<>(S.getOutputs())) {// as.getAlphabet()
				// if it is an output label and there is no transition starting from "e" with
				// the label "l"
				// then a transition is created pointing to the failure state
				if (!as.transitionExists(e.getName(), l)) {// && (S.getOutputs().contains(l))
					as.addTransition(new Transition_(e, l, d));
				}
			}
		}

		Automaton_ modelD = Operations.convertToDeterministicAutomaton(as);
		d = null;
		as = null;
		// add fault state in the list,
		// added here for this state have no transitions
		return modelD;
	}

}
