package com.willin.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.util.BitmapHelper;

import junit.framework.Assert;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class Util {
	
	private static final String TAG = "Util";
	
    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static byte[] decodes = new byte[256];
	
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
	
// ========================================================================================================================================= //    
    
    
    
	private static boolean isBundleEmpty(WeiboParameters bundle) {
		if (bundle == null || bundle.size() == 0) {
			return true;
		}
		return false;
	}
    
	
	
	
	private static boolean deleteDependon(File file, int maxRetryCount){
	    int retryCount = 1;
	    maxRetryCount = (maxRetryCount < 1) ? 5 : maxRetryCount;
	    boolean isDeleted = false;
	
	    if (file != null) {
	      while ((!(isDeleted)) && (retryCount <= maxRetryCount) && (file.isFile()) && (file.exists()))
	        if (!((isDeleted = file.delete()))) {
	          ++retryCount;
	        }	
	    }
	
	    return isDeleted;
    }

	
	
	
	private static void mkdirs(File dir_) {
		if (dir_ == null) {
			return;
		}
		if ((!(dir_.exists())) && (!(dir_.mkdirs())))
			throw new RuntimeException("fail to make " + dir_.getAbsolutePath());
	}

	
	
	
	private static boolean __createNewFile(File file_) {
		if (file_ == null) {
			return false;
		}
		makesureParentExist(file_);
		if (file_.exists())
			delete(file_);
		try {
			return file_.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	
	private static boolean deleteDependon(String filepath, int maxRetryCount) {
		if (TextUtils.isEmpty(filepath))
			return false;
		return deleteDependon(new File(filepath), maxRetryCount);
	}
	
	
	
	
	private static boolean deleteDependon(String filepath) {
		return deleteDependon(filepath, 0);
	}
	
	
	
	private static boolean doesExisted(File file) {
		return ((file != null) && (file.exists()));
	}
	
	
	
	private static boolean doesExisted(String filepath) {
		if (TextUtils.isEmpty(filepath))
			return false;
		return doesExisted(new File(filepath));
	}
	
	
	
	private static void makesureParentExist(File file_) {
		if (file_ == null) {
			return;
		}
		File parent = file_.getParentFile();
		if ((parent != null) && (!(parent.exists())))
			mkdirs(parent);
	}
	
	
	
	private static void createNewFile(File file_) {
		if (file_ == null) {
			return;
		}
		if (!(__createNewFile(file_)))
			throw new RuntimeException(file_.getAbsolutePath() + " doesn't be created!");
	}
	
	
	
	private static void delete(File f) {
		if ((f != null) && (f.exists()) && (!(f.delete()))) {
			throw new RuntimeException(f.getAbsolutePath()
					+ " doesn't be deleted!");
		}

	}
	
	
	
	private static void makesureFileExist(File file) {
		if (file == null)
			return;
		if (!(file.exists())) {
			makesureParentExist(file);
			createNewFile(file);
		}
	}
	
	
	
	
	private static void makesureFileExist(String filePath_) {
		if (filePath_ == null)
			return;
		makesureFileExist(new File(filePath_));
	}
    
	
	
	
// ========================================================================================================================================= //
	
	
    
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	
	
	
	public static byte[] getHtmlByteArray(final String url) {
		 URL htmlUrl = null;     
		 InputStream inStream = null;     
		 try {         
			 htmlUrl = new URL(url);         
			 URLConnection connection = htmlUrl.openConnection();         
			 HttpURLConnection httpConnection = (HttpURLConnection)connection;         
			 int responseCode = httpConnection.getResponseCode();         
			 if(responseCode == HttpURLConnection.HTTP_OK){             
				 inStream = httpConnection.getInputStream();         
			  }     
			 } catch (MalformedURLException e) {               
				 e.printStackTrace();     
			 } catch (IOException e) {              
				e.printStackTrace();    
		  } 
		byte[] data = inputStreamToByte(inStream);

		return data;
	}
	
	
	
	
	
	public static byte[] inputStreamToByte(InputStream is) {
		try{
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
	
	
	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

		if(offset <0){
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if(len <=0 ){
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if(offset + len > (int) file.length()){
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合适文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}
	
	
	
	
	
	
	public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}
	
	
	
	
	
	
	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
			}
		}
		return params;
	}
	
	
	
	
	
	
	public static Bundle parseUrl(String url) {
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}
	
	
	
	
	
	public static String encodeUrl(WeiboParameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			if (first){
			    first = false;
			}
			else{
			    sb.append("&");
			}
			String _key=parameters.getKey(loc);
			String _value=parameters.getValue(_key);
			if(_value==null){
			    Log.i("encodeUrl", "key:"+_key+" 's value is null");
			}
			else{
			    sb.append(URLEncoder.encode(parameters.getKey(loc)) + "="
                        + URLEncoder.encode(parameters.getValue(loc)));
			}
			
		}
		return sb.toString();
	}
	
	
	
	
	public static String encodeParameters(WeiboParameters httpParams) {
		if (null == httpParams || isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();

	}
	
	
	
	
	
	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}
	
	
	
	
	// 将data编码成Base62的字符串
	public static String encodeBase62(byte[] data) {  
	    StringBuffer sb = new StringBuffer(data.length * 2);  
	    int pos = 0, val = 0;  
	    for (int i = 0; i < data.length; i++) {  
	        val = (val << 8) | (data[i] & 0xFF);  
	        pos += 8;  
	        while (pos > 5) {  
	            char c = encodes[val >> (pos -= 6)];  
	            sb.append(  
	            c == 'i' ? "ia" :  
	            c == '+' ? "ib" :  
	            c == '/' ? "ic" : c);  
	            val &= ((1 << pos) - 1);  
	        }  
	    }  
	    if (pos > 0) {  
	        char c = encodes[val << (6 - pos)];  
	        sb.append(  
	        c == 'i' ? "ia" :  
	        c == '+' ? "ib" :  
	        c == '/' ? "ic" : c);  
	    }  
	    return sb.toString();  
	}
	
	
	
	
	// 将字符串解码成byte数组
	public static byte[] decodeBase62(String string) {  
	    if(string==null){
	        return null;
	    }
	    char[] data=string.toCharArray();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(string.toCharArray().length);  
	    int pos = 0, val = 0;  
	    for (int i = 0; i < data.length; i++) {  
	        char c = data[i];  
	        if (c == 'i') {  
	            c = data[++i];  
	            c =  
	            c == 'a' ? 'i' :  
	            c == 'b' ? '+' :  
	            c == 'c' ? '/' : data[--i];  
	        }  
	        val = (val << 6) | decodes[c];  
	        pos += 6;  
	        while (pos > 7) {  
	            baos.write(val >> (pos -= 8));  
	            val &= ((1 << pos) - 1);  
	        }  
	    }  
	    return baos.toByteArray();  
	}  
	
	
	
	
	// 判断当前网络是否为wifi
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	
	
	
	public static final class UploadImageUtils {

		private static void revitionImageSizeHD(String picfile, int size,
				int quality) throws IOException {
			
			
			if (size <= 0) {
				throw new IllegalArgumentException(
						"size must be greater than 0!");
			}
			
			if (!doesExisted(picfile)) {
				throw new FileNotFoundException(picfile == null ? "null"
						: picfile);
			}

			if (!BitmapHelper.verifyBitmap(picfile)) {
				throw new IOException("");
			}

			
			
			int photoSizesOrg = 2 * size;
			FileInputStream input = new FileInputStream(picfile);
			final BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, opts);
			try {
				input.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			

			int rate = 0;
			for (int i = 0;; i++) {
				if ((opts.outWidth >> i <= photoSizesOrg && (opts.outHeight >> i <= photoSizesOrg))) {
					rate = i;
					break;
				}
			}

			opts.inSampleSize = (int) Math.pow(2, rate);
			opts.inJustDecodeBounds = false;

			Bitmap temp = safeDecodeBimtapFile(picfile, opts);

			if (temp == null) {
				throw new IOException("Bitmap decode error!");
			}

			deleteDependon(picfile);
			makesureFileExist(picfile);

			int org = temp.getWidth() > temp.getHeight() ? temp.getWidth()
					: temp.getHeight();
			float rateOutPut = size / (float) org;

			
			if (rateOutPut < 1) {
				Bitmap outputBitmap;
				while (true) {
					try {
						outputBitmap = Bitmap.createBitmap(
								((int) (temp.getWidth() * rateOutPut)),
								((int) (temp.getHeight() * rateOutPut)),
								Bitmap.Config.ARGB_8888);
						break;
					} catch (OutOfMemoryError e) {
						System.gc();
						rateOutPut = (float) (rateOutPut * 0.8);
					}
				}
				if (outputBitmap == null) {
					temp.recycle();
				}
				Canvas canvas = new Canvas(outputBitmap);
				Matrix matrix = new Matrix();
				matrix.setScale(rateOutPut, rateOutPut);
				canvas.drawBitmap(temp, matrix, new Paint());
				temp.recycle();
				temp = outputBitmap;
			}
			
			
			
			final FileOutputStream output = new FileOutputStream(picfile);
			if (opts != null && opts.outMimeType != null
					&& opts.outMimeType.contains("png")) {
				temp.compress(Bitmap.CompressFormat.PNG, quality, output);
			} else {
				temp.compress(Bitmap.CompressFormat.JPEG, quality, output);
			}
			
			
			
			try {
				output.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			temp.recycle();
			
		}

		private static void revitionImageSize(String picfile, int size,
				int quality) throws IOException {
			
			
			if (size <= 0) {
				throw new IllegalArgumentException(
						"size must be greater than 0!");
			}

			
			if (!doesExisted(picfile)) {
				throw new FileNotFoundException(picfile == null ? "null"
						: picfile);
			}

			
			if (!BitmapHelper.verifyBitmap(picfile)) {
				throw new IOException("");
			}

			
			FileInputStream input = new FileInputStream(picfile);
			final BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, opts);
			try {
				input.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int rate = 0;
			for (int i = 0;; i++) {
				if ((opts.outWidth >> i <= size)
						&& (opts.outHeight >> i <= size)) {
					rate = i;
					break;
				}
			}

			opts.inSampleSize = (int) Math.pow(2, rate);
			opts.inJustDecodeBounds = false;

			Bitmap temp = safeDecodeBimtapFile(picfile, opts);

			if (temp == null) {
				throw new IOException("Bitmap decode error!");
			}

			deleteDependon(picfile);
			makesureFileExist(picfile);
			final FileOutputStream output = new FileOutputStream(picfile);
			if (opts != null && opts.outMimeType != null
					&& opts.outMimeType.contains("png")) {
				temp.compress(Bitmap.CompressFormat.PNG, quality, output);
			} else {
				temp.compress(Bitmap.CompressFormat.JPEG, quality, output);
			}
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			temp.recycle();
		}

		
		
		
		public static boolean revitionPostImageSize(String picfile) {
			try {
				if (Weibo.isWifi) {
					revitionImageSizeHD(picfile, 1600, 75);
				} else {
					revitionImageSize(picfile, 1024, 75);
				}

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		
		
		/**
		 * 如果加载时遇到OutOfMemoryError,则将图片加载尺寸缩小一半并重新加载
		 * 
		 * @param bmpFile
		 * @param opts
		 *            注意：opts.inSampleSize 可能会被改变
		 * @return
		 */
		private static Bitmap safeDecodeBimtapFile(String bmpFile,
				BitmapFactory.Options opts) {
			BitmapFactory.Options optsTmp = opts;
			if (optsTmp == null) {
				optsTmp = new BitmapFactory.Options();
				optsTmp.inSampleSize = 1;
			}

			Bitmap bmp = null;
			FileInputStream input = null;

			final int MAX_TRIAL = 5;
			for (int i = 0; i < MAX_TRIAL; ++i) {
				try {
					input = new FileInputStream(bmpFile);
					bmp = BitmapFactory.decodeStream(input, null, opts);
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					optsTmp.inSampleSize *= 2;
					try {
						input.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					break;
				}
			}

			return bmp;
		}
		
		
		
	}
	
	
	
}


// end of file
