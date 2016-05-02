package com.gab.mycrawler.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.parse.StdParse;
import com.gab.mycrawler.parse.iParse;
import com.gab.mycrawler.taskComm.Task;
import com.gab.mycrawler.taskComm.TaskCommunication;
import com.gab.mycrawler.webDriver.PageDriver;
import com.gab.mycrawler.webDriver.UriContent;
import com.sun.glass.events.KeyEvent;

public class ProjectPortal {
	public static Logger logger = Logger.getLogger("com.foo");
	private static Scanner sc;

	public static void main(String[] args)  {
		logger.setLevel(Level.DEBUG);
		// TODO Auto-generated method stub
		try {
			System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
			WebDriver driver = PageDriver.generateDriver();

			iConfig config = new StdConfig("start.xml");

			int taskCount = Integer.parseInt(config.getXpathText("config/taskCount/text()"));
			int timeout = Integer.parseInt(config.getXpathText("config/timeout/text()"));
			int exceptionPause=Integer.parseInt(config.getXpathText("config/exceptionPause/text()"));
			// 1.从服务器请求任务
			String serverUri = config.getXpathText("config/serverUri/text()"); // 任务管理服务器地址
			String clientID = config.getXpathText("config/clientID/text()"); // 用户
			TaskCommunication taskComm = new TaskCommunication(serverUri, clientID); // 连接任务管理服务器
			taskComm.userLogin(clientID); // 用户登录
			// 2.从任务中得到平台、链接、数据对象
			int j=0;	//记录出错次数,初始为0
			for (int i = 1; i <= taskCount; i++) {
				
				logger.debug("Get a task:" + clientID);
				Task task = taskComm.getATask();
				// 任务被处理完毕后的操作
				if (task == null) {
					System.out.println("本次处理任务  " + (i - 1) + " 条！");
					System.out.println("检查是否有新的任务，请输出回车！");
					sc = new Scanner(System.in);
					sc.nextLine();
					System.out.println("新的任务开始！");
					i = 0;
					continue;
					// break;
				}
				logger.debug("Task is gotten:" + task.getTaskid());
				String platform = task.getWebsite();
				String crawlerUri = task.getCrawlerUri();
				String dataObj = task.getDataobj();
				// 3.打开页面，获取内容，或者保存网页源文件 ,内容解析,返回数据对象
				iData data = null;
				try {
					data = new PageDriver().getPageData(crawlerUri, platform, dataObj, timeout, driver);
					j=0;
				} catch (Exception e) {					
					e.printStackTrace();
					logger.warn(e.toString());
					if(j==3){
						System.out.println("访问连续出错三次，请重新启动程序！");
						sc = new Scanner(System.in);
						sc.nextLine();
						break;	//如果连续出错三次，则程序终止。
					}
					System.out.println("访问异常，将暂停 "+exceptionPause+" 三分钟");
					logger.warn("访问异常，将暂停 "+exceptionPause+" 三分钟");
					driver.quit();
					Thread.sleep(exceptionPause*60000);//出错暂停。
					driver = PageDriver.generateDriver();					
					j++;
					continue;
				}
				// 4.提交结果到服务器
				logger.debug("Post a task:" + task.getTaskid());
				taskComm.postData(clientID, task, data);
				logger.debug("Task Posted:" + task.getTaskid());
				System.out.println("已执行 " + i + "/" + taskCount + " 条任务！");

			}
			driver.quit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}

	}

}
