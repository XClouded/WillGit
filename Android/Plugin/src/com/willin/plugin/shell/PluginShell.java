package com.willin.plugin.shell;

import java.io.File;

import com.willin.plugin.core.IPluginMgr;
import com.willin.plugin.core.PluginCore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class PluginShell extends Activity {

	private Toast mToast = null;
	private IPluginMgr mCore = PluginCore.getMgr();
	static ClassLoader mClassloader;
	
	public static final String MSG_PATH = "pluginLocation";
	public static final String MSG_ACTIVITYNAME = "launchActivity";
	
	private String mPluginPath = "";
	private String mLaunchActivity = "";
	
	private boolean bOnResumeCausebyOnCreate = true;
	
	
	public String getSDPath(){
		
		File sdDir = null;
		
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);		
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		}
		
		return sdDir.toString();
	}
	
	
	
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (mClassloader != null) {
			intent.setExtrasClassLoader(mClassloader);
		}

		mPluginPath = intent.getStringExtra( MSG_PATH );
		mLaunchActivity = intent.getStringExtra("launchActivity");

		// test
		mPluginPath = getSDPath() + File.separator + "PluginTest1.apk";
		mLaunchActivity = "com.tencent.test.MainActivity";
		
		if ( mPluginPath == null ||
			 mPluginPath.length() == 0) {
			
			showToast("code 1: 插件路径不存在:" + mPluginPath );
			
			finish();
			return;
		}

		File f = new File(mPluginPath);
		if (!f.exists()) {
			showToast("code 2: 插件路径不存在:" + mPluginPath );
			finish();
			return;
		}

	}

	
	
	private Handler mLoadPluginHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			switch (msg.what) {
			
				case PluginCore.MSG_LOADPLUGIN: {

					File f = new File( mPluginPath );
					if (!f.exists()) {
						showToast("插件不存在" + mPluginPath + "上");
						finish();
						return;
					}
					
					try {
						
						if (! mCore.startPlugin(mPluginPath, PluginShell.this, mLaunchActivity, true)) {
							showToast("插件启动失败");
							finish();
							return;
						}

					} catch (Exception e) {
						e.printStackTrace();
						showToast("插件启动失败," + e.getMessage());
						finish();
						return;
					}
	
					mCore.onResume();
				}
					break;
					
					
				case PluginCore.MSG_ONRESUME: {
					// 回调插件的OnResume
					mCore.onResume();
				}
					break;
					
					
				default:
					break;
			}
		}
	};
	
	
	
	public void showToast(CharSequence text) {
		
		if (mToast == null) {
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		}
		mToast.setText(text);
		mToast.show();
		
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		boolean ret = false;
		
		ret = mCore.onKeyDown(keyCode, event);
		
		if ( !ret ) {
			ret = super.onKeyDown(keyCode, event);
		}
		
		return ret;
		
	}
	
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		boolean ret = false;
		
		ret = mCore.onKeyUp(keyCode, event);
		
		if ( !ret ) {
			ret = super.onKeyUp(keyCode, event);
		}
		
		return ret;
		
	}
	
	
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		
		boolean ret = false;
		
		ret = mCore.onKeyLongPress(keyCode, event);
		
		if ( !ret ) {
			ret = super.onKeyLongPress(keyCode, event);
		}
		
		return ret;
		
	}
	
	
	
	@Override
	public void onBackPressed() {
		
		mCore.onBackPressed();

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		
		boolean ret = false;
		
		ret = mCore.onTouchEvent(motionEvent);
		
		if ( !ret ) {
			ret = super.onTouchEvent(motionEvent);
		}
		
		return ret;
		
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (bOnResumeCausebyOnCreate) {
			
			bOnResumeCausebyOnCreate = false;
			
			// 延迟加载
			mLoadPluginHandler.sendEmptyMessageDelayed( PluginCore.MSG_LOADPLUGIN, 200 ); 
			
		} else {
			mLoadPluginHandler.sendEmptyMessageDelayed( PluginCore.MSG_ONRESUME, 0 );
		}
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();

		mCore.onStop();
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mCore.onPause();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		mCore.onDestroy();
	}
	
	
}

// end of file