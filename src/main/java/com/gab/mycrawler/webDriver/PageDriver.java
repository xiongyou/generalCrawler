package com.gab.mycrawler.webDriver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gab.mycrawler.config.MyPath;
import com.gab.mycrawler.config.ProjectPortal;
import com.gab.mycrawler.data.StdData;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.parse.StdParse;
import com.gab.mycrawler.parse.iParse;

public class PageDriver {
	public static WebDriver generateDriver() {
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		String path=MyPath.getProjectPath();		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-data-dir="+path+"\\libs\\userData"); 
		WebDriver driver = null;		
		
		// 初始化，不显示图片
		/*
		Map<String, Object> contentSettings = new HashMap<String, Object>();
		contentSettings.put("images", 2);

		Map<String, Object> preferences = new HashMap<String, Object>();
		preferences.put("profile.default_content_setting_values", contentSettings);
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		caps.setCapability("chrome.prefs", preferences);
		*/
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		return driver;
	}

	public iData getPageData(String crawlerUri, String platform, String dataObj, int timeout, WebDriver driver) throws Exception {
		
		// 获取页面内容
		UriContent uriContent = new UriContent();
		String pageContent = "";
		iParse parse1 = new StdParse();
		iData data = new StdData();
		try {
			List<String> pageList = new ArrayList<String>();
			pageList = uriContent.getContent(crawlerUri, platform, dataObj, timeout, driver);

			if (pageList.size() == 1) { // 如果为1，则表示只有错误信息，未解析内容
				pageContent = "";
			} else {
				pageContent = pageList.get(1);
			}

			// 4.内容解析,返回数据对象
			
			try {
				if (!pageContent.equals("")) {
					data = parse1.parseData(platform, dataObj, pageContent);
				}
				data.setFieldValue("productUrl", crawlerUri);
				data.setFieldValue("errorInfo", pageList.get(0));//会有配置的错误信息是否解析，所以依然会有错误信息
				
				Date date = new Date();
				String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				data.setFieldValue("extractTime", strDate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				String errorInfo = "";
				if (errorInfo != null)
					data.setFieldValue("errorInfo", errorInfo + e.toString());
				else
					data.setFieldValue("errorInfo", e.toString());
				e.printStackTrace();
			}
		} catch (Exception e) {
			ProjectPortal.logger.debug(e.toString());
			e.printStackTrace();
			throw new Exception();
		}
		return data;
	}
}
