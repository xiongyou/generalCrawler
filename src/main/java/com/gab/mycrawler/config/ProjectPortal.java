package com.gab.mycrawler.config;

import java.util.Scanner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.taskComm.Task;
import com.gab.mycrawler.taskComm.TaskCommunication;
import com.gab.mycrawler.webDriver.PageDriver;



public class ProjectPortal {
	public static Logger logger = Logger.getLogger(ProjectPortal.class);
	private static Scanner sc;
	public static int timeoutCount = 0;

	public static void main(String[] args) {
		logger.setLevel(Level.DEBUG);
		// TODO Auto-generated method stub
		WebDriver driver=null;
		try {
			

			iConfig config = new StdConfig("start.xml");

			int taskCount = Integer.parseInt(config.getXpathText("config/taskCount/text()"));
			int timeout = Integer.parseInt(config.getXpathText("config/timeout/text()"));
			int exceptionPause = Integer.parseInt(config.getXpathText("config/exceptionPause/text()"));
			int exceptionLimit = Integer.parseInt(config.getXpathText("config/exceptionLimit/text()"));
			int timeoutCountLimit=Integer.parseInt(config.getXpathText("config/timeoutCountLimit/text()"));//连续超时次数限制
			// 1.从服务器请求任务
			String serverUri = config.getXpathText("config/serverUri/text()"); // 任务管理服务器地址
			String clientID = config.getXpathText("config/clientID/text()"); // 用户
			TaskCommunication taskComm = new TaskCommunication(serverUri, clientID); // 连接任务管理服务器
			//taskComm.userLogin(clientID); // 用户登录
			// 2.从任务中得到平台、链接、数据对象
			int errorCount = 0; // 记录出错次数,初始为0
			int version=221;
			if(!taskComm.validVersion(version)){
				return;
			}
			System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
			 driver = PageDriver.generateDriver();
			for (int excutedTaskNum = 1; excutedTaskNum <= taskCount; excutedTaskNum++) {
				Task task=null;
				logger.debug("Get a task:" + clientID);
				try{
				 task = taskComm.getATask();
				}
				catch(Exception e){
					e.printStackTrace();
					logger.debug(e.toString());
					continue;
				}
				// 任务被处理完毕后的操作
				if (task == null) {
					System.out.println("本次处理任务  " + (excutedTaskNum - 1) + " 条！");
					System.out.println("检查是否有新的任务，请输出回车！");
					sc = new Scanner(System.in);
					sc.nextLine();
					System.out.println("新的任务开始！");
					excutedTaskNum = 0;
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
					errorCount = 0;
				} catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.toString());
					if (errorCount == exceptionLimit) {
						logger.warn("访问连续出错"+exceptionLimit+"次，请重新启动程序！");
						driver.quit();
						sc = new Scanner(System.in);
						sc.nextLine();
						break; // 如果连续出错三次，则程序终止。
					}
					logger.warn("访问异常，将暂停 " + exceptionPause + " 分钟");
					driver.quit();
					Thread.sleep(1000 * 60 * exceptionPause);// 出错暂停。
					driver = PageDriver.generateDriver();
					errorCount++;
					continue;
				}
				// 4.提交结果到服务器
				logger.debug("Post a task:" + task.getTaskid());
				// 处理连接异常
				taskComm.postData(clientID, task, data);
				logger.debug("Task Posted:" + task.getTaskid());
				logger.debug("已执行 " + excutedTaskNum + "/" + taskCount + " 条任务！");
				//如果连续访问超时的次数达到timeoutCountLimit次，则结束程序。
				if(timeoutCount>=timeoutCountLimit){
					logger.debug("连续访问超时的次数达到 "+timeoutCount+" 次，请检查网络连接，然后输入回车继续");
					
					sc = new Scanner(System.in);
					sc.nextLine();					
				}
				if(excutedTaskNum==taskCount){
					logger.debug(taskCount + " 条任务已经完成，是否继续下一次任务？（Y/N）");
					sc = new Scanner(System.in);
					String str=sc.nextLine().toLowerCase();
					if(str.startsWith("y")){
						excutedTaskNum=0;
					}
				}
			}
			driver.quit();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug(e.toString());
			driver.quit();
		}
	}

}
