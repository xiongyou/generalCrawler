package com.gab.mycrawler.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gab.mycrawler.config.MyPath;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.parse.StdParse;
import com.gab.mycrawler.parse.iParse;



public class StdSaveObj implements iSaveObject {
	public static Logger logger = Logger.getLogger("com.foo");
	
	public static void run(int start, int max,int timeout, Writer writer, Writer err) throws IOException, Exception {
		logger.setLevel(Level.DEBUG);
		
		if (start > max) {
			throw new Exception();
		}
		final int length = (max - start + 1);
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		String path=MyPath.getProjectPath();
		//System.out.println("user-data-dir="+path+"uuu");
		
		
		//ChromeDriver driver = new ChromeDriver(options);
		//driver.get("http://www.baidu.com");
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-data-dir="+path+"\\libs\\userData"); 
		File file = new File("conf/tb_productURL.txt");
		int lineHandled = 0;
		BufferedReader reader = null;
		int lineNum = 0;
		WebDriver driver = null;
		
		//初始化，不显示图片
		/*
		Map<String, Object> contentSettings = new HashMap<String, Object>();
		contentSettings.put("images", 2);

		Map<String, Object> preferences = new HashMap<String, Object>();
		preferences.put("profile.default_content_setting_values", contentSettings);
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		caps.setCapability("chrome.prefs", preferences);
		*/
		
		while (lineHandled < length) {
			String line;
			try {		
			
				driver = new ChromeDriver(options);						
				driver.manage().window().maximize();
				
				reader = new BufferedReader(new FileReader(file));
				lineNum = 0;
				
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
						err.write("\r\n");
						err.flush();
						continue;
					}					
					
					//如果不是配置的网站，则提示出错，然后继续

					String url=tmps[0];
					String platform=tmps[1];
					String dataObj=tmps[3];
					
					iParse parse1 = new StdParse(url, driver, timeout, platform,dataObj);
					 iData data;
					 data=parse1.parseData(dataObj);
					 
					 	 
				
					
					if(data==null){
						err.write(lineNum+"\t"+line+"\t商品下架或访问的地址不存在");
						err.write("\r\n");
						err.flush();
						continue;
					}
					else{
						//System.out.println(data.toString(platform,dataObj));	
						writer.write(lineNum + "\t"+line+"\t"+data.toString(platform,dataObj));						
						writer.write("\r\n");
						writer.flush();
					
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

	@Override
	public void save(int start, int length, int timeout) throws Exception {
		// TODO Auto-generated method stub
		//System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");

		Date date=new Date();
		String path="output/"+ new SimpleDateFormat("yyyy/MM/dd").format(date);
		
		File f=new File(path);
		if(!f.exists())
			f.mkdirs();
		File file = new File(path +"/"+ start + ".txt");
		File err = new File(path+"/" + start + "_err.txt");
		if (!file.exists()) {
			file.createNewFile();
		} else {
			System.out.println("文件已存在或创建文件失败");
			return;
		}

		if (!err.exists()) {
			err.createNewFile();
		} else {
			System.out.println("文件已存在或创建文件失败");
			return;
		}

		OutputStreamWriter oswErr = new OutputStreamWriter(
				new FileOutputStream(err), "utf-8");
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				file), "utf-8");
		BufferedWriter writer = new BufferedWriter(osw);
		BufferedWriter writerErr = new BufferedWriter(oswErr);
		run(start, start + length - 1, timeout, writer,
				writerErr);
		writer.close();
		}

}
