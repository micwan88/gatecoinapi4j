package mic.trade.examples.gatecoin;

import java.math.BigDecimal;

import mic.trade.gatecoin.GatecoinCommonConst;
import mic.trade.gatecoin.GatecoinTradeService;

/**
 * This is an example class for post / cancel order by using GatecoinTradeService
 */
public class OrderExample {
	public static void main(String[] args) {
		/**
		 * Api key is generated from gatecoin web with your account and this is necessary for post order / cancel order
		 * Default constructor is enough if you only use public api such as getting transaction / market depth 
		 */
		try (GatecoinTradeService gatecoinTradeService = new GatecoinTradeService("<yourApiPublicKey>", "<yourApiPrivateKey>")) {
			//Post a bid order
			String orderID = gatecoinTradeService.postOrder(GatecoinCommonConst.TRADE_CURRENCY_ETHHKD, true, 
					new BigDecimal("1.01"), new BigDecimal("2400.05"));
			if (orderID == null || orderID.equals("")) {
				System.err.println("Cannot post an order");
				return;
			} else {
				System.out.println("Post order completed");
				
				//Cancel all open orders in the account
				String result = gatecoinTradeService.cancelOpenOrder();
				if (result != null && result.equals("OK"))
					System.out.println("Cancel order completed");
				else
					System.err.println("Cannot cancel order");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
