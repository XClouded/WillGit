package com.willin.log;

import android.util.Log;

public class ConsoleLog implements ILog {

	@Override
	public void v(String tag, String s) {
		Log.v(tag, s);
	}

	@Override
	public void d(String tag, String s) {
		Log.d(tag, s);
	}

	@Override
	public void i(String tag, String s) {
		Log.i(tag, s);
	}

	@Override
	public void e(String tag, String s) {
		Log.e(tag, s);
	}

	@Override
	public void w(String tag, String s) {
		Log.w(tag, s);
	}

}
