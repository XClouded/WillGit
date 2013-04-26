package com.tencent.test;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.plugintest.R;
import com.willin.plugin.client.PluginActivity;


public class MainActivity extends PluginActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
	}
	
}


// end of file