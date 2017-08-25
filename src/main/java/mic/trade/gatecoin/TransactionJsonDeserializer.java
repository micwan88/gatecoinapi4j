package mic.trade.gatecoin;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mic.trade.bean.Transaction;
import mic.trade.bean.TransactionComparator;

public class TransactionJsonDeserializer implements JsonDeserializer<List<Transaction>> {
	public static final int JSON_TYPE_GATECOIN_API_TRANSACTION = 0;
	public static final int JSON_TYPE_GATECOIN_PUBNUB_TRANSACTION = 1;
	public static final int JSON_TYPE_GATECOIN_API_USER_TRANSACTION = 3;
	
	private static final Logger myLogger = LogManager.getLogger(TransactionJsonDeserializer.class);

	private int jsonType = JSON_TYPE_GATECOIN_API_TRANSACTION;
	private String preDefinedCurrency = null;
	
	public TransactionJsonDeserializer(int jsonType) {
		this(jsonType, null);
	}
	
	public TransactionJsonDeserializer(int jsonType, String preDefinedCurrency) {
		this.preDefinedCurrency = preDefinedCurrency;
		this.jsonType = jsonType;
	}

	@Override
	public List<Transaction> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final ArrayList<Transaction> transactionList = new ArrayList<>();
		
		JsonObject tmpJsonObj = json.getAsJsonObject();
		
		if (jsonType != JSON_TYPE_GATECOIN_PUBNUB_TRANSACTION) {
			JsonElement tmpJsonElement = tmpJsonObj.get("transactions");
			if (tmpJsonElement == null) {
				myLogger.error("Json parsing error: {}", new Gson().toJson(json));
				throw new JsonParseException("No transactions array");
			}
			
			final JsonArray tmpJsonArray = tmpJsonElement.getAsJsonArray();
			
			for (JsonElement transactionJsonElement : tmpJsonArray) {
				tmpJsonObj = transactionJsonElement.getAsJsonObject();
				long tID = tmpJsonObj.get("transactionId").getAsLong();
				
				String tTimeString = tmpJsonObj.get("transactionTime").getAsString();
				Date tTime = new Date(Long.parseLong(tTimeString) * 1000L);
				
				String currency = (preDefinedCurrency==null?tmpJsonObj.get("currencyPair").getAsString():preDefinedCurrency);
				
				tmpJsonElement = tmpJsonObj.get(jsonType==JSON_TYPE_GATECOIN_API_USER_TRANSACTION?"askOrderID":"askOrderId");
				String askOrderID = tmpJsonElement==null?null:tmpJsonElement.getAsString().trim();
				tmpJsonElement = tmpJsonObj.get(jsonType==JSON_TYPE_GATECOIN_API_USER_TRANSACTION?"bidOrderID":"bidOrderId");
				String bidOrderID = tmpJsonElement==null?null:tmpJsonElement.getAsString().trim();
				
				tmpJsonElement = tmpJsonObj.get("way");
				Boolean isBid = tmpJsonElement==null?null:tmpJsonElement.getAsString().trim().equalsIgnoreCase("bid");
				
				BigDecimal price = tmpJsonObj.get("price").getAsBigDecimal();
				BigDecimal quantity = tmpJsonObj.get("quantity").getAsBigDecimal();
				
				transactionList.add(new Transaction(tID, 
						tTime, currency, askOrderID, bidOrderID, price, quantity, isBid));
			}
			
			Collections.sort(transactionList, new TransactionComparator(0, false));
		} else {
			JsonElement tmpJsonElement = tmpJsonObj.get("trade");
			if (tmpJsonElement == null) {
				myLogger.error("Json parsing error: {}", new Gson().toJson(json));
				throw new JsonParseException("No trade object");
			}
			tmpJsonObj = tmpJsonElement.getAsJsonObject();
			long tID = tmpJsonObj.get("tid").getAsLong();
			
			String tTimeString = tmpJsonObj.get("date").getAsString();
			Date tTime = new Date(Long.parseLong(tTimeString) * 1000L);
			
			String currency = (preDefinedCurrency==null?json.getAsJsonObject().get("item").getAsString() 
					+ json.getAsJsonObject().get("currency").getAsString():preDefinedCurrency);
			
			tmpJsonElement = tmpJsonObj.get("askOrderId");
			String askOrderID = tmpJsonElement==null?null:tmpJsonElement.getAsString().trim();
			tmpJsonElement = tmpJsonObj.get("bidOrderId");
			String bidOrderID = tmpJsonElement==null?null:tmpJsonElement.getAsString().trim();
			
			BigDecimal price = tmpJsonObj.get("price").getAsBigDecimal();
			BigDecimal quantity = tmpJsonObj.get("amount").getAsBigDecimal();
			
			transactionList.add(new Transaction(tID, 
					tTime, currency, askOrderID, bidOrderID, price, quantity));
		}
		
		return transactionList;
	}
}
