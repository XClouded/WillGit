package com.willin.plugin.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.willin.plugin.core.PluginContext;

public class PluginActivity extends Activity implements IPluginClient{

	
	/// 状态信息
	protected boolean mIsRunInPlugin = false;
	private boolean mFinished = false;

	protected Activity mOutActivity = null;
	private Activity mActivity = null;
	
	public View mContentView = null;
	private Context mContext = null;
	
	/// 插件信息
	protected String mApkPath = "";
	protected PackageInfo mPackageInfo;
	private ClassLoader mDexClassLoader = null;
	
	// 维护已经加载过的 dex
	final static private Map<String, Context> mLoadedContext = new HashMap<String, Context>();
	
	
	public PluginActivity() {
		super();
	}
	
	// ========================================= override Activity methods ===================================== //
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if (mIsRunInPlugin) {
			mActivity = mOutActivity;
		} 
		else {
			super.onCreate(savedInstanceState);
			mActivity = this;
		}
		
	}
	
	
	@Override
	public void setContentView(int layoutResID) {
		
		if (mIsRunInPlugin) {
			mContentView = LayoutInflater.from(mContext).inflate(layoutResID, null);
			mActivity.setContentView(mContentView);
		} 
		else {
			super.setContentView(layoutResID);
		}
		
	}
	
	
	@Override
	public void setContentView(View view) {
		
		mContentView = view;
		
		if (mIsRunInPlugin) {
			mActivity.setContentView(view);
		}
		else
		{
			super.setContentView(view);
		}

		
	}
	
	
	
	@Override
	public View findViewById(int id) {
		
		if (mIsRunInPlugin && mContentView != null) {
			return mContentView.findViewById(id);
		} 
		else {
			return super.findViewById(id);
		}
		
	}
	
	
	
	@Override
	public void startActivity(Intent intent) {
		
		// 先判断外部能否处理该Intent
		List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		
		if (list != null && list.size() != 0) {
			mActivity.startActivity(intent);
		}
		else if (mIsRunInPlugin) {
			
			intent.putExtra("IsPluginActivity", true);
			intent.putExtra("path", mApkPath);
			mActivity.startActivity(intent);
			
		}
	}
	
	
	
	@Override
	protected void onResume() {
		
		if (mIsRunInPlugin) {
			return;
		}
		super.onResume();
		
	}
	
	
	
	@Override
	protected void onPause() {
		
		if (mIsRunInPlugin) {
			return;
		}
		super.onPause();
		
	}
	
	
	@Override
	protected void onStart() {
		
		if (mIsRunInPlugin) {
			return;
		}
		super.onStart();
	}
	
	
	
	@Override
	protected void onRestart() {
		
		if (mIsRunInPlugin) {
			return;
		}
		super.onRestart();
		
	}
	
	
	
	@Override
	protected void onStop() {
		
		if (mIsRunInPlugin) {
			return;
		}
		super.onStop();
		
	}
	
	
	@Override
	protected void onDestroy() {
		
		if (mIsRunInPlugin) {
			return;
		}
		
		// 清除 dexClassLoader
		mDexClassLoader = null;
		super.onDestroy();
		
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (mIsRunInPlugin) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if (mIsRunInPlugin) {
			return false;
		}
		return super.onKeyUp(keyCode, event);
		
	}
	
	
	
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		
		if (mIsRunInPlugin) {
			return false;
		}
		return super.onKeyMultiple(keyCode, repeatCount, event);
		
	}
	
	
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		
		if (mIsRunInPlugin) {
			return false;
		}
		return super.onKeyLongPress(keyCode, event);
		
	}
	
	
	
	@Override
	public void finish() {
		
		if (mIsRunInPlugin) {
			mOutActivity.finish();
			mFinished = true;
		} 
		else {
			super.finish();
		}
		
	}
	
	
	
	@Override
	public boolean isFinishing() {
		
		if (mIsRunInPlugin) {
			return mFinished;
		} 
		else {
			return super.isFinishing();
		}
	}
	
	
	
	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(mContext);
	}
	
	
	
	@Override
	public String getPackageName() {
		
		if ( mIsRunInPlugin ) {
			
			if ( mPackageInfo != null )
				return mPackageInfo.packageName;
			else
				return super.getPackageName();
		}
		else
		{
			return super.getPackageName();
		}
	}
	
		
	@Override
	public ApplicationInfo getApplicationInfo() {
		
		if ( mIsRunInPlugin ) {
			
			if ( mPackageInfo != null )
				return mPackageInfo.applicationInfo;
			else
				return super.getApplicationInfo();
		}
		else
		{
			return super.getApplicationInfo();
		}
		
	}
	

	@Override
	public Object getSystemService(String name) {
		
		if ( mIsRunInPlugin ) {
			
			if ( WINDOW_SERVICE.equals(name) ||
				 SEARCH_SERVICE.equals(name) ) {
				return mActivity.getSystemService(name);
			}
				
			return mContext.getSystemService(name);
		}
		else
		{
			return super.getSystemService(name);
		}
		
	}

	
	
	
	@Override
	public WindowManager getWindowManager() {
		
		if ( mIsRunInPlugin ) {
			
			if ( mActivity != null )
				return mActivity.getWindowManager();
			else
				return super.getWindowManager();
		}
		else
		{
			return super.getWindowManager();
		}
	}
	
	
	@Override
	public int getChangingConfigurations() {
		
		if ( mIsRunInPlugin ) {
			if ( mActivity != null )
				return mActivity.getChangingConfigurations();
			else
				return super.getChangingConfigurations();
		}
		else
		{
			return super.getChangingConfigurations();
		}

	}
	
	
	@Override
	public Window getWindow() {
		
		if ( mIsRunInPlugin ) {
			if ( mActivity != null )
				return mActivity.getWindow();
			else
				return super.getWindow();
		}
		else
		{
			return super.getWindow();
		}
		
	}
	
	/*
	@Override
	public void setRequestedOrientation(int requestedOrientation) {
		super.setRequestedOrientation(requestedOrientation);
	}
	*/
	
	// ========================================= override Activity methods end ================================= //
	
	
	
	
	
	// ========================================== implement IPluginClient ====================================== //
	
	@Override
	public void client_init(String pluginPath, Activity fromActivity,
			ClassLoader classLoader, PackageInfo info, Intent intent) {

		mIsRunInPlugin = true;
		mDexClassLoader = classLoader;
		mOutActivity = fromActivity;
		mApkPath = pluginPath;
		mPackageInfo = info;

		// 缓存context
		mContext = mLoadedContext.get(mApkPath);
		if (mContext == null) {
			mContext = new PluginContext(fromActivity, 0, mApkPath, mDexClassLoader);
			mLoadedContext.put(mApkPath, mContext);
		}
		
		attachBaseContext(mContext);
		
		// 传递参数
		setIntent(intent);

	}

	
	
	@Override
	public void client_onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState);
	}

	
	
	@Override
	public void client_onDestroy() {
		onDestroy();
	}

	
	
	@Override
	public void client_onStart() {
		onStart();
	}
	
	
	@Override
	public void client_onRestart() {
		onRestart();
	}
	

	@Override
	public void client_onResume() {
		onResume();
	}

	
	
	@Override
	public void client_onPause() {
		onPause();
	}

	
	
	@Override
	public void client_onStop() {
		onStop();
	}

	
	
	@Override
	public void client_onNewIntent(Intent i) {
		onNewIntent( i );
	}

	
	@Override
	public boolean client_onKeyDown(int keyCode, KeyEvent keyEvent) {
		return onKeyDown( keyCode, keyEvent );
	}

	
	@Override
	public boolean client_onKeyUp(int keyCode, KeyEvent keyEvent) {
		return onKeyUp( keyCode, keyEvent );
	}

	
	@Override
	public boolean client_onKeyLongPress(int keyCode, KeyEvent keyEvent) {
		return onKeyLongPress( keyCode, keyEvent );
	}

		
	@Override
	public void client_onBackPressed() {
		onBackPressed();
	}

	
	@Override
	public boolean client_onTouchEvent(MotionEvent motionEvent) {
		return onTouchEvent( motionEvent );
	}

	@Override
	public View client_getContentView() {
		return mContentView;
	}

	@Override
	public Resources client_getResource() {
		if (mContext != null) {
			return mContext.getResources();
		}
		return mActivity.getResources();
	}

	@Override
	public void client_onLowMemory() {
		onLowMemory();
	}

	
	// ========================================== implement IPluginClient end ====================================== //
	
	

	public PackageInfo getPackageInfo() {
		return mPackageInfo;
	}
	
	
	public Resources getOutResources() {
		
		if (mIsRunInPlugin == true) {
			return mOutActivity.getResources();
		}
		
		return mActivity.getResources();
	}
	
	
	public Activity getOutActivity() {
		return mOutActivity;
	}
	
	
}

// end of file