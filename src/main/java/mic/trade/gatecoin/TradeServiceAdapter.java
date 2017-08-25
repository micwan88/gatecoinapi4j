package mic.trade.gatecoin;

import java.math.BigDecimal;
import java.util.List;

import mic.trade.bean.MarketDepth;
import mic.trade.bean.Order;
import mic.trade.bean.Transaction;

public interface TradeServiceAdapter {
	public void closeService();
	
	public List<Transaction> getTransactionList(String currency);
	
	public String cancelOpenOrder(String orderID);
	
	public String postOrder(String currency, boolean isBuy, BigDecimal amount, BigDecimal atPrice);
	
	public List<Order> getOrders(String orderID);
	
	public MarketDepth getMarketDepth(String currency);
	
	public List<Transaction> getTrades(String transactionID);
}
