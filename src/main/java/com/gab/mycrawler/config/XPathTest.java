package com.gab.mycrawler.config;

import java.io.File;
import java.io.IOException;

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
  for (int i = 0; i < books.getLength(); i++) {
   Node book = books.item(i);
   System.out.println(xpath.evaluate("@name", book,
     XPathConstants.STRING));
  }
  System.out.println("===========================================================");
 }
}