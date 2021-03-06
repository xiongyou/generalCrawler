package com.gab.mycrawler.taskComm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gab.mycrawler.config.ProjectPortal;
import com.gab.mycrawler.config.StdConfig;
import com.gab.mycrawler.config.iConfig;
import com.gab.mycrawler.data.IPProxy;
import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.parse.StdParse;
import com.gab.mycrawler.parse.iParse;
import com.sun.org.apache.bcel.internal.generic.NEW;

import net.sf.json.JSONObject;

public class TaskCommunication {
	// 服务器地址
	private String serverUri = "";
	private String clientID = "";
	private int taskdataID;
	private int productInnerId;
	// private String keyword="";
	public static Logger log = Logger.getLogger(TaskCommunication.class);

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getServerUri() {
		return serverUri;
	}

	public void setServerUri(String serverUri) {
		this.serverUri = serverUri;

	}

	// 构造函数，初始化，传入服务器地址
	public TaskCommunication(String serverUri, String clientID) {
		this.setServerUri(serverUri);
		this.setClientID(clientID);
	}

	public void userLogin(String userName) throws Exception {

	}

	public boolean validVersion(int curVersion) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(this.serverUri + "/task/version");

		HttpResponse response = null;
		NameValuePair version = new BasicNameValuePair("version", String.valueOf(curVersion));
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(version);
		post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		int i = 0;
		iConfig config = new StdConfig("start.xml");
		int serverPause = 1;
		try {
			serverPause = Integer.parseInt(config.getXpathText("config/serverPause/text()"));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) {
			try {
				response = client.execute(post);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				ProjectPortal.logger.debug(e.toString());
				i++;
				System.out.println("与服务器连接异常，" + serverPause + "分钟后将与服务器自动连接【次数： " + i + " 】");
				Thread.sleep(1000 * 60 * serverPause);
			}
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			System.out.println("连接错误："+statusCode);
			return false;
		}
		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);		
		// System.out.println(result);
		if (result.startsWith("{")) {
			System.out.println(result);
			try {
				JSONObject jobj = JSONObject.fromObject(result);
				log.debug(jobj.getString("msg"));
				return jobj.getBoolean("success");
			} catch (Exception e) {
				log.warn(e.toString());
				return false;
			}			
		} else {
			log.warn(result);
			throw new Exception("数据格式异常");
		}
	}
	
	public IPProxy getIpProxy() throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(this.serverUri+"/Proxy/getProxy");
		IPProxy ipProxy=new IPProxy();
		

		HttpResponse response = null;
		//post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		int i = 0;
		iConfig config = new StdConfig("start.xml");
		int serverPause = 1;
		try {
			serverPause = Integer.parseInt(config.getXpathText("config/serverPause/text()"));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) {
			try {
				response = client.execute(post);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				ProjectPortal.logger.debug(e.toString());
				i++;
				System.out.println("与服务器连接异常，" + serverPause + "分钟后将与服务器自动连接【次数： " + i + " 】");
				Thread.sleep(1000 * 60 * serverPause);
			}
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			System.out.println("连接错误："+statusCode);
			return null;
		}
		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);		
		// System.out.println(result);
		if (result.startsWith("{")) {
			System.out.println(result);
			try {
				JSONObject jobj = JSONObject.fromObject(result);
				ipProxy.setIp(jobj.getString("ip"));
				ipProxy.setPort(jobj.getInt("port"));
				
				return ipProxy;
			} catch (Exception e) {
				log.warn(e.toString());
				return null;
			}			
		} else {
			log.warn(result);
			return null;		}
	}

	public Task getATask() throws Exception {
		HttpResponse response = this.httpAccess(this.clientID, this.serverUri + "/task/getTask", "getTask", null);
		Task task = new Task();
		String notask = "All Task has been processed!";
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			// throw new ServiceException("登陆出错");
		}
		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		// System.out.println(result+":?");
		if (result.contains("success\":false")) {
			System.out.println("任务已被全部处理，请等待下次任务发布!");
			return null;
		}
		// System.out.println(result);
		if (result.startsWith("{")) {
			System.out.println(result + ":?");
			try {
				JSONObject jobj = JSONObject.fromObject(result);				
				task.setCrawlerUri(jobj.getJSONObject("task").getString("uRL"));
				task.setDataobj(jobj.getJSONObject("task").getString("dataObj"));
				task.setWebsite(jobj.getJSONObject("task").getString("website"));
				task.setTaskid(jobj.getJSONObject("task").getString("taskID"));
				taskdataID = jobj.getInt("TaskDataID");
				productInnerId = jobj.getJSONObject("task").getInt("productInnerId");
				if(jobj.getJSONObject("task").get("keyword")==null){
					task.setKeyword("");
				}
				else{
					task.setKeyword(jobj.getJSONObject("task").getString("keyword"));
				}
			} catch (Exception e) {
				log.warn(e.toString());
			}
			return task;
		} else {
			
			throw new Exception("数据格式异常");
		}
	}

	public void postData(String clientid, Task task, iData data) throws Exception {
		JSONObject jsonObject = new JSONObject();
		String failedinfo = data.getFieldValue("errorInfo");

		if (failedinfo == null) {
			failedinfo = "";
		}
		jsonObject.put("FailedInfo", failedinfo);
		// System.out.println("taskid:?"+task.getTaskid());
		jsonObject.put("TaskID", task.getTaskid());
		jsonObject.put("URL", task.getCrawlerUri());
		jsonObject.put("Data", data.toJson());
		jsonObject.put("keyword", task.getKeyword());
		jsonObject.put("website", task.getWebsite());
		// System.out.println(jsonObject.toString());
		HttpResponse response = httpAccess(clientid, serverUri + "/task/uploadData", "postData", jsonObject);
		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		// 对失败信息“访问超时”做处理。
		if (failedinfo == "访问超时") {
			ProjectPortal.timeoutCount++;
		} else {
			ProjectPortal.timeoutCount = 0;
		}
		// System.out.println(result);
	}

	private HttpResponse httpAccess(String clientId, String uri, String oper, JSONObject jsonObject)
			throws IOException, InterruptedException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(uri);
		NameValuePair servletoper = new BasicNameValuePair("oper", oper);
		HttpResponse response = null;
		iConfig config = new StdConfig("start.xml");
		int serverPause = 1;
		try {
			serverPause = Integer.parseInt(config.getXpathText("config/serverPause/text()"));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (oper == "getTask") {
			NameValuePair loginID = new BasicNameValuePair("ClientID", clientId);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(loginID);
			pairs.add(servletoper);
			post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			int i = 0;

			while (true) {
				try {
					response = client.execute(post);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.toString());
					i++;
					System.out.println("与服务器连接异常，" + serverPause + "分钟后将与服务器自动连接【次数： " + i + " 】");
					Thread.sleep(1000 * 60 * serverPause);
				}
			}
		}
		else if (oper=="proxy"){
			NameValuePair loginID = new BasicNameValuePair("ClientID", clientId);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(loginID);
			pairs.add(servletoper);
			post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			int i = 0;

			while (true) {
				try {
					response = client.execute(post);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.toString());
					i++;
					System.out.println("与服务器连接异常，" + serverPause + "分钟后将与服务器自动连接【次数： " + i + " 】");
					Thread.sleep(1000 * 60 * serverPause);
				}
			}
		}
		else if (oper == "postData") {
			// System.out.println(jsonObject.toString());
			NameValuePair taskid = new BasicNameValuePair("TaskID", jsonObject.getString("TaskID"));
			NameValuePair clientid = new BasicNameValuePair("ClientID", clientId);
			NameValuePair taskdataid = new BasicNameValuePair("TaskDataID", String.valueOf(taskdataID));
			NameValuePair failedinfo = new BasicNameValuePair("FailedInfo", jsonObject.getString("FailedInfo"));
			NameValuePair url = new BasicNameValuePair("URL", jsonObject.getString("URL"));
			NameValuePair keyword = new BasicNameValuePair("keyword", jsonObject.getString("keyword"));
			NameValuePair website = new BasicNameValuePair("website", jsonObject.getString("website"));
			NameValuePair Data = new BasicNameValuePair("Data", jsonObject.getString("Data"));
			NameValuePair productInnerId = new BasicNameValuePair("productInnerId",
					String.valueOf(this.productInnerId));
			// NameValuePair status = new BasicNameValuePair("Status",
			// jsonObject.getString("Status"));
			jsonObject = new JSONObject();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(taskid);
			pairs.add(clientid);
			pairs.add(failedinfo);
			pairs.add(url);
			pairs.add(keyword);
			pairs.add(website);
			pairs.add(Data);
			pairs.add(taskdataid);
			pairs.add(productInnerId);
			pairs.add(servletoper);

			System.out.println(pairs.toString());
			// pairs.add(status);
			post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			int i = 0;
			while (true) {
				try {
					response = client.execute(post);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.toString());
					i++;
					System.out.println("与服务器连接异常，" + serverPause + "分钟后将与服务器自动连接【次数： " + i + " 】");
					Thread.sleep(1000 * 60 * serverPause);
				}
			}
		}
		return response;
	}

	public Task getAProxyTask() throws Exception  {
		HttpResponse response = this.httpAccess(this.clientID, this.serverUri + "/task/getProxyTask", "getTask", null);
		Task task = new Task();
		String notask = "All Task has been processed!";
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			// throw new ServiceException("登陆出错");
		}
		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		// System.out.println(result+":?");
		if (result.contains("success\":false")) {
			System.out.println("任务已被全部处理，请等待下次任务发布!");
			return null;
		}
		// System.out.println(result);
		if (result.startsWith("{")) {
			System.out.println(result + ":?");
			try {
				JSONObject jobj = JSONObject.fromObject(result);				
				task.setCrawlerUri(jobj.getJSONObject("task").getString("uRL"));
				task.setDataobj(jobj.getJSONObject("task").getString("dataObj"));
				task.setWebsite(jobj.getJSONObject("task").getString("website"));
				task.setTaskid(jobj.getJSONObject("task").getString("taskID"));
				taskdataID = jobj.getInt("TaskDataID");
				productInnerId = jobj.getJSONObject("task").getInt("productInnerId");
				if(jobj.getJSONObject("task").get("keyword")==null){
					task.setKeyword("");
				}
				else{
					task.setKeyword(jobj.getJSONObject("task").getString("keyword"));
				}
			} catch (Exception e) {
				log.warn(e.toString());
			}
			return task;
		} else {
			
			throw new Exception("数据格式异常");
		}
	}

}
