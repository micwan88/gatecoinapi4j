package mic.trade.gatecoin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mic.trade.bean.MarketDepth;
import mic.trade.bean.Transaction;
import mic.trade.gatecoin.json.MarketDepthJsonDeserializer;

public class TestGatecoinTradeService {
	public static final String TESTING_CURRENCY = "ETHHKD";
	
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
		List<Transaction> transactionList = tradeService.getTransactionList(TESTING_CURRENCY, false);
		assertNotNull(transactionList);
		
		int listSize = transactionList.size();
		
		assertTrue(listSize > 0);
		
		//Test overload method
		transactionList = tradeService.getTransactionList(TESTING_CURRENCY);
		
		//Size should be the same
		assertEquals(listSize, transactionList.size());
	}
	
	@Test
	public void testGetTransactionHistory() {
		List<Transaction> transactionList = tradeService.getTransactionList(TESTING_CURRENCY, true);
		assertNotNull(transactionList);
		assertTrue(transactionList.size() > 0);
	}
	
	@Test
	public void testGetMarketDepth() {
		MarketDepth marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY, 
				MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_BIDASK);
		
		assertTrue(marketDepth.getAskUnits().size() > 0);
		assertTrue(marketDepth.getBidUnits().size() > 0);
		
		//Test overload method
		marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY);
		
		assertTrue(marketDepth.getAskUnits().size() > 0);
		assertTrue(marketDepth.getBidUnits().size() > 0);
		
		//Test bidUnit Filter
		marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY, 
				MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_BID);
		
		assertTrue(marketDepth.getAskUnits().size() == 0);
		assertTrue(marketDepth.getBidUnits().size() > 0);
		
		//Test bidUnit Filter
		marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY, 
				MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_ASK);
		
		assertTrue(marketDepth.getAskUnits().size() > 0);
		assertTrue(marketDepth.getBidUnits().size() == 0);
	}
}
