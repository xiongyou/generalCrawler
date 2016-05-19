package com.gab.mycrawler.taskComm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gab.mycrawler.data.iData;
import com.gab.mycrawler.parse.StdParse;
import com.gab.mycrawler.parse.iParse;
import com.sun.org.apache.bcel.internal.generic.NEW;

import net.sf.json.JSONObject;


public class TaskCommunication {
		//服务器地址
		private String serverUri = "";
		private String clientID="";
		private int taskdataID;
		
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
				
		//构造函数，初始化，传入服务器地址
		public TaskCommunication(String serverUri,String clientID){
			this.setServerUri(serverUri);
			this.setClientID(clientID);
		}
		public void userLogin(String userName) throws Exception {
	        
	    }
		public Task getATask() throws Exception{			
			 HttpResponse response = this.httpAccess(this.clientID, this.serverUri+"/getTask", "getTask",null);
			 Task task=new Task();
			 String notask = "All Task has been processed!";
			 int statusCode = response.getStatusLine().getStatusCode();
			 if (statusCode != HttpStatus.SC_OK) {
		           // throw new ServiceException("登陆出错");
		     }
		     String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		     //System.out.println(result+":?");
		     if(result.contains("success\":false")){
		    	 System.out.println("任务已被全部处理，请等待下次任务发布!");
		    	 return null;
		     }
		     //System.out.println(result);
		     if (result.startsWith("{")) {
		    	 System.out.println(result+":?");
		    	 JSONObject jobj = JSONObject.fromObject(result);
		         task.setCrawlerUri( jobj.getJSONObject("task").getString("uRL"));
		         task.setDataobj(jobj.getJSONObject("task").getString("dataObj"));
		         task.setWebsite( jobj.getJSONObject("task").getString("website"));
		         task.setTaskid( jobj.getJSONObject("task").getString("taskID"));
		         taskdataID = jobj.getInt("TaskDataID");
		         return task;
		     } else {
		         throw new Exception("连接失败");	            
		     }
		}	
		
		public void postData(String clientid,Task task,iData data) throws Exception{
			JSONObject jsonObject =new JSONObject();
			 String failedinfo = data.getFieldValue("errorInfo");			 
			 
			 if(failedinfo ==null){
				 failedinfo="";
			 }
			 jsonObject.put("FailedInfo", failedinfo);
			 //System.out.println("taskid:?"+task.getTaskid());
			 jsonObject.put("TaskID",task.getTaskid());
			 jsonObject.put("URL", task.getCrawlerUri());
			 jsonObject.put("Data",data.toJson());
			// System.out.println(jsonObject.toString());
			 HttpResponse response = httpAccess(clientid, serverUri+"/uploadData", "postData",jsonObject);
			 String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			 System.out.println(result);
		}		

		private HttpResponse httpAccess(String clientId, String uri,String oper,JSONObject jsonObject ) throws IOException {
	        HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(uri);
	        NameValuePair servletoper = new BasicNameValuePair("oper", oper);
	        HttpResponse response = null;
	        if(oper=="getTask"){
		        NameValuePair loginID = new BasicNameValuePair("ClientID", clientId);
		        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		        pairs.add(loginID);
		        pairs.add(servletoper);
		        post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		        response = client.execute(post);
	        }else if (oper=="postData") {
	        	//System.out.println(jsonObject.toString());
	        	NameValuePair taskid = new BasicNameValuePair("TaskID", jsonObject.getString("TaskID"));
	        	NameValuePair clientid = new BasicNameValuePair("ClientID", clientId);
	        	NameValuePair taskdataid = new BasicNameValuePair("TaskDataID", String.valueOf(taskdataID));
	        	NameValuePair failedinfo = new BasicNameValuePair("FailedInfo", jsonObject.getString("FailedInfo"));
	        	NameValuePair url = new BasicNameValuePair("URL", jsonObject.getString("URL"));
	        	NameValuePair Data = new BasicNameValuePair("Data", jsonObject.getString("Data"));
	        	//NameValuePair status = new BasicNameValuePair("Status", jsonObject.getString("Status"));
	        	jsonObject = new JSONObject();
	        	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	        	pairs.add(taskid);
	        	pairs.add(clientid);
	        	pairs.add(failedinfo);
	        	pairs.add(url);
	        	pairs.add(Data);
	        	pairs.add(taskdataid);
	        	pairs.add(servletoper);
	        	System.out.println(pairs.toString());
	        	//pairs.add(status);
	        	post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		        response = client.execute(post);
			}
	        return response;
	    }
	}


