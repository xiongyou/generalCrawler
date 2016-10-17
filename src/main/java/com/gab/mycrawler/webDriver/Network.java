package com.gab.mycrawler.webDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

/** 
 *  
 * 
 *  */
@SuppressWarnings("unused")
public class Network {

	// 判断网络状态
	public static boolean isConnect() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		
			Process process = runtime.exec("ping " + "www.baidu.com -n 1");
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
				// System.out.println("返回值为:"+line);
			}
			is.close();
			isr.close();
			br.close();

			if (null != sb && !sb.toString().equals("")) {
				
				if (sb.toString().indexOf("TTL") > 0) {
					return true;
				} else {
					// 网络不畅通
					System.out.println("网络连接失败，请检查网络连接！");
					return false;
				}
			}
			return false;
		
	}
}