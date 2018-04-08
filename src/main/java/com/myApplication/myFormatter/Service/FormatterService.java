package com.myApplication.myFormatter.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.myApplication.myFormatter.App;

@Configuration
public class FormatterService {

	private static final Logger _logger = LoggerFactory.getLogger(FormatterService.class);
	
	public String formatJson(String unformatted){
		ObjectMapper mapper = new ObjectMapper();
		String prettyJson = null;
        try {
            Object jsonObject = mapper.readValue(unformatted, Object.class);
            prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);   
            _logger.debug("pretty Json formnatted:::"+prettyJson);
        } catch (IOException e) {
        	 _logger.debug("Exception occured during formatJson");
        }
		return prettyJson;
	}
	
	public String formatXml(String unformatted){
		String prettyXml = null;
		try {
            prettyXml = URLDecoder.decode(unformatted, "UTF-8");
            _logger.debug("pretty Json formnatted:::"+prettyXml);
        } catch (IOException e) {
        	 _logger.debug("Exception occured during formatXml");
        }
		return prettyXml;
	}
	
	public StringBuilder jsonEvaluator(String key, String body){
		
		JsonParser p = new JsonParser();
		List<String> list = new ArrayList<String>();
		List<String> resultList =  check(key, p.parse(body), list);
		StringBuilder sb = new StringBuilder();
		
		for (String list1 : resultList) {
			sb.append(formatJson(list1.toString()));
		}
       
		return sb;
	}
	
	private static List<String> check(String key, JsonElement jsonElement, List<String> list) {
		
        if (jsonElement.isJsonArray()) {
            for (JsonElement jsonElement1 : jsonElement.getAsJsonArray()) {
                check(key, jsonElement1, list);
            }
        } else {
            if (jsonElement.isJsonObject()) {
                Set<Map.Entry<String, JsonElement>> entrySet = jsonElement
                        .getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    String key1 = entry.getKey();
                    if (key1.equals(key)) {
                        list.add(entry.getValue().toString());
                    }
                    check(key, entry.getValue(), list);
                }
            } else {
                if (jsonElement.toString().equals(key)) {
                    list.add(jsonElement.toString());
                }
            }
        }
       
        return list;
    }

	public String jsonToXmlConverter(String jsonBody){
		String xmlResponse = null;
		try {
			xmlResponse = XML.toString(new JSONObject(jsonBody));
		} catch (JSONException e) {
			_logger.debug("Exception occured in jsonToXmlConverter");
		}
		return xmlResponse;
	}
	
	public String xmlToJsonConverter(String xmlBody){
		String jsonResponse = null;
		try {
			jsonResponse = XML.toJSONObject(xmlBody).toString();
		} catch (JSONException e) {
			_logger.debug("Exception occured in xmlToJsonConverter");
		}
		return jsonResponse;
	}
	
	public String formatJsonToSingleLine(String formatted){
		ObjectMapper mapper = new ObjectMapper();
		String singleLineJson = null;
        try {
            Object jsonObject = mapper.readValue(formatted, Object.class);
            singleLineJson = mapper.writeValueAsString(jsonObject);   
            _logger.debug("single line Json :::"+singleLineJson);
        } catch (IOException e) {
        	_logger.debug("Exception occured during formatJsonToSingleLine");
        }
		return singleLineJson;
	}
	
	public String formatXmlToSingleLine(String formatted){
		String singleLineJson = formatted.replaceAll("\n","").replaceAll("\t",""); 
		singleLineJson = singleLineJson.replaceAll("","");
		singleLineJson = singleLineJson.replaceAll("\\s+", "");
		singleLineJson = singleLineJson.trim();
		_logger.debug("formatted:::"+singleLineJson);
		return singleLineJson;
	}
	
	public String jsonPath(String jsonBody, String jsonQuery){
		DocumentContext docCtx = JsonPath.parse(jsonBody);
		JsonPath jsonPath = JsonPath.compile(jsonQuery);
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		try {
			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(docCtx.read(jsonPath));
		} catch (JsonProcessingException e) {
			
		}
		return result;
	}
	
	public String yamlToJsonConverter(String yamlBody) {
		Yaml yaml = new Yaml();
		Map<String, Object> map = (Map<String, Object>) yaml.load(yamlBody);

		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toString();
	}
	
	public String jsonToYamlConverter(String jsonBody){
		JsonNode jsonNodeTree;
		 String jsonAsYaml = null;
		try {
			jsonNodeTree = new ObjectMapper().readTree(jsonBody);
			jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
		} catch (JsonProcessingException e1) {
			_logger.debug("JsonProcessingException occurred during jsonToYamlConverter");
		} catch (IOException e1) {
			_logger.debug("IOException occurred during jsonToYamlConverter");
		}

        return jsonAsYaml;
	}
}
