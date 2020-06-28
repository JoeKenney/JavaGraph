package org.javagraph.agent;

import java.lang.instrument.Instrumentation;

import org.javagraph.Transformer;
import org.javagraph.graph.GraphDB;

public class Agent {

	public static void premain(String args, Instrumentation instrumentation) {		
		instrumentation.addTransformer(new Transformer());

	}

	public Agent() {
		// TODO Auto-generated constructor stub
	}

}
