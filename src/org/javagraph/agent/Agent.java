package org.javagraph.agent;

import java.lang.instrument.Instrumentation;

import org.javagraph.Transformer;

/**
 * The agent class registers the java byte code transformer.
 * 
 * @see java.lang.instrument
 * @see org.javagraph.Transformer
 * @author joe
 *
 */
public class Agent {

	public static void premain(String args, Instrumentation instrumentation) {
		instrumentation.addTransformer(new Transformer());
	}
}
