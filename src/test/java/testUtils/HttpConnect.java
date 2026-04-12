package testUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

public class HttpConnect {
	
	public static HttpResponse<?> doConnectGet(String ServerURL) throws URISyntaxException, IOException, InterruptedException {
		
		HttpRequest ClientRequest = HttpRequest.newBuilder()
			.uri(new URI(ServerURL))
			.build();
			
		HttpClient APIClient = 	HttpClient.newHttpClient();
		HttpResponse<String> ClientRes = APIClient.send(ClientRequest, BodyHandlers.ofString());
		return ClientRes;
	}
	
	
public static HttpResponse<?> doConnectPost(String ServerURL,Object bodyRequest) throws URISyntaxException, IOException, InterruptedException  {
		Gson gson = new Gson();
		String BodyRequestJson;
		HttpRequest ClientRequest;
		
		
		BodyRequestJson = gson.toJson(bodyRequest); 
		ClientRequest = HttpRequest.newBuilder()
					               .uri(new URI(ServerURL))
					               .header("Content-Type", "application/json") 
					               .POST(BodyPublishers.ofString(BodyRequestJson))
					               .build();

		HttpClient APIClient = 	HttpClient.newHttpClient();
		HttpResponse<String> ClientRes = APIClient.send(ClientRequest, BodyHandlers.ofString())	;
		
		return ClientRes;
	}
}
