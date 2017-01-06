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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gab.mycrawler.config.ProjectPortal;
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
		ProjectPortal.logger.debug("Beginning,Open page:" + uri);
		driver.get(uri);

		String tmpUrl = driver.getCurrentUrl();
		stdUrl = tmpUrl;
		/*
		 * if (tmpUrl.indexOf("&") != -1) stdUrl = tmpUrl.substring(0,
		 * tmpUrl.indexOf("&")); else stdUrl = tmpUrl;
		 */
		final WebDriver tmpDriver = driver.switchTo().window(curWindowHandle);

		final List<String> cssSelectorList;
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
							Thread.sleep(1500);
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
							if (b) {
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
			ProjectPortal.logger.debug("Page is opened:" + uri);
		} catch (TimeoutException e) {
			// 如果没有设置下架等错误信息的配置，则默认下架
			/*
			 * if (errorInfoNames.size() == 0) { pageList.add("商品已下架！"); } else
			 * { pageList.add("访问超时"); }
			 */
			pageList.add("访问超时");
			return pageList;
		}

		content = driver.getPageSource().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&")
				.replaceAll("&quot;", "\"");
		 //System.out.println(content);
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
			ProjectPortal.logger.debug("Beginning,Save file:" + fileName);
			File file = new File(path + "/" + fileName + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			BufferedWriter writer = new BufferedWriter(osw);
			writer.write(content);
			writer.close();
			ProjectPortal.logger.debug("File is Saved:" + fileName);
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
		while (ma.find()) {
			sb.append(ma.group());
		}

		String temp = sb.toString();
		return temp.trim().replaceAll("\t", " ").replaceAll("\n", " ").replaceFirst(" ", "");// 去掉空格
	}

}