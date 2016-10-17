package com.gab.mycrawler.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperties 
{
	public static String getValueByKey(String filePath,String key)
	{
		Properties props=new Properties();
		try
		{
			InputStream in=new BufferedInputStream(new FileInputStream(filePath));
			props.load(in);
			String value=props.getProperty(key);
			//System.out.println(value);
			return value;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
