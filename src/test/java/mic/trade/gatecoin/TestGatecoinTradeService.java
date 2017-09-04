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
		try {
			tradeService.close();
		} catch (Exception e) {
			//Do nothing
		}
	}
	
	@Test
	public void testGetRecentTransaction() {
		List<Transaction> transactionList = tradeService.getTransactionList(TESTING_CURRENCY, false);
		assertNotNull("transactionList should not be null if success", transactionList);
		
		int listSize = transactionList.size();
		
		assertTrue("transactionList.size should > 0 if normal", listSize > 0);
		
		//Test overload method
		transactionList = tradeService.getTransactionList(TESTING_CURRENCY);
		
		//Size should be the same
		assertEquals("transactionList.size() should same as before", listSize, transactionList.size());
	}
	
	@Test
	public void testGetTransactionHistory() {
		List<Transaction> transactionList = tradeService.getTransactionList(TESTING_CURRENCY, true);
		assertNotNull("transactionList should not be null if success", transactionList);
		assertTrue("transactionList.size should > 0 if normal", transactionList.size() > 0);
	}
	
	@Test
	public void testGetMarketDepth() {
		MarketDepth marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY, 
				MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_BIDASK);
		
		assertTrue("Ask.size should > 0 if normal", marketDepth.getAskUnits().size() > 0);
		assertTrue("Bid.size should > 0 if normal", marketDepth.getBidUnits().size() > 0);
		
		//Test overload method
		marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY);
		
		assertTrue("Ask.size should > 0 if normal", marketDepth.getAskUnits().size() > 0);
		assertTrue("Bid.size should > 0 if normal", marketDepth.getBidUnits().size() > 0);
		
		//Test bidUnit Filter
		marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY, 
				MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_BID);
		
		assertTrue("Ask.size should == 0 if in bid filter mode", marketDepth.getAskUnits().size() == 0);
		assertTrue("Bid.size should > 0 if normal", marketDepth.getBidUnits().size() > 0);
		
		//Test bidUnit Filter
		marketDepth = tradeService.getMarketDepth(TESTING_CURRENCY, 
				MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_ASK);
		
		assertTrue("Ask.size should > 0 if normal", marketDepth.getAskUnits().size() > 0);
		assertTrue("Bid.size should == 0 if in bid filter mode", marketDepth.getBidUnits().size() == 0);
	}
}
