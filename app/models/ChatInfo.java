/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-14
 */
package models;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 *
 * @ClassName: ChatInfo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-1-14 下午3:47:33
 * @author YangXuelin
 * 
 */
public class ChatInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户id
	 */
	private Long hostId;
	
	/**
	 * 专家用户id
	 */
	private Long guestId;

	/**
	 * 用户可以通话时长（秒）
	 */
	private int timeLimit;
	
	/**
	 * 专家收费标准
	 */
	private float expenses;
	
	/**
	 * 用户实际通话时长
	 */
	private Long duration = 0l;
	
	
	private BigDecimal balance;
	
	public ChatInfo(Long hostId, Long guestId, int timeLimit, float expenses,BigDecimal balance) {
		this.hostId = hostId;
		this.guestId = guestId;
		this.timeLimit = timeLimit;
		this.expenses = expenses;
		this.balance = balance;
	}
	
	public ChatInfo() {
		
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}
	
	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public float getExpenses() {
		return expenses;
	}

	public void setExpenses(float expenses) {
		this.expenses = expenses;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "hostId=" + this.hostId + ", guestId=" + this.guestId + ", timeLimit=" + this.timeLimit;
	}

}
