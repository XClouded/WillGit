package com.willin.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.willin.server.handler.MsgCountServerHandler;
import com.willin.server.handler.TimeServerHandler;

///1 1 1 1 
public class MainServer {

	public static final int SERVER_PORT = 8999;
	public static final int RECEIVER_BUFFER_SIZE = 2048;
	
	public static void main( String[] args ) throws Exception{
		
		SocketAcceptor acceptor = new NioSocketAcceptor();
		
		SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
		sessionConfig.setReceiveBufferSize( RECEIVER_BUFFER_SIZE );
		
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		//�趨�����������һ��һ�У�/r/n���Ķ�ȡ����  
        chain.addLast( "myChain", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName("UTF-8") ) ) );
		
        acceptor.setHandler( new TimeServerHandler() );
        
        // �󶨽ӿڣ�����������
        try
        {
        	acceptor.bind( new InetSocketAddress(SERVER_PORT) );
        	System.out.println("Mina server is listing port:" + SERVER_PORT);
        }
        catch( IOException e )
        {
        	e.printStackTrace();
        }
	}
	
}

// end of file