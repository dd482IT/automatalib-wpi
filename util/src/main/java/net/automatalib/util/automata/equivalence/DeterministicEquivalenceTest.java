/* Copyright (C) 2013 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.automatalib.util.automata.equivalence;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import net.automatalib.automata.UniversalDeterministicAutomaton;
import net.automatalib.automata.concepts.StateIDs;
import net.automatalib.words.Word;
import net.automatalib.words.WordBuilder;



public class DeterministicEquivalenceTest<I> {
	
	public static int mapThreshold = 10000;
	
	private static final class StatePair<S,S2> {
		public final S ref;
		public final S2 other;
		public StatePair(S ref, S2 other) {
			this.ref = ref;
			this.other = other;
		}
	}
	
	private static final class Pred<I> {
		public final int id;
		public final I symbol;
		public Pred(int id, I input) {
			this.id = id;
			this.symbol = input;
		}
	}
	
	private final UniversalDeterministicAutomaton<?, I, ?, ?, ?> reference;
	
	public DeterministicEquivalenceTest(UniversalDeterministicAutomaton<?, I, ?, ?, ?> reference) {
		this.reference = reference;
	}
	
	public Word<I> findSeparatingWord(UniversalDeterministicAutomaton<?,I,?,?,?> other,
			Collection<? extends I> inputs) {
		return findSeparatingWord(reference, other, inputs);
	}
	
	@SuppressWarnings("unchecked")
	public static <I,S,T,S2,T2>
	Word<I> findSeparatingWord(UniversalDeterministicAutomaton<S,I,T,?,?> reference,
			UniversalDeterministicAutomaton<S2,I,T2,?,?> other,
			Collection<? extends I> inputs) {
		int refSize = reference.size();
		int totalStates = refSize * other.size();
		
		if (totalStates < 0 || totalStates > mapThreshold) {
			return findSeparatingWordLarge(reference, other, inputs);
		}
		
		Queue<StatePair<S,S2>> bfsQueue = new ArrayDeque<>();
		
		S refInit = reference.getInitialState();
		S2 otherInit = other.getInitialState();
		
		Object refStateProp = reference.getStateProperty(refInit),
				otherStateProp = other.getStateProperty(otherInit);
		
		if(!Objects.equals(refStateProp, otherStateProp))
			return Word.epsilon();
		
		bfsQueue.add(new StatePair<>(refInit, otherInit));
		
		
		StateIDs<S> refStateIds = reference.stateIDs();
		StateIDs<S2> otherStateIds = other.stateIDs();
		
		StatePair<S,S2> currPair = null;
		int lastId = otherStateIds.getStateId(otherInit) * refSize + refStateIds.getStateId(refInit);
		
		Pred<I>[] preds = new Pred[totalStates];
		preds[lastId] = new Pred<I>(-1, null);
		
		int currDepth = 0;
		int inCurrDepth = 1;
		int inNextDepth = 0;
		
		I lastSym = null;
		
bfs:	while((currPair = bfsQueue.poll()) != null) {
			S refState = currPair.ref;
			S2 otherState = currPair.other;
			
			
			int currId = otherStateIds.getStateId(otherState) * refSize + refStateIds.getStateId(refState);
			lastId = currId;
			
			
			for(I in : inputs) {
				lastSym = in;
				T refTrans = reference.getTransition(refState, in);
				T2 otherTrans = other.getTransition(otherState, in);
				
				if((refTrans == null || otherTrans == null) && refTrans != otherTrans)
					break bfs;
				
				Object refProp = reference.getTransitionProperty(refTrans);
				Object otherProp = other.getTransitionProperty(otherTrans);
				if(!Objects.equals(refProp, otherProp))
					break bfs;
				
				
				S refSucc = reference.getSuccessor(refTrans);
				S2 otherSucc = other.getSuccessor(otherTrans);
				
				int succId = otherStateIds.getStateId(otherSucc) * refSize + refStateIds.getStateId(refSucc);
				
				if(preds[succId] == null) {
					refStateProp = reference.getStateProperty(refSucc);
					otherStateProp = other.getStateProperty(otherSucc);
					
					if(!Objects.equals(refStateProp, otherStateProp))
						break bfs;
					
					preds[succId] = new Pred<>(currId, in);
					bfsQueue.add(new StatePair<>(refSucc, otherSucc));
					inNextDepth++;
				}
			}
			
			lastSym = null;
			
			
			// Next level in BFS reached
			if(--inCurrDepth == 0) {
				inCurrDepth = inNextDepth;
				inNextDepth = 0;
				currDepth++;
			}
		}
		
		if(lastSym == null)
			return null;
		
		WordBuilder<I> sep = new WordBuilder<I>(null, currDepth+1);
		int index = currDepth;
		sep.setSymbol(index--, lastSym);
		
		Pred<I> pred = preds[lastId];
		I sym = pred.symbol;
		while(sym != null) {
			sep.setSymbol(index--, sym);
			pred = preds[pred.id];
			sym = pred.symbol;
		}
		
		
		return sep.toWord();
	} 
	
	public static <I,S,T,S2,T2>
	Word<I> findSeparatingWordLarge(UniversalDeterministicAutomaton<S,I,T,?,?> reference,
			UniversalDeterministicAutomaton<S2,I,T2,?,?> other,
			Collection<? extends I> inputs) {
		Queue<StatePair<S,S2>> bfsQueue = new ArrayDeque<>();
		
		S refInit = reference.getInitialState();
		S2 otherInit = other.getInitialState();
		
		Object refStateProp = reference.getStateProperty(refInit),
				otherStateProp = other.getStateProperty(otherInit);
		
		if(!Objects.equals(refStateProp, otherStateProp))
			return Word.epsilon();
		
		bfsQueue.add(new StatePair<>(refInit, otherInit));
		
		int refSize = reference.size();
		
		StateIDs<S> refStateIds = reference.stateIDs();
		StateIDs<S2> otherStateIds = other.stateIDs();
		
		StatePair<S,S2> currPair = null;
		int lastId = otherStateIds.getStateId(otherInit) * refSize + refStateIds.getStateId(refInit);
		
//		TIntObjectMap<Pred<I>> preds = new TIntObjectHashMap<>();
		Map<Integer,Pred<I>> preds = new HashMap<>(); // TODO: replace by primitive specialization
		preds.put(lastId, new Pred<I>(-1, null));
		
		int currDepth = 0;
		int inCurrDepth = 1;
		int inNextDepth = 0;
		
		I lastSym = null;
		
bfs:	while((currPair = bfsQueue.poll()) != null) {
			S refState = currPair.ref;
			S2 otherState = currPair.other;
			
			
			int currId = otherStateIds.getStateId(otherState) * refSize + refStateIds.getStateId(refState);
			lastId = currId;
			
			
			for(I in : inputs) {
				lastSym = in;
				T refTrans = reference.getTransition(refState, in);
				T2 otherTrans = other.getTransition(otherState, in);
				
				if((refTrans == null || otherTrans == null) && refTrans != otherTrans)
					break bfs;
				
				Object refProp = reference.getTransitionProperty(refTrans);
				Object otherProp = other.getTransitionProperty(otherTrans);
				if(!Objects.equals(refProp, otherProp))
					break bfs;
				
				
				S refSucc = reference.getSuccessor(refTrans);
				S2 otherSucc = other.getSuccessor(otherTrans);
				
				int succId = otherStateIds.getStateId(otherSucc) * refSize + refStateIds.getStateId(refSucc);
				
				if(preds.get(succId) == null) {
					refStateProp = reference.getStateProperty(refSucc);
					otherStateProp = other.getStateProperty(otherSucc);
					
					if(!Objects.equals(refStateProp, otherStateProp))
						break bfs;
					
					preds.put(succId, new Pred<>(currId, in));
					bfsQueue.add(new StatePair<>(refSucc, otherSucc));
					inNextDepth++;
				}
			}
			
			lastSym = null;
			
			
			// Next level in BFS reached
			if(--inCurrDepth == 0) {
				inCurrDepth = inNextDepth;
				inNextDepth = 0;
				currDepth++;
			}
		}
		
		if(lastSym == null)
			return null;
		
		WordBuilder<I> sep = new WordBuilder<I>(null, currDepth+1);
		int index = currDepth;
		sep.setSymbol(index--, lastSym);
		
		Pred<I> pred = preds.get(lastId);
		I sym = pred.symbol;
		while(sym != null) {
			sep.setSymbol(index--, sym);
			pred = preds.get(pred.id);
			sym = pred.symbol;
		}
		
		
		return sep.toWord();
	} 
}
