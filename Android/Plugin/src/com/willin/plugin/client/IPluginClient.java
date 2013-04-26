package com.willin.plugin.client;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;



public interface IPluginClient {

	// 初始化
	void client_init( String pluginPath, Activity fromActivity, ClassLoader classLoader, PackageInfo info, Intent intent );
	
	//  生命周期函数
	void client_onCreate( Bundle savedInstanceState );
	void client_onDestroy();
	void client_onStart();
	void client_onRestart();
	void client_onResume();
	void client_onPause();
	void client_onStop();
	
	void client_onNewIntent( Intent i );		
	
	// 按键响应函数
	boolean client_onKeyDown( int keyCode, KeyEvent keyEvent );
	boolean client_onKeyUp( int keyCode, KeyEvent keyEvent );		
	boolean client_onKeyLongPress( int keyCode, KeyEvent keyEvent );		
	void client_onBackPressed();
	
	// 触屏响应函数
	boolean client_onTouchEvent( MotionEvent motionEvent );

	// View
	public View client_getContentView();
	
	// resource
	public Resources client_getResource();
	
	// low memory
	void client_onLowMemory();	

}



// end of file