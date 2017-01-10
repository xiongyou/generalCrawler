package com.gab.mycrawler.config;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: noah
 * Date: 8/26/13
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestTimeout {

    public static void main(String[] args) {
        final TestTimeout testTimeout = new TestTimeout();

        boolean doClean = testTimeout.readInput();   //等待输入3秒，超时按false处理

        if (doClean) {
            //cleanDb();
            System.out.println("The database was cleaned!");
        } else {
            System.out.println("The clean operation was ignored.");
        }

    }

    public int readInputStreamWithTimeout(InputStream is, byte[] buf, int timeoutMillis)
            throws IOException {
        int bufferOffset = 0;    //读取数据buf偏移量
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;//计算过期时间

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
        return bufferOffset;
    }


    public boolean readInput() {

        System.out.println("Do you want to clean and initialize the database?(y/n)");
        boolean doClean = false;


        byte[] inputData = new byte[1];//设置为1,只读第一个字符

        int readLength = 0;
        try {
            readLength = readInputStreamWithTimeout(System.in, inputData, 10000);////等待3秒，可以修改为更长的时间来观查效果，如6000
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (readLength > 0) {
            switch ((char) inputData[0]) {  //读取按键
                case 'y':
                case 'Y': {
                    doClean = true;  //设置返回为true
                    break;
                }
            }
        }

        return doClean;
    }
}