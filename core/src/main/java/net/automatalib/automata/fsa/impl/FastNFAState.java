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
package net.automatalib.automata.fsa.impl;

import net.automatalib.automata.base.fast.FastNondetState;

public class FastNFAState extends FastNondetState<FastNFAState, FastNFAState> {

	private boolean accepting;
	
	public FastNFAState(int numInputs) {
		super(numInputs);
	}
	
	public FastNFAState(int numInputs, boolean accepting) {
		super(numInputs);
		this.accepting = accepting;
	}
	
	
	public final boolean isAccepting() {
		return accepting;
	}
	
	public final void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}

}
