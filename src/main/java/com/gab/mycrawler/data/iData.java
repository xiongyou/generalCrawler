package com.gab.mycrawler.data;



import java.util.List;

import us.codecraft.webmagic.selector.Json;

public interface iData {	
	/** @pdOid 6a63aca9-07d6-4430-aea6-f5519a312b52 */
	String toJson() throws Exception;
	   /** @pdOid 69cd8bbd-71eb-4eb5-9b35-b2fbed85fc46 */  
	   boolean fromString(String datastr);
	   String toString(String platform,String dataObjName) throws Exception;
	   List<String> getFieldNames() throws Exception;
	   String getFieldValue(String fieldName);
	   void setFieldValue(String fieldName, String Value);
	   
	   List<String> getErrorInfos();
	   String getErrorInfo(String errorInfo);
	   void setErrorInfo(String errorInfo,String value);
	   

	
	
}
