package com.example.osm;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.example.osm.Weather;
import com.example.osm.SimpleXMLParsingHandler;
 
/**
 * 記錄著Rss新聞資料的解析XML處理方式的class （繼承了SimpleXMLParsingHandler）
 */
public class WeatherXMLParsingHandler extends SimpleXMLParsingHandler
{
     /** 用來儲存Rss新聞的物件 */
     private Weather weatherItem;
     /** 用來儲存Rss新聞的物件Stack(堆疊) */
     private Stack<Weather> mWeatherItem_list;
     
     Boolean check1 = false;
     Boolean check2 = false;
 
     /** 建構子 */
     public WeatherXMLParsingHandler()
     {
     }
 
     /**
      * @return回傳RssNews[]。程式會將讀到的物件{ RssNews[] }包成Object[]然後回傳
      */
     @Override
     public Object[] getParsedData()
     {
           Weather[] Arr_RssNews = (Weather[]) mWeatherItem_list
                     .toArray(new Weather[mWeatherItem_list.size()]);
           // 解析結果回報
           return new Object[] { Arr_RssNews };
     }
 
     /**
      * XML文件開始解析時呼叫此method
      */
     @Override
     public void startDocument() throws SAXException
     {
           super.startDocument();
           // 在文件開始的時候，宣告出該RssNews形態的Stack(堆疊)
           mWeatherItem_list = new Stack<Weather>();
     }
 
     /**
      * XML文件結束時呼叫此method
      */
     @Override
     public void endDocument() throws SAXException
     {
           super.endDocument();
     }
 
     /**
      * 解析到Element的開頭時呼叫此method
      */
     @Override
     public void startElement(String namespaceURI, String localName,
                String qName, Attributes atts) throws SAXException
     {
           super.startElement(namespaceURI, localName, qName, atts);
           // 若搞不清楚現在在哪裡的話可以用printNodePos();
           // printNodePos();
           if (getInNode().size() >= 2
                     && getInNode().get(getInNode().size() - 2).equals("cwbopendata")
                     && getInNode().get(getInNode().size() - 1).equals("location"))
           {
                // 在cwbopendata -> location這個位置中
                // 新增一個RssNews
        	    weatherItem = new Weather();
           }
     }
 
     /**
      * 解析到Element的結尾時呼叫此method
      */
     @Override
     public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException
     {
 
           if (getInNode().size() >= 2
                     && getInNode().get(getInNode().size() - 2).equals("cwbopendata")
                     && getInNode().get(getInNode().size() - 1).equals("location"))
           {
        	    // 在cwbopendata -> location這個位置中
                // 新增一筆新聞資料到 Stack(堆疊) 裡
        	    mWeatherItem_list.add(weatherItem);
        	    weatherItem = null;
           }
           super.endElement(namespaceURI, localName, qName);
     }
 
     /**
      * 取得Element的開頭結尾中間夾的字串
      */
     @Override
     public void characters(String fetchStr)
     {
           if (getInNode().size() >= 3
                     && getInNode().get(getInNode().size() - 3).equals("cwbopendata")
                     && getInNode().get(getInNode().size() - 2).equals("location"))
           {
                // 在rss -> channel -> item -> XXX這個位置中
                // 新增Node的上所有資料
                // 在rss -> channel -> item -> title這個位置中
                if (getInNode().lastElement().equals("lat"))
                     // 設定標題
                	 weatherItem.setLat(fetchStr);
                // 在rss -> channel -> item -> link這個位置中
                else if (getInNode().lastElement().equals("lon"))
                     // 設定連結
                	 weatherItem.setLon(fetchStr);
                // 在rss -> channel -> item -> pubDate這個位置中
                else if (getInNode().lastElement().equals("locationName"))
                     // 設定發佈日期
                	 weatherItem.setLocationName(fetchStr);
           }
           
           if (getInNode().size() >= 4
                   && getInNode().get(getInNode().size() - 4).equals("cwbopendata")
                   && getInNode().get(getInNode().size() - 3).equals("location")
                   && getInNode().get(getInNode().size() - 2).equals("weatherElement"))
           {
              // 在rss -> channel -> item -> XXX這個位置中
              // 新增Node的上所有資料
              // 在rss -> channel -> item -> title這個位置中
              if (getInNode().lastElement().equals("elementName"))
              {
            	  if (fetchStr.equals("HUMD"))
            		  check1 = true;
            	  else if (fetchStr.equals("24R"))
            		  check2 = true;
              }
           }
           
           if (getInNode().size() >= 5
                   && getInNode().get(getInNode().size() - 5).equals("cwbopendata")
                   && getInNode().get(getInNode().size() - 4).equals("location")
                   && getInNode().get(getInNode().size() - 3).equals("weatherElement")
                   && getInNode().get(getInNode().size() - 2).equals("elementValue"))
           {
              // 在rss -> channel -> item -> XXX這個位置中
              // 新增Node的上所有資料
              // 在rss -> channel -> item -> title這個位置中
              if (getInNode().lastElement().equals("value"))
              {
            	  if (check1 == true)
            	  {
            		  weatherItem.setHUMD(fetchStr);
            		  check1 = false;
            	  }
            	  else if (check2 == true)
            	  {
            		  weatherItem.setH_24R(fetchStr);
            		  check2 = false;
            	  }
              }
           }
     }
}