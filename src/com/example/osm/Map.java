package com.example.osm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.SAXException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Map extends Activity {
		
	private Button NextPage;
	private ToggleButton toggle;
	private LocationManager locationMgr;
	
	private String provider;
	String PSIBuf;
	String HUMDBuf;
	String H24RBuf;
	Double lat;
	Double lng;
	Double PSI;
	Double HUMD;
	Double H24R;
	String MajorBuf;
	int count = 0;
	String warning = "";
	String where = "";
	private TextView Info;
	String title;
	Bundle intentinfo;
	//建立List，屬性為Poi物件
  	private ArrayList<Poi> Pois = new ArrayList<Poi>();
  	private ArrayList<Poiw> Poiws = new ArrayList<Poiw>();
  	protected static final int REFRESH_DATA = 0x00000001;
  	Weather[] Arr_Weather;
    Weather nearest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
				
		NextPage = (Button) findViewById(R.id.next);
		toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		Info = (TextView) findViewById(R.id.WebInfo);
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		intentinfo = new Bundle();
		NextPage.setOnClickListener(new button_set());
		toggle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 當按鈕第一次被點擊時候響應的事件        
                if (toggle.isChecked()) {
                	if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {
                        	Toast.makeText(Map.this, "已開啟環境資訊 ", Toast.LENGTH_SHORT).show();
                        	initLocationProvider();
                        	whereAmI();
                        }
                        else{
                        	Toast.makeText(Map.this, "請連接網路 ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Map.this, "請重新開啟環境服務 ", Toast.LENGTH_SHORT).show();
                        }
                	}
                	else {
                		Toast.makeText(Map.this, "請開啟定位服務", Toast.LENGTH_SHORT).show();
        				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        				Toast.makeText(Map.this, "請重新開啟環境服務 ", Toast.LENGTH_SHORT).show();
                	}
                }
                // 當按鈕再次被點擊時候響應的事件  
                else {
                	//停止服務
                	locationMgr.removeUpdates(locationListener);
                	title = "";
                	handler.sendEmptyMessage(0);
                	Toast.makeText(Map.this, "已關閉環境資訊", Toast.LENGTH_SHORT).show();
                }
			}
		});
		
		Pois.add(new Poi("二林" , 120.409653 , 23.925175 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-028.xml"));
        Pois.add(new Poi("三重" , 121.493806 , 25.072611 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("三義" , 120.758833 , 24.382942 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-020.xml"));
        Pois.add(new Poi("土城" , 121.451861 , 24.982528 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("士林" , 121.515389 , 25.105417 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("大同" , 121.513311 , 25.0632 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("大里" , 120.677689 , 24.099611 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-021.xml"));
        Pois.add(new Poi("大園" , 121.201811 , 25.060344 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-022.xml"));
        Pois.add(new Poi("大寮" , 120.425081 , 22.565747 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("小港" , 120.337736 , 22.565833 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("中山" , 121.526528 , 25.062361 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("中壢" , 121.221667 , 24.953278 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-022.xml"));
        Pois.add(new Poi("仁武" , 120.332631 , 22.689056 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("斗六" , 120.544994 , 23.711853 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-029.xml"));
        Pois.add(new Poi("冬山" , 121.793872 , 24.633436 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-013.xml"));
        Pois.add(new Poi("古亭" , 121.529556 , 25.020608 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("左營" , 120.292917 , 22.674861 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("平鎮" , 121.203986 , 24.952786 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-022.xml"));
        Pois.add(new Poi("永和" , 121.516306 , 25.017 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("安南" , 120.2175 , 23.048197 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-016.xml"));
        Pois.add(new Poi("朴子" , 120.24735 , 23.465308 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-018.xml"));
        Pois.add(new Poi("汐止" , 121.6423 , 25.067131 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("竹山" , 120.677306 , 23.756389 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-026.xml"));
        Pois.add(new Poi("竹東" , 121.088903 , 24.740644 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-023.xml"));
        Pois.add(new Poi("西屯" , 120.616917 , 24.162197 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-021.xml"));
        Pois.add(new Poi("沙鹿" , 120.568794 , 24.225628 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-021.xml"));
        Pois.add(new Poi("宜蘭" , 121.746394 , 24.747917 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-013.xml"));
        Pois.add(new Poi("忠明" , 120.641092 , 24.151958 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-021.xml"));
        Pois.add(new Poi("松山" , 121.578556 , 25.050694 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("板橋" , 121.458667 , 25.012972 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("林口" , 121.376869 , 25.077197 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("林園" , 120.41175 , 22.4795 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("花蓮" , 121.599769 , 23.971956 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-012.xml"));
        Pois.add(new Poi("金門" , 118.312256 , 24.432133 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-014.xml"));
        Pois.add(new Poi("前金" , 120.288086 , 22.632567 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("前鎮" , 120.307564 , 22.605386 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("南投" , 120.685306 , 23.913 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-026.xml"));
        Pois.add(new Poi("屏東" , 120.488033 , 22.673081 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-025.xml"));
        Pois.add(new Poi("恆春" , 120.788928 , 21.958069 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-025.xml"));
        Pois.add(new Poi("美濃" , 120.530542 , 22.883583 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("苗栗" , 120.8202 , 24.565269 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-020.xml"));
        Pois.add(new Poi("埔里" , 120.967903 , 23.968842 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-026.xml"));
        Pois.add(new Poi("桃園" , 121.319964 , 24.994789 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-022.xml"));
        Pois.add(new Poi("馬公" , 119.566158 , 23.569031 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-015.xml"));
        Pois.add(new Poi("馬祖" , 119.949875 , 26.160469 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-030.xml"));
        Pois.add(new Poi("基隆" , 121.760056 , 25.129167 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-011.xml"));
        Pois.add(new Poi("崙背" , 120.348742 , 23.757547 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-029.xml"));
        Pois.add(new Poi("淡水" , 121.449239 , 25.1645 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("麥寮" , 120.251825 , 23.753506 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-029.xml"));
        Pois.add(new Poi("善化" , 120.297142 , 23.115097 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-016.xml"));
        Pois.add(new Poi("復興" , 120.312017 , 22.608711 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("湖口" , 121.038653 , 24.900142 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-023.xml"));
        Pois.add(new Poi("菜寮" , 121.481028 , 25.06895 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("陽明" , 121.529583 , 25.182722 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("新竹" , 120.972075 , 24.805619 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-023.xml"));
        Pois.add(new Poi("新店" , 121.537778 , 24.977222 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("新莊" , 121.4325 , 25.037972 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("新港" , 120.345531 , 23.554839 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-018.xml"));
        Pois.add(new Poi("新營" , 120.31725 , 23.305633 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-016.xml"));
        Pois.add(new Poi("楠梓" , 120.328289 , 22.733667 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("萬里" , 121.689881 , 25.179667 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
        Pois.add(new Poi("萬華" , 121.507972 , 25.046503 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-009.xml"));
        Pois.add(new Poi("嘉義" , 120.438367 , 23.464789 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-018.xml"));
        Pois.add(new Poi("彰化" , 120.541519 , 24.066 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-028.xml"));
        Pois.add(new Poi("臺西" , 120.202842 , 23.717533 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-029.xml"));
        Pois.add(new Poi("臺東" , 121.15045 , 22.755358 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-027.xml"));
        Pois.add(new Poi("臺南" , 120.202617 , 22.984581 , "http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-016.xml"));
        Pois.add(new Poi("鳳山" , 120.358083 , 22.627392 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("潮州" , 120.561175 , 22.523108 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-025.xml"));
        Pois.add(new Poi("線西" , 120.469061 , 24.131672 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-028.xml"));
        Pois.add(new Poi("橋頭" , 120.305689 , 22.757506 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-017.xml"));
        Pois.add(new Poi("頭份" , 120.898572 , 24.696969 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-020.xml"));
        Pois.add(new Poi("龍潭" , 121.21635 , 24.863869 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-022.xml"));
        Pois.add(new Poi("豐原" , 120.741711 , 24.256586 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-021.xml"));
        Pois.add(new Poi("關山" , 121.161933 , 23.045083 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-027.xml"));
        Pois.add(new Poi("觀音" , 121.082761 , 25.035503 ,"http://opendata.cwb.gov.tw/opendata/MFC/F-C0032-010.xml"));
	
        Poiws.add(new Poiw("基隆" , 25.1348 , 121.7321));
   	    Poiws.add(new Poiw("淡水" , 25.1656 , 121.44));
   	    Poiws.add(new Poiw("板橋" , 24.9993 , 121.4338));
   	    Poiws.add(new Poiw("竹子湖" , 25.165 , 121.5363));
        Poiws.add(new Poiw("新竹" , 24.83 , 121.0061));
        Poiws.add(new Poiw("臺中" , 24.1475 , 120.6759));
        Poiws.add(new Poiw("梧棲" , 24.2587 , 120.5151));
        Poiws.add(new Poiw("澎湖" , 23.5672 , 119.5552));
        Poiws.add(new Poiw("日月潭" , 23.883 , 120.8999));
        Poiws.add(new Poiw("阿里山" , 23.5104 , 120.8051));
        Poiws.add(new Poiw("玉山" , 23.4893 , 120.9517));
        Poiws.add(new Poiw("嘉義" , 23.4977 , 120.4245));
        Poiws.add(new Poiw("高雄" , 22.5679 , 120.308));
        Poiws.add(new Poiw("恆春" , 22.0054 , 120.7381));
        Poiws.add(new Poiw("宜蘭" , 24.7656 , 121.7479));
        Poiws.add(new Poiw("蘇澳" , 24.6017 , 121.8644));
        Poiws.add(new Poiw("花蓮" , 23.977 , 121.605));
        Poiws.add(new Poiw("成功" , 23.0992 , 121.3654));
        Poiws.add(new Poiw("臺東" , 22.754 , 121.1465));
        Poiws.add(new Poiw("大武" , 22.3576 , 120.8957));
        Poiws.add(new Poiw("蘭嶼" , 22.0387 , 121.5506));
        Poiws.add(new Poiw("彭佳嶼" , 25.6294 , 122.0713));
        Poiws.add(new Poiw("東吉島" , 23.259 , 119.6596));
        Poiws.add(new Poiw("新店" , 24.9608 , 121.5165));
        Poiws.add(new Poiw("臺北" , 25.0396 , 121.5067));
        Poiws.add(new Poiw("臺南" , 22.9952 , 120.197));
        Poiws.add(new Poiw("東沙" , 20.42 , 116.43));
        Poiws.add(new Poiw("金門" , 24.4074 , 118.2893));
        Poiws.add(new Poiw("馬祖" , 26.1694 , 119.9232));
        Poiws.add(new Poiw("新屋" , 25.0067 , 121.0475));
	}
	
	private boolean initLocationProvider() {
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		provider = locationMgr.getBestProvider(criteria, true);
		
		if (provider != null) {
			return true;
		}
		return false;
	}
	
	private void whereAmI(){
		//last location
		Location location = locationMgr.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		
		//Location Listener
		 int minTime = 5000;//ms
		 int minDist = 5;//meter
		 locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);
	}
	
	LocationListener locationListener = new LocationListener(){
		 @Override
		 public void onLocationChanged(Location location) {
		  updateWithNewLocation(location);
		 }

		 @Override
		 public void onProviderDisabled(String provider) {
		  updateWithNewLocation(null);
		 }

		 @Override
		 public void onProviderEnabled(String provider) {
		 }

		 @Override
		 public void onStatusChanged(String provider, int status, Bundle extras) {
		 }
		};
		
	private void updateWithNewLocation(Location location) {
			 if (location != null) {
			  //經度
			  lng = location.getLongitude();
			  //緯度
			  lat = location.getLatitude();
			  intentinfo.putDouble("Longitude", lng);
			  intentinfo.putDouble("Latitude", lat);
			  for(Poi mPoi : Pois) 	
				{
					//for迴圈將距離帶入，判斷距離為Distance function，需帶入使用者取得定位後的緯度、經度、景點店家緯度、經度。 
					mPoi.setDistance(Distance(location.getLongitude(),location.getLatitude(),mPoi.getLongitude(),mPoi.getLatitude()));
		        }
			  
			  //依照距離遠近進行List重新排列
		      DistanceSort(Pois);
		      
		      for(Poiw mPoi : Poiws) 	
				{
					//for迴圈將距離帶入，判斷距離為Distance function，需帶入使用者取得定位後的緯度、經度、景點店家緯度、經度。 
					mPoi.setDistance(Distance(location.getLongitude(),location.getLatitude(),mPoi.getLongitude(),mPoi.getLatitude()));
		        }
		      
		      DistanceSortw(Poiws);
		      
		      new Thread(runnable).start();
		      
			 }
			 else{
			  where = "No location found.";
			 }
}
	
	public double Distance(double longitude1, double latitude1, double longitude2,double latitude2) 
	{
		double radLatitude1 = latitude1 * Math.PI / 180;
		double radLatitude2 = latitude2 * Math.PI / 180;
		double l = radLatitude1 - radLatitude2;
		double p = longitude1 * Math.PI / 180 - longitude2 * Math.PI / 180;
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
		                 + Math.cos(radLatitude1) * Math.cos(radLatitude2)
		                 * Math.pow(Math.sin(p / 2), 2)));
		distance = distance * 6378137.0;
		distance = Math.round(distance * 10000) / 10000;
		    
		return distance ;
	}
	
	//List排序，依照距離由近開始排列，第一筆為最近，最後一筆為最遠
	private void DistanceSort(ArrayList<Poi> poi)
	{
		Collections.sort(poi, new Comparator<Poi>() 
		{
			@Override
			public int compare(Poi poi1, Poi poi2) 
			{
				return poi1.getDistance() < poi2.getDistance() ? -1 : 1 ;
			}
		});
	}
	
	private void DistanceSortw(ArrayList<Poiw> poiw)
	{
		Collections.sort(poiw, new Comparator<Poiw>() 
		{
			@Override
			public int compare(Poiw poiw1, Poiw poiw2) 
			{
				return poiw1.getDistance() < poiw2.getDistance() ? -1 : 1 ;
			}
		});
	}

	private class DownloadWebpageTask extends AsyncTask<Void,Integer,String[]> 
    {
    	@Override
        //要在背景中做的事
        protected String[]  doInBackground(Void... params) {
            try {
                return getWebData();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }        
        }
    	
        //取得網路資料
        public String[] getWebData() throws IOException{
        	
        	URL url = new URL("http://opendata.epa.gov.tw/ws/Data/AQX/?$filter=SiteName%20eq%20%27"+java.net.URLEncoder.encode(Pois.get(0).getName(),"UTF-8")+"%27&$select=PSI,MajorPollutant&$orderby=PSI&$skip=0&$top=1&format=json");
			
     	   	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);        
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String jsonString= reader.readLine();
            reader.close();
       
            try {
                return (getJson(jsonString));
            } catch (JSONException e) {
         	   e.printStackTrace();
                return null;
            }
        }
        
        //將JSON字串解析
        public String[]  getJson(String jsonString) throws JSONException {
     	   	//如果是巢狀JSON字串,須分兩次來取資料
            String[] data = new String[2];
            data[0] = new JSONArray(jsonString).getJSONObject(0).getString("PSI");
         	data[1] = new JSONArray(jsonString).getJSONObject(0).getString("MajorPollutant");
            
            return data;
        }
        
        //背景工作處理完"後"需作的事
        protected void onPostExecute(String[] result) {
     	   super.onPostExecute(result);
     	   if(result != null) {
     		   
         	   PSIBuf = result[0];
         	   PSI = Double.valueOf(PSIBuf);
         	   if(result[1] == " "){
         		  MajorBuf = "無";
         	   }
         	   else{
         		  MajorBuf = result[1];
         	   }
         	   
         	   if( PSI > 50 && PSI <= 200 ) {
 				  if( MajorBuf.equals("懸浮微粒") ) {
 					  warning = "目前空氣品質不好，空氣中懸浮微粒(PM10)濃度超過150 mg/m^3，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 					  notification();
 				  }
 				  else if( MajorBuf.equals("二氧化硫") ) {
 					  warning = "目前空氣品質不好，二氧化硫濃度超過140 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 					  notification();
 				  }
 				  else if( MajorBuf.equals("一氧化碳") ) {
 					  warning = "目前空氣品質不好，一氧化碳濃度超過9 ppm，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 					  notification();
 				  }
 				  else if( MajorBuf.equals("臭氧") ) {
 					  warning = "目前空氣品質不好，臭氧濃度超過 120 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 					  notification();
 				  }
 			 }
 			  else if( PSI > 200 && PSI <= 300 ) {
 		    	  if( MajorBuf.equals("懸浮微粒") ) {
 		    		  warning = "目前空氣品質不好，空氣中懸浮微粒(PM10)濃度超過350 mg/m^3，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		    		  notification(); 	
 		        	} 
 		    	  else if( MajorBuf.equals("二氧化硫") ) {
 		    		  warning = "目前空氣品質不好，二氧化硫濃度超過300 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		    		  notification(); 	
 		        	} 
 		    	  else if( MajorBuf.equals("一氧化碳") ) {
 		    		  warning = "目前空氣品質不好，一氧化碳濃度超過15 ppm，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		    		  notification(); 
 		        	} 
 		    	  else if( MajorBuf.equals("臭氧") ) {
 		    		  warning = "目前空氣品質不好，臭氧濃度超過200 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		    		  notification(); 	
 		        	} 
 		    	  else if( MajorBuf.equals("二氧化氮") ) {
 		    		  warning = "目前空氣品質不好，二氧化氮濃度超過600 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		    		  notification(); 	
 		        	}
 		    	} 
 			  else if( PSI > 300 ) {
 		        	if( MajorBuf.equals("懸浮微粒") ) {
 		        		warning = "目前空氣品質不好，空氣中懸浮微粒(PM10)濃度超過420 mg/m^3，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		        		notification();
 		        	} 
 		        	else if( MajorBuf.equals("二氧化硫") ) {
 		        		warning = "目前空氣品質不好，二氧化硫濃度超過600 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		        		notification();
 		        	} 
 		        	else if( MajorBuf.equals("一氧化碳") ) {
 		        		warning = "目前空氣品質不好，一氧化碳濃度超過30 ppm，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		        		notification();
 		        	} 
 		        	else if( MajorBuf.equals("臭氧") ) {
 		        		warning = "目前空氣品質不好，臭氧濃度超過400 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		        		notification();
 		        	} 
 		        	else if( MajorBuf.equals("二氧化氮") ) {
 		        		warning = "目前空氣品質不好，二氧化氮濃度超過1200 ppb，可能加重呼吸道症狀，請即刻作尖峰吐氣流速三次，並上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
 		        		notification();
 		        	}
 		    	}
     	   }
     	  where = "經度: " + lng 
				  + "\n緯度: " + lat 
				  + "\n空汙測站: " + Pois.get(0).getName() 
				  + "\nPSI: " + PSI 
				  + "\n主要空氣汙染物: " + MajorBuf
				  + "\n氣象測站: " + Poiws.get(0).getName() 
				  + "\n今日雨量: " + H24R 
				  + "\n今日濕度: " + HUMD;
		  //顯示資訊
		  Toast.makeText(Map.this, where, Toast.LENGTH_SHORT).show();
		  intentinfo.putString("where", where);
        }
    }
    
    private void notification() {
    	NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	//設定當按下這個通知之後要執行的activity
    	Intent notifyIntent = new Intent(this,Map.class);
    	notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent appIntent=PendingIntent.getActivity(Map.this,0,notifyIntent,0);
    	
    	Notification notification3 = new Notification.Builder(getBaseContext())
            	.setSmallIcon(R.drawable.ic_launcher)
            	.setContentTitle("Test")
            	.setContentText(warning)
            	.setTicker("notification on status bar.")
            	.setStyle(new Notification.BigTextStyle().bigText(warning))
            	.setPriority(2)
            	.setContentIntent(appIntent)
            	.setAutoCancel(false)
            	.build();
            	notificationManager.notify(count,notification3);
    }
	
    private class button_set implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.next){
				if(intentinfo != null){
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Map.this,Page_map.class);
					intent.putExtras(intentinfo);
					startActivity(intent);
				}
				else{
					Toast.makeText(Map.this, "請先啟用資訊服務", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
    
    Runnable runnable = new Runnable(){
		@Override
		public void run(){
			try{
				String url = Pois.get(0).getUrl();;
				Document doc = Jsoup.connect(url).get();
				Elements weather = doc.select("parameterValue");
				title = weather.get(2).text() ;
			}catch(IOException e){
				//TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.sendEmptyMessage(0);
			chkNetwork();
		}
	};
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			Info.setText(title);
		}
	};
	
	public void chkNetwork(){
        new Thread()
            {
        	@Override
            public void run()
            {
				Arr_Weather = getWeather();
                if(Arr_Weather != null) {
             	   for (int i = 0; i < Arr_Weather.length; i++)
                    {
                    	if(Arr_Weather[i].getLocationName().equals(Poiws.get(0).getName()))
                    		nearest = Arr_Weather[i];
                    }
                    if (nearest != null){
                    	H24RBuf = nearest.getH_24R();
                    	H24R = Double.valueOf(H24RBuf);
                 	   	HUMDBuf = nearest.getHUMD();
                 	   	HUMD = Double.valueOf(HUMDBuf);
                    }
                }  
                if( HUMD > 0.8 ) {
           		  warning = "目前氣象資料顯示，濕度超過80%，可能影響呼吸道症狀，請即刻作尖峰吐氣流速三次，上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
           		  notification();
            	}
           	  	if( H24R > 100 ) {
           		  warning = "目前氣象資料顯示，雨量大於 100 ml，可能影響呼吸道症狀，請即刻作尖峰吐氣流速三次，上傳資料，如果發作，儘快使用緩解藥物或迅速就醫。";
           		  notification();
            	}
            }
		}.start();
		new DownloadWebpageTask().execute();
	}
	
	
	public Weather[] getWeather()
    {
          try
          {
        	   String trgUrl="http://opendata.cwb.gov.tw/opendata/DIV2/O-A0003-001.xml";
               // 建立一個Parser物件，並指定擷取規則 (ParsingHandler)
               SimpleXMLParser dataXMLParser = new SimpleXMLParser(
               new WeatherXMLParsingHandler());
               // 呼叫getData方法取得物件陣列
               Object[] data = (Object[]) dataXMLParser.getData(trgUrl);
               if (data != null)
               {
                    // 如果資料形態正確，就回傳
                    if (data[0] instanceof Weather[])
                    {
                          return (Weather[]) data[0];
                    }
               }
          } catch (SAXException e)
          {
               e.printStackTrace();
          } catch (IOException e)
          {
               e.printStackTrace();
          } catch (ParserConfigurationException e)
          {
               e.printStackTrace();
          }
          // 若有錯誤則回傳null
          return null;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
