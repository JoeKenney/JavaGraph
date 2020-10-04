package org.javagraph.test;

import org.javagraph.graph.GraphDB;

/**
 * This is test code for starting up and running three threads with different
 * monitor locking orders. Note - It's probably a bad idea to write actual code
 * that looks like this. The only reason these threads do not deadlock is that
 * each waits for the previous one to finish.
 *
 */
public class Test {

	public static void main(String[] args) {
		Object a = new Object();
		Object b = new Object();
		Object c = new Object();
		Thread first = new Thread(new TestExecutor("First", a, b, c), "First");
		first.start();
		while (first.isAlive()) {
			try {
				first.join();
			} catch (InterruptedException e) {

			}
		}
		Thread second = new Thread(new TestExecutor("Second", b, a, c), "Second");
		second.start();
		while (second.isAlive()) {
			try {
				second.join();
			} catch (InterruptedException e) {

			}
		}
		Thread third = new Thread(new TestExecutor("Third", a, c, b), "Third");

		third.start();
		while (third.isAlive()) {
			try {
				third.join();
			} catch (InterruptedException e) {

			}
		}
		GraphDB.getInstance().dumpDB();
	}

}
