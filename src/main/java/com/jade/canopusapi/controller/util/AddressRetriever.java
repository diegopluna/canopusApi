package com.jade.canopusapi.controller.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jade.canopusapi.controller.WebSocketEventListener;
import com.jade.canopusapi.models.utils.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class AddressRetriever {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    public static Address retrieveAddressByCep(String cep) {
        try {
            URI uri = new URI("https", "viacep.com.br", "/ws/" + cep + "/json/", null);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Map<String, String> addressData = parseJsonResponse(response.toString());
            Address address = new Address();
            address.setCep(addressData.get("cep"));
            address.setStreet(addressData.get("logradouro"));
            address.setDistrict(addressData.get("bairro"));
            address.setMunicipality(addressData.get("localidade"));
            address.setState(addressData.get("uf"));
            return address;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving address for CEP {}: {}", cep, e.getMessage(), e);
        }
        return null;
    }

    private static Map<String, String> parseJsonResponse(String jsonResponse) {
        Map<String, String> addressData = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();
                addressData.put(field.getKey(), field.getValue().asText());
            }
        } catch (Exception e) {
            logger.error("Error occurred during JSON parsing: {}", e.getMessage(), e);;
        }

        return addressData;
    }
}
