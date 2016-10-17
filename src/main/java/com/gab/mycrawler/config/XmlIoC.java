package com.gab.mycrawler.config;

import com.gab.mycrawler.save.iSaveObject;

public class XmlIoC extends StdConfig{

	
	
	public XmlIoC(String filePath) {
		super(filePath);
		// TODO Auto-generated constructor stub
	}
	
	public static Object getBean() 
	{
		try{	
		iConfig config=new StdConfig("start.xml");
		String className=config.getXpathText("config/className/text()");
		System.out.println(className);
		Object obj=Class.forName(className).newInstance();
		return obj;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String args[])
	{
		try{
		
		int start = Integer.parseInt(args[0]);
		int length = Integer.parseInt(args[1]);
		int timeout = Integer.parseInt(args[2]);
		
		iSaveObject saveObject= (iSaveObject) XmlIoC.getBean();
		saveObject.save(start, length, timeout);
		
		
		
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	}
	

}
