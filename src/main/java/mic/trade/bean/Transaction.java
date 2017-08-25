package mic.trade.bean;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
	private long tID = 0;
	private Date tTime = null;
	private String currency = null;
	private String askOrderID = null;
	private String bidOrderID = null;
	private BigDecimal price = null;
	private BigDecimal quantity = null;
	private Boolean isBid = null;

	public Transaction(long tID, Date tTime, String currency, String askOrderID, String bidOrderID, BigDecimal price,
			BigDecimal quantity, Boolean isBid) {
		this.tID = tID;
		this.tTime = tTime;
		this.currency = currency;
		this.askOrderID = askOrderID;
		this.bidOrderID = bidOrderID;
		this.price = price;
		this.quantity = quantity;
		this.isBid = isBid;
	}

	public Transaction(long tID, Date tTime, String currency, String askOrderID, String bidOrderID,
			BigDecimal price, BigDecimal quantity) {
		this(tID, tTime, currency, askOrderID, bidOrderID, price, quantity, null);
	}

	/**
	 * @return the tID
	 */
	public long gettID() {
		return tID;
	}

	/**
	 * @param tID the tID to set
	 */
	public void settID(long tID) {
		this.tID = tID;
	}

	/**
	 * @return the tTime
	 */
	public Date gettTime() {
		return tTime;
	}

	/**
	 * @param tTime the tTime to set
	 */
	public void settTime(Date tTime) {
		this.tTime = tTime;
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
	 * @return the askOrderID
	 */
	public String getAskOrderID() {
		return askOrderID;
	}

	/**
	 * @param askOrderID the askOrderID to set
	 */
	public void setAskOrderID(String askOrderID) {
		this.askOrderID = askOrderID;
	}

	/**
	 * @return the bidOrderID
	 */
	public String getBidOrderID() {
		return bidOrderID;
	}

	/**
	 * @param bidOrderID the bidOrderID to set
	 */
	public void setBidOrderID(String bidOrderID) {
		this.bidOrderID = bidOrderID;
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
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Boolean getIsBid() {
		return isBid;
	}

	public void setIsBid(Boolean isBid) {
		this.isBid = isBid;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return "tID:" + tID + " tTime:" + dateFormat.format(tTime) + " currency:" + currency
				+ " askOrderID:" + askOrderID + " bidOrderID:" + bidOrderID + " price:" + price
				+ " quantity:" + quantity + " isBid:" + isBid;
	}
}
