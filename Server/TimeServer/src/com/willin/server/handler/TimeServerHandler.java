package com.willin.server.handler;

import java.util.Date;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class TimeServerHandler extends IoHandlerAdapter {

	@Override
	public void exceptionCaught( IoSession session, Throwable cause ){
		cause.printStackTrace();
	}
	
	
	@Override
	public void messageReceived( IoSession session, Object message ){
		
		String str = message.toString();
		System.out.println("Message read:");
		System.out.println(str);

		Date date = new Date();
		session.write(date.toString());
		System.out.println("Message written...");
		session.close();
		
	}
	
	
	@Override
	public void sessionIdle( IoSession session, IdleStatus status ) throws Exception{
		System.out.println("IDLE " + session.getIdleCount(status));
	}
	
	
}

// end of file