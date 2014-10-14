/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年3月26日
 */
package vo;

import java.math.BigDecimal;

/**
 * @ClassName: PayInfoVO
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014年3月26日 上午11:19:50
 * @author RenYouchao
 * 
 */
public class PayInfoVO {
	
	/**令牌**/
	private String token;
	/**总共需要多少秒**/
	private Long totalTime;
	/**账户余额**/
	private BigDecimal balance;
	/**专家资费**/
	private Long expenses;
	
	public PayInfoVO(){
		super();
	}
	/**
	 * @return the totalTime
	 */
	public Long getTotalTime() {
		return totalTime;
	}
	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}
	/**
	 * @return the expenses
	 */
	public Long getExpenses() {
		return expenses;
	}
	/**
	 * @param totalTime the totalTime to set
	 */
	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	/**
	 * @param expenses the expenses to set
	 */
	public void setExpenses(Long expenses) {
		this.expenses = expenses;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
