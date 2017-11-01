package com.gab.mycrawler.webDriver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gab.mycrawler.config.MyPath;
import com.gab.mycrawler.config.ProjectPortal;
import com.gab.mycrawler.config.StdConfig;
import com.gab.mycrawler.config.iConfig;
import com.gab.mycrawler.data.IPProxy;
import com.gab.mycrawler.data.StdData;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.parse.StdParse;
import com.gab.mycrawler.parse.iParse;
import com.gab.mycrawler.taskComm.TaskCommunication;
import com.gab.mycrawler.util.CompressUtil;

public class PageDriver {
	public static String get_chrome_proxy_extension(IPProxy ipProxy) {
		
		String path = MyPath.getProjectPath() + "\\libs";
		
		// 创建一个定制Chrome代理扩展(zip文件)
				String secPath = path + "\\userData\\Default\\Secure Preferences";
				File secFile=new File (secPath); 
				//删除代理的扩展文件
				if(secFile.exists()){
					secFile.delete();
				}
		// 创建一个定制Chrome代理扩展(zip文件)
		String zipPath = path + "\\Chrome-extension\\proxy.zip";
		File zipFile=new File (zipPath); 
		if(zipFile.exists()){
			zipFile.delete();
		}
				CompressUtil.zip(path + "\\Chrome-proxy-helper\\manifest.json",
				path + "\\Chrome-extension\\proxy.zip", false, null);

		// 扩展文件不存在，创建

		// 替换模板中的代理参数
		String background1_path = path + "\\Chrome-proxy-helper\\background1.js";

		File file = new File(background1_path);
		try {
		if (!file.exists()) {
			
				file.createNewFile();
			
		}

		InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
		BufferedReader br = new BufferedReader(isr);
		String background_content = "";
		while (true) {

			String line = br.readLine();

			// 到达文件末尾
			if (line == null) {
				break;
			}
			background_content += line + "\r\n";
		}
		br.close();
		background_content = background_content.replaceAll("%proxy_host", ipProxy.getIp())
				.replaceAll("%proxy_port", Integer.toString(ipProxy.getPort())).replaceAll("%username", ipProxy.getUserName())
				.replaceAll("%password", ipProxy.getPassword());
		String background_path = path + "\\Chrome-proxy-helper\\background.js";

		File bgfile = new File(background_path);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(bgfile), "utf-8");
		BufferedWriter writer = new BufferedWriter(osw);
		writer.write(background_content);
		writer.close();

		CompressUtil.zip(background_path, zipPath, false, null);

		return zipPath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static WebDriver generateDriver(IPProxy ipProxy) {
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		if(ipProxy.getUserName().equals("")){
			
			 String path = MyPath.getProjectPath(); 
			 ChromeOptions options = new ChromeOptions();
			  List<String> chromeArgs = new ArrayList<String>();
			 chromeArgs.add("user-data-dir=" + path + "\\libs\\userData");
			 //chromeArgs.add("--incognito");// 隐身模式
			 String  host=ipProxy.getIp()+":"+ipProxy.getPort();
			 chromeArgs.add("proxy-server=http://" 
			 		+host);//代理–proxy-server chromeArgs.add("--disable-images");//禁止图像 //
			 options.addArguments("user-data-dir="+path+"\\libs\\userData"); //
			  //加载用户配置文件 // options.addArguments("--incognito");//隐身模式
			 options.addArguments(chromeArgs);
			 WebDriver driver = new ChromeDriver(options);

				driver.manage().window().maximize();

				return driver;
			 
			 
		}
		else{
		
		String path = MyPath.getProjectPath();
		String extPath=get_chrome_proxy_extension(ipProxy);
		if( extPath!=null){
			ChromeOptions options = new ChromeOptions();
			List<String> chromeArgs = new ArrayList<String>();
			chromeArgs.add("user-data-dir=" + path + "\\libs\\userData");
			//chromeArgs.add("--incognito");// 隐身模式
			options.addArguments(chromeArgs);
			
			options.addExtensions(new File(extPath));
			WebDriver driver = new ChromeDriver(options);

			driver.manage().window().maximize();

			return driver;
		}
		
		else{
			return null;
		}
		}
		
	}

	public static WebDriver generateDriver() {
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		String path = MyPath.getProjectPath();
		ChromeOptions options = new ChromeOptions();
		List<String> chromeArgs = new ArrayList<String>();
		// 创建一个定制Chrome代理扩展(zip文件)
		/*
		String secPath = path + "\\libs\\userData\\Default\\Secure Preferences";
		File secFile=new File (secPath); 
		//删除代理的扩展文件
		if(secFile.exists()){
			secFile.delete();
		}
		chromeArgs.add("user-data-dir=" + path + "\\libs\\userData");// 加载用户配置文件
		//chromeArgs.add("--incognito");// 隐身模式
		 
		 */
		chromeArgs.add("--disable-images");// 禁止图像
		options.addArguments(chromeArgs);
		
		WebDriver driver = null;

		driver = new ChromeDriver(options);
		driver.manage().window().maximize();

		return driver;
	}

	/**
	 * 批量代理IP有效检测 ， 可以先直接批量获取，不验证，让客户端自己去验证
	 *
	 * @param proxyIpMap
	 * @param reqUrl
	 */
	public static boolean checkProxyIp(String proxyHost, Integer proxyPort,String userName,String password, String reqUrl) {

		int statusCode = 0;
		try {
			HttpClient httpClient = new HttpClient();
			httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
			httpClient.getParams().setAuthenticationPreemptive(true);
			// 如果代理需要密码验证，这里设置用户名密码
			if(userName!=null&&!userName.equals("")){
			httpClient.getState().setProxyCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(userName, password));
			}
			// 连接超时时间（默认5秒 5000ms） 单位毫秒（ms）
			int connectionTimeout = 5000;
			// 读取数据超时时间（默认10秒 10000ms） 单位毫秒（ms）
			int soTimeout = 10000;
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

			HttpMethod method = new GetMethod(reqUrl);

			statusCode = httpClient.executeMethod(method);
			// 如果成功，则将ip保存到数据库
			if (statusCode == 200) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.format("代理%s:%s不可用-->%s\n", proxyHost, proxyPort, statusCode);
			return false;
		} finally {
			System.out.format("%s:%s-->%s\n", proxyHost, proxyPort, statusCode);
		}

	}

	public iData getPageData(String crawlerUri, String platform, String dataObj, int timeout, WebDriver driver)
			throws Exception {

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
				data.setFieldValue("errorInfo", pageList.get(0));// 会有配置的错误信息是否解析，所以依然会有错误信息

				Date date = new Date();
				String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				data.setFieldValue("extractTime", strDate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				String errorInfo = "";
				if (errorInfo != null)
					data.setFieldValue("errorInfo", errorInfo + e.toString());
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
