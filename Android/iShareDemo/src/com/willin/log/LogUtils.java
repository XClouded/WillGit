package com.willin.log;

public class LogUtils {

	private static ILog consoleLog = null;
	private static ILog fileLog = null;
	
	public static final int CONSOLE_LOG = 0x1;
	public static final int FILE_LOG	= 0x1 << 1;
	public static final int ALL_ON      = CONSOLE_LOG & FILE_LOG;
	public static final int ALL_OFF     = 0x0;
	
	
	// ============================================================================================================= //
		
	public LogUtils( int flag, String path ) {
	
		if ( (flag & CONSOLE_LOG) > 0 ) {
			consoleLog = new ConsoleLog();
		}
		
		if ( (flag & FILE_LOG) > 0 ) {
			fileLog = new FileLog( path );
		}		
	}

	
	// VERBOSE
	public void v( String tag, String s ){
		
		if ( consoleLog != null )
			consoleLog.v(tag, s);
		
		if ( fileLog != null )
			fileLog.v(tag, s);
		
	}
	
	
	
	// DEBUG
	public void d( String tag, String s ){
		
		if ( consoleLog != null )
			consoleLog.d(tag, s);
		
		if ( fileLog != null )
			fileLog.d(tag, s);
		
	}
	
	
	
	// INFO
	public void i( String tag, String s ){
		
		if ( consoleLog != null )
			consoleLog.i(tag, s);
		
		if ( fileLog != null )
			fileLog.i(tag, s);
		
	}
	
	
	
	// ERROR
	public void e( String tag, String s ){
		
		if ( consoleLog != null )
			consoleLog.e(tag, s);
		
		if ( fileLog != null )
			fileLog.e(tag, s);
		
	}
	
	
	// WARN
	public void w( String tag, String s ){
		
		if ( consoleLog != null )
			consoleLog.w(tag, s);
		
		if ( fileLog != null )
			fileLog.w(tag, s);
		
	}
	
	
}

// end of file