package com.gab.mycrawler.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import us.codecraft.webmagic.selector.Json;
@Entity
@Table(name = "tb_TaskList")
public class TaskList {
	/** @pdOid 产品编号 */
	@Id
	private Long productID;
	
	/** @pdOid 任务状态 */
	private byte status;
	/** @pdOid 任务开始时间 */
	private Date startTime;
	
	/** @pdOid 任务结束时间*/
	private Date endTime;
	public Long getProductID() {
		return productID;
	}
	public void setProductID(Long productID) {
		this.productID = productID;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}	

}
