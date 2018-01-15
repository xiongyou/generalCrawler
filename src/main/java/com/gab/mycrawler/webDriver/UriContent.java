package com.gab.mycrawler.webDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gab.mycrawler.config.Input;
import com.gab.mycrawler.config.ProjectPortal1;
import com.gab.mycrawler.config.StdConfig;
import com.gab.mycrawler.config.iConfig;
import com.gab.mycrawler.save.StdSaveObj;

/**
 * Issue #1: 淘宝的页面数据请求及其复杂，暂时分析无能，所以用selenium替换webmagic直接做浏览器模拟 Issue
 * #2:淘宝有反爬虫安全控制，selenium连续爬取到第50个商品时，会强制要求进行用户认证
 * 
 * @author Jason Phang
 * 
 */
public class UriContent {
	iConfig config = new StdConfig("dataConfig.xml");
	iConfig startConfig = new StdConfig("start.xml");
	private String commentLoadCssSelector;

	/**
	 * 获取网页内容，并返回一个由stdURL,website,content组合在一起的List
	 * 
	 * @param uri
	 * @param timeout
	 * @param driver
	 * @return List<String>
	 * @throws IOException
	 * @throws Exception
	 */
	public List<String> getContent(String uri, String website, String dataObj, int timeout, WebDriver driver)
			throws IOException, Exception {
		List<String> pageList = new ArrayList<String>();

		// System.setProperty("webdriver.chrome.driver",
		// "libs/chromedriver.exe");
		String stdUrl;
		String content = "";
		final String platform = website;
		final String dataObject = dataObj;

		String curWindowHandle = driver.getWindowHandle();

		// 打开页面
		ProjectPortal1.logger.debug("Beginning,Open page:" + uri);
		driver.get(uri);

		
		String userName = startConfig.getXpathText("config/login/@username");
		String password = startConfig.getXpathText("config/login/@password");
		String printPage=startConfig.getXpathText("config/printPage/text()");
		Thread.sleep(200);
		String tmpUrl = driver.getCurrentUrl();
		stdUrl = tmpUrl;
		
		int loginTime=0;
		while(tmpUrl.contains("//login.")&&loginTime<5){
			try{
				ProjectPortal1.logger.debug("需要登录!");
			if("Tmall".equals(website)){
				try{
					driver.switchTo().frame("J_loginIframe");
				}
				catch(Exception e){
					ProjectPortal1.logger.warn("不是天猫网站!");
				}
			}
			
			
			//切换到输入用户名、密码登录
			try{
			WebElement switchBtn=driver.findElement(By.cssSelector(".J_Quick2Static"));
			switchBtn.click();
			}
			//输入用户名密码
			finally{
			WebElement usernameEle=driver.findElement(By.cssSelector("#TPL_username_1"));
			usernameEle.clear();
			usernameEle.sendKeys(userName);
			Thread.sleep(2000);
			
			WebElement pwdEle=driver.findElement(By.cssSelector("#TPL_password_1"));
			pwdEle.clear();
			pwdEle.sendKeys(password);
			Thread.sleep(2000);
			//点击登录
			driver.findElement(By.cssSelector("button#J_SubmitStatic")).click();
			Thread.sleep(2000);
			}
			//pageList.add("需要登录");
			//return pageList;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			loginTime++;
			if(loginTime>=5){
				ProjectPortal1.logger.debug("登录出错超过5次，请检查用户名、密码，然后输入回车!");

				Input.sc.nextLine();
			}
			driver.switchTo().defaultContent();
			tmpUrl = driver.getCurrentUrl();
		}}
		
		/*
		 * if (tmpUrl.indexOf("&") != -1) stdUrl = tmpUrl.substring(0,
		 * tmpUrl.indexOf("&")); else stdUrl = tmpUrl;
		 */
		
		
		
		//淘宝、天猫登录与验证码弹窗处理
		
		
		
		final WebDriver tmpDriver = driver.switchTo().window(curWindowHandle);
		final String u=userName;
		final String pwd=password;
		final List<String> cssSelectorList;
		final String tUrl=uri;
		cssSelectorList = config.getCssSelectors(platform);
		List<String> errorInfoNames = config.getErrorInfoNames(platform);
		// 等待页面加载完毕，超时时间设为 timeout秒
		try {
			(new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					List<Boolean> bList = new ArrayList();

					// 如果没有设置CssSeletor，则默认等待页面加载1.5秒
					if (cssSelectorList.size() == 0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}

					for (String cssSelector : cssSelectorList) {

						String[] selectors = cssSelector.split("\\|");
						int i = 0;
						for (String str : selectors) {
							if (str.equals(""))
								continue;
							String cssContent = "";
							try {
								cssContent = tmpDriver.findElement(By.cssSelector(str)).getText();
							} catch (Exception e) {
								// e.printStackTrace();
							}
							boolean b = (cssContent.equals("") || cssContent.equals("-"));
							boolean sale= cssContent.equals("-");//此处用于判断销量数据是否出现
							if (b) {
								//判断是否出现了登录弹窗
								if(sale){//如果销量数据是-，则表示未加载出来，需要登录或重新加载
								try{
									//注意，此处登录与验证码的弹窗是同一个窗体对象
									tmpDriver.findElement(By.cssSelector("div.sufei-dialog-content"));
									//输入用户名密码
									tmpDriver.switchTo().frame("sufei-dialog-content");  
							        
									//先判断是否为验证码弹窗
									try{
									   WebElement identCodeEle=tmpDriver.findElement(By.cssSelector("div#J_CodeContainer"));
									   ProjectPortal1.logger.warn("需要输入验证码！" );
									   Input.sc.nextLine();
									}
									catch(Exception e){
									
									
									
									WebElement usernameEle=tmpDriver.findElement(By.cssSelector("#TPL_username_1"));
									usernameEle.clear();
									usernameEle.sendKeys(u);
									Thread.sleep(2000);
									
									WebElement pwdEle=tmpDriver.findElement(By.cssSelector("#TPL_password_1"));
									pwdEle.clear();
									pwdEle.sendKeys(pwd);
									Thread.sleep(2000);
									//点击登录
									tmpDriver.findElement(By.cssSelector("button#J_SubmitStatic")).click();
									
									// 此时 没跳出frame，如果这时定位default content中的元素也会报错  
							        // dr.findElement(By.id("id1"));//error  
							        /** 跳出frame,进入default content;重新定位id="id1"的div */  
							        tmpDriver.switchTo().defaultContent(); 
									}
								}
								catch(Exception e){
									tmpDriver.navigate().refresh();
									e.printStackTrace();
									ProjectPortal1.logger.debug("等待加载");
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									tmpDriver.navigate().refresh();
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								}
								break;
							}
							i++;
							if (i == selectors.length) {
								return true;
							}
						}
					}
					return false;
				}
			});
			ProjectPortal1.logger.debug("Page is opened:" + uri);
		} catch (TimeoutException e) {
			// 如果没有设置下架等错误信息的配置，则默认下架
			/*
			 * if (errorInfoNames.size() == 0) { pageList.add("商品已下架！"); } else
			 * { pageList.add("访问超时"); }
			 */
			pageList.add("访问超时");
			if("1".equals(printPage))
			System.out.println(driver.getPageSource().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&")
					.replaceAll("&quot;", "\""));
			return pageList;
		}

