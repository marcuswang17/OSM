package com.example.osm;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Page_map extends Activity {
	
	private MapView mapview;
	private MapController mcontrol;
	private ScaleBarOverlay scale;
	public LocationManager locationManager ;
	private ItemizedOverlay<OverlayItem> iconOverlay;
	Location location;
	private Button center;
	private Button Info;
	private Button home;
	private final Point mpoint = new Point();
	double lat ;
	double lng ;
	DefaultResourceProxyImpl defaultResourceProxyImpl;
	String info;
	GeoPoint myLocation;
	protected final Paint mpaint = new Paint();
    Bundle infomation;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_start);
		//initial osm map
		initialmap();
		
		//receive bundle
		infomation = this.getIntent().getExtras();
		lng = infomation.getDouble("Longitude");
		lat = infomation.getDouble("Latitude");
		info = infomation.getString("where");
		
		//get location
		location = getMyLocation();
		if(location == null){
			//Testing location on the emulator
			myLocation = new GeoPoint(lat,lng);
			MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(this, mapview);
			mcontrol.setCenter(myLocation);
			mapview.getOverlays().add(myLocationOverlay);
		}
		else{
			myLocation = new GeoPoint(location);
			MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(this, mapview);
			mcontrol.setCenter(myLocation);
			mapview.getOverlays().add(myLocationOverlay);
		}
		
		AddMarker();
		draw(null, mapview, true);
		//Add Scale Bar
		scale = new ScaleBarOverlay(this);
		mapview.getOverlays().add(scale);
		
		//Add Button
		center = (Button) findViewById(R.id.center);
		home = (Button) findViewById(R.id.home);
		Info = (Button) findViewById(R.id.Info);
		center.setOnClickListener(button_set);
		home.setOnClickListener(button_set);
		Info.setOnClickListener(button_set);
		/*
		mMyLocationOverlay = new MyLocationNewOverlay(this.getBaseContext(),mapview);
		mMyLocationOverlay.enableMyLocation();
		mMyLocationOverlay.enableFollowLocation();
		mMyLocationOverlay.setDrawAccuracyEnabled(true);
		mMyLocationOverlay.runOnFirstFix(new Runnable(){
			public void run(){
				mcontrol.animateTo(mMyLocationOverlay.getMyLocation());
				Toast.makeText(getApplicationContext(), "work", Toast.LENGTH_LONG).show();
			}
		})*/
	}
	
	public void draw(final Canvas c, MapView map, final boolean shadow){
		if(shadow) return;
		final Projection pj = map.getProjection();
		pj.toMapPixels(myLocation, mpoint);
		c.drawText("Tainan Weather",mpoint.x,mpoint.y,this.mpaint);
	}	
	
	public void initialmap(){
		mapview = (MapView) this.findViewById(R.id.mapview);
		mapview.setTileSource(TileSourceFactory.MAPQUESTOSM);
		mapview.setBuiltInZoomControls(true);
		mapview.setMultiTouchControls(true);
		mapview.setClickable(true);
		mcontrol = (MapController) this.mapview.getController();
		mcontrol.setZoom(15);
		}
	
	private OnClickListener button_set = new OnClickListener(){
		public void onClick(View v){
			//TODO Auto-generated method stub
			switch(v.getId()){
			case
			R.id.center:
				mcontrol.setCenter(myLocation);
				break;
			case
			R.id.Info:
				//new Thread(runnable).start();
				Toast.makeText(getApplicationContext(),info, Toast.LENGTH_SHORT).show();
				break;
			case
			R.id.home:
				Intent intent = new Intent();
				intent.setClass(Page_map.this,Map.class);
				startActivity(intent);
				break;
			}
		}
	};
	
	public void AddMarker(){
			//Create Overlay for marker
				OverlayItem myOverlayItem = new OverlayItem("Here","Current Position", myLocation);
				Drawable marker = this.getResources().getDrawable(R.drawable.markpin);
				myOverlayItem.setMarker(marker);
				
				ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
				overlayArray.add(myOverlayItem);
				
				defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);
				this.iconOverlay = new ItemizedIconOverlay<OverlayItem>(overlayArray, marker,null, defaultResourceProxyImpl);
				mapview.getOverlays().add(iconOverlay);
	}
	
	Location getMyLocation(){
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); 
		return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	}
}
