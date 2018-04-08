package com.myApplication.myFormatter.Controller;

import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import springfox.documentation.annotations.ApiIgnore;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.myApplication.myFormatter.Service.FormatterService;

@Controller
public class FormatterController {

	@Autowired
	@Setter
	private FormatterService formatterService;
	
	// @Endpoint 1 - to get all details
	@GetMapping(path = "/Formatter", produces  = "application/json")
	public @ResponseBody Resource<String> displayFormatterDeatils() {
		Link swaggerUrl = new Link("http://localhost:8099/swagger-ui.html or http://127.0.0.1:8099/swagger-ui.html");
		Link linkJson = linkTo(methodOn(FormatterController.class).getFormatterJson(null)).withRel("JsonFormatter");
		Link treeJson = linkTo(methodOn(FormatterController.class).structureJson()).withRel("JsonTreeFormat");
		Link linkXml = linkTo(methodOn(FormatterController.class).getFormatterXml(null)).withRel("XmlFormatterAndDecoder");
		Link evaluatJson = linkTo(methodOn(FormatterController.class).jsonEvaluator(null, null)).withRel("JsonEvaluator");
		Link jsonPath = linkTo(methodOn(FormatterController.class).jsonPath(null, null)).withRel("JsonPath");
		Link jsonToXmlConverter = linkTo(methodOn(FormatterController.class).jsonToXmlConverter(null)).withRel("ConvertJsonToXml");
		Link xmlToJsonConverter = linkTo(methodOn(FormatterController.class).xmlToJsonConverter(null)).withRel("ConvertXmlToJson");
		Link singleLineJson = linkTo(methodOn(FormatterController.class).singleLineJson(null)).withRel("JsonToSingleLine");
		Link singleLineXml = linkTo(methodOn(FormatterController.class).singleLineXml(null)).withRel("XmlToSingleLine");
		Link yamlToJson = linkTo(methodOn(FormatterController.class).yamlToJsonConverter(null)).withRel("YamlToJsonConverter");
		Link jsonToYaml = linkTo(methodOn(FormatterController.class).jsonToYamlConverter(null)).withRel("JsonToYamlConverter");
		Resource<String> resource = new Resource<String>(""
				+ "1.SwaggerUrl, "
				+ "2.Json To Xml Converter, "
				+ "3.Xml to Json Converter, "
				+ "4.JSON Formatter, "
				+ "5.Json Evaluator, "
				+ "6.Json Path, "
				+ "7.Covert Json to Single Line, "
				+ "8.Json tree Format, "
				+ "9.XML Formatter, "
				+ "10.Convert Xml to Single Line"
				+ "11.YAML to JSON Converter"
				+ "12.JSON to YAML Converter");
		
		resource.add(swaggerUrl);
		resource.add(jsonToXmlConverter);
		resource.add(xmlToJsonConverter);
		resource.add(linkJson);
		resource.add(evaluatJson);
		resource.add(jsonPath);
		resource.add(singleLineJson);
		resource.add(treeJson);
		resource.add(linkXml);
		resource.add(singleLineXml);
		resource.add(yamlToJson);
		resource.add(jsonToYaml);
		
		return resource;
	}

	//----------------------------------------------Common Operation---------------------------------------------------------------------------//
	
	// @Endpoint 2 - Json to Xml converter
	@PostMapping(path = "/convertJsonToXml", consumes = MediaType.TEXT_PLAIN_VALUE, produces = "application/xml")
	public ResponseEntity<String> jsonToXmlConverter(
			@RequestBody String jsonBody) {
		String xmlResponse = formatterService.jsonToXmlConverter(jsonBody);
		ResponseEntity<String> respEntity = null;

		return respEntity.ok(xmlResponse);
	}

