package com.gab.mycrawler.config;

import java.util.Scanner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.gab.mycrawler.data.IPProxy;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.taskComm.Task;
import com.gab.mycrawler.taskComm.TaskCommunication;
import com.gab.mycrawler.webDriver.PageDriver;

public class ProjectPortal {
	public static Logger logger = Logger.getLogger(ProjectPortal.class);

	public static int timeoutCount = 0;

	public static void main(String[] args) {
		logger.setLevel(Level.DEBUG);
		// TODO Auto-generated method stub
		WebDriver driver = null;
		try {
			Input input = new Input();

			iConfig config = new StdConfig("start.xml");

			int taskCount = Integer.parseInt(config.getXpathText("config/taskCount/text()"));
			int timeout = Integer.parseInt(config.getXpathText("config/timeout/text()"));
			int exceptionPause = Integer.parseInt(config.getXpathText("config/exceptionPause/text()"));
			int exceptionLimit = Integer.parseInt(config.getXpathText("config/exceptionLimit/text()"));
			int timeoutCountLimit = Integer.parseInt(config.getXpathText("config/timeoutCountLimit/text()"));// 连续超时次数限制
			int autoExcute = Integer.parseInt(config.getXpathText("config/autoExcute/text()"));
			String proxyAvailable = config.getXpathText("config/proxy/@available");
			String proxyType = config.getXpathText("config/proxy/@type");
			int proxyNum = Integer.parseInt(config.getXpathText("config/proxy/text()"));
			String validUrl = config.getXpathText("config/proxy/@validUrl");
			String userName = config.getXpathText("config/proxy/@userName");
			String password = config.getXpathText("config/proxy/@password");
			// 1.从服务器请求任务
			String serverUri = config.getXpathText("config/serverUri/text()"); // 任务管理服务器地址
			String clientID = config.getXpathText("config/clientID/text()"); // 用户
			TaskCommunication taskComm = new TaskCommunication(serverUri, clientID); // 连接任务管理服务器
			// taskComm.userLogin(clientID); // 用户登录
			// 2.从任务中得到平台、链接、数据对象
			int errorCount = 0; // 记录出错次数,初始为0
			int version = 224;
			if (!taskComm.validVersion(version)) {
				return;
			}

			int totalCount = 1;// 设置多次执行后的总任务数量

			boolean useProxy = false;// 是否使用代理，初始为不使用代理
			boolean startProxy = false;// 是否新设置代理，初始为真

			if (proxyAvailable.equals("1")) {
				startProxy = true;
				useProxy = true;
				// 初始获取一个代理
				while (true) {
					IPProxy ipProxy = new IPProxy();

					ipProxy = taskComm.getIpProxy();

					if (ipProxy == null || !PageDriver.checkProxyIp(ipProxy.getIp(), ipProxy.getPort(),userName, password,validUrl)) {
						logger.warn("获取代理失败，正在重新获取");
						continue;
					}
					ipProxy.setUserName(userName);
					ipProxy.setPassword(password);
					driver = PageDriver.generateDriver(ipProxy);
					// 输出代理的ip
					break;
				}
			} else {
				driver = PageDriver.generateDriver();
				useProxy = false;
			}

			// 当前时间，用于时间限制的代理
			long getProxyTime = System.currentTimeMillis();

			for (int excutedTaskNum = 1; excutedTaskNum <= taskCount; excutedTaskNum++) {
				// 0.获取代理
				// 判断是否启用代理的条件
				if (useProxy) {
					if (proxyType.equals("amount")) {
						// 如果任务不是开始且达到未切换代理次数时，则启用ip代理为false
						if (excutedTaskNum % proxyNum != 0) {
							startProxy = false;
						} else {
							startProxy = true;
						}
					} else {
						// 如果以时间进行限定，任务没有达到指定的时间，则代理为false。初始时肯定是没有达到。
						if (getProxyTime + proxyNum * 60 * 1000 > System.currentTimeMillis()) {
							startProxy = false;
						} else {
							startProxy = true;

						}
					}

					if (startProxy) {
						// 如果代理为真，则启用代理
						if (driver != null) {
							//driver打开的先要进行关闭
							driver.quit();
						}
						while (true) {
							IPProxy ipProxy = new IPProxy();

							ipProxy = taskComm.getIpProxy();
							
							if (ipProxy == null
									|| !PageDriver.checkProxyIp(ipProxy.getIp(), ipProxy.getPort(), userName, password,validUrl)) {
								logger.warn("获取代理失败，正在重新获取");
								continue;
							}
							ipProxy.setUserName(userName);
							ipProxy.setPassword(password);
							driver = PageDriver.generateDriver(ipProxy);
							getProxyTime = System.currentTimeMillis();// 在以时间为限制的时候，需要设置当前获取到代理的时间。
							// 输出代理的ip
							break;
						}
					} else {
						if (driver == null) {
							driver = PageDriver.generateDriver();
						}
					}
				}
				
				//开始任务
				Task task = null;
				logger.debug("Get a task:" + clientID);
				try {
					task = taskComm.getATask();
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug(e.toString());
					continue;
				}
				// 任务被处理完毕后的操作
				if (task == null) {
					// System.out.println("本次处理任务 " + (excutedTaskNum - 1) + "
					// 条！");
					System.out.println("本次处理任务  " + (totalCount - 1) + " 条！");
					System.out.println("检查是否有新的任务，请输出回车！");
					if (input.readInput('\r', autoExcute * 60, true)) {// 就算没有输入回车，到达了等待输入的时间，也继续执行。
						/*
						 * Scanner sc = new Scanner(System.in); sc.nextLine();
						 */
						excutedTaskNum--;
						System.out.println("新的任务开始！");
						// excutedTaskNum = 0;
						// sc.close();
						continue;
					} else {
						System.out.println("未输入回车，任务结束！");
						break;
					}
				}
				logger.debug("Task is gotten:" + task.getTaskid());
				String platform = task.getWebsite();
				String crawlerUri = task.getCrawlerUri();
				String dataObj = task.getDataobj();
				// 3.打开页面，获取内容，或者保存网页源文件 ,内容解析,返回数据对象
				iData data = null;
				try {

					data = new PageDriver().getPageData(crawlerUri, platform, dataObj, timeout, driver);
					if(platform.equals("TaoBao")&&data.getFieldValue("errorInfo").equals("")&&
							(data.getFieldValue("monthSaleCount").equals("-")
									||data.getFieldValue("monthSaleCount").equals(""))){
						logger.debug("淘宝未获取到销量，程序暂停，请联系管理员!");

						Scanner sc = new Scanner(System.in);
						sc.nextLine();
						sc.close();
					}
					errorCount = 0;
				} catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.toString());
					if (errorCount == exceptionLimit) {
						logger.warn("访问连续出错" + exceptionLimit + "次，请重新启动程序！");
						driver.quit();
						Scanner sc = new Scanner(System.in);
						sc.nextLine();
						sc.close();
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
				// 如果连续访问超时的次数达到timeoutCountLimit次，则暂停程序。
				if (timeoutCount >= timeoutCountLimit) {
					logger.debug("连续访问超时的次数达到 " + timeoutCount + " 次，请检查网络连接，然后输入回车继续");

					Scanner sc = new Scanner(System.in);
					sc.nextLine();
					sc.close();
				}
				if (excutedTaskNum == taskCount) {
					logger.debug(taskCount + " 条任务已经完成，本次已执行总任务数为" + (totalCount +1) + "，是否继续下一次任务？（Y/N）");
					if (input.readInput('y', autoExcute * 60, true)) {
						logger.debug("新一轮任务开始！");
						excutedTaskNum = 0;
						continue;
					}

				}
				totalCount++;
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
