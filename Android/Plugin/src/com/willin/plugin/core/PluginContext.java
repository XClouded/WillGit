package com.willin.plugin.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.willin.plugin.core.PluginContext;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;

public class PluginContext extends ContextThemeWrapper {

	private static final String TAG = PluginContext.class.getSimpleName();
	
	private int mThemeResId = 0;
	
	private ClassLoader mClassLoader = null;

	private Context mOutContext = null;
	
	private Resources mResources = null;
	
	private AssetManager mAsset = null;
	
	private Theme mTheme = null;

	
	
	
	
	// constructor
	public PluginContext(Context base, int themeres, String apkPath, ClassLoader classLoader) {
		
		super(base, themeres);
		
		mClassLoader = classLoader;
		mAsset = getMyAssets(apkPath);
		mResources = getMyResources(base, mAsset);
		mTheme = getMyTheme(mResources);
		mOutContext = base;
		
	}
	
	
	
	@Override
	public Resources getResources() {
		return mResources;
	}
	

	@Override
	public AssetManager getAssets() {
		return mAsset;
	}
	

	@Override
	public Theme getTheme() {
		return mTheme;
	}
	
	
	@Override
	public ClassLoader getClassLoader() {
		
		if (mClassLoader != null) {
			return mClassLoader;
		}
		
		return super.getClassLoader();
	}
	
	
		
	private int getInnerRIdValue(String rString) {
		int value = -1;
		
		try {
			
			int rindex = rString.indexOf(".R.");
			String Rpath = rString.substring(0, rindex + 2);
			
			// get the fieldName
			int fieldIndex = rString.lastIndexOf(".");
			String fieldName = rString.substring(fieldIndex + 1, rString.length());
				
			rString = rString.substring(0, fieldIndex);
			
			// get the className
			String type = rString.substring(rString.lastIndexOf(".") + 1, rString.length());
			String className = Rpath + "$" + type;
		
			Class<?> cls = Class.forName(className);
			value = cls.getDeclaredField(fieldName).getInt(null);
		
			Log.d(TAG, "getInnderR rStrnig:" + rString);
			Log.d(TAG, "getInnderR className:" + className);
			Log.d(TAG, "getInnderR fieldName:" + fieldName);
						
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	
	private AssetManager getMyAssets(String apkPath) {
		
		AssetManager instance = null;
		
		try {
			
			// 获取AssetManager对象
			instance = AssetManager.class.newInstance();
			
			final String ADD_ASSERT_PATH = "addAssetPath";
			// 执行addAssetPath添加apk所在路径
			
			Method addAssetPathMethod = AssetManager.class.getDeclaredMethod( ADD_ASSERT_PATH, String.class );
			addAssetPathMethod.invoke(instance, apkPath);
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		
		return instance;
		
	}
	
	
	private Resources getMyResources(Context ctx, AssetManager selfAsset) {
		
		DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
		Configuration con = ctx.getResources().getConfiguration();
		
		return new Resources(selfAsset, metrics, con);
		
	}
	
	
	private Theme getMyTheme(Resources selfResources) {
		
		Theme theme = selfResources.newTheme();
		
		final String DEFAULT_THEME_ID = "com.android.internal.R.style.Theme";
		mThemeResId = getInnerRIdValue( DEFAULT_THEME_ID );
		theme.applyStyle(mThemeResId, true);
		
		return theme;
		
	}
	
	
	public Context getOutContext(){
		return mOutContext;
	}
	
	
	
	public void setClassLoader(ClassLoader classLoader) {
		mClassLoader = classLoader;
	}
	
}


// end of file