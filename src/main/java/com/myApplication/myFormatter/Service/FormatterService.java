package com.myApplication.myFormatter.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

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
	
	public StringBuilder jsonToPojoConverter(String jsonBody, String jsonPropertyNeeded, String className, String packageName){
		
		JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonBody);
        
        JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonBody);
		} catch (JSONException e1) {
			_logger.debug("JSONException during jsonToPojoConverter jsonObject");
		}
        
        Iterator<?> keys = jsonObject.keys();
        
        Map<String, String> pojoCollection = new HashMap<String, String>();

	    while( keys.hasNext() ) {
	        String key = (String)keys.next();
	        
	        if (element.isJsonObject()) {
	            
	            Object aObj = null;
				try {
					aObj = jsonObject.get(key);
				} catch (JSONException e) {
					_logger.debug("JSONException during jsonToPojoConverter aObj");
				}
	            if(aObj instanceof Integer){
	            	pojoCollection.put(key, "Integer");
	            }else if(aObj instanceof String){
	            	pojoCollection.put(key, "String");
	            }else if(aObj instanceof Boolean){
	            	pojoCollection.put(key, "Boolean");
	            }else if(aObj instanceof Long){
	            	pojoCollection.put(key, "Long");
	            }else if(aObj instanceof JSONArray){
	            	pojoCollection.put(key, StringUtils.capitalize(key)+"[]");
	            }else if(aObj instanceof Number){
	            	pojoCollection.put(key, "Double");
	            }else if(aObj instanceof JSONObject) {
	            	pojoCollection.put(key, StringUtils.capitalize(key));
	            }else{
	            	pojoCollection.put(key, "String");
	            }
	        }
	    } 
	    
	    return generatePojo(pojoCollection, jsonPropertyNeeded, className, packageName);
	}
	
public StringBuilder generatePojo(Map<String, String> pojoCollection, String jsonPropertyNeeded, String className, String packageName) {
		
    	StringBuilder pojoGen = new StringBuilder();
    	String header = "package "+packageName+";\n"+"\n";
    	String ClassName = "public class "+className+"{"+"\n";
    	String body = null;
    	String jsonPropertyInclude = null;
    	StringBuilder bodyBuilder = new StringBuilder();
    	
    	for (Map.Entry<String, String> entry : pojoCollection.entrySet())
	    {
    		if("yes".equalsIgnoreCase(jsonPropertyNeeded)){
    			jsonPropertyInclude = "@JsonProperty(\""+entry.getKey()+"\")";
    	        body = "private "+entry.getValue()+" "+entry.getKey()+";";
    	        bodyBuilder.append("\n"+jsonPropertyInclude+"\n"+body+"\n");
    		}else{
    	        body = "private "+entry.getValue()+" "+entry.getKey()+";";
    	        bodyBuilder.append("\n"+body+"\n");
    		}
	    }
    	
    	StringBuilder setterGetterBuilder = new StringBuilder();
    	String getBody = null;
    	String getThisBody = null;
    	String setBody = null;
    	String setThisBody = null;
    	
    	for (Map.Entry<String, String> entry : pojoCollection.entrySet())
	    {
    		getBody = "public "+entry.getValue()+" get"+StringUtils.capitalize(entry.getKey())+"(){"+"\n";
    		getThisBody = "\t"+"return "+entry.getKey()+";\n"+"}"+"\n";
    		
    		setBody = "public void set"+StringUtils.capitalize(entry.getKey())+"("+entry.getValue()+" "+entry.getKey()+"){"+"\n";
    		setThisBody = "\t"+"this."+entry.getKey()+" = "+entry.getKey()+";\n"+"}"+"\n";
    		
    		setterGetterBuilder.append("\n"+getBody+getThisBody+"\n"+setBody+setThisBody);
	    }
    	
    	String footer = "\n}";
    	
    	if("yes".equalsIgnoreCase(jsonPropertyNeeded)){
    		String jsonOrderPropertyStart = "@JsonPropertyOrder({";
    		StringBuilder jsonOrderPropertyBody = new StringBuilder();
    		int index = 0;
    		for (Map.Entry<String, String> entry : pojoCollection.entrySet())
    	    {
    			if(index == 0) {
    				jsonOrderPropertyBody.append("\""+entry.getKey()+"\"");
    				index+=1;
    				continue;
    			}else
    				jsonOrderPropertyBody.append(",\""+entry.getKey()+"\"");
    			
    			index+=1;
    	    }
    		String jsonOrderPropertyEnd = "})\n";
    		String jsonInclude = "@JsonInclude(JsonInclude.Include.NON_EMPTY) \n";
    		pojoGen.append(header+jsonOrderPropertyStart+jsonOrderPropertyBody.toString()+jsonOrderPropertyEnd+
    				jsonInclude+ClassName+bodyBuilder.toString()+setterGetterBuilder.toString()+footer);
    	}else {
    		pojoGen.append(header+ClassName+bodyBuilder.toString()+setterGetterBuilder.toString()+footer);
    	}
    	
    	
    	return pojoGen;
    	
    }
}
