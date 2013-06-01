package com.willin.net.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.Build;

public class URLConnectionTask implements Runnable {

	private HttpURLConnection url;
    private String urlSpec = "";
	
	private String resultData = "";
    private HttpMethod method = HttpMethod.NONE;
	
	@Override
	public void run() {
		
		switch ( method ) {
		
		case GET:
		case HEAD:
		case POST:
		case PUT:
		case TRACE:
		case OPTIONS:
		case DELETE:
			
		case NONE:
		default:
			// TODO, 抛出异常
			break;
		
		}
		
	}
	
	
	private void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
	
	
	private void readBuffer( InputStream stream ) {
		
		resultData = "";
		
        //得到读取的内容(流)
        InputStreamReader in = new InputStreamReader(stream);
        
        // 为输出创建BufferedReader  
        BufferedReader buffer = new BufferedReader(in);  
        String inputLine = null;
        
        //使用循环来读取获得的数据 
        try {
			while (((inputLine = buffer.readLine()) != null))  
			{
			    resultData += inputLine;  
			}
			
	        in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	
	public void get( String urlSpec ) {
		
		try {
			
			URL url = new URL(urlSpec);
			
			if ( url == null )
				return;
			
			//使用HttpURLConnection打开连接 
	        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	        
	        //设置输入和输出流  
	        urlConn.setDoOutput(true);  
	        urlConn.setDoInput(false);
	        
	        //设置请求方式为POST  
	        urlConn.setRequestMethod("GET");  
	        //POST请求不能使用缓存  
	        urlConn.setUseCaches(false); 

	        if ( urlConn.getContentLength() > 0 )
	        	readBuffer( urlConn.getInputStream() );
	        else
	        	System.out.print("getContentLength <= 0");

	        //关闭http连接 
	        urlConn.disconnect();
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void post( String urlSpec, HashMap<String, String> property, String content ) {
			
		try {
			URL url = new URL(urlSpec);
			
			if ( url == null )
				return;
			
			// 使用HttpURLConnection打开连接  
	        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	        
	        //因为这个是post请求,设立需要设置为true  
	        urlConn.setDoOutput(true);  
	        urlConn.setDoInput(true);  
	        // 设置以POST方式  
	        urlConn.setRequestMethod("POST");
	        
	        // Post 请求不能使用缓存  
	        urlConn.setUseCaches(false);
	        // 自动重定向
	        urlConn.setInstanceFollowRedirects(true);
	        
	        
	        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的  
	        if ( property != null && property.isEmpty() == false ) {
	        	
	        	Iterator iterator = property.keySet().iterator();
	        	
	        	while (iterator.hasNext()) {

	        		Map.Entry entry = (Map.Entry) iterator.next();
	        		
	        		String key = (String)entry.getKey();
	        		String pro = (String)entry.getValue();
	        		
	    	        urlConn.setRequestProperty( key, pro );
        		
        		}
	        }
	          
	        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，  
	        // 要注意的是connection.getOutputStream会隐含的进行connect。  
	        urlConn.connect();  
	        //DataOutputStream流  
	        DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
	        

	        //将要上传的内容写入流中  
	        out.writeBytes( URLEncoder.encode( content, "gb2312") );   
	        //刷新、关闭  
	        out.flush();  
	        out.close();
	        
	        
	        readBuffer( urlConn.getInputStream() );
	        
	        //关闭http连接 
	        urlConn.disconnect();
	        
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

}

// end of file