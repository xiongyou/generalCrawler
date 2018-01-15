package com.gab.mycrawler.config;

import java.util.Scanner;

import org.openqa.selenium.WebDriver;

import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.webDriver.PageDriver;

public class Test {

	public static void main(String[] args) {

	

		String crawlerUri = "http://item.yhd.com/item/52880376";
		String platform ="YiHaoDian";		
		String dataObj = "Product";
		int timeout =10;
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		WebDriver driver = PageDriver.generateDriver();
		
		iData data = null;					
		try {
			data = new PageDriver().getPageData(crawlerUri, platform, dataObj, timeout, driver);
			
				System.out.println(data.toString(platform,dataObj));				
			
		} catch (Exception e) {
			e.printStackTrace();			
		}	
		driver.quit();
	}

}
