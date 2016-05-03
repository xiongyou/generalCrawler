package com.gab.mycrawler.jsondata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 * 
 * <p>
 * Title: JSON-XML转换工具
 * </p>
 * <p>
 * desc:
 * <p>
 * Copyright: Copyright(c)Gb 2012
 * </p>
 * 
 * @author http://www.ij2ee.com
 * @time 上午8:20:40
 * @version 1.0
 * @since
 */

public class XmlJSON {

	private static final String STR_JSON = "{\"errorInfo\":\"访问超时或商品已下架\",\"productUrl\":\"https://detail.tmall.com/item.htm?id=42260938716&skuId=4611686060688326620&areaId=500100&cat_id=55070022&rn=a42d493916e6d4e5f515a7e6807085e9&user_id=2243902285&is_b=1\",\"extractTime\":\"2016-04-27 22:29:54\"}";

	public static String xml2JSON(String xml) {
		return new XMLSerializer().read(xml).toString();
	}

	public static String json2XML(String json) {
		JSONObject jobj = JSONObject.fromObject(json);
		String xml = new XMLSerializer().write(jobj);
		return xml;
	}

	public static void main(String[] args) {
		try {
			File file = new File("conf/data.txt");
			BufferedReader reader = null;

			reader = new BufferedReader(new FileReader(file));

			String line = "";
			int i=0;
			while (true) {
				line = reader.readLine();
				i++;
				if(line==null)
					break;				
				String xml1 = json2XML(line);
				String xml="";
				
					xml=xml1.replaceAll("<\\?xml[^>]{20,40}>", "").replaceAll(" type=\"string\"", "");
				
				System.out.println(xml);
				
			}
			String xml = json2XML(STR_JSON);
			System.out.println("xml = " + xml);
			String json = xml2JSON(xml);
			System.out.println("json=" + json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