		content = driver.getPageSource().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&")
				.replaceAll("&quot;", "\"");
		if("1".equals(printPage))
		 System.out.println(content);
		// 如果包含出错数据的处理
		int errorStatus = 0;// 初始化错误状态为0
		for (String errorInfoName : errorInfoNames) {
			String errorInfoRegex = config.getErrorInfoRegex(platform, errorInfoName);
			// 将同一种错误的多个错误的正则表达式拆分为多个，用||分隔
			String[] errorInfoRegs = errorInfoRegex.split("\\|\\|");
			for (String errorInfoReg : errorInfoRegs) {

				String errorInfo = (this.resultOfRegex(content, errorInfoReg).equals("") ? ""
						: config.getErrorInfoText(platform, errorInfoName));
				if (!errorInfo.equals("")) {
					// String errorInfoText=config.getErrorInfoText(platform,
					// errorInfoName);
					pageList.add(errorInfo);
					String isParsed = config.getErrorInfoIsParsed(platform, errorInfoName);
					if (isParsed.equals("1")) { // 如果有错误信息，是否仍然进行解析，如果=1，则添加网页源内容到输出列表
						pageList.add(content);
					}
					errorStatus = 1; // 如果匹配到了错误信息，则修改其状态
					break;
				}
			}
		}
		if (errorStatus == 0) { // 在出错数据处理之后，如果错误状态仍为0，则表示没有错误信息，则按照正常情况进行处理
			pageList.add("");
			// System.out.println(content);
			pageList.add(content);
		}

		// 是否保存页面源文件
		iConfig config = new StdConfig("start.xml");
		String isSaveProductFile = config.getXpathText("config/isSaveProductFiles/text()");

		if (isSaveProductFile.equals("1")) {

			Date date = new Date();

			String path = "productFiles/" + website + "/" + new SimpleDateFormat("yyyy/MM/dd").format(date);
			File f = new File(path);
			if (!f.exists())
				f.mkdirs();

			String fileName = stdUrl.replace('/', '+').replace(':', '^').replace('?', '_');
			ProjectPortal1.logger.debug("Beginning,Save file:" + fileName);
			File file = new File(path + "/" + fileName + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			BufferedWriter writer = new BufferedWriter(osw);
			writer.write(content);
			writer.close();
			ProjectPortal1.logger.debug("File is Saved:" + fileName);
		}
		return pageList;

	}

	/**
	 * 根据正则表达式处理原始字符串
	 * 
	 * @param oldContent
	 * @param regex
	 * @return
	 * @throws Exception
	 */
	public String resultOfRegex(String oldContent, String regex) throws Exception {
		Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
		Matcher ma = pa.matcher(oldContent);

		StringBuffer sb = new StringBuffer();
		if (ma.find()) {
			sb.append(ma.group());
		}

		String temp = sb.toString();
		return temp.trim().replaceAll("\t", " ").replaceAll("\n", " ").replaceFirst(" ", "");// 去掉空格
	}

}