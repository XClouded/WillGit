package com.willin.server.chatsvr;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatServerProtocol extends IoHandlerAdapter {

	
	private final static Logger LOGGER = LoggerFactory.getLogger(ChatServerProtocol.class);
	
	private Set<String> mUsers = Collections.synchronizedSet( new HashSet<String>() );
	private Set<IoSession> mSessions = Collections.synchronizedSet( new HashSet<IoSession>() );
	
	
	// ====================================== exceptionCaught  =========================== //
	// override exceptionCaught
	// 
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        
    	LOGGER.warn("Unexpected exception.", cause);
        // Close connection when unexpected exception is caught.
        session.close(true);
        
    }
    
    
    
    // ======================================= messageReceived =========================== //
    // override messageReceived
    //
    @Override
    public void messageReceived(IoSession session, Object message) {
    	
        Logger log = LoggerFactory.getLogger(ChatServerProtocol.class);
        log.info("received: " + message);
        
        String theMessage = (String) message;
        String[] result = theMessage.split(" ", 2);
        String theCommand = result[0];

        try {

            ChatCommand command = ChatCommand.valueOf(theCommand);
            String user = (String) session.getAttribute("user");

            switch (command.toInt()) {

            case ChatCommand.LOGIN:

                if (user != null) {
                    session.write("LOGIN ERROR user " + user + " already logged in.");
                    return;
                }

                if (result.length == 2) {
                    user = result[1];
                } 
                else {
                    session.write("LOGIN ERROR invalid login command.");
                    return;
                }

                // check if the username is already used
                if (mUsers.contains(user)) {
                    session.write("LOGIN ERROR the name " + user + " is already used.");
                    return;
                }

                mSessions.add(session);
                session.setAttribute("user", user);
                MdcInjectionFilter.setProperty(session, "user", user);

                // Allow all users
                mUsers.add(user);
                session.write("LOGIN OK");
                broadcast("The user " + user + " has joined the chat session.");
                break;

            case ChatCommand.BROADCAST:

                if (result.length == 2) {
                    broadcast(user + ": " + result[1]);
                }
                break;
                
            case ChatCommand.LOGOUT:
                session.write("QUIT OK");
                session.close(true);
                break;
                
            default:
                LOGGER.info("Unhandled command: " + command);
                break;
            }

        } 
        catch (IllegalArgumentException e) {
            LOGGER.debug("Illegal argument", e);
        }
    }
	
    
    
    // ======================================= sessionClosed  ============================ //
    // override sessionClosed
    // 
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	
        String user = (String) session.getAttribute("user");
        mUsers.remove(user);
        mSessions.remove(session);
        broadcast("The user " + user + " has left the chat session.");
        
    }
    
    
    
    // ======================================= isChatUser  =============================== //
    // @param name, name for check
    // @return, is the user in the chat rom?
    //
    public boolean isChatUser(String name) {
        return mUsers.contains(name);
    }

    
    
    // ======================================== getNumberOfUsers ========================== //
    // @return, get the total count of chat room 
    public int getNumberOfUsers() {
        return mUsers.size();
    }
    
    
    
    
    // ======================================== kick  ====================================== //
    // @param name, name to kick
    //     
    public void kick(String name) {
        synchronized (mSessions) {
            for (IoSession session : mSessions) {
                if (name.equals(session.getAttribute("user"))) {
                    session.close(true);
                    break;
                }
            }
        }
    }
    
    
    // ======================================== broadcast ====================================== //
    // @param message,the message for broadcast 
    // 
    public void broadcast(String message) {
        synchronized (mSessions) {
            for (IoSession session : mSessions) {
                if (session.isConnected()) {
                    session.write("BROADCAST OK " + message);
                }
            }
        }
    }
    
    
    
    
}

// end of file