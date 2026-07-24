package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.service.CustomerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("Customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService; // Injecting our service class layer

	// Add Customer to table
	@PostMapping("addCustomer")
	public Customer addCustomer(@RequestBody Customer customer) {
		System.out.println(customer.getBirthDate());
		return customerService.addCustomer(customer);
	}

	// Get Customer ID List
	@GetMapping("getCustomerIds")
	public List<Long> getCustomerIds() {
		return customerService.getCustomerIds();
	}

	// Get List of all Customers
	@GetMapping("getCustomers")
	public List<Customer> getCustomers() {
		return customerService.getCustomers();
	}

	// Update Customer
	@PutMapping("updateCustomer/{id}")
	public Customer updateCustomer(@RequestBody Customer customer, @PathVariable Long id) {
		System.out.println(customer);
		return customerService.updateCustomer(customer, id);
	}

	// Get Customer by emailId
	@GetMapping("getUserByEmail/{emailId}")
	public List<Customer> getUserByEmail(@PathVariable String emailId) {
		return customerService.getUserByEmail(emailId);
	}

	// Add account to table
	@PostMapping("addAccount")
	public Account addAccount(@RequestBody Account account) {
		return customerService.addAccount(account);
	}

	// Get all account records list
	@GetMapping("findAllAccounts")
	public List<Account> findAllAccounts() {
		return customerService.findAllAccounts();
	}

	// Get all account records by particular customer ID
	@GetMapping("getAccountByCustomer/{id}")
	public List<Account> getAccountByCustomer(@PathVariable Long id) {
		System.out.println("Customer ID: " + id);
		return customerService.getAccountByCustomer(id);
	}

	// Get list of account IDS
	@GetMapping("findAllAccountIds")
	public List<Long> findAllAccountIds() {
		return customerService.findAllAccountIds();
	}

	// Get list of account IDS by customer Id
	@GetMapping("getAccountIdsByCustomer/{customerId}")
	public List<Long> getAccountIdsByCustomer(@PathVariable Long customerId) {
		System.out.println("Customer ID: " + customerId);
		return customerService.getAccountIdsByCustomer(customerId);
	}

	// Get account by particular ID
	@GetMapping("getAccountById/{id}")
	public Optional<Account> getAccountById(@PathVariable Long id) {
		return customerService.getAccountById(id);
	}

	// Account deposit
	@PostMapping("depositAccount")
	public Transaction depositAccount(@RequestBody Transaction transaction) {
		System.out.println(transaction);
		return customerService.depositAccount(transaction);
	}

	// Account Withdrawal
	@PostMapping("withdrawAccount")
	public Transaction withdrawAccount(@RequestBody Transaction transaction) {
		System.out.println(transaction);
		return customerService.withdrawAccount(transaction);
	}

	// Account Funds Transfer
	@PostMapping("transferAccounts")
	public FundsTransfer transferAccounts(@RequestBody FundsTransfer fundsTransfer) {
		System.out.println(fundsTransfer);
		return customerService.transferFunds(fundsTransfer);
	}

	// Cheque Book Request
	@PostMapping("chequeRequest")
	public ChequeRequest chequeRequest(@RequestBody ChequeRequest chequerequest) {
		System.out.println(chequerequest);
		return customerService.requestChequeBook(chequerequest);
	}
}