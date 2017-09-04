package mic.trade.gatecoin;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import mic.trade.TradeServiceAdapter;
import mic.trade.bean.MarketDepth;
import mic.trade.bean.Order;
import mic.trade.bean.Transaction;
import mic.trade.gatecoin.json.MarketDepthJsonDeserializer;
import mic.trade.gatecoin.json.OrderJsonDeserializer;
import mic.trade.gatecoin.json.TransactionJsonDeserializer;

public class GatecoinTradeService implements TradeServiceAdapter {
	public static final String APP_PROPERTY_KEY_API_PUBLIC_KEY = "gatecoin.api.pubkey";
	public static final String APP_PROPERTY_KEY_API_PRIVATE_KEY = "gatecoin.api.prikey";
	
	public static final String HTTP_HEADER_API_PUBLIC_KEY = "API_PUBLIC_KEY";
	public static final String HTTP_HEADER_API_REQUEST_SIGNATURE = "API_REQUEST_SIGNATURE";
	public static final String HTTP_HEADER_API_REQUEST_DATE = "API_REQUEST_DATE";
	public static final String HTTP_HEADER_API_CONTENT_TYPE = "Content-Type";
	
	public static final String URL_GATECOIN_API_BASE = "https://api.gatecoin.com/";
	public static final String URL_GET_TRANSACTION = URL_GATECOIN_API_BASE + "Public/Transactions/";
	public static final String URL_GET_ALL_TRANSACTION = URL_GATECOIN_API_BASE + "Public/TransactionsHistory/";
	public static final String URL_GET_MARKET_DEPTH = URL_GATECOIN_API_BASE + "Public/MarketDepth/";
	public static final String URL_GET_USER_TRADES = URL_GATECOIN_API_BASE + "Trade/Trades";
	public static final String URL_ORDERS = URL_GATECOIN_API_BASE + "Trade/Orders";
	
	public static final String MSG_CONTENT_TYPE_GET = "";
	public static final String MSG_CONTENT_TYPE_OTHERS = "application/json";
	
	public static final String RESTFUL_HTTP_METHOD_GET = "GET";
	public static final String RESTFUL_HTTP_METHOD_POST = "POST";
	public static final String RESTFUL_HTTP_METHOD_DELETE = "DELETE";
	
	public static final Pattern PATTERN_RESP_MSG_OK = Pattern.compile("\"message\"\\s*\\:\\s*\"OK\"");
	public static final Pattern PATTERN_RESP_MSG_ORDER_OK = Pattern.compile("\"clOrderId\"\\s*\\:\\s*\"(\\w+)\"\\s*,\\s*\"responseStatus\"\\s*\\:\\s*\\{\\s*\"message\"\\s*\\:\\s*\"OK\"", Pattern.MULTILINE);
	public static final Pattern PATTERN_RESP_MSG_HISTORY_WAIT = Pattern.compile("Available in (\\d+) second\\(s\\)");
	
	private static final Logger myLogger = LogManager.getLogger(GatecoinTradeService.class);
	
	private CloseableHttpClient httpClient = null;
	private String apiPublicKey = null;
	private String apiPrivateKey = null;
	
	public GatecoinTradeService(Properties appProperties) {
		this();
		
		if (appProperties != null) {
			this.apiPublicKey = appProperties.getProperty(APP_PROPERTY_KEY_API_PUBLIC_KEY);
			this.apiPrivateKey = appProperties.getProperty(APP_PROPERTY_KEY_API_PRIVATE_KEY);
		}
	}
	
	public GatecoinTradeService(String apiPublicKey, String apiPrivateKey) {
		this();
		
		this.apiPublicKey = apiPublicKey;
		this.apiPrivateKey = apiPrivateKey;
	}
	
	public GatecoinTradeService() {
		httpClient = HttpClients.createDefault();
	}
	
