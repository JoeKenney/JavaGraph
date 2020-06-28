package org.javagraph.test.DVR;

import java.util.Set;

public class RecordingService {
	
	private  RecordingService() {
	}
	
	Set<String> activeRecordings;
	
	private final static RecordingService instance = new RecordingService();
	
	public static RecordingService getInstance() {
		return instance;
	}
	
	public synchronized void startRecording(String name) {
		activeRecordings.add(name);
		PlaylistService.getInstance().addRecording(name);
	}
	
	public synchronized void cancelRecording(String name) {
		activeRecordings.remove(name);
	}
	
}
