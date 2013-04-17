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
		//设定这个过滤器将一行一行（/r/n）的读取数据  
        chain.addLast( "TimeServerChain", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName("UTF-8") ) ) );
		
        acceptor.setHandler( new TimeServerHandler() );
        
        // 绑定接口，启动服务器
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