
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

@Table(name = "FundsTransfer")

public class FundsTransfer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transferId")
	private Long transferId;

	@Column(name = "fromAcctNo")
	private Long fromAcctNo;
	
	@Column(name = "fromAccbranch")
	private String fromAccbranch;

	@Column(name = "fromAcctype")
	private String fromAcctype;

	
	@Column(name = "toAcctNo")
	private Long toAcctNo;
	
	@Column(name = "toAccbranch")
	private String toAccbranch;

	@Column(name = "toAcctype")
	private String toAcctype;

	@Column(name = "fromAccCCY")
	private String fromAccCCY;

	@Column(name = "toAccCCY")
	private String toAccCCY;

	@Column(name = "amount")
	private float amount;

	@Column(name = "createdDate", columnDefinition = "DATE")
	private LocalDate createdDate;

	@Column(name = "status")
	private String status;

	public FundsTransfer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FundsTransfer(Long transferId,  Long fromAcctNo, String fromAccbranch,
			String fromAcctype, Long toAcctNo, String toAccbranch, String toAcctype, String fromAccCCY, String toAccCCY,
			float amount, LocalDate createdDate, String status) {
		super();
		this.transferId = transferId;
		
		this.fromAcctNo = fromAcctNo;
		this.fromAccbranch = fromAccbranch;
		this.fromAcctype = fromAcctype;
		this.toAcctNo = toAcctNo;
		this.toAccbranch = toAccbranch;
		this.toAcctype = toAcctype;
		this.fromAccCCY = fromAccCCY;
		this.toAccCCY = toAccCCY;
		this.amount = amount;
		this.createdDate = createdDate;
		
		this.status = status;
	}

	public Long getTransferId() {
		return transferId;
	}

	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}

	

	public Long getFromAcctNo() {
		return fromAcctNo;
	}

	public void setFromAcctNo(Long fromAcctNo) {
		this.fromAcctNo = fromAcctNo;
	}

	public String getFromAccbranch() {
		return fromAccbranch;
	}

	public void setFromAccbranch(String fromAccbranch) {
		this.fromAccbranch = fromAccbranch;
	}

	public String getFromAcctype() {
		return fromAcctype;
	}

	public void setFromAcctype(String fromAcctype) {
		this.fromAcctype = fromAcctype;
	}

	public Long getToAcctNo() {
		return toAcctNo;
	}

	public void setToAcctNo(Long toAcctNo) {
		this.toAcctNo = toAcctNo;
	}

	public String getToAccbranch() {
		return toAccbranch;
	}

	public void setToAccbranch(String toAccbranch) {
		this.toAccbranch = toAccbranch;
	}

	public String getToAcctype() {
		return toAcctype;
	}

	public void setToAcctype(String toAcctype) {
		this.toAcctype = toAcctype;
	}

	public String getFromAccCCY() {
		return fromAccCCY;
	}

	public void setFromAccCCY(String fromAccCCY) {
		this.fromAccCCY = fromAccCCY;
	}

	public String getToAccCCY() {
		return toAccCCY;
	}

	public void setToAccCCY(String toAccCCY) {
		this.toAccCCY = toAccCCY;
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

	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "FundsTransfer [transferId=" + transferId + ", customerId=" +  ", fromAcctNo=" + fromAcctNo
				+ ", fromAccbranch=" + fromAccbranch + ", fromAcctype=" + fromAcctype + ", toAcctNo=" + toAcctNo
				+ ", toAccbranch=" + toAccbranch + ", toAcctype=" + toAcctype + ", fromAccCCY=" + fromAccCCY
				+ ", toAccCCY=" + toAccCCY + ", amount=" + amount + ", createdDate=" + createdDate + ", custName="
				+  ", status=" + status + "]";
	}

	}