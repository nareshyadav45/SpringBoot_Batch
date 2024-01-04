package com.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.batch.entity.Customer;
import com.batch.processor.CustomerItemProcessor;
import com.batch.repositories.CustomerRepository;

@Configuration
@EnableBatchProcessing
public class Customer_Batch_Config {
	
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	//ItemReader
	
	@Bean
	public FlatFileItemReader<Customer> customerFlatFileItemReader(){
		FlatFileItemReader<Customer> itemReader=new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setLinesToSkip(1);
		itemReader.setName("CustomerItemReader");
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {
		DefaultLineMapper<Customer> lineMapper=new DefaultLineMapper<>();
		
		DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");
		
		BeanWrapperFieldSetMapper<Customer> beanWrapperFieldSetMapper=new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(Customer.class);
		
		lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		lineMapper.setLineTokenizer(lineTokenizer);

		return lineMapper;
	}
	
	//Item Processor
	
	@Bean
	public CustomerItemProcessor customerItemProcessor() {
		return new CustomerItemProcessor();
	}

	//Item Writter
	@Bean
	public RepositoryItemWriter<Customer> repositoryItemWriter(){
		
		RepositoryItemWriter<Customer> itemWriter=new RepositoryItemWriter<>();
		itemWriter.setRepository(this.customerRepository);
		itemWriter.setMethodName("save");
		
		return itemWriter;
	}
	
	//Step Bean 
	
	@Bean
	public Step step() {
		return this.stepBuilderFactory.get("step-1").<Customer,Customer>chunk(10)
		.reader(customerFlatFileItemReader())
		.processor(customerItemProcessor())
		.writer(repositoryItemWriter())
		.build();
	}
	
	//Job Bean
	@Bean
	public Job job() {
		return jobBuilderFactory.get("customer-job")
				.flow(step())
				.end()
				.build();
	}
	
	
}
