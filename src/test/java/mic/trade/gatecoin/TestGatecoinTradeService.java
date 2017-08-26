package mic.trade.gatecoin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mic.trade.bean.Transaction;

public class TestGatecoinTradeService {
	private GatecoinTradeService tradeService = null;
	
	@Before
	public void beforeTest() {
		tradeService = new GatecoinTradeService();
	}
	
	@After
	public void afterTest() {
		tradeService.closeService();
	}
	
	@Test
	public void testGetRecentTransaction() {
		List<Transaction> transactionList = tradeService.getTransactionList("ETHHKD");
		assertNotNull(transactionList);
		assertTrue(transactionList.size() > 0);
	}
}
