package mic.trade.bean;

import java.math.BigDecimal;

public class Unit {
	private BigDecimal price = null;
	private BigDecimal volume = null;

	/**
	 * @param price
	 * @param volume
	 */
	public Unit(BigDecimal price, BigDecimal volume) {
		this.price = price;
		this.volume = volume;
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
	 * @return the volume
	 */
	public BigDecimal getVolume() {
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	
	public String toString() {
		return "Price:" + price + " Volume:" + volume;
	}
}
