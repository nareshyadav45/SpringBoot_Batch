package com.batch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batch.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}
