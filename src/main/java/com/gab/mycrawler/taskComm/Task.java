package com.gab.mycrawler.taskComm;

public class Task {
	//需要爬取的页面地址
			private String crawlerUri="";
			//爬取的数据对象
			private String dataobj;
			//所在网站
			private String website;
			//任务ID
			private String taskid;
			

			public String getCrawlerUri() {
				return crawlerUri;
			}
			public void setCrawlerUri(String crawlerUri) {
				this.crawlerUri = crawlerUri;
			}
			public String getDataobj() {
				return dataobj;
			}
			public void setDataobj(String dataobj) {
				this.dataobj = dataobj;
			}
			public String getWebsite() {
				return website;
			}
			public void setWebsite(String website) {
				this.website = website;
			}
			
			public String getTaskid() {
				return taskid;
			}
			public void setTaskid(String taskid) {
				this.taskid = taskid;
			}
}
