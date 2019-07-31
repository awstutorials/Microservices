package com.in28minutes.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.xray.spring.aop.XRayEnabled;

@RestController
@XRayEnabled
public class CurrencyConversionController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//@Autowired
	//private CurrencyExchangeServiceProxy proxy;
	
	@Autowired
	private ApiHandler apiHandler;

	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		// Feign - Problem 1
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);

		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(
				"http://MicroservicesLB-1623179640.eu-west-1.elb.amazonaws.com/api/currency-exchange/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,
				uriVariables);

		CurrencyConversionBean response = responseEntity.getBody();

		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
				quantity.multiply(response.getConversionMultiple()), response.getPort());
	}
	
	@GetMapping("/xray/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyXRay(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		

		CurrencyConversionBean response = apiHandler.getConversionBean(from, to);

		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
				quantity.multiply(response.getConversionMultiple()), response.getPort());
	}

//	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
//	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
//			@PathVariable BigDecimal quantity) {
//
//		CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);
//
//		logger.info("{}", response);
//		
//		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
//				quantity.multiply(response.getConversionMultiple()), response.getPort());
//	}

}
