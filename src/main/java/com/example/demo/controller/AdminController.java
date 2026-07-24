
package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Account;
import com.example.demo.model.ChequeRequest;
import com.example.demo.model.Customer;
import com.example.demo.model.FundsTransfer;
import com.example.demo.model.Transaction;
import com.example.demo.model.BankAdmin;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ChequeRequestRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.FundsTransferRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.BankAdminRepository;

//@author Kavitha S

@RestController

@CrossOrigin(origins = "*", allowedHeaders = "*")

@RequestMapping("Admin")
public class AdminController {

	@Autowired
	BankAdminRepository adminrepo;

	@Autowired
	CustomerRepository custrepo;

	@Autowired
	AccountRepository acctrepo;

	@Autowired
	TransactionRepository txnrepo;

	@Autowired
	FundsTransferRepository transferrepo;

	@Autowired
	ChequeRequestRepository chequerepo;

	// Add admin to table
	@PostMapping("addadmin")
	public BankAdmin addadmin(@RequestBody BankAdmin user) {
		System.out.println(user);
		return adminrepo.save(user);
	}

	// Get admin by emailId
	@GetMapping("getadminByEmail/{emailId}")
	public List<BankAdmin> getadminByEmail(@PathVariable String emailId) {
		return adminrepo.findByemailId(emailId);
	}

	// Get Customer by status
	@GetMapping("getcustapprove/{status}")
	public List<Customer> getCustApproval(@PathVariable String status) {
		System.out.println("Status" + status);
		// System.out.println("cust records" + custrepo.findBystatus(status));
		return custrepo.findBystatus(status);
	}

	// Get Account by status
	@GetMapping("getacctapprove/{status}")
	public List<Account> getAcctApproval(@PathVariable String status) {
		System.out.println(acctrepo.findBystatus(status));
		return acctrepo.findBystatus(status);
	}

	// Get Transaction by status
	@GetMapping("gettxnapprove/{status}")
	public List<Transaction> getTxnApproval(@PathVariable String status) {
		//System.out.println(txnrepo.findBystatus(status));
		List<Transaction> tr= txnrepo.findBystatus(status);
		for (Transaction gtr: tr) {
		System.out.println(gtr);
		System.out.println(gtr.getCustomerId());
		}
		return txnrepo.findBystatus(status);
	}

	// Get Transfer by status
	@GetMapping("gettransferapprove/{status}")
	public List<FundsTransfer> getTransferApproval(@PathVariable String status) {
		return transferrepo.findBystatus(status);
	}

	// Get Cheque Request by status
	@GetMapping("getchequeapprove/{status}")
	public List<ChequeRequest> getChequeApproval(@PathVariable String status) {
		
		System.out.println(chequerepo.findBystatus(status));
		
		return chequerepo.findBystatus(status);
	}
	

	// Update customer record as approved status
	@PutMapping("updatecust/{id}")
	public Customer updatecustByStatusId(@RequestBody Customer customer, @PathVariable Long id) {
		customer.setCustomerId(id);
		System.out.println(customer);
		return custrepo.save(customer);
	}
	
	
	// Update acct record as approved status
	@PutMapping("updateacct/{id}")
	public Account updateacctByStatusId(@RequestBody Account account, @PathVariable Long id) {
		account.setAccountId(id);
		System.out.println(account);
		return acctrepo.save(account);
	}
	
	// Update txn record as approved status
	@PutMapping("updatetxn/{id}")
	public Transaction updatetxnByStatusId(@RequestBody Transaction txn, @PathVariable Long id) {
		txn.setTransactionId(id);
		System.out.println(txn);
		return txnrepo.save(txn);
	}
	
	// Update transfer record as approved status
	@PutMapping("updatetransfer/{id}")
	public FundsTransfer updatetransferByStatusId(@RequestBody FundsTransfer transfer, @PathVariable Long id) {
		transfer.setTransferId(id);
		System.out.println(transfer);
		System.out.println(id);
		return transferrepo.save(transfer);
	}
	
	// Update cheques request record as approved status
	@PutMapping("updatecheques/{id}")
	public ChequeRequest updatechequeByStatusId(@RequestBody ChequeRequest request, @PathVariable Long id) {
		request.setRequestId(id);
		System.out.println(request);
		return chequerepo.save(request);
	}

}