	/**
	 * Static method to close the service gracefully
	 * @param GatecoinTradeService
	 */
	public static void closeQuietly(GatecoinTradeService service) {
		try {
			service.close();
		} catch (Exception e) {
			//Do Nothing
		}
	}
	
	@Override
	public void close() throws Exception {
		httpClient.close();
	}
	
	@Override
	public List<Order> getOrders(String orderID) {
		String apiURL = URL_ORDERS + (orderID==null?"":"/" + orderID);
		myLogger.debug("getOrders URL: {}", apiURL);
		
		String timeToken = String.valueOf(new Date().getTime()/1000L);
		String targetMsg = RESTFUL_HTTP_METHOD_GET + apiURL + MSG_CONTENT_TYPE_GET + timeToken;
		String signedHashString = signedHash(targetMsg.toLowerCase(), apiPrivateKey);
		if (signedHashString == null)
			return null;
		
		HttpGet httpget = new HttpGet(apiURL);
		
		httpget.addHeader(HTTP_HEADER_API_PUBLIC_KEY, apiPublicKey);
		httpget.addHeader(HTTP_HEADER_API_REQUEST_SIGNATURE, signedHashString);
		httpget.addHeader(HTTP_HEADER_API_REQUEST_DATE, timeToken);
		httpget.setHeader(HTTP_HEADER_API_CONTENT_TYPE, MSG_CONTENT_TYPE_GET);
		
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpget);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				myLogger.error("No content on the request: {}", apiURL);
				return null;
			}
			
			Type orderListType = new TypeToken<List<Order>>(){}.getType();
			GsonBuilder gsonBuilder = new GsonBuilder();
		    gsonBuilder.registerTypeAdapter(orderListType, 
		    		new OrderJsonDeserializer(OrderJsonDeserializer.JSON_TYPE_GATECOIN_API));
			
		    return gsonBuilder.create().fromJson(EntityUtils.toString(httpEntity, "UTF-8"), orderListType);
		} catch (JsonParseException e) {
			myLogger.error("Cannot parse json response", e);
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unknown error in executing http request", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			myLogger.debug("getOrders end");
		}
		return null;
	}
	
	@Override
	public String postOrder(String currency, boolean isBuy, BigDecimal amount, BigDecimal atPrice) {
		String apiURL = URL_ORDERS;
		
		myLogger.debug("postOrder URL: {}", apiURL);
		
		String timeToken = String.valueOf(new Date().getTime()/1000L);
		String targetMsg = RESTFUL_HTTP_METHOD_POST + apiURL + MSG_CONTENT_TYPE_OTHERS + timeToken;
		String signedHashString = signedHash(targetMsg.toLowerCase(), apiPrivateKey);
		if (signedHashString == null)
			return null;
		
		HttpPost httpPost = new HttpPost(apiURL);
		
		JsonObject outputJson = new JsonObject();
		outputJson.addProperty("Code", currency);
		outputJson.addProperty("Way", isBuy?"Bid":"Ask");
		outputJson.addProperty("Amount", amount);
		outputJson.addProperty("Price", atPrice);
		
		httpPost.setEntity(new StringEntity(outputJson.toString(), "UTF-8"));
		
		httpPost.addHeader(HTTP_HEADER_API_PUBLIC_KEY, apiPublicKey);
		httpPost.addHeader(HTTP_HEADER_API_REQUEST_SIGNATURE, signedHashString);
		httpPost.addHeader(HTTP_HEADER_API_REQUEST_DATE, timeToken);
		httpPost.setHeader(HTTP_HEADER_API_CONTENT_TYPE, MSG_CONTENT_TYPE_OTHERS);
		
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				myLogger.error("No content on the request: {}", apiURL);
				return null;
			}
			
			String responseMsg = EntityUtils.toString(httpEntity, "UTF-8");
			Matcher respMatcher = PATTERN_RESP_MSG_ORDER_OK.matcher(responseMsg);
			if (respMatcher.find())
				return respMatcher.group(1);
			
			myLogger.error("Unknown response msg: {}", responseMsg);
		    return "";
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unknown error in executing http request", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			myLogger.debug("postOrder end");
		}
		return null;
	}
	
	public String cancelOpenOrder() {
		return cancelOpenOrder(null);
	}
	
	@Override
	public String cancelOpenOrder(String orderID) {
		String apiURL = URL_ORDERS + (orderID==null?"":"/" + orderID);
		
		myLogger.debug("cancelOpenOrder URL: {}, orderID: {}", apiURL, orderID);
		
		String timeToken = String.valueOf(new Date().getTime()/1000L);
		String targetMsg = RESTFUL_HTTP_METHOD_DELETE + apiURL + MSG_CONTENT_TYPE_OTHERS + timeToken;
		String signedHashString = signedHash(targetMsg.toLowerCase(), apiPrivateKey);
		if (signedHashString == null)
			return null;
				
		HttpDelete httpDelete = new HttpDelete(apiURL);
		
		httpDelete.addHeader(HTTP_HEADER_API_PUBLIC_KEY, apiPublicKey);
		httpDelete.addHeader(HTTP_HEADER_API_REQUEST_SIGNATURE, signedHashString);
		httpDelete.addHeader(HTTP_HEADER_API_REQUEST_DATE, timeToken);
		httpDelete.setHeader(HTTP_HEADER_API_CONTENT_TYPE, MSG_CONTENT_TYPE_OTHERS);
		
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpDelete);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				myLogger.error("No content on the request: {}", apiURL);
				return null;
			}
			
			String responseMsg = EntityUtils.toString(httpEntity, "UTF-8");
			Matcher respMatcher = PATTERN_RESP_MSG_OK.matcher(responseMsg);
			if (respMatcher.find())
				return "OK";
			
			myLogger.error("Unknown response msg: {}", responseMsg);
		    return "";
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unknown error in executing http request", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			myLogger.debug("cancelOpenOrder end");
		}
		return null;
	}
	
	@Override
	public List<Transaction> getTransactionList(String currency) {
		return getTransactionList(currency, false);
	}
	
	public List<Transaction> getTransactionList(String currency, boolean isGetAll) {
		String apiURL = (isGetAll?URL_GET_ALL_TRANSACTION:URL_GET_TRANSACTION) + currency + "?Count=" + (isGetAll?"10000":"500");
		myLogger.debug("getTransactionList URL: {}", apiURL);
		
		HttpGet httpget = new HttpGet(apiURL);
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpget);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				myLogger.error("No content on the request: {}", apiURL);
				return null;
			}
			
			String responseMsg = EntityUtils.toString(httpEntity, "UTF-8");
			if (isGetAll && responseMsg.startsWith("Available")) {
				//Gatecoin full transaction history not always available and need wait some time
				Matcher respMatcher = PATTERN_RESP_MSG_HISTORY_WAIT.matcher(responseMsg);
				if (respMatcher.find()) {
					HttpClientUtils.closeQuietly(httpResponse);
					//Wait
					TimeUnit.SECONDS.sleep(Long.parseLong(respMatcher.group(1)) + 5);
					httpResponse = httpClient.execute(httpget);
					httpEntity = httpResponse.getEntity();
					if (httpEntity == null) {
						myLogger.error("No content on the request: {}", apiURL);
						return null;
					}
					responseMsg = EntityUtils.toString(httpEntity, "UTF-8");
				} else {
					myLogger.error("Unexpected response from transaction history: {}", responseMsg);
					return null;
				}
			}
			
			Type transactionListType = new TypeToken<List<Transaction>>(){}.getType();
			GsonBuilder gsonBuilder = new GsonBuilder();
		    gsonBuilder.registerTypeAdapter(transactionListType, 
		    		new TransactionJsonDeserializer(TransactionJsonDeserializer.JSON_TYPE_GATECOIN_API_TRANSACTION, currency));
			
		    return gsonBuilder.create().fromJson(responseMsg, transactionListType);
		} catch (JsonParseException e) {
			myLogger.error("Cannot parse json response", e);
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unknown error in executing http request", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			myLogger.debug("getTransactionList end");
		}
		return null;
	}
	
	@Override
	public MarketDepth getMarketDepth(String currency) {
		return getMarketDepth(currency, MarketDepthJsonDeserializer.JSON_TYPE_FILTER_MODE_BIDASK);
	}
	
	public MarketDepth getMarketDepth(String currency, int bidAskFilter) {
		String apiURL = URL_GET_MARKET_DEPTH + currency;
		myLogger.debug("getMarketDepth URL: {}", apiURL);
		
		HttpGet httpget = new HttpGet(apiURL);
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpget);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				myLogger.error("No content on the request: {}", apiURL);
				return null;
			}
			
			GsonBuilder gsonBuilder = new GsonBuilder();
		    gsonBuilder.registerTypeAdapter(MarketDepth.class, 
		    		new MarketDepthJsonDeserializer(MarketDepthJsonDeserializer.JSON_TYPE_GATECOIN_API, bidAskFilter));
			
		    return gsonBuilder.create().fromJson(EntityUtils.toString(httpEntity, "UTF-8"), MarketDepth.class);
		} catch (JsonParseException e) {
			myLogger.error("Cannot parse json response", e);
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unknown error in executing http request", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			myLogger.debug("getMarketDepth end");
		}
		return null;
	}
	
	@Override
	public List<Transaction> getTrades(String transactionID) {
		String apiURL = URL_GET_USER_TRADES + (transactionID==null?"":"?TransactionID=" + transactionID);
		myLogger.debug("getTrades URL: {}", apiURL);
		
		String timeToken = String.valueOf(new Date().getTime()/1000L);
		String targetMsg = RESTFUL_HTTP_METHOD_GET + apiURL + MSG_CONTENT_TYPE_GET + timeToken;
		String signedHashString = signedHash(targetMsg.toLowerCase(), apiPrivateKey);
		if (signedHashString == null)
			return null;
		
		HttpGet httpget = new HttpGet(apiURL);
		
		httpget.addHeader(HTTP_HEADER_API_PUBLIC_KEY, apiPublicKey);
		httpget.addHeader(HTTP_HEADER_API_REQUEST_SIGNATURE, signedHashString);
		httpget.addHeader(HTTP_HEADER_API_REQUEST_DATE, timeToken);
		httpget.setHeader(HTTP_HEADER_API_CONTENT_TYPE, MSG_CONTENT_TYPE_GET);
		
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpget);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				myLogger.error("No content on the request: {}", apiURL);
				return null;
			}
			
			Type transactionListType = new TypeToken<List<Transaction>>(){}.getType();
			GsonBuilder gsonBuilder = new GsonBuilder();
		    gsonBuilder.registerTypeAdapter(transactionListType, 
		    		new TransactionJsonDeserializer(TransactionJsonDeserializer.JSON_TYPE_GATECOIN_API_USER_TRANSACTION));
		    
		    return gsonBuilder.create().fromJson(EntityUtils.toString(httpEntity, "UTF-8"), transactionListType);
		} catch (JsonParseException e) {
			myLogger.error("Cannot parse json response", e);
		} catch (IOException e) {
			myLogger.error("Cannot execute http request", e);
		} catch (Exception e) {
			myLogger.error("Unknown error in executing http request", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			myLogger.debug("getTrades end");
		}
		return null;
	}
	
	private static String signedHash(String message, String privateKey) {
		Mac sha256HMAC = null;
		try {
			sha256HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretkeySpec = new SecretKeySpec(privateKey.getBytes(), "HmacSHA256");
			sha256HMAC.init(secretkeySpec);
			
			return Base64.encodeBase64String(sha256HMAC.doFinal(message.getBytes()));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			myLogger.error("Cannot sign the message", e);
		}
		return null;
	}
}
