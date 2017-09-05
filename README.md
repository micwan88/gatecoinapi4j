# Welcome to GatecoinApi4j

[![Build Status](https://travis-ci.org/micwan88/gatecoinapi4j.svg?branch=master)](https://travis-ci.org/micwan88/gatecoinapi4j)
[![Coverage Status](https://coveralls.io/repos/github/micwan88/gatecoinapi4j/badge.svg?branch=master)](https://coveralls.io/github/micwan88/gatecoinapi4j?branch=master)
[ ![Download](https://api.bintray.com/packages/micwan88/micMavenRepos/gatecoinapi4j/images/download.svg) ](https://bintray.com/micwan88/micMavenRepos/gatecoinapi4j/_latestVersion)

This is a java implementation of [Gatecoin](https://gatecoin.com/) api included RESTful call and PubNub subscribe service (Real time data streaming over Web Socket).

## Contribution
This library is created for my own interest and it is not yet finished. So welcome anyone contribute to it.

## Features
Below are list of features implemented already
- GatecoinTradeService
1. Post an order
2. Cancel an order
3. Cancel all orders
4. Get list of open orders
5. Get list of user transaction
6. Get list of recent transactions
7. Get list of transaction history
8. Get market depth (all items in order book - bid/ask)

- GatecoinPubNubService (Data Streaming)
1. Subscribe all gatecoin PubNub services
2. Unsubscribe all services
3. Auto reconnect handling while disconnected
4. Transaction call back (transaction streaming)
5. OderBook update call back (order book update streaming)
6. Live/History Ticker update call back (ticker update streaming)

## Using gatecoinapi4j
### Maven project
First, you need add jcenter repository in Maven `settings.xml` as gatecoinapi4j only host in jcenter currently.

``` xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
          xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
	<profiles>
		<profile>
			<repositories>
				<repository>
					<snapshots>
						<enabled>false</enabled>
					</snapshots>
					<id>central</id>
					<name>bintray</name>
					<url>https://jcenter.bintray.com</url>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<snapshots>
						<enabled>false</enabled>
					</snapshots>
					<id>central</id>
					<name>bintray-plugins</name>
					<url>https://jcenter.bintray.com</url>
				</pluginRepository>
			</pluginRepositories>
			<id>bintray</id>
		</profile>
	</profiles>
	<activeProfiles>
		<activeProfile>bintray</activeProfile>
	</activeProfiles>
</settings>
```

After that, add below dependency in your project pom file (please modify the version accordingly, refer to release)
```
<dependency>
	<groupId>mic.trade</groupId>
	<artifactId>gatecoinapi4j</artifactId>
	<version>1.0.1</version>
	<type>pom</type>
</dependency>
```

### Gradle project
In your build.gradle, add below dependency (please modify the version accordingly, refer to release)
``` gradle
repositories {
    // gatecoinapi4j is hosting in jcenter
    jcenter()
}

dependencies {
	//GatecoinApi4J
	compile group: 'mic.trade', name: 'gatecoinapi4j', version: '1.0.1'
}
```

### Reference gatecoinapi4j in your project via local Maven repository
To use gatecoinapi4j in your project, you can clone the project, build the jar and install it in your local maven repository by below commands.
```
git clone https://github.com/micwan88/gatecoinapi4j.git
cd gatecoinapi4j
./gradlew clean install
```
After that, `gatecoinapi4j-[version].jar` should be installed under your user directory of `~/.m2` and now you can reference the jar file in `build.gradle` for your project.
``` gradle
repositories {
    mavenLocal()
    //You can add other repository here (like jcenter / mavenCentral) and
    //mavenLocal is use for reference jar build from gatecoinapi4j
}

//Please modify the below version to match with the source of gatecoinapi4j
dependencies {
	//GatecoinApi4J
	compile group: 'mic.trade', name: 'gatecoinapi4j', version: '1.0.2-SNAPSHOT'
}
``` 

### Building a jar and then add it to your project
```
git clone https://github.com/micwan88/gatecoinapi4j.git
cd gatecoinapi4j
./gradlew clean jar
```
After that, you can find your `gatecoinapi4j-[version].jar` in `${project.projectDir}/build/libs`. So you can copy it into your project.

## Example
### GatecoinTradeService
More examples can be found under [examples](https://github.com/micwan88/gatecoinapi4j/tree/master/src/main/java/mic/trade/examples/gatecoin) folder

``` java
public static void main(String[] args) {
	/**
	 * Api key is generated from gatecoin web with your account and this is necessary for post order / cancel order
	 * Default constructor is enough if you only use public api such as getting transaction / market depth (order book items) 
	 */
	GatecoinTradeService gatecoinTradeService = new GatecoinTradeService("<yourApiPublicKey>", "<yourApiPrivateKey>");
	
	//Get recent transaction
	List<Transaction> transactionList = gatecoinTradeService.getTransactionList("ETHHKD");
	for (Transaction transaction : transactionList)
		System.out.println("Time: " + transaction.gettTime() + " Price: " + transaction.getPrice());
	
	//Post an order
	String orderID = gatecoinTradeService.postOrder("ETHHKD", true, new BigDecimal("1"), new BigDecimal("2400"));
	if (orderID == null || orderID.equals(""))
		System.err.println("Cannot post an order");
	else {
		System.out.println("Post order completed");
		
		//Cancel all open order for your account
		String result = gatecoinTradeService.cancelOpenOrder();
		if (result != null && result.equals("OK"))
			System.out.println("Cancel order completed");
	}
	
	//Call close to free up underlying httpclient resources
	gatecoinTradeService.close();
}
```

### GatecoinPubNubService (Real time data streaming)
More examples can be found under [examples](https://github.com/micwan88/gatecoinapi4j/tree/master/src/main/java/mic/trade/examples/gatecoin) folder

``` java
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
			System.out.println("Got new transaction - " + transactionList.get(0));
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
	
	gatecoinPubNubService.close();
}
```

## License
gatecoinapi4j is under [MIT License](https://github.com/micwan88/gatecoinapi4j/blob/master/LICENSE)