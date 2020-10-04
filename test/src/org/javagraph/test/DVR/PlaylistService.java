package org.javagraph.test.DVR;

import java.util.HashSet;
import java.util.Set;

/**
 * A fake playlist service that stores a list of recordings.
 *
 */
public class PlaylistService {

	private PlaylistService() {
	}

	private final static PlaylistService instance = new PlaylistService();
	Set<String> savedRecordings = new HashSet<String>();

	/**
	 * @return the singleton instance of PlaylistService.
	 */
	public static PlaylistService getInstance() {
		return instance;
	}

	/**
	 * Adds a recording to the playlist.
	 * 
	 * @param name - The title of the recording to add to the playlist.
	 */
	public void addRecording(String name) {
		synchronized (this) {
			savedRecordings.add(name);
		}
	}

	/**
	 * Remove the recording from the playlst. Calls the RecordingService to cancel
	 * the recording and free corresponding resources.
	 * 
	 * @param name - The name of the recording to be removed from the playlist.
	 */
	public void deleteRecording(String name) {
		synchronized (this) {
			savedRecordings.remove(name);
			RecordingService.getInstance().cancelRecording(name);
		}
	}

}
