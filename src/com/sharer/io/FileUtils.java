package com.sharer.io;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sharer.entities.Asset;
import com.sharer.entities.Assets;

public class FileUtils {
	public static final String RESOURCES_DIRECTORY = "./repository/";
	
	public static Assets getLocalAssets() {
		Assets assets = new Assets();
		updateXmlClient(assets);
		return assets;
	}
	
	private static void updateXmlClient(Assets assets) {
		try {
			List<Asset> assetList = new ArrayList<Asset>();
			listFilesForFolder(new File(RESOURCES_DIRECTORY.concat("files/")), assetList);
			assets.setAssets(assetList);
			
			// Persist xml for assets
			File file = new File(RESOURCES_DIRECTORY.concat("client.xml"));
			JAXBContext jaxbContext = JAXBContext.newInstance(Assets.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(assets, file);
			
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void listFilesForFolder(final File folder, List<Asset> assets) throws IOException, JAXBException {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry, assets);
	        } else {
	        		String xmlData = readFile(fileEntry.getPath(), Charset.defaultCharset());
	        		JAXBContext context = JAXBContext.newInstance(Asset.class);
	        		Unmarshaller um = context.createUnmarshaller();
	        		
	        		Asset asset = (Asset)um.unmarshal(new StringReader(xmlData));
	        		asset.setLength(fileEntry.length());
	        		assets.add(asset);
	        }
	    }
	}
	
	private static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void saveFile(Asset asset) throws Exception 
	{
		File file = new File(RESOURCES_DIRECTORY.concat("files/")
				.concat(asset.getName())
				.concat("-")
				.concat(String.valueOf(asset.getVersion()))
				.concat(".xml"));
		JAXBContext jaxbContext = JAXBContext.newInstance(Asset.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.marshal(asset, file);
	}
	
}
