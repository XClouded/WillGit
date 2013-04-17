package com.willin.server.timesvr;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.willin.server.ServerImpl;

public class TimeServer extends ServerImpl {

	public static final int TIME_SERVIER_PORT = 8080;
	public static final int RECEIVER_BUFFER_SIZE = 2048;
	
	// ============================================================================== 
	// Override the super.start
	// Using the NioSocketAcceptor for Time reply server
	// Just return the date's string, so simple
	// 
	@Override 
	public int start( int port ) {
		
		super.start(port);
		
		SocketAcceptor acceptor = new NioSocketAcceptor();
		
		SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
		sessionConfig.setReceiveBufferSize( RECEIVER_BUFFER_SIZE );
		
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		//�趨�����������һ��һ�У�/r/n���Ķ�ȡ����  
        chain.addLast( "TimeServerChain", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName("UTF-8") ) ) );
		
        acceptor.setHandler( new TimeServerHandler() );
        
        // �󶨽ӿڣ�����������
        try
        {
        	acceptor.bind( new InetSocketAddress(port) );
        	System.out.println("Mina server is listing port:" + port);
        }
        catch( IOException e )
        {
        	e.printStackTrace();
        }
		
		return OK;
		
	}
	
	
	
	
}


// end of file