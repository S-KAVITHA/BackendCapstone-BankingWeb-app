
package com.example.demo.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ChequeRequestRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.FundsTransferRepository;
import com.example.demo.repository.TransactionRepository;

//@author Kavitha S

@RestController

@CrossOrigin(origins = "*", allowedHeaders = "*")

@RequestMapping("Customer")
public class CustomerController {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	CustomerRepository custrepo;

	@Autowired
	TransactionRepository txnrepo;

	@Autowired
	AccountRepository acctrepo;

	@Autowired
	FundsTransferRepository ftrepo;

	@Autowired
	ChequeRequestRepository requestrepo;

	// Add Customer to table
	@PostMapping("addcustomer")
	public Customer addcustomer(@RequestBody Customer customer) {

		System.out.println(customer.getBirthDate());
		customer.setCreatedDate(LocalDate.now());
		System.out.println(customer);
		return custrepo.save(customer);

	}

	// Get Customer ID List
	@GetMapping("getcustIds")
	public List<Long> listIds() {
		System.out.println(((Collection<Customer>) custrepo.findAll()).stream()
				.map(customer -> customer.getCustomerId()).collect(Collectors.toList()));
		return ((Collection<Customer>) custrepo.findAll()).stream().map(customer -> customer.getCustomerId())
				.collect(Collectors.toList());
	}

	// Get List of all Customers
	@GetMapping("getcustomers")
	public List<Customer> getAllCustomers() {
		List<Customer> customer = (List<Customer>) custrepo.findAll();
		System.out.println(customer);
		return customer;
	}

	

	
	
	@PutMapping("updatecust/{id}")
	public Customer updateacust(@RequestBody Customer customer, @PathVariable Long id) {
		System.out.println(customer);
		
		customer.setCustomerId(id);
		System.out.println(id);
		return custrepo.save(customer);
	}

	
	
	
	// Get Customer by emailId
	@GetMapping("getUserByEmail/{emailId}")
	public List<Customer> getUserByEmail(@PathVariable String emailId) {
		System.out.println(custrepo.findByemailId(emailId));
		return custrepo.findByemailId(emailId);
	}

	// Add account to table
	@PostMapping("addaccount")
	public Account addaccount(@RequestBody Account account) {

		account.setCreatedDate(LocalDate.now());
		System.out.println(account);
		return acctrepo.saveAndFlush(account);

	}

	// Get all account records list
	@GetMapping("getaccounts")
	public List<Account> getAllaccounts() {
		return acctrepo.findAll();
	}

	// Get all account records by particular customer ID
	@GetMapping("getcustaccounts/{id}")
	public List<Account> getaccountBycust(@PathVariable Long id) {
		System.out.println(id);
		return custrepo.findBycustomerId(id);

	}

	// Get list of account IDS
		@GetMapping("getacctlist")
	public List<Long> getAcctIdlist() {

		List<Account> acctlist = acctrepo.findAll();
		List<Long> acctIdalllist = acctlist.stream().map(account -> account.getAccountId())
				.collect(Collectors.toList());

		System.out.println(acctIdalllist);
		return acctIdalllist;
	}

	// Get list of account IDS by customer Id
	@GetMapping("getacctId/{custid}")
	public List<Long> getAcctId(@PathVariable Long custid) {
		System.out.println(custid);
		List<Account> result = custrepo.findBycustomerId(custid);

		System.out.println(
				"print accounts" + result.stream().map(account -> account.getAccountId()).collect(Collectors.toList()));

		List<Long> acctIdlist = result.stream().map(account -> account.getAccountId()).collect(Collectors.toList());

		/*
		 * System.out.println(((Collection<Customer>)
		 * custrepo.findBycustomerId(custid)).stream() .map(customer ->
		 * customer.getCustomerId()).collect(Collectors.toList()));
		 * 
		 * List<Set<Account>> result = ((custrepo.findBycustomerId(custid)).stream()
		 * .map(customer -> customer.getAccounts()).collect(Collectors.toList()));
		 * 
		 * List<Long> ids = result.stream().map(account ->
		 * account.getAccountId()).collect(Collectors.toList());
		 * System.out.println(ids);
		 */
		return acctIdlist;

	}

	// Get account by particular ID
	@GetMapping("getaccount/{id}")
	public Optional<Account> getaccountById(@PathVariable Long id) {
		System.out.println(acctrepo.findById(id));
		return acctrepo.findById(id);
	}

	// Account deposit
	@PostMapping("depositacct")
	public Transaction depositacct(@RequestBody Transaction transaction) {
		System.out.println(transaction);
		transaction.setCreatedDate(LocalDate.now());

		Long getacctNo = transaction.getAccountNo();
		Optional<Account> fetchAcct = getaccountById(getacctNo);

		if (fetchAcct.isPresent()) {
			Account getAcct = fetchAcct.get();
			float getAcctbal = getAcct.getBalance();
			getAcctbal = getAcctbal + transaction.getAmount();
			getAcct.setBalance(getAcctbal);
			acctrepo.save(getAcct);
		}

		return txnrepo.save(transaction);
	}

	// Account Withdrawal
	@PostMapping("withdrawacct")
	public Transaction withdrawacct(@RequestBody Transaction transaction) {
		System.out.println(transaction);
		transaction.setCreatedDate(LocalDate.now());

		Long getacctNo = transaction.getAccountNo();
		Optional<Account> fetchAcct = getaccountById(getacctNo);

		if (fetchAcct.isPresent()) {
			Account getAcct = fetchAcct.get();
			float getAcctbal = getAcct.getBalance();
			getAcctbal = getAcctbal - transaction.getAmount();
			getAcct.setBalance(getAcctbal);
			acctrepo.save(getAcct);
		}

		return txnrepo.save(transaction);
	}

	// Account Funds Transfer
	@PostMapping("transfer")
	public FundsTransfer transferaccts(@RequestBody FundsTransfer fundstrans) {

		fundstrans.setCreatedDate(LocalDate.now());
		System.out.println(fundstrans);

		Long getFacctNo = fundstrans.getFromAcctNo();
		Optional<Account> fetchFAcct = getaccountById(getFacctNo);

		if (fetchFAcct.isPresent()) {
			Account getFAcct = fetchFAcct.get();
			float getFAcctbal = getFAcct.getBalance();
			getFAcctbal = getFAcctbal - fundstrans.getAmount();
			getFAcct.setBalance(getFAcctbal);
			acctrepo.save(getFAcct);
		}

		Long getTacctNo = fundstrans.getToAcctNo();

		Optional<Account> fetchTAcct = getaccountById(getTacctNo);

		if (fetchTAcct.isPresent()) {
			Account getTAcct = fetchTAcct.get();
			float getTAcctbal = getTAcct.getBalance();
			getTAcctbal = getTAcctbal + fundstrans.getAmount();
			getTAcct.setBalance(getTAcctbal);
			acctrepo.save(getTAcct);
		}

		return ftrepo.save(fundstrans);
	}

	// Cheque Book Request
	@PostMapping("request")
	public ChequeRequest chequeRequest(@RequestBody ChequeRequest chequerequest) {

		chequerequest.setCreatedDate(LocalDate.now());
		System.out.println(chequerequest);
		return requestrepo.save(chequerequest);
	}
}
