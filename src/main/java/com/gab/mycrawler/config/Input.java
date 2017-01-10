package com.gab.mycrawler.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Input {
	public boolean readInputStreamWithTimeout(String checkStr,int timeoutMillis) throws IOException{
		boolean result=false;
		int bufferOffset = 0;    //读取数据buf偏移量
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;//计算过期时间        
        InputStream is=System.in;
        byte[] buf=new byte[1];;
        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < buf.length) { //时间到，buf被写满，或者到读取到内容时
            int readLength = Math.min(is.available(), buf.length - bufferOffset); //按可读数据长与buf长度，选择读取长度

            int readResult = is.read(buf, bufferOffset, readLength);

            if (readResult == -1) {//流结束直接结束
                break;
            }

            bufferOffset += readResult;

            if (readResult > 0) {   //读取到内容结束循环
                break;
            }

            try {
                Thread.sleep(10);          //等待10ms读取，减小cpu占用
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (bufferOffset==0)
        	return true;
        String  t = new String(buf);
        if(t.contains(checkStr)){
        	return true;
        }
		return result;
	}
	
	 public int readInputStreamWithTimeout(InputStream is, byte[] buf, int timeoutMillis)
	            throws IOException {
	        int bufferOffset = 0;    //读取数据buf偏移量
	        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;//计算过期时间

	        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < buf.length) { //时间到，buf被写满，或者到读取到内容时
	            int readLength = Math.min(is.available(), buf.length - bufferOffset); //按可读数据长与buf长度，选择读取长度

	            int readResult = is.read(buf, bufferOffset, readLength); 
	            is.skip(is.available());
	            
	            if (readResult == -1) {//流结束直接结束
	                break;
	            }

	            bufferOffset += readResult;

	            if (readResult > 0) {   //读取到内容结束循环
	                break;
	            }

	            try {
	                Thread.sleep(10);          //等待10ms读取，减小cpu占用
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        
	        return bufferOffset;
	    }


	    /**
	     * @param compChar 需要比较的字符，都用小写,回车是\r
	     * @param timeout 超时时间，单位S
	     * @param timeoutIgnore 如果超时，返回什么？
	     * @return
	     */	   
	    public boolean readInput(char compChar,int timeout,boolean timeoutIgnore) {

	        //System.out.println("Do you want to clean and initialize the database?(y/n)");
	        boolean doClean = false;


	        byte[] inputData = new byte[1];//设置为1,只读第一个字符
	        
	        //Scanner sc=new Scanner(System.in);
	        //sc.
	        int readLength = -1;
	        try {
	        	
	            readLength = readInputStreamWithTimeout(System.in, inputData, timeout*1000);////等待timeout秒，可以修改为更长的时间来观查效果，如6000
	           // System.in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        if (readLength > 0) {
	        	//System.out.println((char) inputData[0]);
	        	if(compChar==Character.toLowerCase((char) inputData[0])){
	        		doClean = true;  //设置返回为true                   
	        	}
	        		/*
	            switch ((char) inputData[0]) {  //读取按键
	                case 'y':
	                case 'Y': 
	                case '\r':
	                case '\n':{
	                    doClean = true;  //设置返回为true
	                    break;
	                }
	            }*/
	        }
	        //到时间未输入，则返回timeoutIgnore
	        else if(readLength==0){
	        	return timeoutIgnore;
	        }
	        return doClean;
	    }
}
