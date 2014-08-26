package com.example.badidumapmydemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity {
	MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.map);
		regiseterListener();
		initial();
	}
	
	private void initial() {
		mapView.getMap().setMyLocationEnabled(true);
		mapView.getMap().setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.COMPASS, true, BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	private BDLocationListener myListener = new BDLocationListener() {
		
		@Override
		public void onReceivePoi(BDLocation arg0) {
			Log.i("tonghu",
					"MainActivity.bdLocationListener.new BDLocationListener() {...}－onReceivePoi :");
		}
		
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			Log.i("tonghu",
					"MainActivity.bdLocationListener.new BDLocationListener() {...}－onReceiveLocation :" + arg0);
			if (arg0 == null || mapView == null)
				return;
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(arg0.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(arg0.getLatitude())
					.longitude(arg0.getLongitude()).build();
			mapView.getMap().setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(arg0.getLatitude(),
						arg0.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mapView.getMap().animateMapStatus(u);
			}
		}
	};
	
	private boolean isFirstLoc = false;
	
	
	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getBaseContext(), intent.getAction(), 0).show();
		}
	};
	private LocationClient mLocClient;
	
	private void regiseterListener() {
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		registerReceiver(mReceiver, iFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.getMap().setMyLocationEnabled(false);
		mapView.onDestroy();
		unregisterReceiver(mReceiver);
		mLocClient.stop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
