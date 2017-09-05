package mic.trade.examples.gatecoin;

import java.util.List;

import mic.trade.bean.Transaction;
import mic.trade.gatecoin.GatecoinCommonConst;
import mic.trade.gatecoin.GatecoinTradeService;

/**
 * This is an example class for getting transaction records by using GatecoinTradeService
 */
public class GetTransactionExample {
	public static void main(String[] args) {
		//No need supply api key for all public api
		try (GatecoinTradeService gatecoinTradeService = new GatecoinTradeService()) {
			//Get recent transaction
			List<Transaction> transactionList = gatecoinTradeService.getTransactionList(GatecoinCommonConst.TRADE_CURRENCY_ETHHKD);
			if (transactionList == null) {
				System.err.println("Cannot get transaction");
				return;
			}
			for (Transaction transaction : transactionList)
				System.out.println("TransactionID: " + transaction.gettID() + " Time: " + transaction.gettTime() + " Price: " + transaction.getPrice());
			
			//Get transaction history
			transactionList = gatecoinTradeService.getTransactionList(GatecoinCommonConst.TRADE_CURRENCY_ETHHKD, true);
			if (transactionList == null) {
				System.err.println("Cannot get transaction history");
				return;
			}
			System.out.println("History size: " + transactionList.size());
		}
	}
}
