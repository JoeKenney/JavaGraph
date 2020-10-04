package org.javagraph.hooks;

import org.javagraph.graph.GraphDB;

/**
 * A simple class that hooks the Java bytecode instructions MONITORENTER and
 * MONITOREXIT
 * 
 * WARNING: The signatures of these functions are crafted to work with the
 * bytecode instrumentation as provided by
 * 
 * @see org.javagraph.Transformer.
 *
 */
public class MonitorHooks {

	public static Object premonitorenter(Object obj) {
		GraphDB.getInstance().push(obj);
		return obj;
	}

	public static Object premonitorexit(Object obj) {
		GraphDB.getInstance().pop();
		return obj;
	}

}
