package com.willin.server.chatsvr;

public class ChatCommand {

	// ========================= command ============================== //
	
	public static final int LOGIN = 0;
	public static final int LOGOUT = 1;	
	public static final int BROADCAST = 2;
	
	
	private int mCommand = -1;
	
	
	// ======================== ChatCommand =========================== //
	// private construtor
	//
	private ChatCommand( int command ) {
		mCommand = command;
	}
	
	
	
	// ======================== toInt ================================= //
	// @return, return the command id
	//
	public int toInt() {
		return mCommand;
	}
	
	
	// ======================== valueOf =============================== //
	// @param s, command string
	// @return, construct the ChatCommand object for it.
	//
    public static ChatCommand valueOf(String s) {
        s = s.toUpperCase();
        
        if ("LOGIN".equals(s)) {
            return new ChatCommand(LOGIN);
        }
        
        if ("QUIT".equals(s)) {
            return new ChatCommand(LOGOUT);
        }
        
        if ("BROADCAST".equals(s)) {
            return new ChatCommand(BROADCAST);
        }

        throw new IllegalArgumentException("Unrecognized command: " + s);
    }
	
	
}

// end of file