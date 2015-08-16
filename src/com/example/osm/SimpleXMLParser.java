package com.example.osm;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
 
public class SimpleXMLParser
{
     protected SimpleXMLParsingHandler xmlParsingHandler;
     /**
      * 建構子，必須將xmlParsingHandler給new出來
      *
      */
     public SimpleXMLParser(SimpleXMLParsingHandler parser)
     {
           xmlParsingHandler = parser;
     }
 
     /**
      * 從XML的剖析出物件
      *
      * @param inputStream
      *            來源的FileInputStream
      * @return回傳包含物件陣列的資料 (回傳可以不只一個物件陣列)
      * @throws SAXException
      * @throws ParserConfigurationException
      * @throws IOException
      */
     public Object[] getData(InputStream inputStream) throws SAXException,IOException, ParserConfigurationException
     {
           Object[] data;
           /* 產生SAXParser物件 */
           SAXParserFactory spf = SAXParserFactory.newInstance();
           SAXParser sp = spf.newSAXParser();
           /* 產生XMLReader物件 */
           XMLReader xr = sp.getXMLReader();
           /* 設定自定義的MyHandler給XMLReader */
           if (xmlParsingHandler == null)
           {
                throw new NullPointerException("xmlParsingHandler is null");
           } else
           {
                xr.setContentHandler(xmlParsingHandler);
                /* 解析XML */
                xr.parse(new InputSource(inputStream));
                /* 取得RSS標題與內容列表 */
                data = (Object[]) xmlParsingHandler.getParsedData();
           }
           inputStream.close();
           return data;
     }
 
     /**
      * 從XML的剖析出物件 (多載方法)
      *
      * @param urlPath
      *            url網址
      * @throws IOException
      * @throws SAXException
      * @throws ParserConfigurationException
      */
     public Object[] getData(String urlPath) throws SAXException, IOException,ParserConfigurationException
     {
           URL url = new URL(urlPath);
           HttpURLConnection uc = (HttpURLConnection) url.openConnection();
           uc.setConnectTimeout(15000);
           uc.setReadTimeout(15000); // 設定timeout時間
           uc.connect(); // 開始連線
           int status = uc.getResponseCode();
           if (status == HttpURLConnection.HTTP_OK)
           {
                Object[] data = getData(url.openStream());
                return data;
           }
           return null;
     }
}