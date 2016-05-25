package com.gab.mycrawler.parse;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.gab.mycrawler.config.ProjectPortal;
import com.gab.mycrawler.config.StdConfig;
import com.gab.mycrawler.config.iConfig;
import com.gab.mycrawler.data.StdData;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.save.StdSaveObj;
import com.gab.mycrawler.webDriver.UriContent;

public class StdParse implements iParse {

	iConfig config = new StdConfig("dataConfig.xml");

	private String website;
	private String productURL;
	private int timeout;
	private WebDriver driver;
	private String pageContent = null;
	UriContent uriContent = new UriContent();
	public StdParse(){
		
	}
	public StdParse(String productURL, WebDriver driver, int timeout,String website,String type)
			throws Exception {
		// this.website="TaoBao";
		this.setWebsite(website);
		this.setDriver(driver);
		this.setTimeout(timeout);
		this.setProductURL(productURL);
		this.setPageContent(type);
		
	}
	@Override
	public iData parseData(String platform,String dataObj,String Content) throws Exception{
		// TODO Auto-generated method stub

				iData data = new StdData();
				if(Content.equals("") )
					return null;
				
				List<String> fieldNames = config.getDataObjFieldNames(platform,dataObj);
				for (String fieldName : fieldNames) {
					ProjectPortal.logger.debug("Beginning,Parse Field:"+fieldName);
					String regex = config.getFieldRegex(platform, dataObj,
							fieldName);
					String info="";
					if(!regex.equals(""))						
					info = uriContent
							.resultOfRegex(Content, regex).replaceAll("\t", " ").replaceAll("\n", " ").replaceFirst(" ", "");						
					ProjectPortal.logger.debug("Field is Parsed:"+fieldName);
					data.setFieldValue(fieldName, info);		
					
				}			
				return data;
	}
	@Override
	public iData parseData(String dataName) throws Exception {
		// TODO Auto-generated method stub

		iData data = new StdData();
		if(this.getPageContent().equals("") )
			return null;
		
		List<String> fieldNames = config.getDataObjFieldNames(this.website,dataName);
		for (String fieldName : fieldNames) {
			StdSaveObj.logger.debug("Beginning,Parse Field:"+fieldName);
			String regex = config.getFieldRegex(this.website, dataName,
					fieldName);
			
			String info = uriContent
					.resultOfRegex(this.getPageContent(), regex).replaceAll("\t", " ").replaceAll("\n", " ").replaceFirst(" ", "");						
			StdSaveObj.logger.debug("Field is Parsed:"+fieldName);
			data.setFieldValue(fieldName, info);		
			
		}			
		return data;
	}

	public String getProductURL() {
		return productURL;
	}

	public void setProductURL(String productURL) {
		this.productURL = productURL;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) throws Exception {
		this.timeout = timeout;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public String getPageContent() throws Exception {
		
		return this.pageContent;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setPageContent(String type) throws Exception {
		List<String> pageList = new ArrayList<String>();
		
		 pageList = uriContent.getContent(this.productURL,this.website,type,
				this.timeout, this.driver);
		
		
		if (pageList.size()==1)
		{
			this.pageContent = "";
			return;
		}
		//this.setWebsite( pageList.get(1));
		this.pageContent = pageList.get(1);		
	}

	

}
