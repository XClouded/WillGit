package com.willin.plugin.core;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface IPluginMgr {

	//
	// "启动"插件的视图
	// 
	// @param canonicalPath 全路径
	// @param filePath 文件目录
	// @param bFullScreen
	// @throws Exception
	//
	public boolean startPlugin(String canonicalPath, Activity shell, String activityName, boolean bFullScreen) 
			throws Exception;
	
	
	// 退出插件
	public void exitPlugin();
	
	
	// 
	public boolean onKeyDown(int keyCode, KeyEvent event);
	public boolean onKeyUp(int keyCode, KeyEvent event);
	public boolean onKeyLongPress(int keyCode, KeyEvent event);
	public void onBackPressed();
	public boolean onTouchEvent(MotionEvent motionEvent);
	public void onResume();
	public void onStop();
	public void onPause();
	public void onDestroy();
	
}


// end of file