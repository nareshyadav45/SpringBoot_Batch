package com.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.batch.entity.Customer;

public class CustomerItemProcessor implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer item) throws Exception {
		
		return item;
	}
	

}
