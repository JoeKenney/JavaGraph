package org.javagraph.hooks;

import org.javagraph.graph.GraphDB;

public class MonitorHooks {

	public MonitorHooks() {
	
		// TODO Auto-generated constructor stub
	}

	
	public static  Object premonitorenter(Object obj) {
		GraphDB.getInstance().push(obj);
		
		System.out.println(obj.hashCode());
		return obj;
	}


	public static void postmonitorenter() {
		
	}

	public static void postmonitorexit() {
		
	}


	public static Object premonitorexit(Object obj) {
		GraphDB.getInstance().pop();
		return obj;
	}

	
}
