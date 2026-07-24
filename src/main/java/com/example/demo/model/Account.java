
package com.example.demo.model;

import java.sql.Date;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@author Kavitha S

@Entity
@Table(name = "Account")
@JsonIgnoreProperties
@SequenceGenerator(name = "acct_gen", sequenceName = "acct_gen", initialValue = 1004450001)

public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "acct_gen")
	@Column(name = "accountId")
	private Long accountId;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@JsonIgnoreProperties(ignoreUnknown = true)
	
	private Customer customerId;

	@Column(name = "custName")
	private String custName;

	@Column(name = "acctbranch")
	private String acctbranch;

	@Column(name = "currency")
	private String currency;

	@Column(name = "accttype")
	private String accttype;

	@Column(name = "balance")
	private float balance;

	@Column(name = "createdDate", columnDefinition = "DATE")
	private LocalDate createdDate;

	@Column(name = "status")
	private String status;

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Account(Long accountId, Customer customerId, String custName, String acctbranch, String currency,
			String accttype, float balance, LocalDate createdDate, String status) {
		super();
		this.accountId = accountId;
		this.customerId = customerId;
		this.custName = custName;
		this.acctbranch = acctbranch;
		this.currency = currency;
		this.accttype = accttype;
		this.balance = balance;
		this.createdDate = createdDate;
		this.status = status;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

//@JsonBackReference
	public Customer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Customer customerId) {
		this.customerId = customerId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getAcctbranch() {
		return acctbranch;
	}

	public void setAcctbranch(String acctbranch) {
		this.acctbranch = acctbranch;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccttype() {
		return accttype;
	}

	public void setAccttype(String accttype) {
		this.accttype = accttype;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", customerId=" + customerId + ", custName=" + custName
				+ ", acctbranch=" + acctbranch + ", currency=" + currency + ", accttype=" + accttype + ", balance="
				+ balance + ", createdDate=" + createdDate + ", status=" + status + "]";
	}

}
