
package com.example.demo.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@author Kavitha S

@Entity
@JsonIgnoreProperties
@Table(name = "Transaction")

public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transactionId")
	private Long transactionId;

	/*
	 * @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	 * 
	 * @JoinColumn(name = "customer_id", nullable = true)
	 */

	
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@JsonIgnoreProperties(ignoreUnknown = true)
	private Customer customerId;

	@Column(name = "accountNo")
	private Long accountNo;

	@Column(name = "acctbranch")
	private String acctbranch;

	@Column(name = "currency")
	private String currency;

	@Column(name = "accttype")
	private String accttype;

	@Column(name = "amount")
	private float amount;

	@Column(name = "createdDate", columnDefinition = "DATE")
	private LocalDate createdDate;

	@Column(name = "txntype")
	private String txntype;

	@Column(name = "custName")
	private String custName;

	@Column(name = "status")
	private String status;

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Transaction(Long transactionId, Customer customerId, Long accountNo, String acctbranch, String currency,
			String accttype, float amount, LocalDate createdDate, String txntype, String custName, String status) {
		super();
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.accountNo = accountNo;
		this.acctbranch = acctbranch;
		this.currency = currency;
		this.accttype = accttype;
		this.amount = amount;
		this.createdDate = createdDate;
		this.txntype = txntype;
		this.custName = custName;
		this.status = status;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Customer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Customer customerId) {
		this.customerId = customerId;
	}

	public Long getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
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

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public String getTxntype() {
		return txntype;
	}

	public void setTxntype(String txntype) {
		this.txntype = txntype;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", customerId=" + customerId + ", accountNo=" + accountNo
				+ ", acctbranch=" + acctbranch + ", currency=" + currency + ", accttype=" + accttype + ", amount="
				+ amount + ", createdDate=" + createdDate + ", txntype=" + txntype + ", custName=" + custName
				+ ", status=" + status + "]";
	}

}
