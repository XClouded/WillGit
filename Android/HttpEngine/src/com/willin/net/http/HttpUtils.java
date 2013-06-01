package com.willin.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.text.TextUtils;


public class HttpUtils {
	
	public static final String TAG = "HttpUtils";
	
	private final static int CONNECTION_TIMEOUT_DEFAULT = 10;
	private final static int SO_TIMEOUT_DEFAULT = 30;
	private final static int CONNECTION_TIMEOUT_WIFI = 5;
	private final static int SO_TIMEOUT_WIFI = 30;
	private final static int CONNECTION_TIMEOUT_2G = 5;
	private final static int SO_TIMEOUT_2G= 30;
	private final static int CONNECTION_TIMEOUT_3G = 10;
	private final static int SO_TIMEOUT_3G= 40;
	private final static int CONNECTION_TIMEOUT_UNKNOWN = 20;
	private final static int SO_TIMEOUT_UNKNOWN = 40;
	
	private final static int DEFAULT_RETRY_COUNT = 3;
	
	/**
	 * 获取HttpClient对象
	 * @param connectionTimeout 连接超时时间 			单位：秒
	 * @param soTimeout			数据获取超时时间 		单位：秒
	 * @return
	 */
	public static DefaultHttpClient getHttpClient( Context context, int connectionTimeout, int soTimeout, final int retryCount ){
		// Shamelessly cribbed from AndroidHttpClient
		HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 10 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout * 1000);
		HttpConnectionParams.setSoTimeout(params, soTimeout * 1000);
		
		// 8k
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));

		final ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		DefaultHttpClient client = new DefaultHttpClient(ccm, params);

		// -----for proxy
		String defaultHost = Proxy.getDefaultHost();
		int defaultPort = Proxy.getDefaultPort();
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		int type = activeNetworkInfo == null ? 0 : activeNetworkInfo.getType();
		if (type == ConnectivityManager.TYPE_MOBILE 
			&& defaultHost != null 
			&& defaultPort != -1) {
			
			// set proxy
			HttpHost proxy = new HttpHost(defaultHost, defaultPort);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			
		}
		// -----for proxy end
		
        // 设置 重试
        client.setHttpRequestRetryHandler(new HttpRequestRetryHandler(){

            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

                if (executionCount >= retryCount ) {
                    // 如果超过最大重试次数，那么就不要继续了
                    return false;
                }
                
                if (exception instanceof NoHttpResponseException) {
                    // 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                
                if (exception instanceof SSLHandshakeException) {
                    // 不要重试SSL握手异常
                    return false;
                }
                return false;
            }
        });


		return client;
	}

	
	
	public static DefaultHttpClient getHttpClient( Context context ) {
		return getHttpClient( context, CONNECTION_TIMEOUT_3G, SO_TIMEOUT_3G, 2 );
	}
	
	
	
	public static HttpResponse post( Context context, String url, String host, byte[] data) {
		
		int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;
		int soTimeout = SO_TIMEOUT_DEFAULT;
		
		if( NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_WIFI ){
			// wifi环境
			connectionTimeout = CONNECTION_TIMEOUT_WIFI;
			soTimeout = SO_TIMEOUT_WIFI;
		}else if( 
				NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_CMWAP
				|| NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_CMNET
				|| NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_UNIWAP
				|| NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_UNINET ){
			// 2g网络
			connectionTimeout = CONNECTION_TIMEOUT_2G;
			soTimeout = SO_TIMEOUT_2G;
		}else if(
				NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_3GWAP 
				|| NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_3GNET
				|| NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_CTWAP
				|| NetworkStateQuery.getNetByte(context) == NetworkStateQuery.NET_TYPE_CTNET ){
			// 3g网络
			connectionTimeout = CONNECTION_TIMEOUT_3G;
			soTimeout = SO_TIMEOUT_3G;
		}else{
			// 未知网络类型
			connectionTimeout = CONNECTION_TIMEOUT_UNKNOWN;
			soTimeout = SO_TIMEOUT_UNKNOWN;
		}
		
		
		final HttpClient client = getHttpClient(context, connectionTimeout, soTimeout, DEFAULT_RETRY_COUNT);
		HttpPost httpPost = new HttpPost(url);
		try{
			// 这个保护一下，不确定是否会有异常
			if (!TextUtils.isEmpty(host)) {
				Uri uri = Uri.parse(url);
				if(!uri.getHost().equals(host)){
					httpPost.addHeader("Host", host);
					httpPost.addHeader("X-Online-Host", host);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		if(client.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY) == null){
			// wap类的必须设代理，用代理+gzip的话，可能会有问题。所以限定只有没有使用代理的网络的才gzip
			httpPost.addHeader("accept-encoding", "gzip,deflate");
		}
		
		
		ByteArrayEntity entity = new ByteArrayEntity(data);
		entity.setContentType("application/octet-stream");
		httpPost.setEntity(entity);
		try {
			HttpResponse response = client.execute(httpPost);
			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	public static byte[] post4Api( Context context, String server, String host, byte[] data ) {
		
		HttpResponse response = post( context, server, host, data );
		
		if (response != null && response.getStatusLine().getStatusCode() == 200) {
			
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			try {
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Header header = entity.getContentEncoding();
				if (header != null && header.getValue().toLowerCase().contains("gzip")) {
					is = new GZIPInputStream(is);
				}
				baos = new ByteArrayOutputStream();
				
				// 1k
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = is.read(b, 0, 1024)) != -1) {
					baos.write(b, 0, len);
				}
				baos.flush();
				
				return baos.toByteArray();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (baos != null) {
					try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
}

// end of file