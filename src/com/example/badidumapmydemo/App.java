package com.example.badidumapmydemo;

import java.io.File;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.os.Environment;

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}
}
