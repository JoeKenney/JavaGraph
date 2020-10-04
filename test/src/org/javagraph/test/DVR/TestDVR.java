package org.javagraph.test.DVR;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.javagraph.graph.GraphDB;

/**
 * A test class that randomly adds and removes recordings in different threads.
 *
 */
public class TestDVR {

	public TestDVR() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		String[] programNames = { "Life on Earth", "The World About Us", "Life in the Freezer" };
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Random rand = new Random();

		for (int i = 0; i < 4; i++) {
			java.util.concurrent.Future<?> future = executor.submit(new Runnable() {

				@Override
				public void run() {
					int index = rand.nextInt(programNames.length);
					RecordingService.getInstance().startRecording(programNames[index]);
					index = rand.nextInt(programNames.length);
					PlaylistService.getInstance().deleteRecording(programNames[index]);
					System.out.println("--------------RECORDINGS--------------");
					PlaylistService.getInstance().savedRecordings.stream()
							.forEach(recording -> System.out.println(recording));

				}

			});

			try {
				// Wait until the task finishes, or else deadlock.
				future.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		executor.shutdown();
		GraphDB.getInstance().dumpDB();
		return;
	}
}
