package com.aub.workshop.pokemon; 
 
import java.lang.reflect.Type; 
import java.util.ArrayList; 
import java.util.Base64; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
 
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB; 
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder; 
import com.amazonaws.services.dynamodbv2.document.DynamoDB; 
import com.amazonaws.services.dynamodbv2.model.AttributeValue; 
import com.amazonaws.services.dynamodbv2.model.ScanRequest; 
import com.amazonaws.services.dynamodbv2.model.ScanResult; 
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger; 
import com.amazonaws.services.lambda.runtime.RequestHandler; 
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent; 
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent; 
import static com.aub.workshop.pokemon.Constants.DYNAMODB_TABLE_NAME; 
import static com.aub.workshop.pokemon.Constants.PAGE_SIZE; 
import com.google.gson.Gson; 
import com.google.gson.reflect.TypeToken; 
 
 
public class PokemonListHandlerLambda implements RequestHandler<APIGatewayProxyRequestEvent, 
APIGatewayProxyResponseEvent>{ 
   private static final String QUERY_PARAM = "nextPage"; 
 
   private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build(); 
   private final DynamoDB dynamoDB = new DynamoDB(client); 
   private final Gson gson = new Gson(); 
   private LambdaLogger logger; 
 
   @Override 
   public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context 
context) { 
       // Lambda Logger 
       logger = context.getLogger(); 
       logger.log("Starting Index lambda"); 
 
       String nextPage = input.getQueryStringParameters() != null ? 
               input.getQueryStringParameters().get(QUERY_PARAM) : null; 
       logger.log("Got Query param next page: " + nextPage); 
       Map<String, AttributeValue> lastKeyEvaluated = 
decodeLastEvaluatedKeyFromParameters(nextPage); 
 
      
       String jsonOutput = scanDBAndFormatResponse(lastKeyEvaluated); 
       
       APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent(); 
       response.setStatusCode(200); 
       response.setBody(jsonOutput); 
       return response; 
   } 
 
   private Map<String, AttributeValue> decodeLastEvaluatedKeyFromParameters(String nextPage) { 
       if (nextPage!=null){ 
           byte[] decodedBytes = Base64.getUrlDecoder().decode(nextPage); 
           String decodedString = new String(decodedBytes); 
 
           Type type = new TypeToken<Map<String, AttributeValue>>(){}.getType(); 
           return gson.fromJson(decodedString, type); 
       } 
       return null; 
   } 
 
   private String encodeLastEvaluatedKey(Map<String, AttributeValue> lastKey) { 
       // turn tree to JSON 
       String marshalledLastKey = gson.toJson(lastKey); 
 
       // Base64 URL safe encoding 
       return Base64.getUrlEncoder().encodeToString(marshalledLastKey.getBytes()); 
   } 
 
   private  String scanDBAndFormatResponse(Map<String, AttributeValue> lastKeyEvaluated) { 
        // setup Query and Scan DB 
        dynamoDB.getTable(DYNAMODB_TABLE_NAME); 
        ScanRequest scanSpec = new ScanRequest() 
                .withTableName(DYNAMODB_TABLE_NAME) 
                .withLimit(PAGE_SIZE) 
                .withExclusiveStartKey(lastKeyEvaluated); 
       ScanResult result = client.scan(scanSpec); 
 
 
       
       List<Map<String, Object>> items = new ArrayList<>(); 
       result.getItems().forEach(item -> { 
           Map<String, Object> parsedItem = new HashMap<>(); 
                      item.forEach((key, value) -> 
                   { 
                       if (value.getS() != null) { 
                           parsedItem.put(key, value.getS()); 
// Assuming most attributes are strings 
                       } else { 
                           parsedItem.put(key, value.getN()); 
// Assuming rest of attributes are Number 
                       } 
                   } 
           ); 
 
           items.add(parsedItem); 
       }); 
 
       // Add Last Key + returned Pokemons to response Map 
       Map<String, Object> responseMap = new HashMap<>(); 
       responseMap.put("items", items); 
 
       // get Last Evaluated Key in Page and Encode it to be returned in Response 
       lastKeyEvaluated = result.getLastEvaluatedKey(); 
       if (lastKeyEvaluated != null && !lastKeyEvaluated.isEmpty()){ 
           String encodedKey = encodeLastEvaluatedKey(lastKeyEvaluated); 
           logger.log("key: " + lastKeyEvaluated + " encoded: " + encodedKey); 
           responseMap.put("nextPage", encodedKey); 
       } 
 
       // turn response to JSON and return 
       return gson.toJson(responseMap); 
   } 
} 