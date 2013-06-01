package com.willin.net.http;

public interface HttpEngine {
	
	// Run task queue
	public int begin();
	
	// Stop task queue
	public int stop();
	
	// Add task
	public int addTask( Runnable task );
	
	// Cancel task
	public int cancelTask( Runnable task );
	
	
}

// end of file