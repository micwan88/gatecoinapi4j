package mic.trade.bean;

public class TradeMessage {
	private String errMsg = null;

	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param errMsg the errMsg to set
	 */
	public synchronized void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
