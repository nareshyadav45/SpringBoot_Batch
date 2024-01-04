package com.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.entity.Customer;

//@Component
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer item) throws Exception {
		if(item.getCountry().equals("United States")) 
			return item;
		return null;	
	}
	

	
	
	
	
}
