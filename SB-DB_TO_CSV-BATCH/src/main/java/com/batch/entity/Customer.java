package com.batch.entity;


import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

	@Id
	private int id;
	private String contact_no;
	private String country;
	private String dob;
	private String email;
	private String first_name;
	private String gender;
	private String last_name;
	
	
}
