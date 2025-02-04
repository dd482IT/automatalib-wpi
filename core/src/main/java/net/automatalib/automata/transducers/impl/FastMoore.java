/* Copyright (C) 2013-2022 TU Dortmund
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
package net.automatalib.automata.transducers.impl;

import net.automatalib.automata.base.fast.AbstractFastMutableDet;
import net.automatalib.automata.transducers.MutableMooreMachine;
import net.automatalib.words.Alphabet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A fast implementation of a Moore automaton.
 *
 * @param <I>
 *         input symbol class.
 * @param <O>
 *         output symbol class.
 *
 * @author Malte Isberner
 */
public final class FastMoore<I, @Nullable O> extends AbstractFastMutableDet<FastMooreState<O>, I, FastMooreState<O>, O, Void>
        implements MutableMooreMachine<FastMooreState<O>, I, FastMooreState<O>, O> {

    public FastMoore(Alphabet<I> alphabet) {
        super(alphabet);
    }

    @Override
    public FastMooreState<O> getSuccessor(FastMooreState<O> transition) {
        return transition;
    }

    @Override
    public O getStateOutput(FastMooreState<O> state) {
        return state.getOutput();
    }

    @Override
    public FastMooreState<O> createTransition(FastMooreState<O> successor, Void properties) {
        return successor;
    }

    @Override
    public void setStateOutput(FastMooreState<O> state, O output) {
        state.setOutput(output);
    }

    @Override
    protected FastMooreState<O> createState(@Nullable O property) {
        return new FastMooreState<>(inputAlphabet.size(), property);
    }

}
