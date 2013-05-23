/* Copyright (C) 2013 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * AutomataLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * AutomataLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with AutomataLib; if not, see
 * http://www.gnu.de/documents/lgpl.en.html.
 */
package net.automatalib.util.graphs.copy;

import net.automatalib.commons.util.mappings.Mapping;
import net.automatalib.commons.util.mappings.MutableMapping;
import net.automatalib.graphs.IndefiniteGraph;
import net.automatalib.graphs.MutableGraph;

abstract class AbstractGraphCopy<N1, E1, N2, E2, NP2, EP2, G1 extends IndefiniteGraph<N1,E1>> {
	
	protected final MutableMapping<N1,N2> nodeMapping;
	protected final G1 inGraph;
	protected final MutableGraph<N2, E2, NP2, EP2> outGraph;
	
	protected final Mapping<? super N1,? extends NP2> npMapping;
	protected final Mapping<? super E1,? extends EP2> epMapping;
	
	
	public AbstractGraphCopy(G1 inGraph, MutableGraph<N2, E2, NP2, EP2> outGraph,
			Mapping<? super N1,? extends NP2> npMapping, Mapping<? super E1,? extends EP2> epMapping) {
		this.inGraph = inGraph;
		this.outGraph = outGraph;
		this.nodeMapping = inGraph.createStaticNodeMapping();
		this.npMapping = npMapping;
		this.epMapping = epMapping;
	}
	
	
	protected N2 copyNode(N1 node) {
		NP2 prop = npMapping.get(node);
		N2 n2 = outGraph.addNode(prop);
		
		nodeMapping.put(node, n2);
		return n2;
	}
	
	protected E2 copyEdge(N2 src2, E1 edge) {
		return copyEdge(src2, edge, inGraph.getTarget(edge));
	}
	
	protected E2 copyEdge(N2 src2, E1 edge, N1 tgt1) {
		EP2 prop = epMapping.get(edge);
		
		N2 tgt2 = nodeMapping.get(tgt1);
		
		return outGraph.connect(src2, tgt2, prop);
	}
	
	protected N2 copyEdgeChecked(N2 source, E1 edge, N1 tgt1) {
		EP2 prop = epMapping.get(edge);
		
		N2 tgt2 = nodeMapping.get(tgt1);
		
		N2 freshTgt = null;
		
		if(tgt2 == null)
			freshTgt = tgt2 = copyNode(tgt1);
		
		outGraph.connect(source, tgt2, prop);
		return freshTgt;
	}

	public Mapping<N1,N2> getNodeMapping() {
		return nodeMapping;
	}
	
	public abstract void doCopy();
}
