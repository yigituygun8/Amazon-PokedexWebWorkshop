package com.aub.workshop.pokemon; 
 
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB; 
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder; 
import com.amazonaws.services.dynamodbv2.document.DynamoDB; 
import com.amazonaws.services.dynamodbv2.document.Item; 
import com.amazonaws.services.dynamodbv2.document.Table; 
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger; 
import com.amazonaws.services.lambda.runtime.RequestHandler; 
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent; 
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent; 
import static com.aub.workshop.pokemon.Constants.DYNAMODB_TABLE_NAME; 
import com.google.gson.Gson; 
 
 
public class PokemonGetHandlerLambda implements 
RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{ 
    private final AmazonDynamoDB client = 
AmazonDynamoDBClientBuilder.standard().build(); 
    private final DynamoDB dynamoDB = new DynamoDB(client); 
 
    @Override 
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent 
input, Context context) { 
        LambdaLogger logger = context.getLogger(); 
         
        String pokemonId = input.getPathParameters().get("id"); 
        logger.log("Received call to get pokemon with id: " + pokemonId); 
 
        int pokemonNumber = Integer.parseInt(pokemonId); 
 
        Table table = dynamoDB.getTable(DYNAMODB_TABLE_NAME); 
 
        // Retrieve item from DynamoDB using pokemonId as the key 
        Item item = table.getItem("pokemonNumber", pokemonNumber); 
 
        // Convert item to JSON string 
        Gson gson = new Gson(); 
        String jsonOutput = gson.toJson(item.asMap()); 
 
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent(); 
        response.setStatusCode(200); 
        response.setBody(jsonOutput); 
        return response; 
    } 
} 