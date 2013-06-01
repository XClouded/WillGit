package com.willin.net.http;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.text.TextUtils;


public class NetworkStateQuery {

	public static final String TAG  = "NetworkStateQuery";
	
	
	
	public static final byte NET_TYPE_DEFAULT = (byte) -1;
	// wifi
	public static final byte NET_TYPE_WIFI = (byte)1;
	// cmwap
	public static final byte NET_TYPE_CMWAP = (byte)4;
	// cmnet
	public static final byte NET_TYPE_CMNET = (byte)5;
	// uniwap
	public static final byte NET_TYPE_UNIWAP = (byte)6;
	// uninet
	public static final byte NET_TYPE_UNINET = (byte)7;
	// 3gwap
	public static final byte NET_TYPE_3GWAP = (byte)8;
	// 3gnet
	public static final byte NET_TYPE_3GNET = (byte)9;
	// ctwap
	public static final byte NET_TYPE_CTWAP = (byte)10;
	// ctnet
	public static final byte NET_TYPE_CTNET = (byte)11;
	
	// wap类网络但是具体是哪个wap不清楚
	public static final byte NET_TYPE_WAP = (byte) 98;
	// 未知网络类型
	public static final byte NET_TYPE_UNKNOWN = (byte) 99;
	
	
	
	private static boolean isNetworkActive = true;
	private static byte netByte = NET_TYPE_DEFAULT;
	
	
	
	private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	
	
	
	static final HashMap<String, Byte> NET_TYPES = new HashMap<String, Byte>();
	// initial
	static {
		NET_TYPES.put( "wifi",   NET_TYPE_WIFI );
		NET_TYPES.put( "cmwap",  NET_TYPE_CMWAP );
		NET_TYPES.put( "cmnet",  NET_TYPE_CMNET );
		NET_TYPES.put( "uniwap", NET_TYPE_UNIWAP );
		NET_TYPES.put( "uninet", NET_TYPE_UNINET );
		NET_TYPES.put( "3gwap",  NET_TYPE_3GWAP );
		NET_TYPES.put( "3gnet",  NET_TYPE_3GNET );
		NET_TYPES.put( "ctwap",  NET_TYPE_CTWAP );
		NET_TYPES.put( "ctnet",  NET_TYPE_CTNET );
	}
	
	
	
	public static String getApnName( Context context ) {

		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (conn == null) {
			isNetworkActive = false;
			return "NA";
		}
		
		// network is not available
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			isNetworkActive = false;
			return "NA";
		}
		
		isNetworkActive = true;
		
		// MOBILE（GPRS）0;
		// WIFI         1;
		int type = info.getType();
		
		if (type == ConnectivityManager.TYPE_WIFI) {
			return "wifi";
		} 
		else {
			Cursor c = null;
			try{
				
				c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
				if(c == null){
					return "NA";
				}
				
				c.moveToFirst();
				String apn = c.getString(c.getColumnIndex("apn"));
				return TextUtils.isEmpty(apn) ? "NA" : apn;
				
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(c != null){
					c.close();
				}
			}
			return "NA";
		}
	}
	
	
	
	private static byte refreshNetByte( Context context ) {
		
		synchronized (NET_TYPES) {
			
			netByte = NET_TYPE_UNKNOWN;
			final String apn = getApnName(context);
			Set<String> set = NET_TYPES.keySet();
			for (String key : set) {
				if (apn.contains(key)) {
					netByte = NET_TYPES.get(key);
					break;
				}
			}
			
			// 如果都不是已知的网络类型，看一下是否有代理，有的话就取98
			if(netByte == NET_TYPE_UNKNOWN){
				String defaultHost = Proxy.getDefaultHost();
				int defaultPort = Proxy.getDefaultPort();
				if(!TextUtils.isEmpty(defaultHost) && defaultPort != 0){
					netByte = NET_TYPE_WAP;
				}
			}
			
			return netByte;
		}
		
	}
	
	
	
	public static byte getNetByte( Context context ) {
		
		byte ret = NET_TYPE_UNKNOWN;
		
		if ( netByte == (byte) NET_TYPE_DEFAULT ) {
			ret = refreshNetByte( context );
		}
		
		return ret;
	}
	
	
	
	public static boolean isNetworkActive( Context context ) {
		
		if ( netByte == (byte) NET_TYPE_DEFAULT ) {
			refreshNetByte( context );
		}
		
		return isNetworkActive;
	}
	
	
	
	public static boolean isWifi( Context context ){
		
		return getNetByte( context ) == (byte)1;
	}
	
	
}

// end of file