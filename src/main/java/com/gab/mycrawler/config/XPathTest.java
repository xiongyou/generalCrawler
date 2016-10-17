package com.gab.mycrawler.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class XPathTest {
	
	
	
 public static void main(String[] args) throws ParserConfigurationException,
   SAXException, IOException, XPathExpressionException {
  // 解析文件，生成document对象
  DocumentBuilder builder = DocumentBuilderFactory.newInstance()
    .newDocumentBuilder();
  Document document = builder.parse(new File("dataConfig.xml"));
  // 生成XPath对象
  XPath xpath = XPathFactory.newInstance().newXPath();
  // 获取节点值
  
  
  String webTitle = (String) xpath.evaluate(
    "/dataParseConfig/websites/website[@name='TaoBao']/dataobjects/product/fields/productName/text()", document,
    XPathConstants.STRING);
  System.out.println(webTitle);
  System.out.println("===========================================================");
  // 获取节点属性值
  String webTitleLang = (String) xpath.evaluate(
    "/dataParseConfig/websites/website[@name='TaoBao']/loadElements/cssSelector[@id='2']/@selector", document,
    XPathConstants.STRING);
  System.out.println(webTitleLang);
  System.out.println("===========================================================");
  // 获取节点对象
  Node bookWeb = (Node) xpath.evaluate(
    "dataParseConfig/websites/website[@name='TaoBao']/dataobjects", document,
    XPathConstants.NODE);
  System.out.println(bookWeb.getNodeName());
  NodeList nodes=(NodeList) xpath.evaluate(
		    "dataParseConfig/websites/website[@name='TaoBao']/loadElements/*", document,
		    XPathConstants.NODESET);
  for (int i = 0; i < nodes.getLength(); i++) {
	   Node book = nodes.item(i);
	   System.out.println(book.getNodeName());
	   System.out.println(book.getAttributes().getNamedItem("selector").getNodeValue());
	  }
  System.out.println("===========================================================");
  // 获取节点集合
  NodeList books = (NodeList) xpath.evaluate("/dataParseConfig/websites/website", document,
    XPathConstants.NODESET);
  List<String> productList=new ArrayList();
  List<String> productMonitorList=new ArrayList();
  List<String> storeList=new ArrayList();
  String [] strObj={"Product","ProductMonitor","Store"};
  for (int i = 0; i < books.getLength(); i++) {
   Node book = books.item(i);
   String platform=xpath.evaluate("@name", book,
     XPathConstants.STRING).toString();
   
   //处理配置文件所有属性的合并
   
   
   
   for(String dataObject :strObj){
	   
	   String elementPath = "dataParseConfig/websites/website[@name='"
				+ platform + "']/dataobjects/"+dataObject+"/fields/*";
		NodeList nodes1 = (NodeList) xpath.evaluate(
				elementPath, document,
			    XPathConstants.NODESET);
		
		
		for (int k = 0; k < nodes1.getLength(); k++) {
			Node node = nodes1.item(k);	
			String attr=node.getNodeName();
			if(dataObject=="Product")
			{
				if(!productList.contains(attr)){
					productList.add(attr);
				}
			}
			else if (dataObject=="ProductMonitor")
			{
				if(!productMonitorList.contains(attr)){
					productMonitorList.add(attr);
				}
			}
			else
			{
				if(!storeList.contains(attr)){
					storeList.add(attr);
				}
			}
			
			
		}
		
   }
   
  }

  System.out.print(strObj[0]+"\t");
  for(String attr:productList)
	   System.out.print(attr+"\t");
  System.out.println();

  System.out.print(strObj[1]+"\t");
  for(String attr:productMonitorList)
	   System.out.print(attr+"\t");
  System.out.println();

  System.out.print(strObj[2]+"\t");
  for(String attr:storeList)
	   System.out.print(attr+"\t");
  System.out.println();
  System.out.println("===========================================================");
 }
}