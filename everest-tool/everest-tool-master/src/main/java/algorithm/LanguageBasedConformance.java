/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import model.Automaton_;
import model.LTS;
import model.Transition_;
import util.Constants;


/**
 *
 * @author Camila
 */
public class LanguageBasedConformance {
	/***
	 * Verify if the implementation conforms to the specification given a language D (desirable behaviors) and F (undesirable behaviors), 
	 * it returns the automaton resulting from this verification
	 * 
	 * @param S
	 *            LTS specification
	 * @param I
	 *            LTS implementation
	 * @param D
	 *            regex of desirable behaviors
	 * @param F
	 *            regex of undesirable behaviors
	 * @return
	 */
	public static Automaton_ verifyLanguageConformance(LTS S, LTS I, String D, String F) {//, int nTestCases
		
		//S = Operations.convertToDeterministicAutomaton();
		List<String> alphabet = new ArrayList();
		alphabet.addAll(I.getAlphabet());
		alphabet.addAll(S.getAlphabet());
		HashSet hashSet_s_ = new LinkedHashSet<>(alphabet);
		alphabet = new ArrayList<>(hashSet_s_);					
		S.setAlphabet(alphabet);
		I.setAlphabet(alphabet);
		
		//Operations.addTransitionToStates(S, I);
		
		// construct the fault model containing all behavior considered to be fault
		Automaton_ at = faultModelLanguage(S, D, F);
		// implementation automaton
		Automaton_ ai = I.ltsToAutomaton();
		
		
		
		// intersection between the implementation and fault model to find fault
		Automaton_ ab = Operations.intersection(ai,at);//, nTestCases
		//System.out.println("tamanho ab: " + ab.getFinalStates().size());

		/*System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("<<<<<<<<<<<<<<<<<<<< verification conformance based on language >>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Fault model");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(at);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Implementation");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(ai);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Intersection [Fault model X implementation]");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(ab);*/
		
		Operations.addTransitionToStates(S, I);
		return ab;
	}

	/***
	 *Returns the fault model automaton based on the specification and regex D (desirable behaviors) and F (undesirable behaviors)
	 * 
	 * @param S
	 *            LTS specification
	 * @param D
	 *            regex of desirable behaviors
	 * @param F
	 *           regex of undesirable behaviors
	 * @return
	 */
	public static Automaton_ faultModelLanguage(LTS S, String D, String F) {
		// builds the underlying automaton specification
		Automaton_ as = S.ltsToAutomaton();
		// builds the automato complement of the specification
		Automaton_ aCompS = Operations.complement(as);

		Automaton_ falhaF = null;
		Automaton_ falhaD = null;
		Automaton_ af = null;
		Automaton_ ad = null;

		if (!F.equals("")) {
			// construct automata that accepts the language F with states with name started with "f"
			af = Operations.regexToAutomaton(F, "f", S.getAlphabet());

			// Fault automaton that shows the undesirable behaviors present in the specification
			falhaF = Operations.intersection(af, as);//, null
		}

		if (!D.equals("")) {
			// construct automato that accepts the D language with states with name started with "d"
			if(D.contains(Constants.DELTA) || D.contains(Constants.DELTA_TXT)) {
				D = D.replace(Constants.DELTA_TXT, Constants.DELTA);				
			}
			
			ad = Operations.regexToAutomaton(D, "d", S.getAlphabet());
			if(as.getAlphabet().contains(Constants.DELTA_UNICODE)) {
				List<String> alphabet = new ArrayList<String>();
				alphabet.remove(Constants.DELTA_UNICODE);
				for (String s : as.getAlphabet()) {
					if(s.equals(Constants.DELTA_UNICODE)) {
						alphabet.add(Constants.DELTA);
					}else {
						alphabet.add(s);
					}
				}				
				ad.setAlphabet(alphabet);

				//System.out.println(ad);
				
				List<Transition_> transitions = new ArrayList<Transition_>();
				for (Transition_ t : ad.getTransitions()) {
					//System.out.println(t);
					if(t.getLabel() != null) {
						if(t.getLabel().contains(Constants.DELTA_UNICODE_n)) {
							transitions.add(new Transition_(t.getIniState(), Constants.DELTA, t.getEndState()));
						}else {
							transitions.add(new Transition_(t.getIniState(), t.getLabel(), t.getEndState()));
						}
					}
					
				}
				ad.setTransitions(transitions);
			}
						
			
			// Fault automaton that shows desirable behaviors not in specification
			falhaD = Operations.intersection(ad, aCompS);//, null
			
		}

		/*System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("<<<<<<<<<<<<<<<<<<<< fault model >>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Automaton D : " + D);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(ad);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Automaton F : " + F);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(af);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Specification");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(as);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Specification complement");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(aCompS);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Fail D [Intersection  D X  specification complement]");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(falhaD);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("Fail F [Intersection  F X specification]");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println(falhaF);*/

		if (!D.equals("") && !F.equals("")) {
			// the failure model is the union between the two behaviors considered failure
			return Operations.union(falhaD, falhaF);
		} else {
			if (!D.equals("") && F.equals("")) {
				return falhaD;
			} else {
				if (D.equals("") && !F.equals("")) {
					return falhaF;
				} else {
					return null;
				}

			}
		}

	}

}
