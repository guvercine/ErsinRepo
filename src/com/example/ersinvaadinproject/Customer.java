package com.example.ersinvaadinproject;

import java.util.Date;

public class Customer {
	
	private int id;
	private String name,surname,gender,city,active;
	private String birthdate;
	
	
	public Customer(int id,String name,String sname,String gender,String date,String city,String active){
		
		this.id=id;
		this.name=name;
		this.surname=sname;
		this.gender=gender;
		this.birthdate=date;
		this.city=city;
		this.active=active;
		
		
	}

	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	
	

}
