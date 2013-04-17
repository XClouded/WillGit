package com.willin.server;

import java.util.HashSet;

public class ServerImpl implements IServer {

	// ===========================  status code ====================== //
	public static final int OK = 0x00;
	public static final int FAILED = 0x01;
	public static final int STATUS_NO_PORT = 0x02;

		
	private HashSet<Integer> mPortSet = null;
	
	// the port of the server using
	private int mPort = -1;
	
	
	
	// ============================ ServerImpl ======================= //
	// constructor
	//
	public ServerImpl() {
		
		mPortSet = new HashSet<Integer>();
		
	}
	
	
	
	// ============================ start ======================= //
	// @param, port is the server want to bind
	// @return, return the total status
	//
	@Override
	public int start( int port ) {
		
		int ret = FAILED;
		
		if ( mPortSet != null &&
			 !mPortSet.contains( port ) ) {
			
			mPortSet.add( port );
			
			mPort = port;
			
			ret = OK;
		}
		
		return ret;
	}

	
	// ============================ stop ======================= //
	// @return, return the total status
	//
	@Override
	public int stop() {

		int ret = FAILED;
		
		if ( mPortSet != null &&
			 mPortSet.contains( mPort ) ) {
				
			mPortSet.remove(mPort);
			
			ret = OK;
		}
		else
		{
			ret = STATUS_NO_PORT;
		}
		
		return ret;
	}
	
	

}

// end of file
