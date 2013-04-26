

package com.willin.plugin.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.willin.plugin.client.IPluginClient;
import com.willin.plugin.core.APKFileParser;
import com.willin.plugin.core.IPluginMgr;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import dalvik.system.DexClassLoader;

public class PluginCore implements IPluginMgr{

	// message constants
	private static final int MSG_INDEX = 0;
	// ================================================================== //
	public static final int MSG_LOADPLUGIN = MSG_INDEX + 1;
	public static final int MSG_ONRESUME = MSG_INDEX + 2;

	// waiting for adding	
	// ================================================================== //
		
	// cache
	private HashMap<String, DexClassLoader> mClassLoaderMap = null;
	private HashMap<String, PackageInfo> mPackageInfoMap = null;
	
	
	// singleton
	protected static PluginCore myself = null;
	
	
	// 启动Activity的类
	private Class<?> mClassLaunchActivity = null;	
	// 启动的Activity类实例
	private IPluginClient mRunningActivity = null;
	private String mRunningActivityName = null;	

	private String mPluginPath = null;
	private ClassLoader mClassloader;
	
	// OnResume的回调是否由OnCreate引起的，判断一次后关掉 
	private boolean bOnResumeCausebyOnCreate = true;
	
	//
	private Activity mShell = null;
	
	
	// private constructor
	private PluginCore(){
		mClassLoaderMap = new HashMap<String, DexClassLoader>();
		mPackageInfoMap = new HashMap<String, PackageInfo>();
		
		mPluginPath = "";
		mRunningActivityName = "";

	}
	
	
	// public singleton
	public static PluginCore getMgr() {
		if ( myself == null ) {
			synchronized (PluginCore.class) {
				if ( myself == null ) {
					myself = new PluginCore();
				}
			}
		}
		
		return myself;
	}

	
	@Override
	public boolean startPlugin(String canonicalPath, Activity shell, String activityName, boolean bFullScreen)
			throws Exception {
		
		mShell = shell;
		
		// 取得插件信息
		PackageInfo packageInfo = mPackageInfoMap.get(canonicalPath);
		if (packageInfo == null) {
			packageInfo = APKFileParser.getPackageInfo(canonicalPath, PackageManager.GET_ACTIVITIES);
			if (packageInfo == null) {
				return false;
			}
			mPackageInfoMap.put(canonicalPath, packageInfo);
		}
		
		// 判断是否指定了启动的Activity
		if ( activityName == null ||
			 activityName.length() == 0 ) {
			
			// 根据dex文件所在路径，拿到插件的所有Activity类名
			if ( (packageInfo.activities == null) ||
				 (packageInfo.activities.length == 0) ) {				
				return false;				
			}
			
			mRunningActivityName = packageInfo.activities[0].name;
		}
		else
		{
			mRunningActivityName = activityName;
		}
		
		// 加载classLoader,如果包的版本号没有改变, 使用缓存的classLoader.如果改变, 把以前的classLoader做为新的Parent
		String pluginId = packageInfo.packageName + packageInfo.versionCode;
		DexClassLoader dexClassLoader = mClassLoaderMap.get(pluginId);
		if (dexClassLoader == null) {
			// 如果有升级,
			DexClassLoader preClassLoader = mClassLoaderMap.get(packageInfo.packageName);
			if (preClassLoader != null) {
				clearLayoutInflateCache();
				mClassLoaderMap.remove(preClassLoader);
			}

			String cache = mShell.getCacheDir().getCanonicalPath();
			dexClassLoader = new DexClassLoader(canonicalPath, cache, null, mShell.getClassLoader());
			
			mClassLoaderMap.put(pluginId, dexClassLoader); // 缓存当前版本的classLoader
			mClassLoaderMap.put(packageInfo.packageName, dexClassLoader); // 缓存该插件的classLoader
		}
				
		// 拿到插件入口的Activity实例
		mClassloader = dexClassLoader;
		mClassLaunchActivity =  mClassloader.loadClass(mRunningActivityName);

		// 先退出当前的activity
		if ( mRunningActivity != null )
		{
			mRunningActivity.client_onDestroy();
		}
		mRunningActivity = (IPluginClient) mClassLaunchActivity.newInstance();
		
		// 初始化插件
		mRunningActivity.client_init(canonicalPath, mShell, dexClassLoader, packageInfo, mShell.getIntent() );		

		// 启动插件
		mRunningActivity.client_onCreate(null);
		
		return true;
	}
	
	
	

	@Override
	public void exitPlugin() {

		clearLayoutInflateCache();
		
		mRunningActivity.client_onDestroy();
		mRunningActivity = null;

		mClassLoaderMap.remove(mPluginPath);
		
		mClassloader = null;
		mClassLaunchActivity = null;
		
		mRunningActivityName = null;
		mPluginPath = null;
		
	}
	
	
	
	
	
	private boolean clearLayoutInflateCache() {
		Field field = null;
		try {
			
			field = LayoutInflater.class.getDeclaredField("sConstructorMap");
			field.setAccessible(true);
			HashMap<String, Constructor> sConstructorMap = (HashMap<String, Constructor>) field.get(null);
			if ( sConstructorMap != null ) {
				sConstructorMap.clear();
			}

			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		boolean ret = false;
		
		if ( mRunningActivity != null ) {
			ret = mRunningActivity.client_onKeyDown(keyCode, event);
		}
		
		return ret;
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		boolean ret = false;
		
		if ( mRunningActivity != null ) {
			ret = mRunningActivity.client_onKeyUp(keyCode, event);
		}
		
		return ret;
		
	}


	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {

		boolean ret = false;
		
		if ( mRunningActivity != null ) {
			ret = mRunningActivity.client_onKeyLongPress( keyCode, event );
		}
		
		return ret;
		
	}


	@Override
	public void onBackPressed() {
		
		if ( mRunningActivity != null ) {
			mRunningActivity.client_onBackPressed();
		}
		else
		{
			mShell.onBackPressed();
		}
		
	}


	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {

		boolean ret = false;
		
		if ( mRunningActivity != null ) {
			ret = mRunningActivity.client_onTouchEvent(motionEvent);
		}
		
		return ret;
		
	}


	@Override
	public void onResume() {
		
		if ( mRunningActivity != null ) {
			mRunningActivity.client_onResume();
		}
		
	}


	@Override
	public void onStop() {
		if ( mRunningActivity != null ) {
			mRunningActivity.client_onStop();
		}
	}


	@Override
	public void onPause() {
		if ( mRunningActivity != null ) {
			mRunningActivity.client_onPause();
		}
	}


	@Override
	public void onDestroy() {
		if ( mRunningActivity != null ) {
			mRunningActivity.client_onDestroy();
		}
	}
	
	
}

// end of file