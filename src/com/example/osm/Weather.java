package com.example.osm;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Weather implements Serializable
{
     private String locationName = null;
     private String lat = null;
     private String lon = null;
     private String HUMD = null;
     private String H_24R = null;
 
     public void setLocationName(String locationName)
     {
           this.locationName = locationName;
     }
 
     public String getLocationName()
     {
           return locationName;
     }
     
     public void setLat(String lat)
     {
           this.lat = lat;
     }
 
     public String getLat()
     {
           return lat;
     }
     
     public void setLon(String lon)
     {
           this.lon = lon;
     }
 
     public String getLon()
     {
           return lon;
     }
 
     public void setHUMD(String HUMD)
     {
           this.HUMD = HUMD;
     }
 
     public String getHUMD()
     {
           return HUMD;
     }
     
     public void setH_24R(String H_24R)
     {
           this.H_24R = H_24R;
     }
 
     public String getH_24R()
     {
           return H_24R;
     }
 
     @Override
     public String toString()
     {
           return "Weather [locationName=" + locationName + ", lat=" + lat + ", lon=" + lon + ", HUMD=" + HUMD + ", H_24R=" + H_24R + "]";
     }
}