package com.gab.mycrawler.data;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.selector.Json;

import com.gab.mycrawler.config.StdConfig;
import com.gab.mycrawler.config.iConfig;

import net.sf.json.JSONObject;

public   class StdData implements iData {
	iConfig config = new StdConfig("dataConfig.xml");
     private Map<String, String> datastore=new LinkedHashMap <String, String>();
     private Map<String,String> errorInfoMap=new LinkedHashMap <String, String>();
     
	@Override
	public String toJson() throws Exception{
		// TODO Auto-generated method stub
		List<String> fieldNames=new ArrayList<String>();		
		fieldNames=this.getFieldNames();		
		JSONObject jobj=new JSONObject();
		for(int i=0;i<fieldNames.size();i++)
		{
			String fieldName=fieldNames.get(i);
			String value=this.getFieldValue(fieldName).replaceAll("\t", " ").replaceAll("\n", " ").replaceFirst(" ", "");
			jobj.put(fieldName, value);
		}		
		return jobj.toString();
		
	}
	@Override
	public String toString(String platform,String dataObjName) throws Exception{
		List<String> fieldNames=new ArrayList<String>();
		
		fieldNames=this.getFieldNames();
		String str="";
		for(int i=0;i<fieldNames.size();i++)
		{
			String fieldName=fieldNames.get(i);
			String value=this.getFieldValue(fieldName).replaceAll("\t", " ").replaceAll("\n", " ").replaceFirst(" ", "");
			
			str+=value+"\t";
		}
		Date date=new Date();
		String strDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		return str+"\t"+strDate;
	}
	@Override
	public boolean fromString(String datastr) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public List<String> getFieldNames() throws Exception {
		// TODO Auto-generated method stub
		List<String> fieldNames=new ArrayList<String>(this.datastore.keySet());		
		return fieldNames;
	}
	
	@Override
	public String getFieldValue(String fieldName) {
		// TODO Auto-generated method stub	
		return this.datastore.get(fieldName);
	}
	
	@Override
	public void setFieldValue(String fieldName, String value) {
		// TODO Auto-generated method stub		
		this.datastore.put(fieldName, value);
	}
	
	@Override
	public List<String> getErrorInfos() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getErrorInfo(String errorInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setErrorInfo(String errorInfo, String value) {
		// TODO Auto-generated method stub
		
	}
	
	
}
