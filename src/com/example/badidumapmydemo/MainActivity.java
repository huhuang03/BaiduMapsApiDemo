package com.example.badidumapmydemo;

import android.R.layout;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.w;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MainActivity extends Activity implements OnGetGeoCoderResultListener {
	MapView mapView;
	private GeofenceClient geofenceClient;
	
	//overlay
	LatLng llA = new LatLng(39.963175, 116.400244);
	LatLng llB = new LatLng(39.942821, 116.369199);
	LatLng llC = new LatLng(39.939723, 116.425541);
	LatLng llD = new LatLng(39.906965, 116.401394);
	LatLng southwest = new LatLng(39.92235, 116.380338);
	LatLng northeast = new LatLng(39.947246, 116.414977);
	BitmapDescriptor bda = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
	BitmapDescriptor bdb = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
	BitmapDescriptor bdc = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
	BitmapDescriptor bdd = BitmapDescriptorFactory.fromResource(R.drawable.icon_markd);
	BitmapDescriptor bdn = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);
	
	//地理位置编码
	View geoCodeOptionView;
	TextView geoCodeCity;
	TextView geoCodeStreet;
	TextView geoCodeLon;
	TextView geoCodeLat;
	GeoCoder geoCoder;
	
	/* 通过位置查坐标 */
	public void geoCode(View view) {
		geoCoder.geocode(new GeoCodeOption().city(geoCodeCity.getText().toString()).address(geoCodeStreet.getText().toString()));
	}
	
	public void revertGeoCode(View view) {
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(Double.valueOf(geoCodeLat.getText().toString()), Double.valueOf(geoCodeLon.getText().toString()))));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.map);
		regiseterListener();
		initial();
	}
	
	private OnGeofenceTriggerListener geofenceTriggerListener = new OnGeofenceTriggerListener() {
		
		@Override
		public void onGeofenceTrigger(String arg0) {
			Log.i("tong", "enter in-->" + arg0);
		}
		
		@Override
		public void onGeofenceExit(String arg0) {
			
		}
	};
	
	private void initial() {
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(this);
//		geofenceClient = new GeofenceClient(getApplicationContext());
//		geofenceClient.registerGeofenceTriggerListener(geofenceTriggerListener);
//		
//		BDGeofence fence = new BDGeofence.Builder().setGeofenceId("公司").setCircularRegion(116.345703, 39.981324, BDGeofence.RADIUS_TYPE_SMALL)
//				.setExpirationDruation(10L * (3600 * 1000)).setCoordType(BDGeofence.COORD_TYPE_BD09LL).build();
//		geofenceClient.setInterval(199009999);
//		geofenceClient.addBDGeofence(fence, new OnAddBDGeofencesResultListener() {
//
//			@Override
//			public void onAddBDGeofencesResult(int arg0, String arg1) {
//				Log.i("tonghu",
//						"MainActivity.initial().new OnAddBDGeofencesResultListener() {...}－onAddBDGeofencesResult :");
//				Toast.makeText(getBaseContext(), "add surcess?" + arg1 + " ," + arg0, 0).show();
//			}
//			
//		});
//		geofenceClient.start();
		
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
		
		//geocode
		geoCodeOptionView = View.inflate(this, R.layout.v_geocode, null);
//		ViewGroup contentView = (ViewGroup) findViewById(R.id.content);
//		contentView.addView(geoCodeOptionView);
		addContentView(geoCodeOptionView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		geoCodeCity = (TextView) geoCodeOptionView.findViewById(R.id.city);
		geoCodeStreet = (TextView) geoCodeOptionView.findViewById(R.id.geocodekey);
		geoCodeLat = (TextView) geoCodeOptionView.findViewById(R.id.lat);
		geoCodeLon = (TextView) geoCodeOptionView.findViewById(R.id.lon);
		geoCodeOptionView.setVisibility(View.GONE);
	}
	
	private BDLocationListener myListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			Log.i("tonghu",
					"MainActivity.bdLocationListener.new BDLocationListener() {...}－onReceiveLocation :" + arg0);
			if (arg0 == null || mapView == null)
				return;
			Log.i("tonghu", "lat: " + arg0.getLatitude() + " ,lon: " + arg0.getLongitude());
			
			if (isFirstLoc) {
				MyLocationData locData = new MyLocationData.Builder()
				.accuracy(arg0.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(arg0.getLatitude())
				.longitude(arg0.getLongitude()).build();
				mapView.getMap().setMyLocationData(locData);
				isFirstLoc = false;
				LatLng ll = new LatLng(arg0.getLatitude(),
						arg0.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mapView.getMap().animateMapStatus(u);
				mLocClient.stop();
			}
		}
	};
	
	private boolean isFirstLoc = true;
	
	
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
	private Marker markerA;
	private Marker markerB;
	private Marker markerC;
	private Marker markerD;
	
	private void regiseterListener() {
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		registerReceiver(mReceiver, iFilter);
	}
	
	private void resetOverlay() {
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mapView.getMap().setMapStatus(msu);
		
		OverlayOptions ooa = new MarkerOptions().icon(bda).position(llA).draggable(true);
		markerA = (Marker) mapView.getMap().addOverlay(ooa);
		OverlayOptions oob = new MarkerOptions().icon(bdb).position(llB);
		markerB = (Marker) mapView.getMap().addOverlay(oob);
		OverlayOptions ooc = new MarkerOptions().icon(bdc).position(llC).anchor(0.5f, 0.5f).rotate(45).perspective(false);
		markerC = (Marker) mapView.getMap().addOverlay(ooc);
		OverlayOptions ood = new MarkerOptions().icon(bdd).position(llD).perspective(false);
		markerD = (Marker) mapView.getMap().addOverlay(ood);
		
		LatLngBounds llb = new LatLngBounds.Builder().include(northeast).include(southwest).build();
		GroundOverlayOptions goo = new GroundOverlayOptions().image(bdGround).positionFromBounds(llb);
		mapView.getMap().addOverlay(goo);
		
		MapStatusUpdate msut = MapStatusUpdateFactory.newLatLng(llb.getCenter());
		mapView.getMap().setMapStatus(msut);
		
		mapView.getMap().setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMarkerDragEnd(Marker arg0) {
				Toast.makeText(getBaseContext(), "新位置：" + arg0.getPosition().latitude + " ," + arg0.getPosition().longitude
						, 0).show();
			}
			
			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mapView.getMap().setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker arg0) {
				markerButton = new Button(getApplicationContext());
				LatLng ll = arg0.getPosition();
				Point screenLocation = mapView.getMap().getProjection().toScreenLocation(ll);
				screenLocation.y -= 47;
				ll = mapView.getMap().getProjection().fromScreenLocation(screenLocation);
				OnInfoWindowClickListener listener = null;
				if (arg0 == markerA || arg0 == markerD) {
					markerButton.setText("更改位置");
					listener = new OnInfoWindowClickListener() {
						public void onInfoWindowClick() {
							LatLng llNew = new LatLng(arg0.getPosition().latitude + 0.005,
									arg0.getPosition().longitude + 0.005);
							arg0.setPosition(llNew);
							mapView.getMap().hideInfoWindow();
						}
					};
				} else if (arg0 == markerB) {
					markerButton.setText("修改图标");
					listener = new OnInfoWindowClickListener() {
						
						@Override
						public void onInfoWindowClick() {
							mapView.getMap().hideInfoWindow();
							arg0.setIcon(bdn);
						}
					};
				} else if (arg0 == markerC) {
					markerButton.setText("删除");
					listener = new OnInfoWindowClickListener() {
						
						@Override
						public void onInfoWindowClick() {
							mapView.getMap().hideInfoWindow();
							arg0.remove();
						}
					};
				}
				mInfoWindow = new InfoWindow(markerButton, ll, listener);
				mapView.getMap().showInfoWindow(mInfoWindow);
				return false;
			}
		});
	}
	
	InfoWindow mInfoWindow;
	
	Button markerButton;
	
	private void clearOverlay() {
		mapView.getMap().clear();
	}
	
	@Override
	protected void onDestroy() {
		mapView.getMap().setMyLocationEnabled(false);
		mapView.onDestroy();
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mLocClient.stop();
		if (geofenceClient != null) {
			geofenceClient.stop();
		}
		bda.recycle();
		bdb.recycle();
		bdc.recycle();
		bdd.recycle();
		bdGround.recycle();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.overlay_reset:
			resetOverlay();
			break;
		case R.id.overlay_clear:
			clearOverlay();
			break;
		case R.id.geocode:
			geoCodeOptionView.setVisibility(geoCodeOptionView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getBaseContext(), "找不到坐标", 0).show();
		} else {
			mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(arg0.getLocation()));
			mapView.getMap().clear();
			mapView.getMap().addOverlay(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)).
					position(arg0.getLocation()));
			Toast.makeText(getBaseContext(), "new location, lon: " + 
					arg0.getLocation().longitude + " , lat" + arg0.getLocation().latitude, 0).show();
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getBaseContext(), "找不到坐标", 0).show();
		} else {
			mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(arg0.getLocation()));
			mapView.getMap().clear();
			mapView.getMap().addOverlay(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka))
					.position(arg0.getLocation()));
			Toast.makeText(getBaseContext(), "new location: " + arg0.getAddress(), 0).show();
		}
	}
	
	
}
