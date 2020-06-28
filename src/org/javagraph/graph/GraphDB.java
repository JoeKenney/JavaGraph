package org.javagraph.graph;
import java.io.File;
import java.util.Map;
import java.util.Stack;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.dbms.api.DatabaseNotFoundException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction; 


public class GraphDB {

	private enum RelTypes implements RelationshipType
	{
		LOCKS
	}
	private enum NodeLabel implements Label 
	{
		THREAD,MONITOR;
		
	}
	private static final GraphDB instance = new GraphDB();
	private static final String DEFAULT_DATABASE_NAME = "neo4j";
	protected  GraphDatabaseService graphDB;
	private static final String queryString = "MERGE (n:Monitor {hash: $hash, class:$class}) return n";
	private ThreadLocal<Stack<Long>> threadMonitors = 
			new ThreadLocal<Stack<Long>>() {
		@Override public Stack<Long> initialValue() {
			Stack<Long> nodes = new Stack<Long>();
			Thread thread = Thread.currentThread();
			try (Transaction tx = graphDB.beginTx()) {
				Node threadNode = tx.createNode(NodeLabel.THREAD);
				threadNode.setProperty("threadId", thread.getId());
				
				nodes.push(threadNode.getId());
				tx.commit();
			}
			return nodes;
		}
	};
	
	private GraphDB() {
		
		 DatabaseManagementService dbms = new DatabaseManagementServiceBuilder(new File("/home/joe/javagraphdb/")  ).build();
		 graphDB = dbms.database(DEFAULT_DATABASE_NAME);
		 
	}
	
	public static GraphDB getInstance() {
		return instance;
	}

	public void push(Object o) {
		Stack<Long> stack = threadMonitors.get();
		Long topId = stack.peek(); 
		
		try(Transaction tx = graphDB.beginTx()) {
			
			Map<String,Object> parameters =  Map.ofEntries(
					Map.entry("hash",  o.hashCode()),
					Map.entry("class", o.getClass().getName())
					);
			Node monitor = (Node) tx.execute(queryString, parameters).columnAs("n").next();
			stack.push(monitor.getId());
			Node previous = (Node) tx.getNodeById(topId);
			previous.createRelationshipTo(monitor, RelTypes.LOCKS);
			tx.commit();
					
		}
		
	}
	
	public void pop() {
		threadMonitors.get().pop();
	}
}
