package com.gab.mycrawler.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.webDriver.PageDriver;


public class EveryDayCrawler {
	public static Logger logger = Logger.getLogger("com.foo");
	private static Scanner sc;
	public static int timeoutCount = 0;

	public static void run(int start, int max,int timeout, Writer writer, Writer err, WebDriver driver,int exceptionPause) throws IOException, Exception {
		if (start > max) {
			throw new Exception();
		}
		final int length = (max - start + 1);		
		File file = new File("conf/tb_productURL.txt");
		int lineHandled = 0;
		BufferedReader reader = null;
		int lineNum = 0;
		
		while (lineHandled < length) {
			String line;
			try {	
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk")); 
				lineNum = 0;
				int j=0;
				while (true) {
					//判断网络连接是否正常
					/*
					if(!Network.isConnect()){
						Thread.sleep(10000);
						continue;
					}
					*/
					lineNum++;
					line = reader.readLine();
					if (lineNum < start) {
						continue;
					}

					if (lineNum > max) {
						break;
					}

					// 到达文件末尾
					if (line == null) {
						break;
					}
					System.out.println("当前行 " + lineNum);
					lineHandled++;

					final String[] tmps = line.split("\t");
					if (tmps.length != 4) {
						err.write(lineNum + "\t" + line + "\t数据格式错误");
						System.out.println(lineNum  + "\t数据格式错误");
						err.write("\r\n");
						err.flush();
						continue;
					}					
					
					String crawlerUri = tmps[0];
					String platform = tmps[1];
					String keyword=tmps[2];
					String dataObj = tmps[3];
					
					iData data = null;					
					try {
						data = new PageDriver().getPageData(crawlerUri, platform, dataObj, timeout, driver);
						if(!data.getFieldValue("errorInfo").equals("")){
							System.out.println(data.getFieldNames().size());
							err.write(lineNum + "\t"+keyword+"\t"+data.toString(platform,dataObj));
							err.write("\r\n");
							err.flush();
						}
						else{
						writer.write(lineNum + "\t"+keyword+"\t"+data.toString(platform,dataObj));						
						writer.write("\r\n");
						writer.flush();
						}
						j = 0;
						
					} catch (Exception e) {
						e.printStackTrace();
						logger.warn(e.toString());
						if (j == 3) {
							logger.warn("访问连续出错三次，请重新启动程序！");
							sc = new Scanner(System.in);
							sc.nextLine();
							break; // 如果连续出错三次，则程序终止。
						}
						logger.warn("访问异常，将暂停 " + exceptionPause + " 分钟");
						driver.quit();
						Thread.sleep(1000 * 60 * exceptionPause);// 出错暂停。
						driver = PageDriver.generateDriver();
						err.write(lineNum+"\t"+line+"\t访问异常");
						err.write("\r\n");
						err.flush();
						start = lineNum--;
						lineHandled--;
						j++;
						continue;
					}		
				}
				if(line==null)
					break;
			} catch (StaleElementReferenceException e) {
				if (reader != null) {
					reader.close();
				}
				driver.quit();
				start = lineNum--;
				lineHandled--;
				continue;
			}catch(Exception e){
				
				System.out.println(e.toString());
				if (reader != null) {
					reader.close();
				}		
				driver.quit();
				start = lineNum--;
				lineHandled--;
				continue;
			}
			
			
		}
		driver.quit();
	}

	public static void save( int start,	int length ,int timeout,WebDriver driver,int exceptionPause) throws IOException, Exception{
	
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		Date date=new Date();
		String path="output/"+ new SimpleDateFormat("yyyy/MM/dd").format(date);
		File f=new File(path);
		if(!f.exists())
			f.mkdirs();
		File file = new File(path +"/"+ start + ".txt");
		File err = new File(path+"/" + start + "_err.txt");
		if (file.exists()) {
			file.delete();
		} 
		file.createNewFile();

		if (err.exists()) {
			err.delete();
		} 
		err.createNewFile();

		OutputStreamWriter oswErr = new OutputStreamWriter(
				new FileOutputStream(err), "utf-8");
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				file), "utf-8");
		BufferedWriter writer = new BufferedWriter(osw);
		BufferedWriter writerErr = new BufferedWriter(oswErr);
		run(start, start + length - 1, timeout, writer,
				writerErr,driver, exceptionPause);
		writer.close();
	}

	
	public static void main(String[] args) {
		logger.setLevel(Level.DEBUG);
		// TODO Auto-generated method stub
		try {
			System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
			WebDriver driver = PageDriver.generateDriver();

			iConfig config = new StdConfig("everyday.xml");

			int startLine = Integer.parseInt(config.getXpathText("config/startline/text()"));
			int length = Integer.parseInt(config.getXpathText("config/length/text()"));	
			
			int timeout = Integer.parseInt(config.getXpathText("config/timeout/text()"));
			int exceptionPause = Integer.parseInt(config.getXpathText("config/exceptionPause/text()"));
			
			
			EveryDayCrawler.save(startLine, length, timeout, driver,exceptionPause);
			
			driver.quit();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug(e.toString());

		}

	}

}