	// @Endpoint 3 - Xml to Json converter
	@PostMapping(path = "/convertXmlToJson", consumes = MediaType.TEXT_PLAIN_VALUE, produces = "application/json")
	public ResponseEntity<String> xmlToJsonConverter(@RequestBody String xmlBody) {
		String jsonResponse = formatterService.xmlToJsonConverter(xmlBody);
		ResponseEntity<String> respEntity = null;

		return respEntity.ok(jsonResponse);
	}
		
	//----------------------------------------------JSON Opertations---------------------------------------------------------------------------//
	// @Endpoint 4 - to format JSON
	@PostMapping(path = "/jsonFormatter", consumes = MediaType.TEXT_PLAIN_VALUE, produces = "application/json")
	public ResponseEntity<String> getFormatterJson(@RequestBody String stringToFormat) {
		ResponseEntity<String> respEntity = null;
		String prettyJson = formatterService.formatJson(stringToFormat);
        return respEntity.ok(prettyJson);
	}

	// @Endpoint 5 - find key and display value from structured json
	@PostMapping(path = "/jsonEvaluator", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<StringBuilder> jsonEvaluator(
			@RequestParam String searchKey, @RequestBody String jsonBody) {
		StringBuilder response = formatterService.jsonEvaluator(searchKey,
				jsonBody);
		ResponseEntity<StringBuilder> respEntity = null;
		return respEntity.ok(response);
	}
	
	// @Endpoint 6 - JsonPath to query json and find value
	@PostMapping(path = "/jsonPath", consumes = MediaType.TEXT_PLAIN_VALUE, produces = "application/json")
	public ResponseEntity<String> jsonPath(@RequestBody String jsonBody, @RequestParam String jsonQuery) {
		ResponseEntity<String> respEntity = null;
		String result = formatterService.jsonPath(jsonBody, jsonQuery);
		return respEntity.ok(result);
	}
	
	// @Endpoint 7 - to format JSON to a single line
	@PostMapping(path = "/jsonToSingleLine", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> singleLineJson(@RequestBody String formatted) {
		ResponseEntity<String> respEntity = null;
		String singleLine = formatterService.formatJsonToSingleLine(formatted);
		return respEntity.ok(singleLine);
	}
		
	// @Endpoint 8 - to structure json
	@ApiIgnore
	@RequestMapping("/jsonTree")
	public ModelAndView structureJson() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		return mav;
	}

	//----------------------------------------------XML Opertations---------------------------------------------------------------------------//
	// @Endpoint 9 - to format XML
	@PostMapping(path = "/xmlFormatterAndDecoder", consumes = MediaType.TEXT_PLAIN_VALUE, produces = "application/xml")
	public ResponseEntity<String> getFormatterXml(
			@RequestBody String stringToFormat) {
		ResponseEntity<String> respEntity = null;
		String prettyXml = formatterService.formatXml(stringToFormat);
		return respEntity.ok(prettyXml);
	}
		
	// @Endpoint 10 - to format XML to a single line
	@PostMapping(path = "/xmlToSingleLine", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> singleLineXml(@RequestBody String formatted) {
		ResponseEntity<String> respEntity = null;
		String singleLine = formatterService.formatXmlToSingleLine(formatted);
		return respEntity.ok(singleLine);
	}
		
	// @Endpoint 11 - convert YAML to JSON
	@PostMapping(path = "/yamlToJson", consumes = MediaType.TEXT_PLAIN_VALUE, produces = "application/json")
	public ResponseEntity<String> yamlToJsonConverter(@RequestBody String yamlBody) {
		ResponseEntity<String> respEntity = null;
		String jsonResponse = formatterService.yamlToJsonConverter(yamlBody);
		return respEntity.ok(jsonResponse); 
	}
	
	// @Endpoint 11 - convert YAML to JSON
	@PostMapping(path = "/jsonToYaml", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> jsonToYamlConverter(@RequestBody String jsonBody) {
		ResponseEntity<String> respEntity = null;
		String yamlResponse = formatterService.jsonToYamlConverter(jsonBody);
		return respEntity.ok(yamlResponse);
	}
}
