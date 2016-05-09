package com.gab.mycrawler.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;


public interface iConfig {

	/**
	 * 解析文件，生成document对象
	 * 
	 * @param fileName
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @pdOid 249405cf-6cec-45c2-8dcd-3a6d5fea4ff8
	 */
	boolean loadFromFile(String fileName);
	
	public String getXpathText(String elementPath) throws Exception;

	/**
	 * 返回指定网站中需要解析的数据对象描述信息，包含是对象的名称
	 * 
	 * @param platform
	 * @throws Exception
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @pdOid 42455579-b572-4fcc-bea4-bb9d9d88b217
	 */
	//Map<String, String> getDataObjects(String platform) throws Exception;

	/**
	 * 返回某个数据对象的字段名称的List
	 * @param dataObject
	 * @return
	 * @throws Exception
	 */
	//List<String> getDataObjFieldNames( String dataObject) throws Exception;
	
	/**
	 * 返回某个平台下数据对象与字段属性的List
	 * 
	 * @param platform
	 * @param dataObject
	 * @throws Exception
	 * @pdOid f3369800-2508-4644-a010-f89ac6a83935
	 */
	List<String> getDataObjFieldNames(String platform, String dataObject)
			throws Exception;

	
	/**
	 * 返回某个平台下某个数据对象的字段与正则表达式的
	 * 
	 * @param platform
	 * @param object
	 * @param fieldName
	 * @throws Exception
	 * @pdOid 4c6f4d7b-79ea-42bf-ad02-14c5463d99dc
	 */
	String getFieldRegex(String platform, String object, String fieldName)
			throws Exception;

	/**
	 * 返回某个平台下某个数据对象的字段与第二个正则表达式的
	 * 
	 * @param platform
	 * @param object
	 * @param fieldName
	 * @throws Exception
	 * @pdOid 4c6f4d7b-79ea-42bf-ad02-14c5463d99dc
	 */
	String getFieldSecondRegex(String platform, String object, String fieldName)
			throws Exception;
	
	 /**返回某个平台下某个数据对象的CssSeletor列表
	 * @param platform
	 * @param dataObject
	 * @return
	 * @throws Exception
	 */
	List<String> getCssSelectors (String platform, String dataObject) throws Exception;

	/**
	 * 返回某个平台下错误信息名称的List
	 * @param platform
	 * @return
	 * @throws Exception
	 */
	List<String> getErrorInfoNames(String platform) throws Exception;
	
	/**
	 * 返回某个平台下错误信息名称对应的正则表达式
	 * 
	 * @param platform
	 * @param errorInfo
	 * @throws Exception
	 * @pdOid 4c6f4d7b-79ea-42bf-ad02-14c5463d99dc
	 */
	String getErrorInfoRegex(String platform, String errorInfo)
			throws Exception;
	
	/**
	 * 获取某个平台下出现错误信息时是否解析该页面
	 * 
	 * @param platform
	 * @param errorInfo
	 * @throws Exception
	 * @pdOid 4c6f4d7b-79ea-42bf-ad02-14c5463d99dc
	 */
	String getErrorInfoIsParsed(String platform, String errorInfo)
			throws Exception;
	
	List<String> getCssSelectors(String platform) throws Exception;
}
