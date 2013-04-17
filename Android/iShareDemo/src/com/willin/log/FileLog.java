package com.willin.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


public class FileLog implements ILog {

	private String path = "";
	private File f = null;
	private FileOutputStream fos = null;
	
	
	private final int V = 0;
	private final int D = 1;
	private final int I = 2;
	private final int E = 3;
	private final int W = 4;
	
	
	// =========================================================================================================== //
	
	@Override
	public void v(String tag, String s) {
		writeTagAndInfo( V, tag, s );
	}

	@Override
	public void d(String tag, String s) {
		writeTagAndInfo( D, tag, s );
	}

	@Override
	public void i(String tag, String s) {
		writeTagAndInfo( I, tag, s );
	}

	@Override
	public void e(String tag, String s) {
		writeTagAndInfo( E, tag, s );
	}

	@Override
	public void w(String tag, String s) {
		writeTagAndInfo( W, tag, s );
	}
	
	
	// constructor
	public FileLog( String p ){

		path = p;
		
	}
	
	
	// prepare
	public boolean prepare(){
		
		boolean ret = false;
		
		do
		{
			f = new File( path );
			
			if ( f == null )
				break;
			
			try {
				
				if ( f.exists() == false && f.createNewFile() == false )
					break;
				
				fos = new FileOutputStream(f);
				if ( fos == null )
					break;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			ret = true;			
			
		}while( false );
		
		
		return ret;
	}
	
	
	
	// close
	public void close(){
		
		try {
			
			if ( fos != null ){
				fos.flush();
				fos.close();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		path = "";
		fos = null;
		f = null;
		
	}
	
	
	
	// write a line
	private void writeALine( String s ){
		
		if ( fos != null ){
			
			s += "\r\n";
			
			byte[] buffer = s.getBytes();
			
			try {
				
				fos.write( buffer, 0, buffer.length );				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	
	
	// formation
	private String formation( int type, String tag, String s ){
		
		String ret = null;
		
		switch( type ){
		
		case V:
			ret = "VERBOSE";
			break;
			
		case D:
			ret = "DEBUG";
			break;
			
		case I:
			ret = "INFO";
			break;
			
		case E:
			ret = "ERROR";
			break;
			
		case W:
			ret = "WARN";
			break;
		
		default:
			ret = "";			
		}
		
		ret += "\t\t";
		
		Date now = new Date();
	    DateFormat dFormat = DateFormat.getDateInstance(); //默认语言（汉语）下的默认风格（MEDIUM风格，比如：2008-6-16 20:54:53）
	    ret += dFormat.format(now);
	      
	    ret += "\t\t";
	    
	    ret += tag;
	    
	    ret += "\t\t";
	    
	    ret += s;
	    	    
		
		return ret;
		
	}
	
	
	// write type, tag, info
	private void writeTagAndInfo( int type, String tag, String s ){
		
		String format = formation( type, tag, s );
		
		writeALine( format );
	}
	

}

// end of file