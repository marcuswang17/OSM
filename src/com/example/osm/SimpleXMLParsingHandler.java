package com.example.osm;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
/**
 * 處理XML的方法細節的Class 有四個方法必須覆寫而且要呼叫super方法
 */
public abstract class SimpleXMLParsingHandler extends DefaultHandler
{
     /** XML解析Node的堆疊 */
     private Stack<String> in_node;
     public Stack<String> getInNode()
     {
           return in_node;
     }
 
     /**
      * 將轉換的資料回傳
      */
     public abstract Object getParsedData();
     /**
      * XML文件開始解析時呼叫此method，這裡要加上new出自訂的物件的程式
      */
     @Override
     public void startDocument() throws SAXException
     {
           in_node = new Stack<String>();
     }
 
     /**
      * XML文件結束解析時呼叫此method
      */
     @Override
     public void endDocument() throws SAXException
     {
     }
 
     /**
      * 解析到Element的開頭時呼叫此method
      */
     @Override
     public void startElement(String namespaceURI, String localName,
                String qName, Attributes atts) throws SAXException
     {
           in_node.push(qName);
     }
 
     /**
      * 解析到Element的結尾時呼叫此method
      */
     @Override
     public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException
     {
           in_node.pop();
     }
 
     /** 取得Element的開頭結尾中間夾的字串 */
     @Override
     public void characters(char ch[], int start, int length)
     {
           String fetchStr = new String(ch).substring(start, start + length);
           // printNodePos();
           characters(fetchStr);
     }
 
     /**
      * 取得Element的開頭結尾中間夾的字串這裡需要做「新增Node的上所有資料」
      *
      * @param fetchStr
      *            取得到的字串
      */
     public void characters(String fetchStr)
     {
     }
 
     /**
      * 印出現在Node的位置，除錯用。例如:rss -> channel -> title 
      */
     public String printNodePos()
     {
           StringBuffer sb = new StringBuffer();
           // 印出現在Node的位置
           for (int i = 0; i < in_node.size(); i++)
           {
                if (i > 0)
                     sb.append(" -> ");
                sb.append(in_node.get(i));
           }
           sb.append("\n");
           return sb.toString();
     }
 
     /**
      * 在參數堆中找到我要的參數
      */
     public static String findAttr(Attributes atts, String findStr)
     {
           int i;
           for (i = 0; i < atts.getLength(); i++)
           {
                if (atts.getQName(i).compareToIgnoreCase(findStr) == 0)
                {
                     break;
                }
           }
           return atts.getValue(i);
     }
}