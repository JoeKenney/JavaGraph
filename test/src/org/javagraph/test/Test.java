package org.javagraph.test;

public class Test {

	public static void main(String[] args) {
		Object a = new Object();
		Object b = new Object();
		Object c = new Object();
		Thread first = new Thread(new TestExecutor("First",a,b,c));
		first.start();
		while(first.isAlive()) {
			try{
				first.join();
			} catch (InterruptedException e) {
				
			}
		}
		Thread second = new Thread(new TestExecutor("Second", b,a,c));
		second.start();
		while(second.isAlive()) {
			try{
				second.join();
			} catch (InterruptedException e) {
				
			}
		}
		Thread third = new Thread(new TestExecutor("Third", a,c,b));
		
		third.start();
		while(third.isAlive()) {
			try{
				third.join();
			} catch (InterruptedException e) {
				
			}
		}		
		

	}

}
