package mic.trade.examples.gatecoin;

import java.util.List;

import mic.trade.bean.MarketDepth;
import mic.trade.bean.Unit;
import mic.trade.gatecoin.GatecoinCommonConst;
import mic.trade.gatecoin.GatecoinTradeService;
import mic.trade.gatecoin.json.MarketDepthJsonDeserializer;

/**
 * This is an example class for getting market depth records (order book records) by using GatecoinTradeService
 */
public class GetMarketDepthExample {
	public static void main(String[] args) {
		//No need supply api key for all public api
		try (GatecoinTradeService gatecoinTradeService = new GatecoinTradeService()) {
			//Get market depth for both bid/ask
			MarketDepth marketDepth = gatecoinTradeService.getMarketDepth(GatecoinCommonConst.TRADE_CURRENCY_ETHHKD);
			if (marketDepth == null) {
				System.err.println("Cannot get market depth");
				return;
			}
			List<Unit> unitList = marketDepth.getBidUnits();
			for (Unit marketUnit : unitList)
				System.out.println("Bid Market Unit - " + marketUnit);
			unitList = marketDepth.getAskUnits();
			for (Unit marketUnit : unitList)
				System.out.println("Ask Market Unit - " + marketUnit);
			
			//Get market depth for bid only
			marketDepth = gatecoinTradeService.getMarketDepth(GatecoinCommonConst.TRADE_CURRENCY_ETHHKD, 
					MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_BID);
			if (marketDepth == null) {
				System.err.println("Cannot get market depth");
				return;
			}
			unitList = marketDepth.getBidUnits();
			for (Unit marketUnit : unitList)
				System.out.println("Bid Market Unit - " + marketUnit);
		}
	}
}
