package com.in28minutes.microservices.currencyexchangeservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.xray.spring.aop.XRayEnabled;

@Component
@XRayEnabled
public class CurrencyExchangeServiceImpl {
	
	@Autowired
	private ExchangeValueRepository repository;

	public ExchangeValue findByFromAndTo(String from, String to) {
		
		ExchangeValue exchangeValue = 
				repository.findByFromAndTo(from, to);
		return exchangeValue;

	}

}
