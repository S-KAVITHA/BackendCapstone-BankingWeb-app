
package com.example.demo.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@author Kavitha S

@Entity
@JsonIgnoreProperties
@Table(name = "customer")

@NamedEntityGraph(name = "customer.accounts", attributeNodes = @NamedAttributeNode(value = "accounts", subgraph = "customer.accounts.accountId"), subgraphs = @NamedSubgraph(name = "customer.accounts.accountId", attributeNodes = @NamedAttributeNode("accountId")))
@NamedEntityGraph(name = "customer.requests", attributeNodes = @NamedAttributeNode(value = "requests", subgraph = "customer.requests.requestId"), subgraphs = @NamedSubgraph(name = "customer.requests.requestId", attributeNodes = @NamedAttributeNode("requestId")))
@NamedEntityGraph(name = "customer.transactions", attributeNodes = @NamedAttributeNode(value = "transactions", subgraph = "customer.transactions.transactionId"), subgraphs = @NamedSubgraph(name = "customer.transactions.transactionId", attributeNodes = @NamedAttributeNode("transactionId")))


@SequenceGenerator(name = "cust_gen", sequenceName = "cust_gen", initialValue = 1450001)

public class Customer {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cust_gen")
	@Column(name = "customerId")
	private Long customerId;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "emailId")
	private String emailId;

	@Column(name = "password")
	private String password;

	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "zipcode")
	private long zipcode;

	@Column(name = "country")
	private String country;

	@Column(name = "birthDate", columnDefinition = "DATE")
	private java.sql.Date birthDate;

	@Column(name = "agree")
	private boolean agree;


	@Column(name = "status")
	private String status;

	@Column(name = "createdDate", columnDefinition = "DATE")
	private LocalDate createdDate;

	@OneToMany(mappedBy = "customerId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Account> accounts = new HashSet<Account>();

	@OneToMany(mappedBy = "customerId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Transaction> transactions = new HashSet<Transaction>();

	@OneToMany(mappedBy = "customerId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<ChequeRequest> requests = new HashSet<ChequeRequest>();

	
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Customer(Long customerId, String firstName, String lastName, String emailId, String password, String address,
			String city, String state, long zipcode, String country, Date birthDate, boolean agree, String status,
			LocalDate createdDate, Set<Account> accounts, Set<Transaction> transactions, Set<ChequeRequest> requests) {
		super();
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailId = emailId;
		this.password = password;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.country = country;
		this.birthDate = birthDate;
		this.agree = agree;
		this.status = status;
		this.createdDate = createdDate;
		this.accounts = accounts;
		this.transactions = transactions;
		this.requests = requests;
	}



	public Long getCustomerId() {
		return customerId;
	}



	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getEmailId() {
		return emailId;
	}



	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public long getZipcode() {
		return zipcode;
	}



	public void setZipcode(long zipcode) {
		this.zipcode = zipcode;
	}



	public String getCountry() {
		return country;
	}



	public void setCountry(String country) {
		this.country = country;
	}



	public java.sql.Date getBirthDate() {
		return birthDate;
	}



	public void setBirthDate(java.sql.Date birthDate) {
		this.birthDate = birthDate;
	}



	public boolean isAgree() {
		return agree;
	}



	public void setAgree(boolean agree) {
		this.agree = agree;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public LocalDate getCreatedDate() {
		return createdDate;
	}



	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}



	public Set<Account> getAccounts() {
		return accounts;
	}



	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}



	public Set<Transaction> getTransactions() {
		return transactions;
	}



	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}



	public Set<ChequeRequest> getRequests() {
		return requests;
	}



	public void setRequests(Set<ChequeRequest> requests) {
		this.requests = requests;
	}


	@Override
	public String toString() {
		return " + customerId +";
	}
	

}
