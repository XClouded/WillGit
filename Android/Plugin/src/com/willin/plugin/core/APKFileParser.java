package com.willin.plugin.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;



public class APKFileParser {

	
	// 获取 APK 包的签名信息
	public static Signature[] getSignaure(String archiveFilePath) {
	
		Signature[] ret = null;
		
		try {

			final String PACKAGE_PARSER_NAME = "android.content.pm.PackageParser";
			final String PARSE_PACKAGE_NAME = "parsePackage";
			Class<?> classPackageParser = Class.forName(PACKAGE_PARSER_NAME);
			Constructor<?> con = classPackageParser.getConstructor(new Class[] { String.class });
			Method PackageParser_parsePackage = classPackageParser.getMethod(PARSE_PACKAGE_NAME, 
					new Class[] { File.class, String.class, DisplayMetrics.class, int.class });
			
			final String PACKEGE_NAME = "android.content.pm.PackageParser$Package";
			final String COLLECT_CERTIFICATES_NAME = "collectCertificates";
			Class<?> classPackage = Class.forName(PACKEGE_NAME);
			Method PackageParser_collectCertificates = classPackageParser.getMethod(COLLECT_CERTIFICATES_NAME, 
					new Class[] { classPackage, int.class });
			
			// 准备参数
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			final File sourceFile = new File(archiveFilePath);
						
			Object packageParser = con.newInstance(archiveFilePath);
			
			// 调用方法
			// PackageParser.Package pkg = packageParser.parsePackage( sourceFile, archiveFilePath, metrics, 0);
			Object Package = PackageParser_parsePackage.invoke(packageParser, sourceFile, archiveFilePath, metrics, 0);
			if (Package == null) {
				return ret;
			}
		
			// 调用方法
			// packageParser.collectCertificates(pkg, 0);
			PackageParser_collectCertificates.invoke(packageParser, Package, PackageManager.GET_SIGNATURES);
			
			// get signatures
			Field Package_mSignatures = classPackage.getField("mSignatures");			
			Object signatures = Package_mSignatures.get(Package);
			if ( signatures != null && 
				 signatures instanceof Signature[] ) {
				ret = (Signature[]) signatures;
			}
			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return ret;
		
	}
	
	
	
	// 比较签名是否一致
	public static boolean isSignaturesSame(Signature[] s1, Signature[] s2) {
		
		boolean ret = false;
		
		assert( s1 != null &&
				s1.length > 0 &&
				s2 != null &&
				s2.length > 0 );
		
		if ( s1 != null &&
				s1.length > 0 &&
				s2 != null &&
				s2.length > 0 ) {
		
			HashSet<Signature> set1 = new HashSet<Signature>();
			for (Signature sig : s1) {
				set1.add(sig);
			}
			
			HashSet<Signature> set2 = new HashSet<Signature>();
			for (Signature sig : s2) {
				set2.add(sig);
			}

			ret = set1.equals(set2);
			
		}
		
		return ret;
	}
	
	
	
	// 获得APK包的PackageInfo
	public static PackageInfo getPackageInfo(String archiveFilePath, int flags) {
		
		PackageInfo ret = null;
		
		// 获得类
		try {
			
			final String PATH_packageParser = "android.content.pm.PackageParser";
			
			Class<?> classPackageParser = Class.forName(PATH_packageParser);
			Constructor<?> con = classPackageParser.getConstructor(new Class[] { String.class });
			Method PackageParser_parsePackage = classPackageParser.getMethod("parsePackage", 
					new Class[] { File.class, String.class, DisplayMetrics.class, int.class });
			
			
			final String PATH_packageParserPackage = "android.content.pm.PackageParser$Package";
			
			Class<?> classPackage = Class.forName(PATH_packageParserPackage);
			Method PackageParser_collectCertificates = classPackageParser.getMethod("collectCertificates", new Class[] { classPackage, int.class });
			
			// 实例化PackageParser
			Object packageParser = con.newInstance(archiveFilePath);
			
			// 准备参数
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			final File sourceFile = new File(archiveFilePath);
			
			// 调用方法 
			// PackageParser.Package pkg = packageParser.parsePackage( sourceFile, archiveFilePath, metrics, 0);
			Object Package = PackageParser_parsePackage.invoke(packageParser, sourceFile, archiveFilePath, metrics, 0);
			if (Package == null) {
				return ret;
			}
			
			// 调用方法 
			// packageParser.collectCertificates(pkg, 0);
			if( (flags|PackageManager.GET_SIGNATURES) != 0){
				PackageParser_collectCertificates.invoke(packageParser, Package, flags);
			}
			
			Object packageInfo = null;
			Method PackageParser_generatePackageInfo = null;
			
			try {
				// generatePackageInfo(android.content.pm.PackageParser$Package,int[],int,long,long)
				PackageParser_generatePackageInfo = classPackageParser.getMethod("generatePackageInfo", 
					new Class[] { classPackage, int[].class, int.class, long.class, long.class });
				packageInfo = PackageParser_generatePackageInfo.invoke(packageParser, Package, null, flags, 0, 0);
			}
			catch (NoSuchMethodException e3) {
				try {
					// generatePackageInfo(android.content.pm.PackageParser.Package,int,int)
					PackageParser_generatePackageInfo = classPackageParser.getMethod("generatePackageInfo", 
							new Class[] { classPackage, int.class, int.class });
					packageInfo = PackageParser_generatePackageInfo.invoke(packageParser, Package, null, flags);
				} catch (NoSuchMethodException e1) {
					try{
					// generatePackageInfo(android.content.pm.PackageParser.Package,int[],int)
					PackageParser_generatePackageInfo = classPackageParser.getMethod("generatePackageInfo",
							new Class[] { classPackage, int[].class, int.class });
					packageInfo = PackageParser_generatePackageInfo.invoke(packageParser, Package, null, flags);
					} catch (NoSuchMethodException e2) {
						// generatePackageInfo(android.content.pm.PackageParser$Package,int[],int,long,long,java.util.HashSet,boolean,int,int)
						PackageParser_generatePackageInfo = classPackageParser.getMethod("generatePackageInfo",
								new Class[] { classPackage, int[].class, int.class, long.class, long.class, HashSet.class, boolean.class, int.class, int.class });
						packageInfo = PackageParser_generatePackageInfo.invoke(packageParser, Package, null, flags, 0, 0, null, false, 0, 0);
					}
				}
			}
			
			if ( packageInfo != null &&
				 packageInfo instanceof PackageInfo ) {
				ret = (PackageInfo) packageInfo;
			}
			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return ret;
		
	}
	
	
	// 获取ICON
	public static Drawable getAPKIcon(Context ct, String apkPath) {
		
		Drawable ret = null;
		
		assert( ct != null &&
				apkPath != null &&
				apkPath.length() > 0 );
		
		if ( ct != null && 
			 apkPath != null && 
			 apkPath.length() > 0 ) {

			PackageManager pm = ct.getPackageManager();
			if ( pm != null ) {
				
				PackageInfo pi = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES );
				if ( pi != null ) {
					ApplicationInfo appInfo = pi.applicationInfo;
					
					// AssertManager
					final String PATH_AssetManager = "android.content.res.AssetManager";
					try {
						Class<?> assetMagCls = Class.forName(PATH_AssetManager);
						Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[]) null);
						
						AssetManager assetMag = (AssetManager) assetMagCt.newInstance((Object[]) null);
						
						Class<?>[] typeArgs = new Class[1];
						typeArgs[0] = String.class;
						Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);

						Object[] valueArgs = new Object[1];
						valueArgs[0] = apkPath;
						assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
						
						// Resources
						DisplayMetrics metrics = new DisplayMetrics();
						metrics.setToDefaults();
						Configuration con = ct.getResources().getConfiguration();
						Resources res = new Resources(assetMag, metrics, con);
						
						if (appInfo.icon != 0) {
							ret = res.getDrawable(appInfo.icon);
						}
						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		}

		
		return ret;
		
	}
	
	
	
	// APK是否可用
	public static boolean isApkFileOK(Context ct, String apkPath) {
		
		boolean ret = false;
		
		assert( ct != null &&
				apkPath != null &&
				apkPath.length() > 0 );
		
		if ( ct != null && 
			 apkPath != null && 
			 apkPath.length() > 0 ) {
			
			PackageManager pm = ct.getPackageManager();
			if ( pm != null ) {
				PackageInfo pi = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES );
				if ( pi != null && 
					 pi.applicationInfo != null ) {
					ret = true;
				}
			}
			
		}
		
		return ret;
	}
	
	
}

// end of file