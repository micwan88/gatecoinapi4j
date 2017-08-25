package mic.trade.gatecoin.json;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mic.trade.bean.Order;

public class OrderJsonDeserializer implements JsonDeserializer<List<Order>> {
	public static final int JSON_TYPE_GATECOIN_API = 0;
	public static final int JSON_TYPE_GATECOIN_PUBNUB = 1;
	
	private static final Logger myLogger = LogManager.getLogger(OrderJsonDeserializer.class);

	private int jsonType = JSON_TYPE_GATECOIN_API;
	
	public OrderJsonDeserializer(int jsonType) {
		this.jsonType = jsonType;
	}

	@Override
	public List<Order> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final ArrayList<Order> orderList = new ArrayList<>();
		
		JsonObject tmpJsonObj = json.getAsJsonObject();
		
		if (jsonType != JSON_TYPE_GATECOIN_PUBNUB) {
			JsonElement tmpJsonElement = tmpJsonObj.get("orders");
			if (tmpJsonElement == null) {
				myLogger.error("Json parsing error: {}", tmpJsonObj.toString());
				throw new JsonParseException("No orders array");
			}
			
			final JsonArray tmpJsonArray = tmpJsonElement.getAsJsonArray();
			
			for (JsonElement orderJsonElement : tmpJsonArray) {
				tmpJsonObj = orderJsonElement.getAsJsonObject();
				
				String orderID = tmpJsonObj.get("clOrderId").getAsString();
				String currency = tmpJsonObj.get("code").getAsString();
				
				String tmpString = tmpJsonObj.get("side").getAsString();
				boolean isBid = tmpString.equals("0")?true:false;
				BigDecimal price = tmpJsonObj.get("price").getAsBigDecimal();
				BigDecimal amount = tmpJsonObj.get("initialQuantity").getAsBigDecimal();
				BigDecimal remainAmount = tmpJsonObj.get("remainingQuantity").getAsBigDecimal();
				int status = tmpJsonObj.get("status").getAsInt();
				String statusDesc = tmpJsonObj.get("statusDesc").getAsString();
				
				tmpString = tmpJsonObj.get("date").getAsString();
				Date orderTime = new Date(Long.parseLong(tmpString) * 1000L);
				
				orderList.add(new Order(orderID, currency, isBid, price, amount, remainAmount, status, statusDesc, orderTime));
			}
		} else {
			JsonElement tmpJsonElement = tmpJsonObj.get("order");
			if (tmpJsonElement == null) {
				myLogger.error("Json parsing error: {}", tmpJsonObj.toString());
				throw new JsonParseException("No orders object");
			}
			tmpJsonObj = tmpJsonElement.getAsJsonObject();
			
			String orderID = tmpJsonObj.get("oid").getAsString();
			String currency = tmpJsonObj.get("code").getAsString();
			
			String tmpString = tmpJsonObj.get("side").getAsString();
			boolean isBid = tmpString.equals("0")?true:false;
			BigDecimal price = tmpJsonObj.get("price").getAsBigDecimal();
			BigDecimal amount = tmpJsonObj.get("initAmount").getAsBigDecimal();
			BigDecimal remainAmount = tmpJsonObj.get("remainAmout").getAsBigDecimal();
			int status = tmpJsonObj.get("status").getAsInt();
			
			tmpString = json.getAsJsonObject().get("stamp").getAsString();
			Date orderTime = new Date(Long.parseLong(tmpString) * 1000L);
			
			orderList.add(new Order(orderID, currency, isBid, price, amount, remainAmount, status, orderTime));
		}
		
		return orderList;
	}
}
