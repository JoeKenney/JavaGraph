package org.javagraph.test;

public class TestExecutor implements Runnable {
	Object a,b,c;
	String name;
	public TestExecutor(String name,Object a, Object b, Object c) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println("Hello from thread: "+name);
		synchronized(a) {  //FIRST!
			synchronized(b) {  //SECOND!
				synchronized(c) {  //THIRD!
					System.out.println("Hello from thread: "+name);					
				}
			}
			synchronized(c) { //FOURTH!
				
			}
		}		
	}
}
