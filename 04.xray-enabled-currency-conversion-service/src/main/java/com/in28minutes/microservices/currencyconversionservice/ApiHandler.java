package com.in28minutes.microservices.currencyconversionservice;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@XRayEnabled
public class ApiHandler {
	
	public CurrencyConversionBean getConversionBean(String from, String to) {
		CloseableHttpResponse response = null;
		CurrencyConversionBean currencyConversionBean = null;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		String url = "http://MicroservicesLB-1623179640.eu-west-1.elb.amazonaws.com/api/currency-exchange/currency-exchange/from/"+from+"/to/"+to;
		HttpGet httpGet = new HttpGet(url);

		try {
			response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			ObjectMapper mapper = new ObjectMapper();
			
			Map<String, Object> jsonMap = mapper.readValue(inputStream, Map.class);
			//Map<String, String> content = (Map<String, String>) jsonMap.get("responseData");
			
			currencyConversionBean = mapper.convertValue(jsonMap, CurrencyConversionBean.class);
			
			
			EntityUtils.consume(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("http call failed");
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (Exception ex) {
			}
		}
		
		return currencyConversionBean;
	}

}
