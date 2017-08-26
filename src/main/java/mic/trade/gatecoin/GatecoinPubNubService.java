package mic.trade.gatecoin;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import mic.trade.bean.TradeMessage;

public class GatecoinPubNubService extends SubscribeCallback {
	public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-ee68e350-4ef7-11e6-bfbb-02ee2ddab7fe";
	public static final String PUBNUB_CHANNEL_KEY_TRANSACTION_PREFIX = "trade.";
	public static final String PUBNUB_CHANNEL_KEY_ORDERBOOK_PREFIX = "order.";
	public static final String PUBNUB_CHANNEL_KEY_MKTDEPTH_PREFIX = "marketdepth.";
	public static final String PUBNUB_CHANNEL_KEY_TICKER24H_PREFIX = "ticker_24h.";
	public static final String PUBNUB_CHANNEL_KEY_TICKER_HIST_PREFIX = "hist_ticker.";
	
	private static final Logger myLogger = LogManager.getLogger(GatecoinPubNubService.class);
	
	private long errorSleepTime = 5000L;
	private PubNub pubNubClient = null;
	private GatecoinPubNubCallBackInterface pubNubCallBackService = null;
	private TradeMessage tradeMessage = null;

	public void setErrorSleepTime(long errorSleepTime) {
		myLogger.debug("setErrorSleepTime for pubnubservice: {}", errorSleepTime);
		this.errorSleepTime = errorSleepTime;
	}

	public TradeMessage getTradeMessage() {
		return tradeMessage;
	}
	
	public GatecoinPubNubService(GatecoinPubNubCallBackInterface pubNubCallBackService, long errorSleepTime) {
		this(pubNubCallBackService, errorSleepTime, new TradeMessage());
	}
	
	public GatecoinPubNubService(GatecoinPubNubCallBackInterface pubNubCallBackService, long errorSleepTime, TradeMessage tradeMessage) {
		myLogger.debug("Init GatecoinPubNubService");
		
		this.pubNubCallBackService = pubNubCallBackService;
		this.tradeMessage = tradeMessage;	
		this.errorSleepTime = errorSleepTime;
		
		PNConfiguration pnConfiguration = new PNConfiguration();
	    pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE_KEY);
	    pnConfiguration.setSecure(false);
	    pnConfiguration.setPresenceTimeout(300);
	    pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
	     
	    pubNubClient = new PubNub(pnConfiguration);
	    pubNubClient.addListener(this);
	}
	
	public void subscribeService(String[] channelNameArray) {
		myLogger.debug("subscribeService: {}", Arrays.toString(channelNameArray));
		
		pubNubClient.subscribe().channels(Arrays.asList(channelNameArray)).execute();
	}
	
	public void unsubscribeAllService() {
		myLogger.debug("unsubscribeAllService");
		
		pubNubClient.unsubscribeAll();
	}
	
	public void closeService() {
		myLogger.debug("closeService");
		
		pubNubCallBackService.destroy();
		
		if (pubNubClient.getSubscribedChannels().size() > 0) {
			unsubscribeAllService();
			
			//Destroy is in event after un-subscribed
			//pubNubClient.destroy();
		} else
			pubNubClient.destroy();
	}
	
	private void sleepWhileError() {
		try {
			Thread.sleep(errorSleepTime);
		} catch (InterruptedException e) {
			//Do Nothing
		}
	}

	@Override
	public void status(PubNub pubnub, PNStatus status) {
		//Try not log status if heartbeat success
		//myLogger.debug(status);
		
		if (status.getOperation() != null) {
			switch (status.getOperation()) {
				case PNSubscribeOperation:
					myLogger.debug(status);
					switch (status.getCategory()) {
						case PNConnectedCategory:
							// this is expected for a subscribe, this means there is no error or issue whatsoever
							myLogger.debug("Connected");
							
							pubNubCallBackService.connectedCallBack(pubnub, status);
							break;
							
						case PNReconnectedCategory:
							// this usually occurs if subscribe temporarily fails but reconnects. This means
							// there was an error but there is no longer any issue
							myLogger.debug("Reconnected");
							break;
							
						case PNDisconnectedCategory:
							// this is the expected category for an unsubscribe. This means there
							// was no error in unsubscribing from everything
							myLogger.debug("Unsubscribed");
							break;
							
						case PNUnexpectedDisconnectCategory:
							myLogger.error("Disconnected ...");
							break;
							
						case PNTimeoutCategory:
							myLogger.error("Timeout ...");
							break;
							
						default:
							if (status.isError()) {
								myLogger.error("Unknown status error: {}", status);
								tradeMessage.setErrMsg(status.toString());
							}
							break;
					}
					break;
					
				case PNUnsubscribeOperation:
					myLogger.debug(status);
					
					pubNubCallBackService.unsubscribedCallBack(pubnub, status);
					pubnub.destroy();
					break;
					
				case PNHeartbeatOperation:
					if (status.isError()) {
						myLogger.error("Heartbeat failed: {}", status);
						if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
							myLogger.error("Heartbeat return disconnected");
							pubnub.disconnect();
							myLogger.error("Sleep {} ms ...", errorSleepTime);
							sleepWhileError();
							myLogger.error("Try to reconnect ...");
							pubnub.reconnect();
						}
					}
					break;
					
				default:
					myLogger.error("Unknown operation type: {}", status);
					break;
			}
		} else {
			myLogger.debug(status);
			myLogger.debug("Status Operation is null");
			
			if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
				pubNubCallBackService.reconnectCallBack(pubnub, status);
			}
		}
	}

	/**
	 * message is a synchronize callback
	 */
	@Override
	public void message(PubNub pubnub, PNMessageResult message) {
		myLogger.debug(message);
		
		String channelName = null;
		if (message.getChannel() != null)
			channelName = message.getChannel();
		else if (message.getSubscription() != null)
			channelName = message.getSubscription();
		
		if (channelName != null) {
			if (channelName.startsWith(PUBNUB_CHANNEL_KEY_TRANSACTION_PREFIX)) {
				pubNubCallBackService.msgTransactionCallBack(pubnub, message);
			} else if (channelName.startsWith(PUBNUB_CHANNEL_KEY_ORDERBOOK_PREFIX)) {
				pubNubCallBackService.msgOrderbookCallBack(pubnub, message);
			} else if (channelName.startsWith(PUBNUB_CHANNEL_KEY_MKTDEPTH_PREFIX)) {
				pubNubCallBackService.msgMktDepthCallBack(pubnub, message);
			} else if (channelName.startsWith(PUBNUB_CHANNEL_KEY_TICKER24H_PREFIX)) {
				pubNubCallBackService.msgTicker24hCallBack(pubnub, message);
			} else if (channelName.startsWith(PUBNUB_CHANNEL_KEY_TICKER_HIST_PREFIX)) {
				pubNubCallBackService.msgTickerHistoryCallBack(pubnub, message);
			} else
				myLogger.error("Unknown channel: {}", channelName);
			return;
		}
		
		myLogger.error("Unknown msg: {}", message);
	}

	@Override
	public void presence(PubNub pubnub, PNPresenceEventResult presence) {
		// TODO Auto-generated method stub
	}
}
