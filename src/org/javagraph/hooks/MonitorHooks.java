package org.javagraph.hooks;

import org.javagraph.graph.GraphDB;

public class MonitorHooks {

	public static  Object premonitorenter(Object obj) {
		GraphDB.getInstance().push(obj);
		return obj;
	}


	public static Object premonitorexit(Object obj) {
		GraphDB.getInstance().pop();
		return obj;
	}

	
}
