# Welcome to GatecoinApi4j

[![Build Status](https://travis-ci.org/micwan88/gatecoinapi4j.svg?branch=master)](https://travis-ci.org/micwan88/gatecoinapi4j)
[![Coverage Status](https://coveralls.io/repos/github/micwan88/gatecoinapi4j/badge.svg?branch=master)](https://coveralls.io/github/micwan88/gatecoinapi4j?branch=master)

This is a java implementation of [Gatecoin](https://gatecoin.com/) api included RESTful call and PubNub subscribe service (Real time data streaming over Web Socket).

### Contribution
This library is created for my own interest and it is not yet finished. So welcome anyone contribute to it.

### Features
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
3. Transaction call back (transaction streaming)
4. OderBook update call back (order book update streaming)
5. Live/History Ticker update call back (ticker update streaming)

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
	compile group: 'mic.trade', name: 'gatecoinapi4j', version: '0.1.0-SNAPSHOT'
}
``` 

### Building a jar only
```
git clone https://github.com/micwan88/gatecoinapi4j.git
cd gatecoinapi4j
./gradlew clean jar
```
After that, you can find your gatecoinapi4j.jar in ${project.projectDir}/build/libs

### GatecoinTradeService Example
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
			System.out.println("Cannot post an order");
		else {
			System.out.println("Post order completed");
			
			//Cancel an order
			String result = gatecoinTradeService.cancelOpenOrder(orderID);
			if (result != null && result.equals("OK"))
				System.out.println("Cancel order completed");
		}
		
		//Call closeService to free up httpclient resources
		gatecoinTradeService.closeService();
	}
```