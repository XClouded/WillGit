package com.willin.server.handler;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;


//�򵥵���Ϣ������ 


public class MsgCountServerHandler extends IoHandlerAdapter{

	private AtomicInteger count = new AtomicInteger( 0 );
	
	// ��һ���ͻ������ӽ���ʱ
	@Override
	public void sessionOpened( IoSession session ) throws Exception{
		System.out.println("client connection : " + session.getRemoteAddress()); 
	}
	
	
	// ��һ���ͻ��˹ر�ʱ
	@Override
	public void sessionClosed( IoSession session ) throws Exception{
		System.out.println("client disconnection : " +session.getRemoteAddress() + " is Disconnection");  
	}
	
	
	// �����յ��ͻ��˵���Ϣ
	@Override
	public void messageReceived( IoSession session, Object message ) throws Exception{
		String str = (String)message;  
		  
		//��ӡ�ͻ���  
		System.out.println("receive client message : [ " + str + " ]");  
	  
		//��д��Ϣ���ͻ���  
		session.write(count.incrementAndGet()); 
	}
	
}

// end of file