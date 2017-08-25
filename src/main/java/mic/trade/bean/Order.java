package mic.trade.bean;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
	private String orderID = null;
	private String currency = null;
	private boolean isBid = false;
	private BigDecimal price = null;
	private BigDecimal amount = null;
	private BigDecimal remainAmount = null;
	private int status = 0;
	private String statusDesc = null;
	private Date orderTime = null;

	/**
	 * @param orderID
	 * @param currency
	 * @param isBid
	 * @param price
	 * @param amount
	 * @param remainAmount
	 * @param status
	 */
	public Order(String orderID, String currency, boolean isBid, BigDecimal price, BigDecimal amount,
			BigDecimal remainAmount, int status, Date orderTime) {
		this(orderID, currency, isBid, price, amount, remainAmount, status, null, orderTime);
	}

	/**
	 * @param orderID
	 * @param currency
	 * @param isBid
	 * @param price
	 * @param amount
	 * @param remainAmount
	 * @param status
	 * @param statusDesc
	 * @param orderTime
	 */
	public Order(String orderID, String currency, boolean isBid, BigDecimal price, BigDecimal amount,
			BigDecimal remainAmount, int status, String statusDesc, Date orderTime) {
		this.orderID = orderID;
		this.currency = currency;
		this.isBid = isBid;
		this.price = price;
		this.amount = amount;
		this.remainAmount = remainAmount;
		this.status = status;
		this.statusDesc = statusDesc;
		this.orderTime = orderTime;
	}

	/**
	 * @return the orderID
	 */
	public String getOrderID() {
		return orderID;
	}

	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the isBid
	 */
	public boolean isBid() {
		return isBid;
	}

	/**
	 * @param isBid the isBid to set
	 */
	public void setBid(boolean isBid) {
		this.isBid = isBid;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the remainAmount
	 */
	public BigDecimal getRemainAmount() {
		return remainAmount;
	}

	/**
	 * @param remainAmount the remainAmount to set
	 */
	public void setRemainAmount(BigDecimal remainAmount) {
		this.remainAmount = remainAmount;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the statusDesc
	 */
	public String getStatusDesc() {
		return statusDesc;
	}

	/**
	 * @param statusDesc the statusDesc to set
	 */
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	/**
	 * @return the orderTime
	 */
	public Date getOrderTime() {
		return orderTime;
	}

	/**
	 * @param orderTime the orderTime to set
	 */
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	
	public String toString() {
		return "orderID: " + orderID;
	}
}
