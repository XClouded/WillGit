package com.willin.log;

public interface ILog {

	// VERBOSE
	public void v( String tag, String s );
	
	
	// DEBUG
	public void d( String tag, String s );
	
	
	// INFO
	public void i( String tag, String s );
	
	
	
	// ERROR
	public void e( String tag, String s );
	
	
	// WARN
	public void w( String tag, String s );
	
	
}
