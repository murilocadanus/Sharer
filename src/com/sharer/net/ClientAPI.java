package com.sharer.net;

import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sharer.entities.Asset;
import com.sharer.entities.Assets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ClientAPI {
	private static final String END_POINT = "http://localhost:8080/";
	private Client client = Client.create();
	
	public boolean uploadFile(Asset asset) {
		WebResource webResource = client.resource(END_POINT.concat("file/upload"));
		String inputData = "{"
				+ "\"documentName\":\"" + asset.getName() + "\","
				+ "\"version\":\"" + asset.getVersion() + "\","
				+ "\"data\":\"" + asset.getData() + "\""
				+ "}";
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, inputData);
		
		if(response.getStatus() != 200){
			throw new RuntimeException("HTTP Error: "+ response.getStatus());
		}
		
		return true;
	}
	
	public Assets downloadDictionary() throws Exception {
		WebResource webResource = client.resource(END_POINT.concat("file/downloadDictionary"));
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if(response.getStatus() != 200){
			throw new RuntimeException("HTTP Error: "+ response.getStatus());
		}
		
		String result = response.getEntity(String.class);
		JSONObject jsonResponse = (JSONObject) new JSONParser().parse(new StringReader(result));
		String xmlData = (String) ((JSONObject)jsonResponse.get("data")).get("fileContent");
		
		JAXBContext context = JAXBContext.newInstance(Assets.class);
		Unmarshaller um = context.createUnmarshaller();
		
		Assets assets = (Assets)um.unmarshal(new StringReader(xmlData));
		
		return assets;
	}
	
	public Asset download(String fileToDownload) throws Exception {
		WebResource webResource = client.resource(END_POINT.concat("file/download"));
		ClientResponse response = webResource.queryParam("pathToFile", fileToDownload)
											.type("application/json")
											.get(ClientResponse.class);
		
		if(response.getStatus() != 200){
			throw new RuntimeException("HTTP Error: "+ response.getStatus());
		}
		
		String result = response.getEntity(String.class);
		JSONObject jsonResponse = (JSONObject) new JSONParser().parse(new StringReader(result));
		String xmlData = (String) ((JSONObject)jsonResponse.get("data")).get("fileContent");
		
		JAXBContext context = JAXBContext.newInstance(Asset.class);
		Unmarshaller um = context.createUnmarshaller();
		
		Asset asset = (Asset)um.unmarshal(new StringReader(xmlData));
		
		return asset;		
	}
	
}
