package org.javagraph.test.DVR;

import java.util.HashSet;
import java.util.Set;

/**
 * A fake recording service.
 */
public class RecordingService {

	private RecordingService() {
	}

	Set<String> activeRecordings = new HashSet<String>();

	private final static RecordingService instance = new RecordingService();

	/**
	 * 
	 * @return the singleton instance of the RecordingService.
	 */
	public static RecordingService getInstance() {
		return instance;
	}

	/**
	 * Start recording a program. Calls the PlaylistService to add the new recording
	 * to the playlist.
	 * 
	 * @param name - the name of the new recording.
	 */
	public void startRecording(String name) {
		synchronized (this) {
			activeRecordings.add(name);
			PlaylistService.getInstance().addRecording(name);
		}
	}

	/**
	 * Stop recording the program.
	 * 
	 * @param name - The name of the recording to stop.
	 */
	public synchronized void cancelRecording(String name) {
		synchronized (this) {
			activeRecordings.remove(name);
		}
	}

}
