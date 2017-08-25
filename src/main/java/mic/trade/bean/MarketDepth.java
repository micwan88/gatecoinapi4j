package mic.trade.bean;

import java.util.List;

public class MarketDepth {
	private List<Unit> askUnits = null;
	private List<Unit> bidUnits = null;

	/**
	 * @param askUnits
	 * @param bidUnits
	 */
	public MarketDepth(List<Unit> askUnits, List<Unit> bidUnits) {
		this.askUnits = askUnits;
		this.bidUnits = bidUnits;
	}

	/**
	 * @return the askUnits
	 */
	public List<Unit> getAskUnits() {
		return askUnits;
	}

	/**
	 * @param askUnits the askUnits to set
	 */
	public void setAskUnits(List<Unit> askUnits) {
		this.askUnits = askUnits;
	}

	/**
	 * @return the bidUnits
	 */
	public List<Unit> getBidUnits() {
		return bidUnits;
	}

	/**
	 * @param bidUnits the bidUnits to set
	 */
	public void setBidUnits(List<Unit> bidUnits) {
		this.bidUnits = bidUnits;
	}
	
	public String toString() {
		return "ask count:" + String.valueOf(askUnits==null?0:askUnits.size()) + " bid count:" + String.valueOf(bidUnits==null?0:bidUnits.size());
	}
}
