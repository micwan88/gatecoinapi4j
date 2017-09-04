package mic.trade.examples.gatecoin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import mic.trade.bean.TradeMessage;
import mic.trade.bean.Transaction;
import mic.trade.gatecoin.GatecoinCommonConst;
import mic.trade.gatecoin.GatecoinPubNubCallBackInterface;
import mic.trade.gatecoin.GatecoinPubNubService;
import mic.trade.gatecoin.json.TransactionJsonDeserializer;

/**
 * This is an example class for getting transaction records (real time data) by using GatecoinPubNubService 
 */
public class GetTransactionStreamingExample {
	public static void main(String[] args) {
		//Init gson
		Type transactionListType = new TypeToken<List<Transaction>>(){}.getType();
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(transactionListType, 
	    		new TransactionJsonDeserializer(TransactionJsonDeserializer.JSON_TYPE_GATECOIN_PUBNUB_TRANSACTION, 
	    				GatecoinCommonConst.TRADE_CURRENCY_ETHHKD));
	    Gson gson = gsonBuilder.create();
		
	    //Define custom call back
		GatecoinPubNubCallBackInterface myCustomCallBack = new GatecoinPubNubCallBackInterface() {
			@Override
			public void unsubscribedCallBack(PubNub pubnub, PNStatus status) {
			}
			
			@Override
			public void reconnectCallBack(PubNub pubnub, PNStatus status) {
			}
			
			@Override
			public void msgTransactionCallBack(PubNub pubnub, PNMessageResult message) {
				List<Transaction> transactionList = gson.fromJson(message.getMessage(), transactionListType);
				System.out.println("Got transaction - " + transactionList.get(0));
			}
			
			@Override
			public void msgTickerHistoryCallBack(PubNub pubnub, PNMessageResult message) {
			}
			
			@Override
			public void msgTicker24hCallBack(PubNub pubnub, PNMessageResult message) {
			}
			
			@Override
			public void msgOrderbookCallBack(PubNub pubnub, PNMessageResult message) {
			}
			
			@Override
			public void msgMktDepthCallBack(PubNub pubnub, PNMessageResult message) {
			}
			
			@Override
			public TradeMessage getTradeMessage() {
				return null;
			}
			
			@Override
			public void destroy() {
			}
			
			@Override
			public void connectedCallBack(PubNub pubnub, PNStatus status) {
			}
		};
		
		GatecoinPubNubService gatecoinPubNubService = new GatecoinPubNubService(myCustomCallBack, 5000L);
		
		String[] channelNameArray = new String[] {
				GatecoinPubNubService.PUBNUB_CHANNEL_KEY_TRANSACTION_PREFIX + GatecoinCommonConst.TRADE_CURRENCY_ETHHKD
		};
		
		gatecoinPubNubService.subscribeService(channelNameArray);
		
		try {
			//Play the data streaming for 2 minutes
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e) {
			//Do Nothing
		}
		
		GatecoinPubNubService.closeQuietly(gatecoinPubNubService);
	}
}
