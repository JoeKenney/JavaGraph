package org.javagraph.graph;

import java.io.File;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;

import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * Provides the backend functions to store the relationships between Java
 * threads and monitor objects.
 * 
 * A thread's acquired monitors can be thought of as a stack. As a thread
 * acquires a monitor, it is pushed to the top of the stack. As the monitor is
 * released, it is popped off the top of the stack. A ThreadLocal stack is used
 * to capture this information, and the structure is also stored in a local
 * Neo4J database for further investigation.
 *
 * @author joe
 *
 */
public class GraphDB {

	private enum RelTypes implements RelationshipType {
		LOCKS
	}

	private enum NodeLabel implements Label {
		THREAD, MONITOR;

	}

	DatabaseManagementService dbms;
	private static final GraphDB instance = new GraphDB();
	private static final String DEFAULT_DATABASE_NAME = "neo4j";
	protected GraphDatabaseService graphDB;
	private static final String queryString = "MERGE (n:Monitor {hash: $hash, class:$class}) return n";

	private ThreadLocal<Stack<Long>> threadMonitors = new ThreadLocal<Stack<Long>>() {
		@Override
		public Stack<Long> initialValue() {
			Stack<Long> nodes = new Stack<Long>();
			Thread thread = Thread.currentThread();
			try (Transaction tx = graphDB.beginTx()) {
				Node threadNode = tx.createNode(NodeLabel.THREAD);
				threadNode.setProperty("name", thread.getName());
				threadNode.setProperty("threadId", thread.getId());
				nodes.push(threadNode.getId());
				tx.commit();
			}
			return nodes;
		}
	};

	private GraphDB() {

		dbms = new DatabaseManagementServiceBuilder(new File("")).setConfig(BoltConnector.enabled, true)
				.setConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7687)).build();

		graphDB = dbms.database(DEFAULT_DATABASE_NAME);
		Transaction tx = graphDB.beginTx();
		tx.execute("MATCH (n) DETACH DELETE n");
		tx.commit();

	}

	/**
	 * Return the singleton GraphDB instance.
	 * 
	 * @return the singleton GraphDB instance
	 */
	public static GraphDB getInstance() {
		return instance;
	}

	/**
	 * Pushes a Java monitor to this thread's stack, and stores the monitor
	 * relationship to the database.
	 * 
	 * @param o - Handle to the Java monitor.
	 */
	public void push(Object o) {
		Stack<Long> stack = threadMonitors.get();
		Long topId = stack.peek();

		try (Transaction tx = graphDB.beginTx()) {

			Map<String, Object> parameters = Map.ofEntries(Map.entry("hash", "0x" + Long.toHexString(o.hashCode())),
					Map.entry("class", o.getClass().getName()));
			Node monitor = (Node) tx.execute(queryString, parameters).columnAs("n").next();
			stack.push(monitor.getId());
			Node previous = tx.getNodeById(topId);
			previous.createRelationshipTo(monitor, RelTypes.LOCKS);
			tx.commit();
		}
	}

	/**
	 * Remove the most recent monitor that was pushed to the stack.
	 * 
	 * @throws EmptyStackException - If pop is called on an empty stack.
	 */
	public void pop() {
		threadMonitors.get().pop();
	}

	public void dumpNode(Node node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ID: " + node.getId() + "   Labels: ");
		node.getLabels().forEach(label -> buffer.append(label.name()));
		System.out.println(buffer);

	}

	public void dumpDB() {
		try (Transaction tx = graphDB.beginTx()) {
			System.out.println("Nodes");
			tx.getAllNodes().stream().forEach(node -> dumpNode(node));
			tx.getAllRelationships().stream().forEach(rel -> System.out.println(rel.toString()));
		}
	}

	public void shutdown() {
		dbms.shutdown();
	}
}
