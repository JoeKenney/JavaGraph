package org.javagraph.agent;

import java.lang.instrument.Instrumentation;

import org.javagraph.Transformer;

public class Agent {

	public static void premain(String args, Instrumentation instrumentation) {		
		instrumentation.addTransformer(new Transformer());

	}


}
