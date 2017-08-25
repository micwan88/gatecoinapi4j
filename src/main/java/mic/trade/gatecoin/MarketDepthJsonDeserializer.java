package mic.trade.gatecoin;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mic.trade.bean.MarketDepth;
import mic.trade.bean.Unit;
import mic.trade.bean.UnitComparator;

public class MarketDepthJsonDeserializer implements JsonDeserializer<MarketDepth> {
	public static final int JSON_TYPE_GATECOIN_API = 0;
	public static final int JSON_TYPE_GATECOIN_PUBNUB = 1;
	
	public static final int JSON_TYPE_FILTER_MODE_BID = 1;
	public static final int JSON_TYPE_FILTER_MODE_ASK = 2;
	public static final int JSON_TYPE_FILTER_MODE_BIDASK = JSON_TYPE_FILTER_MODE_BID | JSON_TYPE_FILTER_MODE_ASK;
	
	private static final Logger myLogger = LogManager.getLogger(MarketDepthJsonDeserializer.class);

	private int jsonType = JSON_TYPE_GATECOIN_API;
	private int jsonFilterMode = JSON_TYPE_FILTER_MODE_BIDASK;

	/**
	 * @param jsonType
	 * @param jsonFilterMode
	 */
	public MarketDepthJsonDeserializer(int jsonType, int jsonFilterMode) {
		this.jsonType = jsonType;
		this.jsonFilterMode = jsonFilterMode;
	}

	@Override
	public MarketDepth deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		final ArrayList<Unit> askList = new ArrayList<>();
		final ArrayList<Unit> bidList = new ArrayList<>();
		
		JsonArray asksJsonArray = null;
		JsonArray bidsJsonArray = null;
		JsonElement tmpJsonElement = null;
		
		JsonObject tmpJsonObj = json.getAsJsonObject();
		
		if ((jsonFilterMode & JSON_TYPE_FILTER_MODE_ASK) == JSON_TYPE_FILTER_MODE_ASK) {
			tmpJsonElement = tmpJsonObj.get("asks");
			if (tmpJsonElement == null) {
				myLogger.error("Json parsing error: {}", tmpJsonObj.toString());
				throw new JsonParseException("No asks object");
			}
			asksJsonArray = tmpJsonElement.getAsJsonArray();
		}
		
		if ((jsonFilterMode & JSON_TYPE_FILTER_MODE_BID) == JSON_TYPE_FILTER_MODE_BID) {
			tmpJsonElement = tmpJsonObj.get("bids");
			if (tmpJsonElement == null) {
				myLogger.error("Json parsing error: {}", tmpJsonObj.toString());
				throw new JsonParseException("No bids object");
			}
			bidsJsonArray = tmpJsonElement.getAsJsonArray();
		}
		
		final String priceTagName = (jsonType==JSON_TYPE_GATECOIN_PUBNUB?"p":"price");
		final String volumeTagName = (jsonType==JSON_TYPE_GATECOIN_PUBNUB?"q":"volume");
		
		if (asksJsonArray != null) {
			for (JsonElement askJsonElement : asksJsonArray) {
				tmpJsonObj = askJsonElement.getAsJsonObject();
				
				BigDecimal price = tmpJsonObj.get(priceTagName).getAsBigDecimal();
				BigDecimal volume = tmpJsonObj.get(volumeTagName).getAsBigDecimal();
				
				askList.add(new Unit(price, volume));
			}
			Collections.sort(askList, new UnitComparator(true));
		}
		
		if (bidsJsonArray != null) {
			for (JsonElement bidJsonElement : bidsJsonArray) {
				tmpJsonObj = bidJsonElement.getAsJsonObject();
				
				BigDecimal price = tmpJsonObj.get(priceTagName).getAsBigDecimal();
				BigDecimal volume = tmpJsonObj.get(volumeTagName).getAsBigDecimal();
				
				bidList.add(new Unit(price, volume));
			}
			Collections.sort(bidList, new UnitComparator(false));
		}
		
		return new MarketDepth(askList, bidList);
	}

}
