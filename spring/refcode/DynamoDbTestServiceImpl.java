package com.test.dynamodb.misc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.test.dynamodb.misc.vo.DynamoDbResponseVO;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

@Controller
public class DynamoDbTestServiceImpl {
	
	/** The filter properties for events. */
	private static String[]	filterPropertiesForDynamoDb = {};
	
    private String s3Protocol = "HTTP";
    
    private String s3UseProxy;
    
    static String tableName = "MusicCollection";
    
	//@Autowired
	//private AmazonDynamoDBClient amazonDynamoDBClient;
    
    // Reference
    // BatchGetItem https://github.com/awslabs/aws-dynamodb-examples/blob/master/src/main/java/com/amazonaws/codesamples/lowlevel/LowLevelBatchGet.java
    // Query    

    @DynamoDBTable(tableName="MusicCollection")
    public static class MusicCollection {
        private String artist;
        private String songTitle;
        private String albumTitle;
        
        //Partition key
        @DynamoDBHashKey(attributeName="Artist")
        public String getArtist() { return artist; }
        public void setArtist(String artist) { this.artist = artist; }
        
        //Range key
        @DynamoDBRangeKey(attributeName="SongTitle")
        public String getSongTitle() { return songTitle; }
		public void setSongTitle(String songTitle) { this.songTitle = songTitle; }
		
        @DynamoDBAttribute(attributeName="AlbumTitle")
        public String getAlbumTitle() { return albumTitle; }    
        public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }
    }
    
	@RequestMapping(value = { "/dynamodb/test/v1" }, method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE })
	public
	ResponseEntity<Object> getNoticeList(HttpServletRequest request, 
			@RequestHeader(required = true) Short mcc,
			@RequestHeader(required = true) String model, 
			@RequestHeader(required = true) String appVersion, 
			@RequestHeader(required = true) String osVersion,
			@RequestHeader(required = false) Long mnc,
			@RequestHeader(required = false) String sg,
			@RequestHeader(required = false) String ai) 
		throws ClientRequestBindingException {
		
		validateMandatoryHeaders(mcc, model, appVersion, osVersion);
		
		AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJDXV4ZCIYRKGTH2A", "+fb3n4RRY7xv5bV1e6ItLgzrCUQGyGnNxtgFfcDB");
		
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		if(Boolean.parseBoolean(s3UseProxy)) {
        	clientConfiguration.setProxyHost("168.219.61.252");
            clientConfiguration.setProxyPort(8080);
        }
        
        clientConfiguration.setProtocol(Protocol.valueOf(s3Protocol));
		AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(awsCredentials, clientConfiguration);
		amazonDynamoDBClient.withEndpoint("http://10.251.30.102:8000"); 
		
		DynamoDbResponseVO response = new DynamoDbResponseVO();
		
		// Query
		String partitionKey = "Radioheal";
		String rangekey = "Creep";
		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withS(partitionKey));
		//eav.put(":val2", new AttributeValue().withS(rangekey));
		
		DynamoDBQueryExpression<MusicCollection> queryExpression = new DynamoDBQueryExpression<MusicCollection>()
	            .withKeyConditionExpression("Artist = :val1")
	            .withExpressionAttributeValues(eav);

		DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
        List<MusicCollection> queryResult = mapper.query(MusicCollection.class, queryExpression);
        
        StringBuffer sb = new StringBuffer();
        
        for (MusicCollection music : queryResult) {
        	sb.append(music.getSongTitle());
        }
        
        response.setResult(sb.toString());
		
		// BatchGetItem
		/*
		Map<String, KeysAndAttributes> requestItems = new HashMap<String, KeysAndAttributes>();
		List<Map<String, AttributeValue>> tableKeys = new ArrayList<Map<String, AttributeValue>>(); 
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("Artist", new AttributeValue().withS("Radioheal"));
        key.put("SongTitle", new AttributeValue().withS("Creep"));
        tableKeys.add(key);
        
        requestItems.put(tableName,
                new KeysAndAttributes()
                    .withKeys(tableKeys));
        
        BatchGetItemResult result;
        BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest();
        
        StringBuffer sb = new StringBuffer();
        sb.append("SoneTiles: ");
        
        do {
        	
        	batchGetItemRequest.withRequestItems(requestItems);
            result = amazonDynamoDBClient.batchGetItem(batchGetItemRequest);
            
            List<Map<String, AttributeValue>> batchGetResults = result.getResponses().get(tableName);
            if (batchGetResults != null){
                System.out.println("Items in table " + tableName);
                for (Map<String,AttributeValue> row : batchGetResults) {
                	
                	for (Map.Entry<String, AttributeValue> item : row.entrySet()) {
                		if (item.getKey().equals("SongTitle")) {
                			AttributeValue value = item.getValue();
                			sb.append(value.getS());
                		}
                	}
                }
            }
        } while (result.getUnprocessedKeys().size() > 0);
		
        response.setResult(sb.toString());
        //*/
		
		// Get rows of a table
		//List<String> attributes = new ArrayList<String>();
		//attributes.add("SongTitle");
		//ScanResult scanResult = amazonDynamoDBClient.scan("MusicCollection", attributes);
		//response.setResult(scanResult.toString());

		// Get the list of tables
		//ListTablesResult tableList = amazonDynamoDBClient.listTables();
		//response.setResult(tableList.toString());
	
		return generateResponseEntity(filterPropertiesForDynamoDb, response);
	}
}
