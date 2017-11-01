package com.gab.mycrawler.data;

import java.util.Date;

public class IPProxy {
	
	private String ip; //IP地址
	
	private int port; //端口号
	
	private String password;
	
	private String userName;
    /*
	private int types; //类型

	private int protocal;//协议

	private String country;//国家

	private String area;//区域

	private Date updateTime;//更新时间

	private double speed;//响应速度
    */
	//private int score;//评分
	
	//private Date usedTime; //记录上一次被使用的时间




	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
