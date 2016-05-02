package com.gab.mycrawler.parse;

import com.gab.mycrawler.data.iData;

public interface iParse {
	/** @param content 
	    * @param dataName
	 * @throws Exception 
	    * @pdOid e343d5e8-b15b-4dac-80e1-b18285749080 */
	   iData parseData( String dataName) throws Exception;
	   /**
	    * 根据平台，数据对象解析内容
	 * @param platform
	 * @param dataObj
	 * @param Content
	 * @return
	 * @throws Exception
	 */
	iData parseData(String platform,String dataObj,String Content) throws Exception;
}
