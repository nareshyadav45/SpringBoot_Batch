package com.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Sort;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.batch.entity.Customer;
import com.batch.processor.CustomerItemProcessor;
import com.batch.repository.CustomerRepository;

@Configuration
@EnableBatchProcessing
public class CustomerConfig {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	//ItemReader
	@Bean
	public RepositoryItemReader<Customer> repositoryItemReader(){
		RepositoryItemReader<Customer> itemReader=new RepositoryItemReader<>();
		itemReader.setRepository(customerRepository);
		itemReader.setMethodName("findAll");
		
		Map<String, org.springframework.data.domain.Sort.Direction> map=new HashMap<>();
		map.put("id", org.springframework.data.domain.Sort.Direction.ASC);
		itemReader.setSort(map);
		return itemReader;
	}
	
	//ItemProcessor
	@Bean
	public CustomerItemProcessor itemProcessor() {
		return new CustomerItemProcessor();
	}
	
	//ItemWritter
	@Bean
	public FlatFileItemWriter<Customer> flatFileItemWriter(){
		FlatFileItemWriter<Customer> fileItemWriter=new FlatFileItemWriter<>();
		fileItemWriter.setName("csvFileWritter");
		fileItemWriter.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		
		BeanWrapperFieldExtractor<Customer> beanWrapperFieldExtractor=new BeanWrapperFieldExtractor<>();
		beanWrapperFieldExtractor.setNames(new String[] {"id","contact_no","country","dob","email","first_name","gender","last_name"});
		
		
		DelimitedLineAggregator<Customer> delimitedLineAggregator=new DelimitedLineAggregator<>();
		delimitedLineAggregator.setDelimiter(",");
		delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
		
		fileItemWriter.setLineAggregator(delimitedLineAggregator);
		return fileItemWriter;
	}
	
	//Step Bean
	
	@Bean
	public Step step() {
		return stepBuilderFactory.get("Db_To_Csv").<Customer,Customer>chunk(10)
				.reader(repositoryItemReader())
				.processor(itemProcessor())
				.writer(flatFileItemWriter())
				.build();
	}
	
	//Job Bean
	@Bean
	public Job job() {
		return jobBuilderFactory.get("Db_to_Csv")
				.flow(step())
				.end()
				.build();
	}
	
}
