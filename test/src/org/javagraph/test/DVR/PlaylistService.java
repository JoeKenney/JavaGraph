package org.javagraph.test.DVR;

import java.util.Set;

public class PlaylistService {

	public PlaylistService() {
	}

	private final static PlaylistService instance = new PlaylistService();
	Set<String> savedRecordings;
	
	public static PlaylistService getInstance() {
		return instance;
	}
	
	public synchronized void addRecording(String name) {
		savedRecordings.add(name);
	}
	
	public synchronized void deleteRecording(String name) {
		savedRecordings.remove(name);
		RecordingService.getInstance().cancelRecording(name);
	}
		
}
