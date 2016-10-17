package com.gab.mycrawler.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StdConfig implements iConfig {

	private String filePath;

	private Document document;
	private XPath xpath = XPathFactory.newInstance().newXPath();

	public StdConfig(String filePath) {
		this.filePath = filePath;
		this.loadFromFile(this.filePath);
	}

	@Override
	public boolean loadFromFile(String filePath) {
		// TODO Auto-generated method stub
		// 解析文件，生成document对象
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			document = builder.parse(new File(filePath));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取XPath路径所表示的值
	 * 
	 * @param elementPath
	 * @return
	 * @throws Exception
	 */
	public String getXpathText(String elementPath) throws Exception {
		String XpathText = (String) xpath.evaluate(elementPath, document,
				XPathConstants.STRING);
		
		return XpathText;
	}

	
	public Node getNodeObj(String elementPath) throws Exception {
		// 获取节点对象
		Node bookWeb = (Node) xpath.evaluate(elementPath, document,
				XPathConstants.NODE);
		System.out.println(bookWeb.getNodeName());

		System.out
				.println("===========================================================");
		return bookWeb;
	}

	/**
	 * 获取节点集合
	 * 
	 * @param elementPath
	 * @return
	 * @throws Exception
	 */
	public NodeList getNodeList(String elementPath) throws Exception {
		NodeList nodes = (NodeList) xpath.evaluate(elementPath, document,
				XPathConstants.NODESET);
		return nodes;
	}
/*
	@Override
	public Map<String, String> getDataObjects(String platform) throws Exception {
		// TODO Auto-generated method stub
		String elementPath = "dataParseConfig/websites/website[@name='"
				+ platform + "']/dataobjects/*";
		NodeList nodes = getNodeList(elementPath);
		Map<String, String> platformObjMap = new HashMap<String, String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			platformObjMap.put(platform, node.getNodeName());
		}

		return platformObjMap;
	}
	
	*/

	@Override
	public List<String> getDataObjFieldNames(String platform, String dataObject) throws Exception {
		// TODO Auto-generated method stub
		
		String elementPath = "dataParseConfig/websites/website[@name='"
				+ platform + "']/dataobjects/"+dataObject+"/fields/*";
		NodeList nodes = getNodeList(elementPath);
		List<String> fieldNames = new ArrayList< String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			fieldNames.add( node.getNodeName());
		}
		return fieldNames;
	}

	@Override
	public String getFieldRegex(String platform, String object, String fieldName) throws Exception {
		// TODO Auto-generated method stub
		String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/dataobjects/"+object+"/fields/"+fieldName+"/@regex";
		return this.getXpathText(elementPath);
	}

	@Override
	public String getFieldSecondRegex(String platform, String object, String fieldName) throws Exception {
		// TODO Auto-generated method stub
		String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/dataobjects/"+object+"/fields/"+fieldName+"/@second_regex";
		return this.getXpathText(elementPath);
	}
	@Override
	public List<String> getCssSelectors (String platform, String dataObject) throws Exception{
		List<String> cssSelectorList=new ArrayList<String>();
		List<String> dataObjFieldNames=	this.getDataObjFieldNames(platform, dataObject);
		String cssSelector;
		for(String fieldName:dataObjFieldNames){
			String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/dataobjects/"+dataObject+"/fields/"+fieldName+"/@cssSelector";
			try{
				cssSelector=this.getXpathText(elementPath);
				if(cssSelector.equals(""))
					continue;
				cssSelectorList.add(cssSelector);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return cssSelectorList;
	}

	@Override
	public List<String> getErrorInfoNames(String platform) throws Exception {
		// TODO Auto-generated method stub
		String elementPath = "dataParseConfig/websites/website[@name='"
				+ platform + "']/errorInfos/*";
		NodeList nodes = getNodeList(elementPath);
		List<String> errorInfoNames = new ArrayList< String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			errorInfoNames.add( node.getNodeName());
		}
		return errorInfoNames;
	}

	@Override
	public String getErrorInfoRegex(String platform, String errorInfo) throws Exception {
		// TODO Auto-generated method stub
		String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/errorInfos/"+errorInfo+"/@regex";
		return this.getXpathText(elementPath);
	}

	@Override
	public String getErrorInfoIsParsed(String platform, String errorInfo) throws Exception {
		// TODO Auto-generated method stub
		String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/errorInfos/"+errorInfo+"/@isParsed";
		return this.getXpathText(elementPath);
	}

	@Override
	public List<String> getCssSelectors(String platform) throws Exception {
		// TODO Auto-generated method stub
		String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/loadElements/*";
			NodeList nodes=getNodeList(elementPath);
			List<String> cssSelectorList=new ArrayList<String>();
	  for (int i = 0; i < nodes.getLength(); i++) {
		   Node book = nodes.item(i);
		   cssSelectorList.add(book.getAttributes().getNamedItem("selector").getNodeValue());
		  }
		return cssSelectorList;
	}

	@Override
	public String getErrorInfoText(String platform, String errorInfo) throws Exception {
		// TODO Auto-generated method stub
		String elementPath="/dataParseConfig/websites/website[@name='"+platform+"']/errorInfos/"+errorInfo+"/text()";
		return this.getXpathText(elementPath);
	}
	
	



	

}
