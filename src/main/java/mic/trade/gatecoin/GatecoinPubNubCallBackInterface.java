package mic.trade.gatecoin;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import mic.trade.bean.TradeMessage;

public interface GatecoinPubNubCallBackInterface {
	public void connectedCallBack(PubNub pubnub, PNStatus status);
	
	public void reconnectCallBack(PubNub pubnub, PNStatus status);
	
	public void unsubscribedCallBack(PubNub pubnub, PNStatus status);
	
	public void msgTransactionCallBack(PubNub pubnub, PNMessageResult message);
	
	public void msgOrderbookCallBack(PubNub pubnub, PNMessageResult message);
	
	public void msgMktDepthCallBack(PubNub pubnub, PNMessageResult message);
	
	public void msgTicker24hCallBack(PubNub pubnub, PNMessageResult message);
	
	public void msgTickerHistoryCallBack(PubNub pubnub, PNMessageResult message);
	
	public TradeMessage getMessageBus();
	
	public void destroy();
}
