package com.willin.server;

import com.willin.server.timesvr.TimeServer;


///1 1 1 1 
public class Main {

	private static int startTimeServer(){
		
		int ret = ServerImpl.FAILED;
		
		IServer timeServer = new TimeServer();
		ret = timeServer.start( TimeServer.TIME_SERVIER_PORT  );
		
		return ret;
	}
		
	
	public static void main( String[] args ) throws Exception{
		
		startTimeServer();
	}
	
}

// end of file